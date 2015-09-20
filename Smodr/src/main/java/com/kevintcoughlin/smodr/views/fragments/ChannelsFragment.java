package com.kevintcoughlin.smodr.views.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.kevintcoughlin.smodr.R;
import com.kevintcoughlin.smodr.adapters.BinderAdapter;
import com.kevintcoughlin.smodr.models.Channel;
import com.kevintcoughlin.smodr.utils.AppUtil;
import com.kevintcoughlin.smodr.viewholders.ChannelViewBinder;
import com.kevintcoughlin.smodr.views.activities.MainActivity;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * A fragment that displays a collection of channels.
 *
 * @author kevincoughlin
 */
public final class ChannelsFragment extends TrackedFragment {
	/**
	 * Screen name for this view.
	 */
	@NonNull
	public static final String TAG = ChannelsFragment.class.getSimpleName();
	/**
	 * The number of columns to display in the {@link #mRecyclerView}.
	 */
	private static final int NUM_COLUMNS = 4;

	@Nullable
	private BinderAdapter mAdapter;

	@Bind(R.id.list)
	RecyclerView mRecyclerView;

	@Override
	public void onAttach(final Context context) {
		super.onAttach(context);
		mAdapter = new BinderAdapter(context);
		mAdapter.registerViewType(R.layout.item_grid_channel_layout, new ChannelViewBinder(), Channel.class);
		mAdapter.setOnItemClickListener(item -> ((MainActivity) getActivity()).onChannelSelected((Channel) item));
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_recycler_layout, container, false);
		ButterKnife.bind(this, view);
		return view;
	}
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), NUM_COLUMNS));
		mRecyclerView.setAdapter(mAdapter);

		ParseQuery.getQuery(Channel.class).setLimit(1000).fromLocalDatastore().findInBackground((objects, e) -> {
			if (e == null && mAdapter != null && objects != null && !objects.isEmpty()) {
				mAdapter.setItems(objects);
			} else if (e != null) {
				AppUtil.toast(getContext(), e.getLocalizedMessage());
			}
		});

		ParseQuery.getQuery(Channel.class).setLimit(1000).findInBackground((objects, e) -> {
			if (e == null && mAdapter != null && objects != null && !objects.isEmpty()) {
				ParseObject.pinAllInBackground(objects);
				mAdapter.setItems(objects);
			} else if (e != null) {
				AppUtil.toast(getContext(), e.getLocalizedMessage());
			}
		});
	}

	public interface OnChannelSelected {
		void onChannelSelected(@NonNull final Channel o);
	}
}
