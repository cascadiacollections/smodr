package com.kevintcoughlin.smodr.views.fragments;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kevintcoughlin.smodr.R;
import com.kevintcoughlin.smodr.SmodrApplication;
import com.kevintcoughlin.smodr.adapters.ChannelsAdapter;
import com.kevintcoughlin.smodr.data.database.table.ChannelTable;

import com.kevintcoughlin.smodr.data.provider.SmodrProvider;
import com.kevintcoughlin.smodr.models.Channel;

import java.util.ArrayList;

/**
 * Fragment that displays SModcast Channels in a GridView
 */
public class ChannelsFragment extends Fragment implements GridView.OnItemClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = "ChannelsGridViewFragment";
    private static final int LOADER_ID = 0;
    private Callbacks mCallbacks = sChannelCallbacks;
    private GridView mGridView;
    private ChannelsAdapter mAdapter;

    public interface Callbacks {
        public void onChannelSelected(String shortName, String photoUrl, long channelId, String title);
    }

    private static Callbacks sChannelCallbacks = new Callbacks() {
        @Override
        public void onChannelSelected(String id, String photoUrl, long channelId, String title) {
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = sChannelCallbacks;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.channels_grid_layout, container, false);

        mGridView = (GridView) view.findViewById(R.id.grid_view);
        mAdapter = new ChannelsAdapter(getActivity(), null, false);
        mGridView.setAdapter(mAdapter);
        getActivity().getSupportLoaderManager().initLoader(LOADER_ID, null, this);
        mGridView.setOnItemClickListener(this);

        track();

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
        String shortName = cursor.getString(cursor.getColumnIndex(ChannelTable.SHORT_NAME));
        String coverImageUrl = cursor.getString(cursor.getColumnIndex(ChannelTable.COVER_PHOTO_URL));
        long channelId = cursor.getLong(cursor.getColumnIndex(ChannelTable._ID));
        String title = cursor.getString(cursor.getColumnIndex(ChannelTable.TITLE));
        mCallbacks.onChannelSelected(shortName, coverImageUrl, channelId, title);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                SmodrProvider.CHANNEL_CONTENT_URI,
                null,
                null,
                null,
                ChannelTable._ID + " ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // If no channels in the DB then bootstrap it!
        if (data.getCount() == 0) {
            bootstrapChannelsData();
        }
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    private void bootstrapChannelsData() {
        ArrayList<Channel> mChannels = new ArrayList<>();
        mChannels.add(new Channel("hollywood-babble-on", "Hollywood Babble-On"));
        mChannels.add(new Channel("smodcast", "Smodcast"));
        mChannels.add(new Channel("jay-silent-bob-get-old", "Jay & Silent-Bob Get Old"));
        mChannels.add(new Channel("tell-em-steve-dave", "Tell ‘Em Steve-Dave!"));
        mChannels.add(new Channel("fatman-on-batman", "Fatman on Batman"));
        mChannels.add(new Channel("edumacation-2", "Edumacation"));
        mChannels.add(new Channel("i-sell-comics", "I Sell Comics"));
        mChannels.add(new Channel("plus-one", "Plus One"));
        mChannels.add(new Channel("fsf", "Film School Fridays"));
        mChannels.add(new Channel("last-week-on-earth-with-ben-gleib", "Last Week on Earth"));
        mChannels.add(new Channel("the-secret-stash", "The Secret Stash"));
        mChannels.add(new Channel("netheads", "Netheads"));
        mChannels.add(new Channel("get-up-on-this", "Get Up on This"));
        mChannels.add(new Channel("team-jack", "Team Jack"));
        mChannels.add(new Channel("tha-breaks", "Tha Breaks"));
        mChannels.add(new Channel("having-sex", "Having Sex w/ Katie Morgan"));
        mChannels.add(new Channel("feab", "Four Eyes and a Beard"));
        mChannels.add(new Channel("highlands-a-peephole-history", "Highlands: A Peephole History"));
        mChannels.add(new Channel("waking-from-the-american-dream", "Waking From The American Dream"));
        mChannels.add(new Channel("smodco-smorning-show", "SModCo SMorning Show"));
        mChannels.add(new Channel("smoviemakers", "SMoviemakers"));
        mChannels.add(new Channel("sound-bite-nation", "Soundbite Nation"));
        mChannels.add(new Channel("sminterview", "SMinterview"));
        mChannels.add(new Channel("bagged-boarded-live", "Bagged & Boarded Live"));

        for (Channel c : mChannels) {
            com.kevintcoughlin.smodr.data.model.Channel dbChannel = new com.kevintcoughlin.smodr.data.model.Channel();
            dbChannel.setShortName(c.getShortName());
            dbChannel.setTitle(c.getTitle());
            dbChannel.setCoverPhotoUrl(c.getImageUrl());

            // @TODO: Batch
            getActivity()
                    .getContentResolver()
                    .insert(SmodrProvider.CHANNEL_CONTENT_URI, dbChannel.getContentValues());
        }
    }

    private void track() {
        Tracker t = ((SmodrApplication) getActivity().getApplication())
                .getTracker(SmodrApplication.TrackerName.APP_TRACKER);

        t.setScreenName(TAG);
        t.send(new HitBuilders.AppViewBuilder().build());
    }
}
