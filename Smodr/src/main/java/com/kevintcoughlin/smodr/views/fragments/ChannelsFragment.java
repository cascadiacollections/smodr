package com.kevintcoughlin.smodr.views.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.kevintcoughlin.smodr.R;
import com.kevintcoughlin.smodr.adapters.ChannelsAdapter;
import com.kevintcoughlin.smodr.http.SmodcastClient;
import com.kevintcoughlin.smodr.models.Channel;
import com.kevintcoughlin.smodr.utils.AppUtil;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * A fragment that displays a collection of {@link Channel}s.
 *
 * @author kevincoughlin
 */
public final class ChannelsFragment extends TrackedFragment implements ChannelsAdapter.ChannelViewHolder.IChannelViewHolderClicks {
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
	 * Adapter containing a collection of {@link Channel}s.
	 */
	@NonNull
	private final ChannelsAdapter mAdapter = new ChannelsAdapter();
	/**
	 * Linear layout manager for the {@link #mRecyclerView}.
	 */
	@Nullable
	private LinearLayoutManager mLayoutManager;
	/**
	 * The recycler view containing episodes.
	 */
	@Nullable
	@Bind(R.id.list)
	RecyclerView mRecyclerView;
	/**
	 * The number of columns to display in the {@link #mRecyclerView}.
	 */
	private static final int NUM_COLUMNS = 4;

	@Override
	public void onChannelClick(final int position) {
		if (mCallbacks != null) {
			mCallbacks.onChannelSelected(mAdapter.getItem(position));
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
	public void onAttach(final Activity activity) {
		super.onAttach(activity);

        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = null;
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		for (final String name : AppUtil.getStrings(getActivity(), R.array.podcasts)) {
			SmodcastClient
					.getClient()
					.getFeed(name)
					.subscribeOn(Schedulers.newThread())
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(rss -> {
						final Channel channel = rss.getChannel();
						channel.setShortName(name);
						mAdapter.addChannel(channel);
					});
		}
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_recycler_layout, container, false);
		ButterKnife.bind(this, view);
		mLayoutManager = new GridLayoutManager(getActivity(), NUM_COLUMNS);
		if (mRecyclerView != null) {
			mRecyclerView.setLayoutManager(mLayoutManager);
			mRecyclerView.setHasFixedSize(true);
			mRecyclerView.setAdapter(mAdapter);
		}
		mAdapter.setClickListener(this);
		return view;
    }
}
