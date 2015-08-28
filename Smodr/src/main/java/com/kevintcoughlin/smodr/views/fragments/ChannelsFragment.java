package com.kevintcoughlin.smodr.views.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.kevintcoughlin.smodr.adapters.ChannelsAdapter;
import com.kevintcoughlin.smodr.models.Channel;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * A fragment that displays a collection of {@link Channel}s.
 *
 * @author kevincoughlin
 */
public final class ChannelsFragment extends TrackedRecyclerViewFragment implements ChannelsAdapter.ChannelViewHolder
		.IChannelViewHolderClicks {
	/**
	 * Screen name for this view.
	 */
	@NonNull
	public static final String TAG = ChannelsFragment.class.getSimpleName();
	/**
	 * Callback interface for activity communication.
	 */
	@Nullable
	private Callbacks mCallbacks;
	/**
	 * The number of columns to display in the {@link #mRecyclerView}.
	 */
	private static final int NUM_COLUMNS = 4;
	/**
	 * Constructor.
	 */
	public ChannelsFragment() {
		mAdapter = new ChannelsAdapter();
	}

	@Override
	public void onChannelClick(final int position) {
		if (mCallbacks != null) {
			mCallbacks.onChannelSelected(getAdapter().getItem(position));
		}
	}

	/**
	 * Handler for {@link Channel} selection to the {@link Activity}.
	 */
	public interface Callbacks {
		/**
		 * Called when a {@link Channel} is selected.
		 *
		 * @param channel
		 * 		the {@link Channel} selected.
		 */
		void onChannelSelected(final ParseObject channel);
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
	public void onCreate(@Nullable final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);

		ParseQuery.getQuery("Channel").findInBackground((objects, e) -> {
			if (mAdapter != null) {
				((ChannelsAdapter) mAdapter).setChannels(objects);
			}
		});
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, @Nullable final Bundle
			savedInstanceState) {
		mLayoutManager = new GridLayoutManager(getContext(), NUM_COLUMNS);
		getAdapter().setClickListener(this);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	protected ChannelsAdapter getAdapter() {
		return (ChannelsAdapter) mAdapter;
	}
}
