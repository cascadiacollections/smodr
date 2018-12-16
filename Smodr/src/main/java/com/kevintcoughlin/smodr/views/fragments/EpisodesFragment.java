package com.kevintcoughlin.smodr.views.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.kevintcoughlin.smodr.adapters.BinderRecyclerAdapter;
import com.kevintcoughlin.smodr.adapters.EpisodeView;
import com.kevintcoughlin.smodr.adapters.EpisodeViewHolder;
import com.kevintcoughlin.smodr.models.Feed;
import com.kevintcoughlin.smodr.models.Item;
import com.kevintcoughlin.smodr.services.FeedService;
import com.kevintcoughlin.smodr.services.MediaPlaybackService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public final class EpisodesFragment extends BinderRecyclerFragment<Item, EpisodeViewHolder> implements Callback<Feed> {

    private final class ItemAdapter extends BinderRecyclerAdapter<Item, EpisodeViewHolder> {
        ItemAdapter(final OnClick<Item> onClick) {
            super(new EpisodeView(onClick));
        }
    }

    @NonNull
    public static final String TAG = EpisodesFragment.class.getSimpleName();

    @NonNull
    private final BinderRecyclerAdapter<Item, EpisodeViewHolder> mAdapter = new ItemAdapter(this);

    @NonNull
    private final RecyclerView.LayoutManager mLinearLayoutManager = new LinearLayoutManager(getContext());

    @Override
    protected BinderRecyclerAdapter<Item, EpisodeViewHolder> getAdapter() {
        return mAdapter;
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        return mLinearLayoutManager;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.smodcast.com/")
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();
        final FeedService service = retrofit.create(FeedService.class);

        service.feed("https://feeds.feedburner.com/SModcasts").enqueue(this);
    }

    @Override
    public void onResponse(@NonNull final Call<Feed> call, @NonNull final Response<Feed> response) {
        final Feed feed = response.body();
        if (feed != null) {
            mAdapter.setItems((feed.channel.item));
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onFailure(@NonNull final Call<Feed> call, @NonNull final Throwable t) {

    }

    @Override
    public void onClick(@NonNull final Item item) {
        final Intent intent = MediaPlaybackService.createIntent(getContext(), item);
        getActivity().startService(intent);
    }
}
