package com.kevintcoughlin.smodr;

import android.app.Application;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import java.util.HashMap;

/**
 * Smodr App
 */
public final class SmodrApplication extends Application {
    private static final String PROPERTY_ID = "UA-28569939-11";
    private final HashMap<TrackerName, Tracker> mTrackers = new HashMap<>();
    private static Bus mEventBus;
    private static SmodrApplication instance;

    public static Bus getEventBus() {
        return mEventBus;
    }

    public SmodrApplication() {
        instance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mEventBus = new Bus(ThreadEnforcer.ANY);

        if (BuildConfig.DEBUG) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            analytics.setDryRun(true);
            analytics.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
        }
    }

    public static SmodrApplication getInstance() {
        return instance;
    }

    public synchronized Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t = analytics.newTracker(PROPERTY_ID);
            mTrackers.put(trackerId, t);
        }
        return mTrackers.get(trackerId);
    }

    public enum TrackerName {
        APP_TRACKER
    }
}
