package com.kevintcoughlin.smodr.views.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kevintcoughlin.smodr.R;
import com.kevintcoughlin.smodr.SmodrApplication;
import com.kevintcoughlin.smodr.adapters.EpisodesAdapter;
import com.kevintcoughlin.smodr.http.SmodcastClient;
import com.kevintcoughlin.smodr.models.Item;
import com.kevintcoughlin.smodr.models.Rss;
import com.kevintcoughlin.smodr.services.MediaPlaybackService;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Fragment that displays SModcast Channel's episodes in a ListView
 */
public class EpisodesFragment extends ListFragment {
    public static final String INTENT_EPISODE_URL = "intent_episode_url";
    public static final String INTENT_EPISODE_TITLE = "intent_episode_title";
    public static final String INTENT_EPISODE_DESCRIPTION = "intent_episode_description";
    public static final String ARG_CHANNEL_NAME = "SHORT_NAME";
    public static final String ARG_CHANNEL_PHOTO_URL = "COVER_PHOTO_URL";

    private static final String TAG = "EpisodesFragment";
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ArrayList<Item> mItems;
    private ArrayAdapter<Item> mItemsAdapter;
    private String mChannelShortName;
    private String mCoverPhotoUrl;

    public EpisodesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        mChannelShortName = bundle.getString(ARG_CHANNEL_NAME, "smodcast");
        mCoverPhotoUrl = bundle.getString(ARG_CHANNEL_PHOTO_URL, "http://smodcast.com/wp-content/blogs.dir/1/files_mf/smodcast1400.jpg");

        mItems = new ArrayList<>();
        mItemsAdapter = new EpisodesAdapter(getActivity(), mItems);

        getEpisodes(mChannelShortName);
        setListAdapter(mItemsAdapter);

        track();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.episodes_list_layout, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setColorScheme(
            android.R.color.holo_orange_dark,
            android.R.color.transparent,
            android.R.color.holo_orange_dark,
            android.R.color.transparent
        );

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getEpisodes(mChannelShortName);
            }
        });

        return rootView;
    }

    // @TODO: Convert to ButterKnife view injection
    // @TODO: Temporary music player
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Item i = (Item) l.getItemAtPosition(position);

        trackEpisodeSelected(i.getTitle());

        Intent intent = new Intent(getActivity(), MediaPlaybackService.class);
        intent.setAction(MediaPlaybackService.ACTION_PLAY);
        intent.putExtra(INTENT_EPISODE_URL, i.getEnclosure().getUrl());
        intent.putExtra(INTENT_EPISODE_TITLE, i.getTitle());
        intent.putExtra(INTENT_EPISODE_DESCRIPTION, i.getDescription());

        getActivity().startService(intent);
    }

    /**
     * Fetch RSS feed and consume for episodes.
     * @param shortName
     */
    private void getEpisodes(String shortName) {
        SmodcastClient.getClient().getFeed(shortName, new Callback<Rss>() {
            @Override
            public void success(Rss rss, Response response) {
                try {
                    consumeFeed(rss);
                } catch (Exception e) {
                    trackException("Consume Feed");
                }
                if (mSwipeRefreshLayout.isRefreshing()) mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                try {
                    consumeFeed(null);
                } catch (Exception e) {
                    trackException("RetrofitError Get Feed");
                }
                if (mSwipeRefreshLayout.isRefreshing()) mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void consumeFeed(Rss rss) throws Exception {
        if (!mItems.isEmpty()) mItems.clear(); // @TODO: Hack

        mItems.addAll(rss.getChannel().getItems());
        mItemsAdapter.notifyDataSetChanged();
    }

    private void track() {
        Tracker t = ((SmodrApplication) getActivity().getApplication()).getTracker(
                SmodrApplication.TrackerName.APP_TRACKER);

        t.setScreenName(TAG);
        t.send(new HitBuilders.AppViewBuilder().build());
    }

    private void trackException(String description) {
        Tracker t = ((SmodrApplication) getActivity()
                .getApplication())
                .getTracker(SmodrApplication.TrackerName.APP_TRACKER);

        t.send(new HitBuilders.ExceptionBuilder()
                .setDescription(TAG + ":" + description)
                .setFatal(true)
                .build());
    }

    private void trackEpisodeSelected(String episodeTitle) {
        Tracker t = ((SmodrApplication) getActivity()
                .getApplication())
                .getTracker(SmodrApplication.TrackerName.APP_TRACKER);

        t.send(new HitBuilders.EventBuilder()
                .setCategory("EPISODE")
                .setAction("SELECTED")
                .setLabel(episodeTitle)
                .build());
    }
}