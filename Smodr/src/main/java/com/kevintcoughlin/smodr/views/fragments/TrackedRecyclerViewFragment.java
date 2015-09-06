package com.kevintcoughlin.smodr.views.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.kevintcoughlin.smodr.R;
import com.kevintcoughlin.smodr.adapters.BinderAdapter;

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
	RecyclerView mRecyclerView;
	/**
	 * Linear layout manager for the {@link #mRecyclerView}.
	 */
	@Nullable
	LinearLayoutManager mLayoutManager;
	/**
	 * Array adapter backing {@link #mRecyclerView}.
	 */
	@Nullable
	BinderAdapter mAdapter;
	/**
	 * The resource id of the layout to inflate.
	 */
	int mLayoutResId = R.layout.fragment_recycler_layout;
	/**
	 * {@link Bundle} key for {@link List < Parcelable >}.
	 */
	protected static final String STATE_RECYCLER_ITEMS = "STATE_RECYCLER_ITEMS";
	/**
	 * {@link Bundle} key for {@link android.support.v7.widget.LinearLayoutManager.SavedState}.
	 */
	private static final String STATE_LAYOUT_MANAGER = "STATE_LAYOUT_MANAGER_STATE";

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

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
}
