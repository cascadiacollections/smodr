package com.kevintcoughlin.smodr.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kevintcoughlin.smodr.models.Item;

import org.jetbrains.annotations.Contract;

public final class MediaService extends Service implements MediaPlayer.OnErrorListener,
        MediaPlayer.OnPreparedListener {
    public static final int THIRTY_SECONDS_IN_MILLISECONDS = 30000;
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
    @NonNull
    public static final String ACTION_FORWARD = "com.kevintcoughlin.smodr.app.FORWARD";
    @NonNull
    public static final String ACTION_REWIND = "com.kevintcoughlin.smodr.app.REWIND";
    @Nullable
    private MediaPlayer mMediaPlayer;
    private final IBinder mBinder = new MediaServiceBinder();
    @Nullable
    private IPlaybackListener mListener;

    public class MediaServiceBinder extends Binder {
        public MediaService getService() {
            return MediaService.this;
        }
    }

    @Contract(pure = true)
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @NonNull
    public static Intent createIntent(@NonNull final Context context, @NonNull final Item item) {
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

        final @Nullable String url = intent.getStringExtra(INTENT_EPISODE_URL);
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
                case ACTION_FORWARD:
                    seekTo(THIRTY_SECONDS_IN_MILLISECONDS);
                    break;
                case ACTION_REWIND:
                    seekTo(-THIRTY_SECONDS_IN_MILLISECONDS);
                    break;
            }
        }

        return Service.START_REDELIVER_INTENT;
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
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }
        mListener = null;
    }

    public void seekTo(final int milliseconds) {
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(mMediaPlayer.getCurrentPosition() + milliseconds);
        }
    }

    public boolean isPlaying() {
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
    }

    public void resumePlayback() {
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
            mListener.onStartPlayback();
        }
    }

    public void pausePlayback() {
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
            mListener.onStopPlayback();
        }
    }

    public void stopPlayback() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mListener.onStopPlayback();
        }
    }

    public interface IPlaybackListener {
        void onStartPlayback();
        void onStopPlayback();
    }

    public void setPlaybackListener(final IPlaybackListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onPrepared(final MediaPlayer mediaPlayer) {
        resumePlayback();
    }

    private void startPlayback(@Nullable final String url) {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            stopPlayback();
        }

        try {
            final Uri uri = Uri.parse(url);

            mMediaPlayer = MediaPlayer.create(this, uri);
            mMediaPlayer.start();
            mListener.onStartPlayback();
        } catch (NullPointerException exception) {
            Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}