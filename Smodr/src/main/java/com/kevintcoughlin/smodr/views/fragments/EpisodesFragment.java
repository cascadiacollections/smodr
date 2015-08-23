package com.kevintcoughlin.smodr.views.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.kevintcoughlin.smodr.adapters.EpisodesAdapter;
import com.kevintcoughlin.smodr.http.SmodcastClient;
import com.kevintcoughlin.smodr.models.Item;
import com.kevintcoughlin.smodr.utils.AppUtil;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * A fragment that displays a collection of {@link Item}s.
 *
 * @author kevincoughlin
 */
public final class EpisodesFragment extends TrackedRecyclerViewFragment implements EpisodesAdapter.ItemViewHolder
		.IItemViewHolderClicks {
	/**
	 * Param for sending a channel's name.
	 */
	@NonNull
	public static final String ARG_CHANNEL_NAME = "SHORT_NAME";
	/**
	 * Callback interface for activity communication.
	 */
	@Nullable
	private Callbacks mCallbacks;

	/**
	 * Constructor.
	 */
	public EpisodesFragment() {
		mAdapter = new EpisodesAdapter();
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	    final Bundle bundle = getArguments();
	    if (bundle != null) {
		    refresh(bundle.getString(ARG_CHANNEL_NAME, "Smodcast"));
	    }
    }

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		mLayoutManager = new LinearLayoutManager(getContext());
		getAdapter().setClickListener(this);
		return super.onCreateView(inflater, container, savedInstanceState);
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
	public void onItemClick(final int position) {
		if (mCallbacks != null) {
			mCallbacks.onEpisodeSelected(getAdapter().getItem(position));
		}
	}

	/**
	 * Handler for {@link Item} selection to the {@link Activity}.
	 */
	public interface Callbacks {
		/**
		 * Called when an {link @Item} is selected.
		 *
		 * @param item
		 * 		the {@link Item} selected.
		 */
		void onEpisodeSelected(final Item item);
	}

	/**
	 * Refreshes the channel's feed of episodes.
	 *
	 * @param name
	 *      the name of the channel to refresh.
	 */
	private void refresh(@NonNull final String name) {
		SmodcastClient
				.getClient()
				.getFeed(name)
				.subscribeOn(Schedulers.newThread())
				.observeOn(AndroidSchedulers.mainThread())
				.doOnError(error -> AppUtil.toast(getContext(), error.getLocalizedMessage()))
				.subscribe(rss -> getAdapter().setResults(rss.getChannel().getItems()));
	}

	@Override
	protected EpisodesAdapter getAdapter() {
		return (EpisodesAdapter) mAdapter;
	}
}