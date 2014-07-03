package com.kevintcoughlin.smodr.util;

import android.view.SurfaceHolder;

import java.io.IOException;

public interface IPlayer {

    int getCurrentPosition();

    int getDuration();

    boolean isLooping();

    boolean isPlaying();

    void pause();

    void prepare() throws IllegalStateException, IOException;

    void prepareAsync();

    void release();

    void reset();

    void seekTo(int msec);

    void setAudioStreamType(int streamtype);

    void setDataSource(String path) throws IllegalStateException, IOException,
            IllegalArgumentException, SecurityException;

    void setDisplay(SurfaceHolder sh);

    void setLooping(boolean looping);

    void start();

    void stop();
}