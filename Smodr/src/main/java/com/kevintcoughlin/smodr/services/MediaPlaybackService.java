package com.kevintcoughlin.smodr.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.kevintcoughlin.smodr.R;
import com.kevintcoughlin.smodr.views.activities.ChannelsActivity;
import com.kevintcoughlin.smodr.views.fragments.EpisodesFragment;

import java.io.IOException;

public class MediaPlaybackService extends Service implements MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener {
    public static final int NOTIFICATION_ID = 37;
    public static final String ACTION_PLAY = "com.kevintcoughlin.smodr.app.PLAY";
    public static final String ACTION_PAUSE = "com.kevintcoughlin.smodr.app.PAUSE";
    public static final String ACTION_RESUME = "com.kevintcoughlin.smodr.app.RESUME";
    public static final String ACTION_STOP = "com.kevintcoughlin.smodr.app.STOP";

    private final String SERVICE_NAME = "Smodr";
    private String mTitle = "";
    private String mDescription = "";

    // State
    private boolean mIsPlaying = false;
    private boolean mPrepared = false;

    MediaPlayer mMediaPlayer = null;

    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (intent == null && intent.getAction() == null) {
            stopPlayback();
        } else {
            if (intent.getAction().equals(ACTION_PLAY)) {
                String url = intent.getStringExtra(EpisodesFragment.INTENT_EPISODE_URL);
                mTitle = intent.getStringExtra(EpisodesFragment.INTENT_EPISODE_TITLE);
                mDescription = intent.getStringExtra(EpisodesFragment.INTENT_EPISODE_DESCRIPTION);
                if (url != null) {
                    try {
                        if (mMediaPlayer == null) {
                            mMediaPlayer = new MediaPlayer();
                            mMediaPlayer.setOnPreparedListener(this);
                        } else {
                            stopPlayback();
                        }
                        mMediaPlayer.setDataSource(url);
                        mMediaPlayer.prepareAsync();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    stopPlayback();
                }
            } else if (intent.getAction().equals(ACTION_PAUSE)) {
                pausePlayback();
                createNotification();
            } else if (intent.getAction().equals(ACTION_RESUME)) {
                if (mPrepared) {
                    mMediaPlayer.start();
                    mIsPlaying = true;
                } else {
                    stopPlayback();
                }
                createNotification();
            } else if (intent.getAction().equals(ACTION_STOP)) {
                stopPlayback();
                createNotification();
            }
        }

        return Service.START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        stopPlayback();
        mIsPlaying = false;
        mPrepared = false;

        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mIsPlaying = false;
        mPrepared = false;
    }

    private void pausePlayback() {
       mMediaPlayer.pause();
       mIsPlaying = false;
    }

    private void stopPlayback() {
        mMediaPlayer.reset();
        mIsPlaying = false;
        mPrepared = false;

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.cancel(NOTIFICATION_ID);
        stopForeground(true);
    }

    private void createNotification() {
        final String action;
        if (!mIsPlaying && !mPrepared) {
            action = ACTION_PLAY;
        } else if (!mIsPlaying && mPrepared) {
            action = ACTION_RESUME;
        } else if (mIsPlaying && mPrepared) {
            action = ACTION_PAUSE;
        } else {
            action = ACTION_STOP;
        }

        Intent mIntent = new Intent(this, MediaPlaybackService.class);
        mIntent.setAction(action);

        PendingIntent mPendingIntent = PendingIntent.getService(
                this,
                0,
                mIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.icon)
                        .setOngoing(true)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setContentTitle(SERVICE_NAME)
                        .setContentText(mTitle)
                        .addAction(
                                (mIsPlaying) ? R.drawable.ic_action_pause : R.drawable.ic_action_play,
                                (mIsPlaying) ? "Pause" : "Play",
                                mPendingIntent);

        Intent resultIntent = new Intent(this, ChannelsActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(ChannelsActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
        mIsPlaying = true;
        mPrepared = true;

        createNotification();
    }

}
