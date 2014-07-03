package com.kevintcoughlin.smodr;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;
import java.util.EnumSet;

import hugo.weaving.DebugLog;

/**
 * A wrapper class for {@link android.media.MediaPlayer}.
 * <p>
 * Encapsulates an instance of MediaPlayer, and makes a record of its internal state accessible via a
 * getState() accessor. Most of the frequently used methods are available, but some still
 * need adding.
 * </p>
 */
public class EpisodePlayer {
    public static String BROADCAST_ACTION = "media-playback";
    private static String tag = "MediaPlayerWrapper";
    private MediaPlayer mPlayer;
    private State mCurrentState;
    private EpisodePlayer mWrapper;
    private Context mContext;

    public EpisodePlayer (Context context) {
        mContext = context;
        mWrapper = this;
        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mCurrentState = State.IDLE;
        mPlayer.setOnPreparedListener(mOnPreparedListener);
        mPlayer.setOnCompletionListener(mOnCompletionListener);
        mPlayer.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
        mPlayer.setOnErrorListener(mOnErrorListener);
        mPlayer.setOnInfoListener(mOnInfoListener);
    }

    public static enum State {
        IDLE, ERROR, INITIALIZED, PREPARING, PREPARED, STARTED, STOPPED, PLAYBACK_COMPLETE, PAUSED;
    }

