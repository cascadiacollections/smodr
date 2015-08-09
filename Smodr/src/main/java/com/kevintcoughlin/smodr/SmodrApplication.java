package com.kevintcoughlin.smodr;

import android.app.Application;
import android.support.annotation.NonNull;
import android.util.ArrayMap;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;

import java.util.Map;

public final class SmodrApplication extends Application {
	@NonNull
	private static final String PROPERTY_ID = "UA-28569939-11";
	@NonNull
    private final Map<TrackerName, Tracker> mTrackers = new ArrayMap<>();

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            final GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            analytics.setDryRun(true);
            analytics.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
        }
    }

    public synchronized Tracker getTracker(@NonNull final TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {
            final Tracker t = GoogleAnalytics.getInstance(this).newTracker(PROPERTY_ID);
            mTrackers.put(trackerId, t);
        }
        return mTrackers.get(trackerId);
    }

    public enum TrackerName {
        APP_TRACKER
    }
}
