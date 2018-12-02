package com.kevintcoughlin.smodr;

import android.app.Application;

/**
 * The Smodr {@link Application}.
 *
 * @author kevincoughlin
 */
public final class SmodrApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //Fabric.with(this, new Crashlytics());
    }
}
