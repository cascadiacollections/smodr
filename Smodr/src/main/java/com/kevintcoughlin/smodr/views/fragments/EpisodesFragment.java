package com.kevintcoughlin.smodr.views.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.widget.Toast;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kevintcoughlin.smodr.SmodrApplication;
import com.kevintcoughlin.smodr.adapters.EpisodesAdapter;
import com.kevintcoughlin.smodr.http.SmodcastClient;
import com.kevintcoughlin.smodr.models.Rss;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public final class EpisodesFragment extends Fragment {
	public static final String TAG = EpisodesFragment.class.getSimpleName();
    public static final String ARG_CHANNEL_NAME = "SHORT_NAME";

    private final EpisodesAdapter mAdapter = new EpisodesAdapter();

    public EpisodesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

	    final Bundle bundle = getArguments();
	    if (bundle != null) {
		    refresh(bundle.getString(ARG_CHANNEL_NAME));
	    }

        track();
    }

    private void refresh(final String shortName) {
        SmodcastClient.getClient().getFeed(shortName, new Callback<Rss>() {
	        @Override
	        public void success(Rss rss, Response response) {
		        mAdapter.setResults(rss.getChannel().getItems());
	        }

	        @Override
	        public void failure(RetrofitError retrofitError) {
		        Toast.makeText(getActivity(), retrofitError.getMessage(), Toast.LENGTH_SHORT).show();
	        }
        });
    }

    private void track() {
        Tracker t = ((SmodrApplication) getActivity().getApplication()).getTracker(
		        SmodrApplication.TrackerName.APP_TRACKER);

        t.setScreenName(TAG);
        t.send(new HitBuilders.AppViewBuilder().build());
    }

//    private void trackEpisodeSelected(String episodeTitle) {
//	    Tracker t = ((SmodrApplication) getActivity()
//			    .getApplication())
//			    .getTracker(SmodrApplication.TrackerName.APP_TRACKER);
//
//	    t.send(new HitBuilders.EventBuilder()
//			    .setCategory("EPISODE")
//			    .setAction("SELECTED")
//			    .setLabel(episodeTitle)
//			    .build());
//    }
}