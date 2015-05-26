package com.kevintcoughlin.smodr.views.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kevintcoughlin.smodr.SmodrApplication;

/**
 * Fragment that tracks analytics.
 * Created by kevincoughlin on 5/25/15.
 */
public class TrackedFragment extends Fragment {
	public static final String TAG = TrackedFragment.class.getSimpleName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		track();
	}

	private void track() {
		final Tracker t = ((SmodrApplication) getActivity().getApplication())
				.getTracker(SmodrApplication.TrackerName.APP_TRACKER);
		t.setScreenName(TAG);
		t.send(new HitBuilders.AppViewBuilder().build());
	}
}
