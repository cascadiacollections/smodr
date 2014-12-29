package com.kevintcoughlin.smodr.ui.presenters;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.kevintcoughlin.smodr.models.Channel;
import com.kevintcoughlin.smodr.ui.ChannelsView;
import com.kevintcoughlin.smodr.ui.activities.ChannelActivity;
import com.kevintcoughlin.smodr.ui.adapters.RecyclerChannelsAdapter;
import com.kevintcoughlin.smodr.ui.presenters.mapper.ChannelMapper;

import org.parceler.Parcels;

public class ChannelsPresenterImpl implements ChannelsPresenter, AdapterView.OnItemClickListener {
    public static final String TAG = ChannelsPresenterImpl.class.getSimpleName();
    private ChannelsView mChannelsView;
    private RecyclerChannelsAdapter mChannelAdapter;
    private ChannelMapper mChannelMapper;

    public ChannelsPresenterImpl(ChannelsView channelsView, ChannelMapper channelMapper) {
        mChannelsView = channelsView;
        mChannelMapper = channelMapper;
    }

    @Override
    public void initializeViews() {
        mChannelsView.initializeToolbar();
        mChannelsView.initializeRecyclerView();

        // @TODO: Refactor to mapper interface?
        Channel[] channels = {
            new Channel("hollywood-babble-on", "Hollywood Babble-On"),
            new Channel("smodcast", "Smodcast"),
            new Channel("jay-silent-bob-get-old", "Jay & Silent-Bob Get Old"),
            new Channel("tell-em-steve-dave", "Tell ‘Em Steve-Dave!"),
            new Channel("fatman-on-batman", "Fatman on Batman"),
            new Channel("edumacation-2", "Edumacation"),
            new Channel("i-sell-comics", "I Sell Comics"),
            new Channel("plus-one", "Plus One"),
            new Channel("fsf", "Film School Fridays"),
            new Channel("last-week-on-earth-with-ben-gleib", "Last Week on Earth"),
            new Channel("the-secret-stash", "The Secret Stash"),
            new Channel("netheads", "Netheads"),
            new Channel("get-up-on-this", "Get Up on This"),
            new Channel("team-jack", "Team Jack"),
            new Channel("tha-breaks", "Tha Breaks"),
            new Channel("having-sex", "Having Sex w/ Katie Morgan"),
            new Channel("feab", "Four Eyes and a Beard"),
            new Channel("highlands-a-peephole-history", "Highlands: A Peephole History"),
            new Channel("waking-from-the-american-dream", "Waking From The American Dream"),
            new Channel("smodco-smorning-show", "SModCo SMorning Show"),
            new Channel("smoviemakers", "SMoviemakers"),
            new Channel("sound-bite-nation", "Soundbite Nation"),
            new Channel("sminterview", "SMinterview"),
            new Channel("bagged-boarded-live", "Bagged & Boarded Live"),
        };

        mChannelAdapter = new RecyclerChannelsAdapter(mChannelsView.getContext(), channels);
        mChannelAdapter.setHasStableIds(true);
        mChannelAdapter.setOnItemClickListener(this);
        mChannelMapper.registerAdapter(mChannelAdapter);
    }

    @Override
    public void saveState(Bundle outState) {

    }

    @Override
    public void restoreState(Bundle savedState) {

    }

    @Override
    public void releaseAllResources() {
        mChannelAdapter = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(mChannelsView.getContext(), ChannelActivity.class);
        intent.putExtra("channel", Parcels.wrap(mChannelAdapter.getItem(position)));
        mChannelsView.getContext().startActivity(intent);
    }
}
