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
        final Channel[] channels = {
                new Channel(0, "hollywood-babble-on", "Hollywood Babble-On"),
                new Channel(1, "smodcast", "Smodcast"),
                new Channel(2, "jay-silent-bob-get-old", "Jay & Silent-Bob Get Old"),
                new Channel(3, "tell-em-steve-dave", "Tell ‘Em Steve-Dave!"),
                new Channel(4, "fatman-on-batman", "Fatman on Batman"),
                new Channel(5, "edumacation-2", "Edumacation"),
                new Channel(6, "i-sell-comics", "I Sell Comics"),
                new Channel(7, "plus-one", "Plus One"),
                new Channel(8, "fsf", "Film School Fridays"),
                new Channel(9, "last-week-on-earth-with-ben-gleib", "Last Week on Earth"),
                new Channel(10, "the-secret-stash", "The Secret Stash"),
                new Channel(11, "netheads", "Netheads"),
                new Channel(12, "get-up-on-this", "Get Up on This"),
                new Channel(13, "team-jack", "Team Jack"),
                new Channel(14, "tha-breaks", "Tha Breaks"),
                new Channel(15, "having-sex", "Having Sex w/ Katie Morgan"),
                new Channel(16, "feab", "Four Eyes and a Beard"),
                new Channel(17, "highlands-a-peephole-history", "Highlands: A Peephole History"),
                new Channel(18, "waking-from-the-american-dream", "Waking From The American Dream"),
                new Channel(19, "smodco-smorning-show", "SModCo SMorning Show"),
                new Channel(20, "smoviemakers", "SMoviemakers"),
                new Channel(21, "sound-bite-nation", "Soundbite Nation"),
                new Channel(22, "sminterview", "SMinterview"),
                new Channel(23, "bagged-boarded-live", "Bagged & Boarded Live"),
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
