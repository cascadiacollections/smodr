package com.kevintcoughlin.smodr.views.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.kevintcoughlin.smodr.R;

import java.util.List;

/**
 * A {@link TrackedFragment} that contains a {@link android.support.v7.widget.RecyclerView}.
 *
 * @author kevincoughlin
 */
public abstract class TrackedRecyclerViewFragment extends TrackedFragment {
	/**
	 * The {@link TrackedRecyclerViewFragment}'s {@link RecyclerView}.
	 */
	@Nullable
	@Bind(R.id.list)
	protected RecyclerView mRecyclerView;
	/**
	 * Linear layout manager for the {@link #mRecyclerView}.
	 */
	@Nullable
	protected LinearLayoutManager mLayoutManager;
	/**
	 * Array adapter backing {@link #mRecyclerView}.
	 */
	@Nullable
	protected Adapter mAdapter;
	/**
	 * The resource id of the layout to inflate.
	 */
	protected int mLayoutResId = R.layout.fragment_recycler_layout;
	/**
	 * {@link Bundle} key for {@link List < Parcelable >}.
	 */
	protected static final String STATE_RECYCLER_ITEMS = "STATE_RECYCLER_ITEMS";
	/**
	 * {@link Bundle} key for {@link android.support.v7.widget.LinearLayoutManager.SavedState}.
	 */
	protected static final String STATE_LAYOUT_MANAGER = "STATE_LAYOUT_MANAGER_STATE";

	@Nullable
	@Override
	public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle
			savedInstanceState) {
		final View view = inflater.inflate(mLayoutResId, container, false);
		ButterKnife.bind(this, view);
		return view;
	}

	@Override
	public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if (mRecyclerView != null) {
			mRecyclerView.setLayoutManager(mLayoutManager);
			mRecyclerView.setHasFixedSize(true);
			mRecyclerView.setAdapter(mAdapter);
		}
		if (mLayoutManager != null && savedInstanceState != null
				&& savedInstanceState.containsKey(STATE_LAYOUT_MANAGER)) {
			mLayoutManager.onRestoreInstanceState(savedInstanceState.getParcelable(STATE_LAYOUT_MANAGER));
		}
	}

	@Override
	public void onSaveInstanceState(@NonNull final Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mLayoutManager != null) {
			outState.putParcelable(STATE_LAYOUT_MANAGER, mLayoutManager.onSaveInstanceState());
		}
	}

	protected abstract <T extends Adapter> T getAdapter();
}
