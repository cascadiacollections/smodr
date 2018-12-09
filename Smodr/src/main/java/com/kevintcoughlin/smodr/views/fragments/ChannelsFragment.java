package com.kevintcoughlin.smodr.views.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kevintcoughlin.smodr.R;
import com.kevintcoughlin.smodr.adapters.ItemsAdapter;
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
    @NonNull
    public static final String TAG = ChannelsFragment.class.getSimpleName();

    @Nullable
    private ItemsAdapter mAdapter = new ItemsAdapter();

    @Bind(R.id.list)
    public RecyclerView mRecyclerView;

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

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.smodcast.com/")
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();
        final FeedService service = retrofit.create(FeedService.class);
        final Call<Feed> feed = service.feed("https://feeds.feedburner.com/SModcasts");

        feed.enqueue(new Callback<Feed>() {
            @Override
            public void onResponse(@NonNull Call<Feed> call, @NonNull Response<Feed> response) {
                final Feed feed = response.body();
                if (feed != null) {
                    System.out.println(feed.channel.title);
                    if (mAdapter != null) {
                        mAdapter.setItems((feed.channel.item));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Feed> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
    }
}
