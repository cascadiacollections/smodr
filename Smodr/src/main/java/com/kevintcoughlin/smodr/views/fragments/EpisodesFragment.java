package com.kevintcoughlin.smodr.views.fragments;

import android.app.Activity;
import android.content.Context;
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
import com.kevintcoughlin.smodr.adapters.EpisodesAdapter;
import com.kevintcoughlin.smodr.utils.AppUtil;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * A fragment that displays a collection of {@link Item}s.
 *
 * @author kevincoughlin
 */
public final class EpisodesFragment extends TrackedRecyclerViewFragment implements EpisodesAdapter.ItemViewHolder
		.IItemViewHolderClicks, SwipeRefreshLayout.OnRefreshListener {
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
	protected SwipeRefreshLayout mSwipeRefreshLayout;
	/**
	 * The channel name for this {@link EpisodesFragment}.
	 */
	@NonNull
	private String mChannelName = "Smodcast";
	/**
	 * Callback interface for activity communication.
	 */
	@Nullable
	private Callbacks mCallbacks;

	/**
	 * Constructor.
	 */
	public EpisodesFragment() {
		mLayoutResId = R.layout.fragment_episodes_layout;
		mAdapter = new EpisodesAdapter();
	}

	@Override
	public void onCreate(@Nullable final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		final Bundle bundle = getArguments();
	    if (bundle != null) {
		    mChannelName = bundle.getString(ARG_CHANNEL_NAME, "Smodcast");
	    }
    }

	@Override
	public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
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
		getAdapter().setClickListener(this);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onAttach(final Context context) {
		super.onAttach(context);

		if (!(context instanceof Callbacks)) {
			throw new IllegalStateException("Activity must implement fragment's callbacks.");
		}

		mCallbacks = (Callbacks) context;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = null;
	}

	@Override
	public void onItemClick(final int position) {
		if (mCallbacks != null) {
			mCallbacks.onEpisodeSelected(getAdapter().getItem(position));
		}
	}

	@Override
	public void onRefresh() {
		refresh(mChannelName);
	}

	/**
	 * Handler for {@link Item} selection to the {@link Activity}.
	 */
	public interface Callbacks {
		/**
		 * Called when an {link @Item} is selected.
		 *
		 * @param item
		 * 		the {@link Item} selected.
		 */
		void onEpisodeSelected(final ParseObject item);
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
			.findInBackground((episodes, e) -> {
				if (e == null) {
					if (mAdapter != null) {
						((EpisodesAdapter) mAdapter).setResults(episodes);
					}
				} else {
					AppUtil.toast(getContext(), e.getLocalizedMessage());
				}
				if (mSwipeRefreshLayout != null) {
					mSwipeRefreshLayout.setRefreshing(false);
				}
			});
	}

	@Override
	protected EpisodesAdapter getAdapter() {
		return (EpisodesAdapter) mAdapter;
	}
}