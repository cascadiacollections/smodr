package com.kevintcoughlin.smodr.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.kevintcoughlin.smodr.R;
import com.kevintcoughlin.smodr.views.activities.MainActivity;

import java.io.IOException;

public final class MediaPlaybackService extends Service implements MediaPlayer.OnErrorListener,
        MediaPlayer.OnPreparedListener {
    @NonNull
    public static final String INTENT_EPISODE_URL = "intent_episode_url";
    @NonNull
    public static final String INTENT_EPISODE_TITLE = "intent_episode_title";
    @NonNull
    public static final String INTENT_EPISODE_DESCRIPTION = "intent_episode_description";
    @NonNull
    public static final String ACTION_PLAY = "com.kevintcoughlin.smodr.app.PLAY";
    @NonNull
    private static final String ACTION_PAUSE = "com.kevintcoughlin.smodr.app.PAUSE";
    @NonNull
    private static final String ACTION_RESUME = "com.kevintcoughlin.smodr.app.RESUME";
    @NonNull
    private static final String ACTION_STOP = "com.kevintcoughlin.smodr.app.STOP";
    @NonNull
    private String mTitle = "";
    @NonNull
    private String mDescription = "";
    @Nullable
    private MediaPlayer mMediaPlayer;
    private boolean mPrepared = false;
    private static final int NOTIFICATION_ID = 37;

    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if ((intent != null) && (intent.getAction() == null)) {
            stopPlayback();
        } else if (intent != null) {
            if (ACTION_PLAY.equals(intent.getAction())) {
                final String url = intent.getStringExtra(INTENT_EPISODE_URL);
                mTitle = intent.getStringExtra(INTENT_EPISODE_TITLE);
                mDescription = intent.getStringExtra(INTENT_EPISODE_DESCRIPTION);
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
                    if (mMediaPlayer != null) {
                        mMediaPlayer.start();
                    }
                } else {
                    stopPlayback();
                }
                createNotification();
            } else if (intent.getAction().equals(ACTION_STOP)) {
                stopPlayback();
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
        mPrepared = false;
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopPlayback();
        mPrepared = false;
    }

    private void pausePlayback() {
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
        }
    }

    private void stopPlayback() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
        }
        mPrepared = false;

        final NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (mNotificationManager != null) {
            mNotificationManager.cancel(NOTIFICATION_ID);
        }
        stopForeground(true);
    }

    private void createNotification() {
        final Intent mIntent = new Intent(this, MediaPlaybackService.class);
        mIntent.setAction(getAction());
        final PendingIntent mPendingIntent = PendingIntent.getService(this, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        final NotificationCompat.Action action = new NotificationCompat.Action.Builder(
                getIcon(),
                getString(getTitle()),
                mPendingIntent).build();

        final NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.icon))
                        .setSmallIcon(getIcon())
                        .setOngoing(true)
                        .setContentTitle(mTitle)
                        .setContentText(mDescription)
                        .addAction(action);

        final Intent resultIntent = new Intent(this, MainActivity.class);
        final TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        final PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);

        final NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        final Notification notification = mBuilder.build();

        if (mNotificationManager != null) {
            mNotificationManager.notify(NOTIFICATION_ID, notification);
        }
        startForeground(NOTIFICATION_ID, notification);
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
        createNotification();
        mPrepared = true;
    }

    @DrawableRes
    private int getIcon() {
        return (mMediaPlayer != null && mMediaPlayer.isPlaying())
                ? R.drawable.ic_action_pause
                : R.drawable.ic_action_play;
    }

    @StringRes
    private int getTitle() {
        return (mMediaPlayer != null && mMediaPlayer.isPlaying())
                ? R.string.notification_action_pause
                : R.string.notification_action_play;
    }

    private String getAction() {
        return (mMediaPlayer != null && mMediaPlayer.isPlaying()) ? ACTION_PAUSE : ACTION_RESUME;
    }
}