package com.kevintcoughlin.smodr.views.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.Bind;
import com.kevintcoughlin.smodr.R;
import com.kevintcoughlin.smodr.adapters.BinderAdapter;
import com.kevintcoughlin.smodr.utils.AppUtil;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * A fragment that displays a collection of items.
 *
 * @author kevincoughlin
 */
public final class EpisodesFragment extends TrackedRecyclerViewFragment implements SwipeRefreshLayout.OnRefreshListener {
	/**
	 * Param for sending a channel's name.
	 */
	@NonNull
	public static final String ARG_CHANNEL_NAME = "SHORT_NAME";
	/**
	 * {@link SwipeRefreshLayout} for this fragment.
	 */
	@Nullable
	@Bind(R.id.refresh)
	SwipeRefreshLayout mSwipeRefreshLayout;
	/**
	 * The channel name for this {@link EpisodesFragment}.
	 */
	@NonNull
	private String mChannelName = "Smodcast";

	/**
	 * Constructor.
	 */
	public EpisodesFragment() {
		mLayoutResId = R.layout.fragment_episodes_layout;
	}

	@Override
	public void onCreate(@Nullable final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Bundle bundle = getArguments();
	    if (bundle != null) {
		    mChannelName = bundle.getString(ARG_CHANNEL_NAME, "Smodcast");
	    }
    }

	@Override
	public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mAdapter = new BinderAdapter(getContext());
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
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, @Nullable final Bundle
			savedInstanceState) {
		mLayoutManager = new LinearLayoutManager(getContext());
		return super.onCreateView(inflater, container, savedInstanceState);
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

		ParseQuery.getQuery("Item")
				.whereEqualTo("feed_title", name)
				.orderByDescending("pubDate")
				.fromLocalDatastore()
				.setLimit(1000)
				.findInBackground((episodes, e) -> {
					if (e == null && mAdapter != null && episodes != null) {
						mAdapter.setItems(episodes);
					}
					if (mSwipeRefreshLayout != null) {
						mSwipeRefreshLayout.setRefreshing(false);
					}
				});

		ParseQuery.getQuery("Item")
			.whereEqualTo("feed_title", name)
				.orderByDescending("pubDate")
				.setLimit(1000)
			.findInBackground((episodes, e) -> {
				if (e == null && mAdapter != null && episodes != null) {
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
}