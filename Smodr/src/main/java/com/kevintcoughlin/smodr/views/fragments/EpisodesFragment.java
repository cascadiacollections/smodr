package com.kevintcoughlin.smodr.views.fragments;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kevintcoughlin.smodr.R;
import com.kevintcoughlin.smodr.SmodrApplication;
import com.kevintcoughlin.smodr.adapters.EpisodesAdapter;
import com.kevintcoughlin.smodr.data.database.table.EpisodesTable;
import com.kevintcoughlin.smodr.data.model.Episodes;
import com.kevintcoughlin.smodr.data.provider.SmodrProvider;
import com.kevintcoughlin.smodr.http.SmodcastClient;
import com.kevintcoughlin.smodr.models.Item;
import com.kevintcoughlin.smodr.models.Rss;
import com.kevintcoughlin.smodr.services.MediaPlaybackService;
import com.manuelpeinado.fadingactionbar.FadingActionBarHelper;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Fragment that displays SModcast Channel's episodes in a ListView
 */
public class EpisodesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener {

    public static final String INTENT_EPISODE_URL = "intent_episode_url";
    public static final String INTENT_EPISODE_TITLE = "intent_episode_title";
    public static final String INTENT_EPISODE_DESCRIPTION = "intent_episode_description";

    public static final String ARG_CHANNEL_NAME = "SHORT_NAME";
    public static final String ARG_CHANNEL_PHOTO_URL = "COVER_PHOTO_URL";

    private static final String TAG = "EpisodesFragment";
    private ListView mListView;
    private FadingActionBarHelper mFadingHelper;
    private EpisodesAdapter mAdapter;
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

        getEpisodes(mChannelShortName);

        track();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = mFadingHelper.createView(inflater);

        mListView = (ListView) view.findViewById(R.id.episodes_list);
        mAdapter = new EpisodesAdapter(getActivity(), null, false);
        mListView.setAdapter(mAdapter);
        getActivity().getSupportLoaderManager().initLoader(0, null, this);
        mListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mFadingHelper = new FadingActionBarHelper()
                .actionBarBackground(R.color.ab_background)
                .headerLayout(R.layout.header)
                .contentLayout(R.layout.episode_listview)
                .lightActionBar(true);

        mFadingHelper.initActionBar(activity);
    }

    /**
     * Fetch RSS feed and consume for episodes.
     * @param shortName
     */
    private void getEpisodes(String shortName) {
        SmodcastClient.getClient().getFeed(shortName, new Callback<Rss>() {
            @Override
            public void success(Rss rss, Response response) {
                 consumeFeed(rss);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                try {
                    consumeFeed(null);
                } catch (Exception e) {
                    trackException("RetrofitError Get Feed");
                }
            }
        });
    }

    private void consumeFeed(Rss rss) {
        boolean needsToUpdate = true;
        Cursor c = mAdapter.getCursor();
        String mDate = null;

        // @TODO: only if right channel
        if (c.getCount() > 0) {
            c.moveToFirst();
            mDate = c.getString(c.getColumnIndex(EpisodesTable.PUB_DATE));
        }

        DateTimeFormatter formatter = DateTimeFormat.forPattern("E, dd MMM yyyy HH:mm:ss Z");
        DateTime topDate = formatter.parseDateTime(mDate);

        List<Item> items = rss.getChannel().getItems();

        for (int i = 0; i < items.size(); i++) {
            if (!needsToUpdate) return;

            Item item = items.get(i);
            Episodes episode = new Episodes();

            DateTime newDate = formatter.parseDateTime(item.getPubDate());

            if (newDate.isAfter(topDate.toInstant())) {
                episode.setTitle(item.getTitle());
                episode.setDescription(item.getDescription());
                episode.setGuid(item.getGuid());
                episode.setPubdate(item.getPubDate());
                episode.setEnclosureLink(item.getEnclosure().getUrl());
                episode.setLink(item.getLink());
                episode.setUrl(item.getUrl());

                // @TODO: Batch
                getActivity()
                        .getContentResolver()
                        .insert(SmodrProvider.EPISODES_CONTENT_URI, episode.getContentValues());
            } else {
                needsToUpdate = false;
            }
        }
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                SmodrProvider.EPISODES_CONTENT_URI,
                null,
                null,
                null,
                EpisodesTable._ID + " ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Item i = (Item) mListView.getItemAtPosition(position);

        trackEpisodeSelected(i.getTitle());

        Intent intent = new Intent(getActivity(), MediaPlaybackService.class);
        intent.setAction(MediaPlaybackService.ACTION_PLAY);
        intent.putExtra(INTENT_EPISODE_URL, i.getEnclosure().getUrl());
        intent.putExtra(INTENT_EPISODE_TITLE, i.getTitle());
        intent.putExtra(INTENT_EPISODE_DESCRIPTION, i.getDescription());

        getActivity().startService(intent);
    }

}