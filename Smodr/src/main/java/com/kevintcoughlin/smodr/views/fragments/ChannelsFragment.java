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
import com.kevintcoughlin.smodr.R;
import com.kevintcoughlin.smodr.adapters.ChannelsAdapter;
import com.kevintcoughlin.smodr.http.SmodcastClient;
import com.kevintcoughlin.smodr.models.Channel;
import com.kevintcoughlin.smodr.utils.AppUtil;
import org.parceler.Parcels;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
		void onChannelSelected(final Channel channel);
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
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null && savedInstanceState.containsKey(STATE_RECYCLER_ITEMS)) {
			getAdapter().setChannels(Parcels.unwrap(savedInstanceState.getParcelable(STATE_RECYCLER_ITEMS)));
		} else {
			for (final String name : AppUtil.getStrings(getContext(), R.array.podcasts)) {
				SmodcastClient
						.getClient()
						.getFeed(name)
						.subscribeOn(Schedulers.newThread())
						.observeOn(AndroidSchedulers.mainThread())
						.doOnError(error -> AppUtil.toast(getContext(), error.getLocalizedMessage()))
						.subscribe(rss -> {
							final Channel channel = rss.getChannel();
							channel.setShortName(name);
							getAdapter().addChannel(channel);
						});
			}
		}
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		mLayoutManager = new GridLayoutManager(getContext(), NUM_COLUMNS);
		getAdapter().setClickListener(this);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable(STATE_RECYCLER_ITEMS, Parcels.wrap(getAdapter().getChannels()));
	}

	@Override
	protected ChannelsAdapter getAdapter() {
		return (ChannelsAdapter) mAdapter;
	}
}
