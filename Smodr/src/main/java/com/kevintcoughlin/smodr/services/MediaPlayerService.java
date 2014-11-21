package com.kevintcoughlin.smodr.services;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.kevintcoughlin.smodr.R;
import com.kevintcoughlin.smodr.models.Item;

import java.io.IOException;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class MediaPlayerService extends Service {

    public static final String ACTION_PLAY = "action_play";
    public static final String ACTION_PAUSE = "action_pause";
    public static final String ACTION_STOP = "action_stop";

    private MediaPlayer mMediaPlayer;
    private MediaSessionManager mManager;
    private MediaSession mSession;
    private MediaController mController;
    private NotificationManager mNotificationManager;

    private Item mEpisode;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void handleIntent(Intent intent) {
        mEpisode = intent.getParcelableExtra("episode");
        switch (intent.getAction()) {
            case ACTION_PLAY:
                mController.getTransportControls().play();
                break;
            case ACTION_PAUSE:
                mController.getTransportControls().pause();
                break;
            case ACTION_STOP:
                mController.getTransportControls().stop();
                break;
            default:
                break;
        }
    }

    private Notification.Action generateAction(int icon, String title, String intentAction) {
        Intent intent = new Intent(getApplicationContext(), MediaPlayerService.class);
        intent.setAction(intentAction);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);
        return new Notification.Action.Builder(icon, title, pendingIntent).build();
    }

    private void buildNotification(Notification.Action action) {
        Notification.MediaStyle style = new Notification.MediaStyle();
        style.setMediaSession(mSession.getSessionToken());

        Intent intent = new Intent(getApplicationContext(), MediaPlayerService.class);
        intent.setAction(ACTION_STOP);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);
        Notification.Builder builder = new Notification.Builder( this )
                .setSmallIcon(R.drawable.icon)
                .setContentTitle(mEpisode.getTitle())
                .setContentText(mEpisode.getDescription())
                .setDeleteIntent(pendingIntent)
                .setStyle(style)
                .addAction(action);

        Notification notification = builder.build();
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mManager == null) {
            try {
                initMediaSessions();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        handleIntent(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    private void initMediaSessions() throws RemoteException {
        mMediaPlayer = new MediaPlayer();
        mManager = (MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE);
        mSession = new MediaSession(this, "smodr");
        mController = new MediaController(this, mSession.getSessionToken());
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
            }
        });

        mSession.setCallback(new MediaSession.Callback() {
            @Override
            public void onPlay() {
                super.onPlay();
                Log.e("MediaPlayerService", "onPlay");
                try {
                    mMediaPlayer.stop();
                    mMediaPlayer.setDataSource(getApplicationContext(), Uri.parse(mEpisode.getEnclosure().getUrl()));
                    mMediaPlayer.prepareAsync();
                    buildNotification(generateAction(android.R.drawable.ic_media_pause, "Pause", ACTION_PAUSE));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPause() {
                super.onPause();
                Log.e("MediaPlayerService", "onPause");
                mMediaPlayer.pause();
                buildNotification(generateAction(android.R.drawable.ic_media_play, "Play", ACTION_PLAY));
            }

            @Override
            public void onSkipToNext() {
                super.onSkipToNext();
                Log.e("MediaPlayerService", "onSkipToNext");
                buildNotification(generateAction(android.R.drawable.ic_media_pause, "Pause", ACTION_PAUSE));
            }

            @Override
            public void onSkipToPrevious() {
                super.onSkipToPrevious();
                Log.e("MediaPlayerService", "onSkipToPrevious");
                buildNotification(generateAction(android.R.drawable.ic_media_pause, "Pause", ACTION_PAUSE));
            }

            @Override
            public void onFastForward() {
                super.onFastForward();
                Log.e("MediaPlayerService", "onFastForward");
            }

            @Override
            public void onRewind() {
                super.onRewind();
                Log.e("MediaPlayerService", "onRewind");
            }

            @Override
            public void onStop() {
                super.onStop();
                Log.e("MediaPlayerService", "onStop");
                mMediaPlayer.stop();
                NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(1);
                Intent intent = new Intent(getApplicationContext(), MediaPlayerService.class);
                stopService(intent);
            }

            @Override
            public void onSeekTo(long pos) {
                super.onSeekTo(pos);
            }
        });
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mSession.release();
        mMediaPlayer.release();
        mMediaPlayer = null;
        return super.onUnbind(intent);
    }
}