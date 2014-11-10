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
import com.kevintcoughlin.smodr.ui.activities.ChannelsActivity;

public class MediaPlaybackService extends Service implements MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener {
    public static final int NOTIFICATION_ID = 37;
    public static final String ACTION_PLAY = "com.kevintcoughlin.smodr.app.PLAY";
    public static final String ACTION_PAUSE = "com.kevintcoughlin.smodr.app.PAUSE";
    public static final String ACTION_RESUME = "com.kevintcoughlin.smodr.app.RESUME";
    public static final String ACTION_STOP = "com.kevintcoughlin.smodr.app.STOP";

    private final String SERVICE_NAME = "Smodr";
    private int mId;
    private String mTitle = "";
    private String mDescription = "";
    private int mPosition = 0;

    private final int POOL_SIZE = 2;
    private final int SAVE_PLAYBACK_POSITION_INTERVAL = 5000;
    private Context mContext;

    private boolean mIsPlaying = false;
    private boolean mPrepared = false;

    MediaPlayer mMediaPlayer = null;

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = getApplicationContext();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
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
        stopPlayback();
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
        Intent mIntent = new Intent(this, MediaPlaybackService.class);
        mIntent.setAction(ACTION_STOP);

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
                                R.drawable.ic_action_pause,
                                getString(R.string.notification_action_pause),
                                mPendingIntent
                        );

        Intent resultIntent = new Intent(this, ChannelsActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(ChannelsActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = mBuilder.build();

        mNotificationManager.notify(NOTIFICATION_ID, notification);
        startForeground(NOTIFICATION_ID, notification);
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();

        // At the end of the episode, seek to the beginning.
        if (mPosition >= mediaPlayer.getDuration())
            mPosition = 0;

        mediaPlayer.seekTo(mPosition);
        createNotification();

        mIsPlaying = true;
        mPrepared = true;
    }

    private int getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    private int getDuration() {
        return mMediaPlayer.getDuration();
    }
}
