package com.kevintcoughlin.smodr.util;

import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kevintcoughlin.smodr.R;

public class AnalyticsManager {
    private static Context sAppContext = null;

    private static Tracker mTracker;
    private final static String TAG = "AnalyticsManager";

    public static synchronized void setTracker(Tracker tracker) {
        mTracker = tracker;
    }

    private static boolean canSend() {
        return sAppContext != null && mTracker != null && PrefUtils.isTosAccepted(sAppContext) &&
                PrefUtils.isAnalyticsEnabled(sAppContext);
    }

    public static void sendScreenView(String screenName) {
        if (canSend()) {
            mTracker.setScreenName(screenName);
            mTracker.send(new HitBuilders.AppViewBuilder().build());
        }
    }

    public static void sendEvent(String category, String action, String label, long value) {
        if (canSend()) {
            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory(category)
                    .setAction(action)
                    .setLabel(label)
                    .setValue(value)
                    .build());
        }
    }

    public static void sendEvent(String category, String action, String label) {
        sendEvent(category, action, label, 0);
    }

    public Tracker getTracker() {
        return mTracker;
    }

    public static synchronized void initializeAnalyticsTracker(Context context) {
        sAppContext = context;
        if (mTracker == null) {
            int useProfile = R.xml.global_tracker;
            mTracker = GoogleAnalytics.getInstance(context).newTracker(useProfile);
        }
    }
}
