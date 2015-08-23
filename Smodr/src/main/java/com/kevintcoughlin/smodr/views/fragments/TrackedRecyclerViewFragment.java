package com.kevintcoughlin.smodr.views.fragments;

import android.os.Bundle;
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

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		final View view = inflater.inflate(mLayoutResId, container, false);
		ButterKnife.bind(this, view);
		return view;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if (mRecyclerView != null) {
			mRecyclerView.setLayoutManager(mLayoutManager);
			mRecyclerView.setHasFixedSize(true);
			mRecyclerView.setAdapter(mAdapter);
		}
	}

	protected abstract <T extends Adapter> T getAdapter();
}
