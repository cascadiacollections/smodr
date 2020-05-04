package com.kevintcoughlin.smodr;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * The Smodr {@link Application}.
 *
 * @author kevincoughlin
 */
public final class SmodrApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
    }
}
