package com.kevintcoughlin.smodr;

import android.app.Application;
import android.support.annotation.NonNull;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;
import com.parse.Parse;
import io.fabric.sdk.android.Fabric;

/**
 * The Smodr {@link Application}.
 *
 * @author kevincoughlin
 */
public final class SmodrApplication extends Application {
	@NonNull
	private static final String PROPERTY_ID = "UA-28569939-11";

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "xdsymmQJ9lxCgIlbZEW3V1oeNAkKp54yl4fehfzR", "ZCAACD4VyKimQgPo6WVGRcfA3Ddd9AcheSDFWrbo");

        if (BuildConfig.DEBUG) {
            final GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            analytics.setDryRun(true);
            analytics.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
        }
    }

    /**
     * Returns the {@link Tracker} for the {@link Application}.
	 *
	 * @return the {@link Application}'s {@link Tracker}.
	 */
    public synchronized Tracker getTracker() {
        return GoogleAnalytics.getInstance(this).newTracker(PROPERTY_ID);
    }
}
