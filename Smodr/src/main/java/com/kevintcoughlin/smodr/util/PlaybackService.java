package com.kevintcoughlin.smodr.util;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;

import com.kevintcoughlin.smodr.PlayerStatus;

/**
 * Controls the MediaPlayer that plays a FeedMedia-file
 */
public class PlaybackService extends Service {
    private static final String TAG = "PlaybackService";
    private static final String AVRCP_ACTION_PLAYER_STATUS_CHANGED = "com.android.music.playstatechanged";
    private static final String AVRCP_ACTION_META_CHANGED = "com.android.music.metachanged";

    public static final String EXTRA_PLAYABLE = "PlaybackService.PlayableExtra";
    public static final String EXTRA_SHOULD_STREAM = "extra.de.danoeh.antennapod.service.shouldStream";
    public static final String EXTRA_START_WHEN_PREPARED = "extra.de.danoeh.antennapod.service.startWhenPrepared";
    public static final String EXTRA_PREPARE_IMMEDIATELY = "extra.de.danoeh.antennapod.service.prepareImmediately";
    public static final String ACTION_PLAYER_STATUS_CHANGED = "action.de.danoeh.antennapod.service.playerStatusChanged";
    public static final String ACTION_PLAYER_NOTIFICATION = "action.de.danoeh.antennapod.service.playerNotification";
    public static final String EXTRA_NOTIFICATION_CODE = "extra.de.danoeh.antennapod.service.notificationCode";
    public static final String EXTRA_NOTIFICATION_TYPE = "extra.de.danoeh.antennapod.service.notificationType";
    public static final String ACTION_SHUTDOWN_PLAYBACK_SERVICE = "action.de.danoeh.antennapod.service.actionShutdownPlaybackService";
    public static final String ACTION_SKIP_CURRENT_EPISODE = "action.de.danoeh.antennapod.service.skipCurrentEpisode";

    public static final int INVALID_TIME = -1;
    public static final int NOTIFICATION_TYPE_ERROR = 0;
    public static final int NOTIFICATION_TYPE_INFO = 1;
    public static final int NOTIFICATION_TYPE_BUFFER_UPDATE = 2;
    public static final int NOTIFICATION_TYPE_RELOAD = 3;
    public static final int NOTIFICATION_TYPE_BUFFER_START = 5;
    public static final int NOTIFICATION_TYPE_BUFFER_END = 6;
    public static final int NOTIFICATION_TYPE_PLAYBACK_END = 7;
    public static boolean isRunning = false;
    public static boolean started = false;

