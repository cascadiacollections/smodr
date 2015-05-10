package com.kevintcoughlin.smodr.views.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kevintcoughlin.smodr.R;
import com.kevintcoughlin.smodr.SmodrApplication;
import com.kevintcoughlin.smodr.adapters.ChannelsAdapter;
import com.kevintcoughlin.smodr.models.Channel;

import java.util.ArrayList;

/**
 * Fragment that displays SModcast Channels in a GridView
 */
public final class ChannelsFragment extends Fragment {
	public static final String TAG = ChannelsFragment.class.getSimpleName();
    private Callbacks mCallbacks = sChannelCallbacks;
    private ChannelsAdapter mAdapter = new ChannelsAdapter();

    public interface Callbacks {
        void onChannelSelected(Channel channel);
    }

    private static Callbacks sChannelCallbacks = new Callbacks() {
        @Override
        public void onChannelSelected(Channel channel) {
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
        final View view = inflater.inflate(R.layout.channels_grid_layout, container, false);
	    mAdapter.setResults(getChannels());
        track();
        return view;
    }

    private ArrayList<Channel> getChannels() {
        final ArrayList<Channel> channels = new ArrayList<>();
        channels.add(new Channel("hollywood-babble-on", "Hollywood Babble-On"));
        channels.add(new Channel("smodcast", "Smodcast"));
        channels.add(new Channel("jay-silent-bob-get-old", "Jay & Silent-Bob Get Old"));
        channels.add(new Channel("tell-em-steve-dave", "Tell ‘Em Steve-Dave!"));
        channels.add(new Channel("fatman-on-batman", "Fatman on Batman"));
        channels.add(new Channel("edumacation-2", "Edumacation"));
        channels.add(new Channel("i-sell-comics", "I Sell Comics"));
        channels.add(new Channel("plus-one", "Plus One"));
        channels.add(new Channel("fsf", "Film School Fridays"));
        channels.add(new Channel("last-week-on-earth-with-ben-gleib", "Last Week on Earth"));
        channels.add(new Channel("the-secret-stash", "The Secret Stash"));
        channels.add(new Channel("netheads", "Netheads"));
        channels.add(new Channel("get-up-on-this", "Get Up on This"));
        channels.add(new Channel("team-jack", "Team Jack"));
        channels.add(new Channel("tha-breaks", "Tha Breaks"));
        channels.add(new Channel("having-sex", "Having Sex w/ Katie Morgan"));
        channels.add(new Channel("feab", "Four Eyes and a Beard"));
        channels.add(new Channel("highlands-a-peephole-history", "Highlands: A Peephole History"));
        channels.add(new Channel("waking-from-the-american-dream", "Waking From The American Dream"));
        channels.add(new Channel("smodco-smorning-show", "SModCo SMorning Show"));
        channels.add(new Channel("smoviemakers", "SMoviemakers"));
        channels.add(new Channel("sound-bite-nation", "Soundbite Nation"));
        channels.add(new Channel("sminterview", "SMinterview"));
        channels.add(new Channel("bagged-boarded-live", "Bagged & Boarded Live"));
        return channels;
    }

    private void track() {
        Tracker t = ((SmodrApplication) getActivity().getApplication())
                .getTracker(SmodrApplication.TrackerName.APP_TRACKER);

        t.setScreenName(TAG);
        t.send(new HitBuilders.AppViewBuilder().build());
    }
}
