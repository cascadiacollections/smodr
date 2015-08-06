package com.kevintcoughlin.smodr;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.ArrayMap;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import java.util.Map;

/**
 * Smodr App
 */
public final class SmodrApplication extends Application {
	@NonNull
	private static final String PROPERTY_ID = "UA-28569939-11";
	@NonNull
    private final Map<TrackerName, Tracker> mTrackers = new ArrayMap<>();
    @NonNull
	private static final Bus mEventBus = new Bus(ThreadEnforcer.ANY);
	@Nullable
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

        if (BuildConfig.DEBUG) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            analytics.setDryRun(true);
            analytics.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
        }
    }

	@Nullable
	public static SmodrApplication getInstance() {
        return instance;
    }

	public synchronized Tracker getTracker(@NonNull final TrackerName trackerId) {
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
