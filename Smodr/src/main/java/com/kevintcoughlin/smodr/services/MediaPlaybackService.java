package com.kevintcoughlin.smodr.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;

import com.kevintcoughlin.smodr.R;
import com.kevintcoughlin.smodr.views.activities.ChannelsActivity;
import com.kevintcoughlin.smodr.views.fragments.EpisodesFragment;

import java.io.IOException;

public class MediaPlaybackService extends Service implements MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener {
    public static final int NOTIFICATION_ID = 37;
    public static final String ACTION_PLAY = "com.kevintcoughlin.app.PLAY";
    private final String SERVICE_NAME = "Smodr";

    private String mTitle = "";
    private String mDescription = "";

    MediaPlayer mMediaPlayer = null;

    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals(ACTION_PLAY)) {
            String url = intent.getStringExtra(EpisodesFragment.INTENT_EPISODE_URL);
            mTitle = intent.getStringExtra(EpisodesFragment.INTENT_EPISODE_TITLE);
            mDescription = intent.getStringExtra(EpisodesFragment.INTENT_EPISODE_DESCRIPTION);

            try {
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setOnPreparedListener(this);
                mMediaPlayer.setDataSource(url);
                mMediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
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

    private void stopPlayback() {
        mMediaPlayer.reset();
        stopForeground(true);
    }

    private void createNotification() {
        PendingIntent pi = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                new Intent(getApplicationContext(), ChannelsActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        Notification notification = new Notification();
        notification.tickerText = mTitle;
        notification.icon = R.drawable.icon;
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        notification.setLatestEventInfo(
                getApplicationContext(),
                SERVICE_NAME,
                mTitle,
                pi
        );
        startForeground(NOTIFICATION_ID, notification);
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
        createNotification();
    }
}
