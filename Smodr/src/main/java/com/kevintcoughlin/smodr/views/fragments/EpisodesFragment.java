package com.kevintcoughlin.smodr.views.fragments;

import android.app.Activity;
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
import com.kevintcoughlin.smodr.adapters.EpisodesAdapter;
import com.kevintcoughlin.smodr.http.SmodcastClient;
import com.kevintcoughlin.smodr.models.Item;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public final class EpisodesFragment extends TrackedFragment implements EpisodesAdapter.ItemViewHolder.IItemViewHolderClicks {
	@NonNull
	public static final String ARG_CHANNEL_NAME = "SHORT_NAME";
	@NonNull
	private final EpisodesAdapter mAdapter = new EpisodesAdapter();
	@Nullable
	private LinearLayoutManager mLayoutManager;
	@Nullable
	private Callbacks mCallbacks;
	@Bind(R.id.list)
	RecyclerView mRecyclerView;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	    final Bundle bundle = getArguments();
	    if (bundle != null) {
            final String channel = bundle.getString(ARG_CHANNEL_NAME, "Smodcast");
		    refresh(channel);
	    }
    }

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_recycler_layout, container, false);
		ButterKnife.bind(this, view);
		mLayoutManager = new LinearLayoutManager(getActivity());
		mRecyclerView.setLayoutManager(mLayoutManager);
		mRecyclerView.setHasFixedSize(true);
		mRecyclerView.setAdapter(mAdapter);
		mAdapter.setClickListener(this);
		return view;
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
	public void onItemClick(final int position) {
		if (mCallbacks != null) {
			mCallbacks.onEpisodeSelected(mAdapter.getItem(position));
		}
	}

	public interface Callbacks {
		void onEpisodeSelected(Item item);
	}

	private void refresh(@NonNull final String name) {
		SmodcastClient
				.getClient()
				.getFeed(name)
				.subscribeOn(Schedulers.newThread())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(rss -> mAdapter.setResults(rss.getChannel().getItems()));
	}
}