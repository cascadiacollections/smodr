package com.kevintcoughlin.smodr.views.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.kevintcoughlin.smodr.R;
import com.kevintcoughlin.smodr.adapters.BinderAdapter;
import com.kevintcoughlin.smodr.utils.AppUtil;
import com.kevintcoughlin.smodr.viewholders.ChannelViewBinder;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * A fragment that displays a collection of channels.
 *
 * @author kevincoughlin
 */
public final class ChannelsFragment extends TrackedRecyclerViewFragment {
	/**
	 * Screen name for this view.
	 */
	@NonNull
	public static final String TAG = ChannelsFragment.class.getSimpleName();
	/**
	 * The number of columns to display in the {@link #mRecyclerView}.
	 */
	private static final int NUM_COLUMNS = 4;

	@Override
	public void onAttach(final Context context) {
		super.onAttach(context);
		mAdapter = new BinderAdapter(context);
		mAdapter.registerViewType(R.layout.item_grid_channel_layout, new ChannelViewBinder(), ParseObject.class);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, @Nullable final Bundle
			savedInstanceState) {
		mLayoutManager = new GridLayoutManager(getContext(), NUM_COLUMNS);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		ParseQuery.getQuery("Channel").setLimit(1000).fromLocalDatastore().findInBackground((objects, e) -> {
			if (e == null && mAdapter != null && objects != null) {
				mAdapter.setItems(objects);
			} else if (e != null) {
				AppUtil.toast(getContext(), e.getLocalizedMessage());
			}
		});

		ParseQuery.getQuery("Channel").setLimit(1000).findInBackground((objects, e) -> {
			if (e == null && mAdapter != null && objects != null) {
				ParseObject.pinAllInBackground(objects);
				mAdapter.setItems(objects);
			} else if (e != null) {
				AppUtil.toast(getContext(), e.getLocalizedMessage());
			}
		});
	}
}
