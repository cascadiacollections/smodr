package com.kevintcoughlin.smodr;

import android.app.Application;
import android.support.annotation.NonNull;
import android.util.ArrayMap;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;

import java.util.Map;

/**
 * The Smodr {@link Application}.
 *
 * @author kevincoughlin
 */
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

	/**
	 * Returns the {@link Tracker} for the {@link Application}.
	 *
	 * @param trackerId
	 * 		The Google Analytics tracker identifier.
	 * @return the {@link Application}'s {@link Tracker}.
	 */
	public synchronized Tracker getTracker(@NonNull final TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {
            final Tracker t = GoogleAnalytics.getInstance(this).newTracker(PROPERTY_ID);
            mTrackers.put(trackerId, t);
        }
        return mTrackers.get(trackerId);
    }

	/**
	 * Enum of Google Analytic {@link Tracker} identifiers.
	 */
	public enum TrackerName {
        APP_TRACKER
    }
}