    @DebugLog
    public void setDataSource(Uri url) {
        if (mCurrentState == State.IDLE) {
            try {
                mPlayer.setDataSource(mContext, url);
                mCurrentState = State.INITIALIZED;
                prepareAsync();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            reset();
            try {
                mPlayer.setDataSource(mContext, url);
                mCurrentState = State.INITIALIZED;
                prepareAsync();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @DebugLog
    public void prepareAsync() {
        if (EnumSet.of(State.INITIALIZED, State.STOPPED).contains(mCurrentState)) {
            mPlayer.prepareAsync();
            mCurrentState = State.PREPARING;
        } else throw new RuntimeException();
    }

    @DebugLog
    public boolean isPlaying() {
        Log.d(tag, "isPlaying()");
        if (mCurrentState != State.ERROR) {
            return mPlayer.isPlaying();
        } else throw new RuntimeException();
    }

    @DebugLog
    public void seekTo(int mSec) {
        if (EnumSet.of(State.PREPARED, State.STARTED, State.PAUSED, State.PLAYBACK_COMPLETE).contains(mCurrentState)) {
            mPlayer.seekTo(mSec);
        } else throw new RuntimeException();
    }

    @DebugLog
    public void pause() {
        if (EnumSet.of(State.STARTED, State.PAUSED).contains(mCurrentState)) {
            mPlayer.pause();
            mCurrentState = State.PAUSED;
            sendMessage();
        } else throw new RuntimeException();
    }

    @DebugLog
    public void start() {
        if (EnumSet.of(State.PREPARED, State.STARTED, State.PAUSED, State.PLAYBACK_COMPLETE).contains(mCurrentState)) {
            mPlayer.start();
            mCurrentState = State.STARTED;
            sendMessage();
        } else throw new RuntimeException();
    }

    @DebugLog
    public void stop() {
        if (EnumSet.of(State.PREPARED, State.STARTED, State.STOPPED, State.PAUSED, State.PLAYBACK_COMPLETE).contains(
                mCurrentState)) {
            mPlayer.stop();
            mCurrentState = State.STOPPED;
            sendMessage();
        } else throw new RuntimeException();
    }

    @DebugLog
    public void reset() {
        mPlayer.reset();
        mCurrentState = State.IDLE;
    }

    @DebugLog
    public State getState() {
        return mCurrentState;
    }

    @DebugLog
    public void release() {
        mPlayer.release();
    }

    @DebugLog
     private void sendMessage() {
         Intent intent = new Intent("media-playback");
         intent.putExtra("state", getState());
         LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
     }

    private OnPreparedListener mOnPreparedListener = new OnPreparedListener() {
        @DebugLog
        @Override
        public void onPrepared(MediaPlayer mp) {
            Log.d(tag, "playback prepared.");
            mCurrentState = State.PREPARED;
            mWrapper.onPrepared(mp);
            start();
            mCurrentState = State.STARTED;
        }

    };

    private OnCompletionListener mOnCompletionListener = new OnCompletionListener() {
        @DebugLog
        @Override
        public void onCompletion(MediaPlayer mp) {
            Log.d(tag, "playback completed.");
            mCurrentState = State.PLAYBACK_COMPLETE;
            stop();
            mWrapper.onCompletion(mp);
        }

    };

    private OnBufferingUpdateListener mOnBufferingUpdateListener = new OnBufferingUpdateListener() {
        @DebugLog
        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            Log.d(tag, percent + "% buffered.");
            mWrapper.onBufferingUpdate(mp, percent);
        }
    };

    private OnErrorListener mOnErrorListener = new OnErrorListener() {
        @DebugLog
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Log.d(tag, "OnError - Error code: " + what + " Extra code: " + extra);
            switch(what){
                case -1004:
                    Log.d("Streaming Media", "MEDIA_ERROR_IO");
                    break;
                case -1007:
                    Log.d("Streaming Media", "MEDIA_ERROR_MALFORMED");
                    break;
                case 200:
                    Log.d("Streaming Media", "MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK");
                    break;
                case 100:
                    Log.d("Streaming Media", "MEDIA_ERROR_SERVER_DIED");
                    break;
                case -110:
                    Log.d("Streaming Media", "MEDIA_ERROR_TIMED_OUT");
                    break;
                case 1:
                    Log.d("Streaming Media", "MEDIA_ERROR_UNKNOWN");
                    break;
                case -1010:
                    Log.d("Streaming Media", "MEDIA_ERROR_UNSUPPORTED");
                    break;
            }

            switch(extra){
                case 800:
                    Log.d("Streaming Media", "MEDIA_INFO_BAD_INTERLEAVING");
                    break;
                case 702:
                    Log.d("Streaming Media", "MEDIA_INFO_BUFFERING_END");
                    break;
                case 701:
                    Log.d("Streaming Media", "MEDIA_INFO_METADATA_UPDATE");
                    break;
                case 802:
                    Log.d("Streaming Media", "MEDIA_INFO_METADATA_UPDATE");
                    break;
                case 801:
                    Log.d("Streaming Media", "MEDIA_INFO_NOT_SEEKABLE");
                    break;
                case 1:
                    Log.d("Streaming Media", "MEDIA_INFO_UNKNOWN");
                    break;
                case 3:
                    Log.d("Streaming Media", "MEDIA_INFO_VIDEO_RENDERING_START");
                    break;
                case 700 :
                    Log.d("Streaming Media", "MEDIA_INFO_VIDEO_TRACK_LAGGING");
                    break;
            }
            mCurrentState = State.ERROR;
            reset();
            mWrapper.onError(mp, what, extra);
            return false;
        }

    };

    private OnInfoListener mOnInfoListener = new OnInfoListener() {
        @DebugLog
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            Log.d(tag, "playback info.");
            mWrapper.onInfo(mp, what, extra);
            return false;
        }

    };

    @DebugLog
    public void onPrepared(MediaPlayer mp) {}

    @DebugLog
    public void onCompletion(MediaPlayer mp) {}

    @DebugLog
    public void onBufferingUpdate(MediaPlayer mp, int percent) {}

    @DebugLog
    boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @DebugLog
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @DebugLog
    public int getCurrentPosition() {
        if (mCurrentState != State.ERROR) {
            return mPlayer.getCurrentPosition();
        } else {
            return 0;
        }
    }

    @DebugLog
    public int getDuration() {
        if (EnumSet.of(State.PREPARED, State.STARTED, State.PAUSED, State.STOPPED, State.PLAYBACK_COMPLETE).contains(mCurrentState)) {
            return mPlayer.getDuration();
        } else {
            return 100;
        }
    }

}
