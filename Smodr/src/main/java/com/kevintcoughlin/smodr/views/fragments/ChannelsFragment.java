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

public final class ChannelsFragment extends TrackedFragment implements ChannelsAdapter.ChannelViewHolder.IChannelViewHolderClicks {
	@NonNull
	public static final String TAG = ChannelsFragment.class.getSimpleName();
	@Nullable
	private Callbacks mCallbacks;
	@NonNull
	private final ChannelsAdapter mAdapter = new ChannelsAdapter();
	@Nullable
	private LinearLayoutManager mLayoutManager;
	@Bind(R.id.list)
	RecyclerView mRecyclerView;
	private static final int NUM_COLUMNS = 4;

	@Override
	public void onChannelClick(final int position) {
		if (mCallbacks != null) {
			mCallbacks.onChannelSelected(mAdapter.getItem(position));
		}
	}

	public interface Callbacks {
        void onChannelSelected(Channel channel);
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
		mRecyclerView.setLayoutManager(mLayoutManager);
		mRecyclerView.setHasFixedSize(true);
		mRecyclerView.setAdapter(mAdapter);
		mAdapter.setClickListener(this);
		return view;
    }
}
