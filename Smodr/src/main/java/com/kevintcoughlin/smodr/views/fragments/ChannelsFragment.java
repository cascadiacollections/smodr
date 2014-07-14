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
        mChannels.add(new Channel("hollywood-babble-on", "Hollywood Babble-On", "http://smodcast.com/wp-content/blogs.dir/1/files_mf/babbleon140063.jpg"));
        mChannels.add(new Channel("smodcast", "Smodcast", "http://i1.sndcdn.com/artworks-000031871422-hgbyb8-original.jpg?30a2558"));
        mChannels.add(new Channel("jay-silent-bob-get-old", "Jay & Silent-Bob Get Old", "http://smodcast.com/wp-content/blogs.dir/1/files_mf/getold1400.jpg"));
        mChannels.add(new Channel("tell-em-steve-dave", "Tell ‘Em Steve-Dave!", "http://i1.sndcdn.com/artworks-000037405000-3krh8k-crop.jpg?30a2558"));
        mChannels.add(new Channel("fatman-on-batman", "Fatman on Batman", "http://smodcast.com/wp-content/blogs.dir/1/files_mf/fatmanbatman1400.jpg"));
        mChannels.add(new Channel("edumacation-2", "Edumacation", "http://smodcast.com/wp-content/blogs.dir/1/files_mf/1400x1400itunes.jpg"));
        mChannels.add(new Channel("i-sell-comics", "I Sell Comics", "http://smodcast.com/wp-content/blogs.dir/1/files_mf/iscitunes.jpg"));
        mChannels.add(new Channel("plus-one", "Plus One", "http://smodcast.com/wp-content/blogs.dir/1/files_mf/plusone1400.jpg"));
        mChannels.add(new Channel("fsf", "Film School Fridays", "http://smodcast.com/wp-content/blogs.dir/1/files_mf/itunes.jpg"));
        mChannels.add(new Channel("last-week-on-earth-with-ben-gleib", "Last Week on Earth", "http://smodcast.com/wp-content/blogs.dir/1/files_mf/lastweekitunes88.jpg"));
        mChannels.add(new Channel("the-secret-stash", "The Secret Stash", "http://smodcast.com/wp-content/blogs.dir/1/files_mf/secretstash59.jpg"));
        mChannels.add(new Channel("netheads", "Netheads", "http://smodcast.com/wp-content/blogs.dir/1/files_mf/itunes1400.jpg"));
        mChannels.add(new Channel("get-up-on-this", "Get Up on This", "http://smodcast.com/wp-content/blogs.dir/1/files_mf/getuponthisitunes91.png"));
        mChannels.add(new Channel("team-jack", "Team Jack", "http://smodcast.com/wp-content/blogs.dir/1/files_mf/teamjackitunes75.jpg"));
        mChannels.add(new Channel("tha-breaks", "Tha Breaks", "http://smodcast.com/wp-content/blogs.dir/1/files_mf/thabreaks37.png"));
        mChannels.add(new Channel("having-sex", "Having Sex w/ Katie Morgan", "http://smodcast.com/wp-content/blogs.dir/1/files_mf/havingsexitunes18.png"));
        mChannels.add(new Channel("feab", "Four Eyes and a Beard", "http://smodcast.com/wp-content/blogs.dir/1/files_mf/feabitunes74.jpg"));
        mChannels.add(new Channel("highlands-a-peephole-history", "Highlands: A Peephole History", "http://smodcast.com/wp-content/blogs.dir/1/files_mf/peepholeitunes36.jpg"));
        mChannels.add(new Channel("waking-from-the-american-dream", "Waking From The American Dream", "http://smodcast.com/wp-content/blogs.dir/1/files_mf/wakingitunes64.jpg"));
        mChannels.add(new Channel("smodco-smorning-show", "SModCo SMorning Show", "http://smodcast.com/wp-content/blogs.dir/1/files_mf/smorningitunes78.jpg"));
        mChannels.add(new Channel("smoviemakers", "SMoviemakers", "http://smodcast.com/wp-content/blogs.dir/1/files_mf/smoviemakersitunes85.jpg"));
        mChannels.add(new Channel("sound-bite-nation", "Soundbite Nation", "http://smodcast.com/wp-content/blogs.dir/1/files/2011/08/featured1.jpg"));
        mChannels.add(new Channel("sminterview", "SMinterview", "http://smodcast.com/wp-content/blogs.dir/1/files_mf/sminterviewitunes59.jpg"));
        mChannels.add(new Channel("bagged-boarded-live", "Bagged & Boarded Live", "http://smodcast.com/wp-content/blogs.dir/1/files_mf/600x600itunes.jpg"));

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
