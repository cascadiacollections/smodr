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

import com.kevintcoughlin.smodr.R;
import com.kevintcoughlin.smodr.adapters.BinderAdapter;
import com.kevintcoughlin.smodr.models.Feed;
import com.kevintcoughlin.smodr.models.Item;
import com.kevintcoughlin.smodr.services.FeedService;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

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

	@Nullable
	private OnChannelSelected mListener;

	@Bind(R.id.list)
	RecyclerView mRecyclerView;

	@Override
	public void onAttach(final Context context) {
		super.onAttach(context);
		if (context instanceof OnChannelSelected) {
			mListener = ((OnChannelSelected) context);
		}
		mAdapter = new BinderAdapter(context);
//		mAdapter.registerViewType(R.layout.item_grid_channel_layout, new ChannelViewBinder(), Item.class);
		if (mListener != null) {
			mAdapter.setOnItemClickListener(item -> mListener.onChannelSelected((Item) item));
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_recycler_layout, container, false);
		ButterKnife.bind(this, view);
		return view;
	}
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), NUM_COLUMNS));
		mRecyclerView.setAdapter(mAdapter);

		final Retrofit retrofit = new Retrofit.Builder()
				.baseUrl("https://www.smodcast.com/")
				.addConverterFactory(SimpleXmlConverterFactory.create())
				.build();

		final FeedService service = retrofit.create(FeedService.class);
		final Call<Feed> feed = service.feed("http://feeds.feedburner.com/SModcasts");
		feed.enqueue(new Callback<Feed>() {
			@Override
			public void onResponse(Call<Feed> call, Response<Feed> response) {
				Feed feed = response.body();
				System.out.println(feed.channel.title);
			}

			@Override
			public void onFailure(Call<Feed> call, Throwable t) {
				System.out.println(t.getMessage());
			}
		});
	}

	public interface OnChannelSelected {
		void onChannelSelected(@NonNull final Item item);
	}
}
