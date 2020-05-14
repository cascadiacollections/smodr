package com.kevintcoughlin.smodr.views.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.kevintcoughlin.common.adapter.BinderRecyclerAdapter;
import com.kevintcoughlin.common.fragment.BinderRecyclerFragment;
import com.kevintcoughlin.smodr.models.Channel;
import com.kevintcoughlin.smodr.models.Feed;
import com.kevintcoughlin.smodr.models.Item;
import com.kevintcoughlin.smodr.services.FeedService;
import com.kevintcoughlin.smodr.viewholders.EpisodeView;
import com.kevintcoughlin.smodr.viewholders.EpisodeViewHolder;

import org.jetbrains.annotations.Contract;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public final class EpisodesFragment extends BinderRecyclerFragment<Item, EpisodeViewHolder> implements Callback<Feed> {
    private static final String EPISODE_FEED_URL = "com.kevintcoughlin.smodr.views.fragments.EpisodesFragment.feedUrl";
    private static final String BASE_URL = "https://www.smodcast.com/";
    private OnItemSelected<Item> mEpisodeClickListener;

    public static Fragment create(@NonNull Channel channel) {
        final Fragment fragment = new EpisodesFragment();
        final Bundle bundle = new Bundle();
        bundle.putString(EPISODE_FEED_URL, channel.getLink());
        fragment.setArguments(bundle);
        return fragment;
    }

    private static final class ItemAdapter extends BinderRecyclerAdapter<Item, EpisodeViewHolder> {
        ItemAdapter(final BinderRecyclerAdapter.OnClick<Item> onClick) {
            super(new EpisodeView(onClick));
        }
    }

    @NonNull
    public static final String TAG = EpisodesFragment.class.getSimpleName();

    @NonNull
    private final BinderRecyclerAdapter<Item, EpisodeViewHolder> mAdapter = new ItemAdapter(this);

    @NonNull
    private final LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getContext());

    @Contract(pure = true)
    @Override
    protected BinderRecyclerAdapter<Item, EpisodeViewHolder> getAdapter() {
        return mAdapter;
    }

    @Contract(pure = true)
    @Override
    protected LinearLayoutManager getLayoutManager() {
        return mLinearLayoutManager;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        mEpisodeClickListener = (OnItemSelected<Item>) getActivity();
    }

    @Override
    public void onRefresh() {
        super.onRefresh();

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();

        final FeedService service = retrofit.create(FeedService.class);
        final Bundle arguments = getArguments();

        if (arguments != null) {
            final String feedUrl = arguments.getString(EPISODE_FEED_URL);
            service.feed(feedUrl).enqueue(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mEpisodeClickListener = null;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(
                getRecyclerView().getContext(),
                getLayoutManager().getOrientation()
        );
        getRecyclerView().addItemDecoration(mDividerItemDecoration);

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();

        final FeedService service = retrofit.create(FeedService.class);
        final Bundle arguments = getArguments();

        if (arguments != null) {
            final String feedUrl = arguments.getString(EPISODE_FEED_URL);
            service.feed(feedUrl).enqueue(this);
        }
    }

    @Override
    public void onResponse(@NonNull final Call<Feed> call, @NonNull final Response<Feed> response) {
        final Feed feed = response.body();
        if (feed != null) {
            mAdapter.setItems(feed.getChannel().getItem());
            mAdapter.notifyDataSetChanged();
            setRefreshing(false);
        }
    }

    @Override
    public void onFailure(@NonNull final Call<Feed> call, @NonNull final Throwable t) {
        Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(@NonNull final Item item) {
        if (mEpisodeClickListener != null) {
            mEpisodeClickListener.onItemSelected(item);
        }
    }
}
