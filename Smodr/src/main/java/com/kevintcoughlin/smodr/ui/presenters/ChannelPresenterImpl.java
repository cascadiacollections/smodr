package com.kevintcoughlin.smodr.ui.presenters;

import android.content.Intent;

import com.kevintcoughlin.smodr.http.SmodcastClient;
import com.kevintcoughlin.smodr.models.Channel;
import com.kevintcoughlin.smodr.models.Rss;
import com.kevintcoughlin.smodr.ui.ChannelView;
import com.kevintcoughlin.smodr.ui.adapters.EpisodesListAdapter;
import com.kevintcoughlin.smodr.ui.presenters.mapper.IndividualChannelMapper;

import org.parceler.Parcels;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ChannelPresenterImpl implements ChannelPresenter {
    public static final String TAG = ChannelPresenterImpl.class.getSimpleName();
    private ChannelView mChannelView;
    private IndividualChannelMapper mChannelMapper;
    private Channel mChannel;

    public ChannelPresenterImpl(ChannelView channelView, IndividualChannelMapper channelMapper) {
        mChannelView = channelView;
        mChannelMapper = channelMapper;
    }

    @Override
    public void initializeViews() {
        mChannelView.initializeToolbar();
        mChannelView.initializeSwipeRefreshLayout();
        mChannelView.initializeRecyclerView();
        mChannelView.setFABPlaybackIcon(false); // @TODO: call service
        mChannelView.setTitle(mChannel.getTitle());
        mChannelView.setName(mChannel.getTitle());
        mChannelView.setDescription(mChannel.getDescription());
        mChannelView.setThumbnail(mChannel.getShortName());
    }

    @Override
    public void onApplyColorChange(int color) {

    }

    @Override
    public void onSwipeRefresh() {
        initializeData();
    }

    @Override
    public void onFavourite() {
        System.out.println("Favorited");
    }

    @Override
    public void initializeData() {
        SmodcastClient.getClient().getFeed(mChannel.getShortName(), new Callback<Rss>() {
            @Override
            public void success(Rss rss, Response response) {
                EpisodesListAdapter adapter = new EpisodesListAdapter(mChannelView.getContext(), rss.getChannel().getItems());
                mChannelMapper.registerAdapter(adapter);
                mChannelView.hideRefreshing();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                mChannelView.toastMangaError();
                mChannelView.hideRefreshing();
            }
        });
    }

    @Override
    public void handleInitialData(Intent intent) {
        if (intent != null) {
            if (intent.hasExtra("channel")) {
                mChannel = Parcels.unwrap(intent.getExtras().getParcelable("channel"));
            }
        }
    }
}
