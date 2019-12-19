package com.kevintcoughlin.smodr.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kevintcoughlin.smodr.models.Item;

public final class MediaService extends Service implements MediaPlayer.OnErrorListener,
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
    public static final String ACTION_PAUSE = "com.kevintcoughlin.smodr.app.PAUSE";
    @NonNull
    public static final String ACTION_RESUME = "com.kevintcoughlin.smodr.app.RESUME";
    @NonNull
    public static final String ACTION_STOP = "com.kevintcoughlin.smodr.app.STOP";
    @Nullable
    private MediaPlayer mMediaPlayer;

    public static Intent createIntent(@NonNull Context context, @NonNull final Item item) {
        final Intent intent = new Intent(context, MediaService.class);
        final String mediaUrlString = item.getUri().toString();

        intent.setAction(MediaService.ACTION_PLAY);
        intent.putExtra(MediaService.INTENT_EPISODE_URL, mediaUrlString);
        intent.putExtra(MediaService.INTENT_EPISODE_TITLE, item.title);
        intent.putExtra(MediaService.INTENT_EPISODE_DESCRIPTION, item.description);

        return intent;
    }

    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        super.onStartCommand(intent, flags, startId);
        final String url = intent.getStringExtra(INTENT_EPISODE_URL);
        final String action = intent.getAction();

        if (action != null) {
            switch (action) {
                case ACTION_PAUSE:
                    pausePlayback();
                    break;
                case ACTION_PLAY:
                    startPlayback(url);
                    break;
                case ACTION_RESUME:
                    resumePlayback();
                    break;
                case ACTION_STOP:
                    stopPlayback();
                    break;
            }
        }

        return Service.START_REDELIVER_INTENT;
    }

    private void resumePlayback() {
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
        }
    }

    private void startPlayback(final String url) {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            stopPlayback();
        }

        final Uri uri = Uri.parse(url);

        mMediaPlayer = MediaPlayer.create(this, uri);
        mMediaPlayer.start();
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
        stopPlayback();
    }

    private void pausePlayback() {
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
        }
//        stopForeground(true);
    }

    private void stopPlayback() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }
//        stopForeground(true);
    }


    @Override
    public void onPrepared(final MediaPlayer mediaPlayer) {
        resumePlayback();
    }
}