    private static final int NOTIFICATION_ID = 1;
    private PlaybackServiceMediaPlayer mediaPlayer;
    private static volatile MediaType currentMediaType = MediaType.UNKNOWN;
    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public PlaybackService getService() {
            return PlaybackService.this;
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "Received onUnbind event");
        return super.onUnbind(intent);
    }

    @SuppressLint("NewApi")
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service created.");
        isRunning = true;

        registerReceiver(shutdownReceiver, new IntentFilter(
                ACTION_SHUTDOWN_PLAYBACK_SERVICE));
        registerReceiver(audioBecomingNoisy, new IntentFilter(
                AudioManager.ACTION_AUDIO_BECOMING_NOISY));
        registerReceiver(skipCurrentEpisodeReceiver, new IntentFilter(
                ACTION_SKIP_CURRENT_EPISODE));
        mediaPlayer = new PlaybackServiceMediaPlayer(this, mediaPlayerCallback);

    }

    @SuppressLint("NewApi")
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service is about to be destroyed");
        isRunning = false;
        started = false;
        currentMediaType = MediaType.UNKNOWN;

        unregisterReceiver(shutdownReceiver);
        unregisterReceiver(audioBecomingNoisy);
        unregisterReceiver(skipCurrentEpisodeReceiver);
        mediaPlayer.shutdown();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "Received onBind event");
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Log.d(TAG, "OnStartCommand called");

        final Playable playable = intent.getParcelableExtra(EXTRA_PLAYABLE);

        if ((flags & Service.START_FLAG_REDELIVERY) != 0) {
            Log.d(TAG, "onStartCommand is a redelivered intent, calling stopForeground now.");
            stopForeground(true);
        } else {
            started = true;
            boolean stream = intent.getBooleanExtra(EXTRA_SHOULD_STREAM, true);
            boolean startWhenPrepared = intent.getBooleanExtra(EXTRA_START_WHEN_PREPARED, false);
            boolean prepareImmediately = intent.getBooleanExtra(EXTRA_PREPARE_IMMEDIATELY, false);
            sendNotificationBroadcast(NOTIFICATION_TYPE_RELOAD, 0);
            mediaPlayer.playMediaObject(playable, stream, startWhenPrepared, prepareImmediately);
        }

        return Service.START_REDELIVER_INTENT;
    }

    /**
     * Handles media button events
     */
    private void handleKeycode(int keycode) {
        Log.d(TAG, "Handling keycode: " + keycode);

        final PlayerStatus status = mediaPlayer.getPSMPInfo().playerStatus;
        switch (keycode) {
            case KeyEvent.KEYCODE_HEADSETHOOK:
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                if (status == PlayerStatus.PLAYING) {
                    mediaPlayer.pause(true, true);
                } else if (status == PlayerStatus.PAUSED || status == PlayerStatus.PREPARED) {
                    mediaPlayer.resume();
                } else if (status == PlayerStatus.PREPARING) {
                    mediaPlayer.setStartWhenPrepared(!mediaPlayer.isStartWhenPrepared());
                } else if (status == PlayerStatus.INITIALIZED) {
                    mediaPlayer.setStartWhenPrepared(true);
                    mediaPlayer.prepare();
                }
                break;
            case KeyEvent.KEYCODE_MEDIA_PLAY:
                if (status == PlayerStatus.PAUSED || status == PlayerStatus.PREPARED) {
                    mediaPlayer.resume();
                } else if (status == PlayerStatus.INITIALIZED) {
                    mediaPlayer.setStartWhenPrepared(true);
                    mediaPlayer.prepare();
                }
                break;
            case KeyEvent.KEYCODE_MEDIA_PAUSE:
                if (status == PlayerStatus.PLAYING) {
                    mediaPlayer.pause(true, true);
                }
                break;
            case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD: {
                mediaPlayer.seekDelta(PlaybackController.DEFAULT_SEEK_DELTA);
                break;
            }
            case KeyEvent.KEYCODE_MEDIA_REWIND: {
                mediaPlayer.seekDelta(-PlaybackController.DEFAULT_SEEK_DELTA);
                break;
            }
        }
    }

    private final PlaybackServiceMediaPlayer.PSMPCallback mediaPlayerCallback = new PlaybackServiceMediaPlayer.PSMPCallback() {
        @Override
        public void statusChanged(PlaybackServiceMediaPlayer.PSMPInfo newInfo) {
            currentMediaType = mediaPlayer.getCurrentMediaType();
            switch (newInfo.playerStatus) {
                case INITIALIZED:
                    break;

                case PREPARED:
                    break;

                case PAUSED:
                    stopForeground(true);
                    break;

                case STOPPED:
                    break;

                case PLAYING:
                    Log.d(TAG, "Audiofocus successfully requested");
                    Log.d(TAG, "Resuming/Starting playback");
                    //(newInfo);
                    break;
                case ERROR:
                    break;
            }

            sendBroadcast(new Intent(ACTION_PLAYER_STATUS_CHANGED));
        }

        @Override
        public void shouldStop() {
            stopSelf();
        }

        @Override
        public void onBufferingUpdate(int percent) {
            sendNotificationBroadcast(NOTIFICATION_TYPE_BUFFER_UPDATE, percent);
        }

        @Override
        public boolean onMediaPlayerInfo(int code) {
            switch (code) {
                case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                    sendNotificationBroadcast(NOTIFICATION_TYPE_BUFFER_START, 0);
                    return true;
                case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                    sendNotificationBroadcast(NOTIFICATION_TYPE_BUFFER_END, 0);
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public boolean onMediaPlayerError(Object inObj, int what, int extra) {
            final String TAG = "PlaybackService.onErrorListener";
            Log.w(TAG, "An error has occured: " + what + " " + extra);
            if (mediaPlayer.getPSMPInfo().playerStatus == PlayerStatus.PLAYING) {
                mediaPlayer.pause(true, false);
            }
            sendNotificationBroadcast(NOTIFICATION_TYPE_ERROR, what);
            stopSelf();
            return true;
        }

        @Override
        public boolean endPlayback(boolean playNextEpisode) {
            PlaybackService.this.endPlayback(true);
            return true;
        }

    };

    private void endPlayback(boolean playNextEpisode) {
        Log.d(TAG, "Playback ended");
    }

    private void sendNotificationBroadcast(int type, int code) {
        Intent intent = new Intent(ACTION_PLAYER_NOTIFICATION);
        intent.putExtra(EXTRA_NOTIFICATION_TYPE, type);
        intent.putExtra(EXTRA_NOTIFICATION_CODE, code);
        sendBroadcast(intent);
    }

    private BroadcastReceiver audioBecomingNoisy = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // sound is about to change, eg. bluetooth -> speaker
            Log.d(TAG, "Pausing playback because audio is becoming noisy");
        }
        // android.media.AUDIO_BECOMING_NOISY
    };

    private BroadcastReceiver shutdownReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null &&
                    intent.getAction().equals(ACTION_SHUTDOWN_PLAYBACK_SERVICE)) {
                stopSelf();
            }
        }

    };

    private BroadcastReceiver skipCurrentEpisodeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null &&
                    intent.getAction().equals(ACTION_SKIP_CURRENT_EPISODE)) {
                Log.d(TAG, "Received SKIP_CURRENT_EPISODE intent");
                mediaPlayer.endPlayback();
            }
        }
    };

    public static MediaType getCurrentMediaType() {
        return currentMediaType;
    }

    public void resume() {
        mediaPlayer.resume();
    }

    public void prepare() {
        mediaPlayer.prepare();
    }

    public void pause(boolean abandonAudioFocus, boolean reinit) {
        mediaPlayer.pause(abandonAudioFocus, reinit);
    }

    public void reinit() {
        mediaPlayer.reinit();
    }

    public PlaybackServiceMediaPlayer.PSMPInfo getPSMPInfo() {
        return mediaPlayer.getPSMPInfo();
    }

    public PlayerStatus getStatus() {
        return mediaPlayer.getPSMPInfo().playerStatus;
    }

    public Playable getPlayable() {
        return mediaPlayer.getPSMPInfo().playable;
    }

    public boolean isStartWhenPrepared() {
        return mediaPlayer.isStartWhenPrepared();
    }

    public void setStartWhenPrepared(boolean s) {
        mediaPlayer.setStartWhenPrepared(s);
    }


    public void seekTo(final int t) {
        mediaPlayer.seekTo(t);
    }


    public void seekDelta(final int d) {
        mediaPlayer.seekDelta(d);
    }

    /**
     * call getDuration() on mediaplayer or return INVALID_TIME if player is in
     * an invalid state.
     */
    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    /**
     * call getCurrentPosition() on mediaplayer or return INVALID_TIME if player
     * is in an invalid state.
     */
    public int getCurrentPosition() {
        return mediaPlayer.getPosition();
    }

    public boolean isStreaming() {
        return mediaPlayer.isStreaming();
    }

}
