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

    private final String SERVICE_NAME = "Smodr";
    private String mTitle = "";
    private String mDescription = "";

    MediaPlayer mMediaPlayer = null;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals(ACTION_PLAY)) {
            String url = intent.getStringExtra(EpisodesFragment.INTENT_EPISODE_URL);
            mTitle = intent.getStringExtra(EpisodesFragment.INTENT_EPISODE_TITLE);
            mDescription = intent.getStringExtra(EpisodesFragment.INTENT_EPISODE_DESCRIPTION);

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
        } else if (intent.getAction().equals(ACTION_PAUSE)) {
            stopPlayback();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        stopPlayback();
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void stopPlayback() {
        mMediaPlayer.reset();
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(NOTIFICATION_ID);
        stopForeground(true);
    }

    private void createNotification() {
        Intent mPauseIntent = new Intent(this, MediaPlaybackService.class);
        mPauseIntent.setAction(ACTION_PAUSE);

        PendingIntent mPendingPauseIntent = PendingIntent.getService(
                this,
                0,
                mPauseIntent,
                PendingIntent.FLAG_CANCEL_CURRENT
        );

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.icon)
                        .setOngoing(true)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setContentTitle(SERVICE_NAME)
                        .setContentText(mTitle)
                        .addAction(R.drawable.ic_action_stop, "Stop", mPendingPauseIntent);

        Intent resultIntent = new Intent(this, ChannelsActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(ChannelsActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
        createNotification();
    }

}
