package com.kevintcoughlin.smodr.views.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.kevintcoughlin.smodr.R;
import com.kevintcoughlin.smodr.adapters.BinderAdapter;
import com.kevintcoughlin.smodr.models.Episode;
import com.kevintcoughlin.smodr.utils.AppUtil;
import com.kevintcoughlin.smodr.viewholders.EpisodeViewBinder;
import com.kevintcoughlin.smodr.views.activities.MainActivity;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * A fragment that displays a collection of items.
 *
 * @author kevincoughlin
 */
public final class EpisodesFragment extends TrackedFragment implements SwipeRefreshLayout.OnRefreshListener {
	/**
	 * Param for sending a channel's name.
	 */
	@NonNull
	public static final String ARG_CHANNEL_NAME = "SHORT_NAME";

	@Nullable
	private BinderAdapter mAdapter;

	@Bind(R.id.list)
	RecyclerView mRecyclerView;
	/**
	 * {@link SwipeRefreshLayout} for this fragment.
	 */
	@Bind(R.id.refresh)
	SwipeRefreshLayout mSwipeRefreshLayout;
	/**
	 * The channel name for this {@link EpisodesFragment}.
	 */
	@NonNull
	private String mChannelName = "Smodcast";

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		mAdapter = new BinderAdapter(context);
		mAdapter.registerViewType(R.layout.item_list_episode_layout, new EpisodeViewBinder(), Episode.class);
		mAdapter.setOnItemClickListener(item -> ((MainActivity) getActivity()).onEpisodeSelected((ParseObject) item));
	}

	@Override
	public void onCreate(@Nullable final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Bundle bundle = getArguments();
	    if (bundle != null) {
		    mChannelName = bundle.getString(ARG_CHANNEL_NAME, "Smodcast");
	    }
    }

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_episodes_layout, container, false);
		ButterKnife.bind(this, view);
		return view;
	}

	@Override
	public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		mRecyclerView.setAdapter(mAdapter);
		if (mSwipeRefreshLayout != null) {
			mSwipeRefreshLayout.setColorSchemeResources(
					R.color.colorAccent,
					R.color.colorPrimary,
					R.color.colorPrimaryDark
			);
			mSwipeRefreshLayout.setOnRefreshListener(this);
			mSwipeRefreshLayout.setRefreshing(true);
		}
		onRefresh();
	}

	@Override
	public void onRefresh() {
		refresh(mChannelName);
	}

	/**
	 * Refreshes the channel's feed of episodes.
	 *
	 * @param name
	 *      the name of the channel to refresh.
	 */
	private void refresh(@NonNull final String name) {
		if (mSwipeRefreshLayout != null) {
			mSwipeRefreshLayout.setRefreshing(true);
		}

		ParseQuery.getQuery(Episode.class)
				.whereEqualTo("feed_title", name)
				.orderByDescending("pubDate")
				.fromLocalDatastore()
				.setLimit(1000)
				.findInBackground((episodes, e) -> {
					if (e == null && mAdapter != null && episodes != null && !episodes.isEmpty()) {
						mAdapter.setItems(episodes);
					}
					if (mSwipeRefreshLayout != null) {
						mSwipeRefreshLayout.setRefreshing(false);
					}
				});

		ParseQuery.getQuery(Episode.class)
			.whereEqualTo("feed_title", name)
				.orderByDescending("pubDate")
				.setLimit(1000)
				.findInBackground((episodes, e) -> {
					if (e == null && mAdapter != null && episodes != null && !episodes.isEmpty()) {
						ParseObject.pinAllInBackground(episodes);
						mAdapter.setItems(episodes);
					} else if (e != null) {
						AppUtil.toast(getContext(), e.getLocalizedMessage());
					}
					if (mSwipeRefreshLayout != null) {
						mSwipeRefreshLayout.setRefreshing(false);
					}
				});
	}

	public interface OnEpisodeSelected {
		void onEpisodeSelected(@NonNull final ParseObject o);
	}
}