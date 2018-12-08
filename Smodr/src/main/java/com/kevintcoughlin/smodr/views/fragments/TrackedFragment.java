package com.kevintcoughlin.smodr.views.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

/**
 * Fragment that tracks analytics.
 *
 * @author kevincoughlin
 */
public abstract class TrackedFragment extends Fragment {
    /**
     * The name of the screen to track.
     */
    @NonNull
    private static final String TAG = TrackedFragment.class.getSimpleName();

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        track();
    }

    /**
     * Sends a screen view to Google Analytics,
     * using the @{link #TAG} as the screen name.
     */
    private void track() {
//		final Tracker t = ((SmodrApplication) getActivity().getApplication()).getTracker();
//		t.setScreenName(TAG);
//		t.send(new HitBuilders.AppViewBuilder().build());
    }
}
