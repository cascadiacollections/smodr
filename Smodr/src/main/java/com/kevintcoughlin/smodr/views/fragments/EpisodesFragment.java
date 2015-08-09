package com.kevintcoughlin.smodr.views.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kevintcoughlin.smodr.R;
import com.kevintcoughlin.smodr.SmodrApplication;
import com.kevintcoughlin.smodr.adapters.EpisodesAdapter;
import com.kevintcoughlin.smodr.http.SmodcastClient;
import com.kevintcoughlin.smodr.models.Rss;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

public final class EpisodesFragment extends TrackedFragment {
	@NonNull
	private static final String TAG = EpisodesFragment.class.getSimpleName();
	@NonNull
	public static final String ARG_CHANNEL_NAME = "SHORT_NAME";
	@NonNull
	private final EpisodesAdapter mAdapter = new EpisodesAdapter();
	@Bind(R.id.list)
	RecyclerView mRecyclerView;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	    final Bundle bundle = getArguments();
	    if (bundle != null) {
            final String channel = bundle.getString(ARG_CHANNEL_NAME, "Smodcast");
		    refresh(channel);
            trackEpisodeSelected(channel);
	    }
    }

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_recycler_layout, container, false);
		ButterKnife.bind(this, view);
		final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
		mRecyclerView.setLayoutManager(layoutManager);
		mRecyclerView.setHasFixedSize(true);
		mRecyclerView.setAdapter(mAdapter);
		return view;
	}

	private void refresh(@NonNull final String shortName) {
		SmodcastClient.getClient().getFeed(shortName, new Callback<Rss>() {
			@Override
			public void success(final Rss rss, final Response response) {
				mAdapter.setResults(rss.getChannel().getItems());
            }

			@Override
			public void failure(final RetrofitError error) {
				Timber.e(error, error.getMessage());
            }
        });
    }

	private void trackEpisodeSelected(@NonNull final String episodeTitle) {
	    final Tracker t = ((SmodrApplication) getActivity()
			    .getApplication())
			    .getTracker(SmodrApplication.TrackerName.APP_TRACKER);
	    t.send(new HitBuilders.EventBuilder()
			    .setCategory("EPISODE")
			    .setAction("SELECTED")
			    .setLabel(episodeTitle)
			    .build());
    }
}