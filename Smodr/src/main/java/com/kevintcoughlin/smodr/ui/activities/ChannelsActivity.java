package com.kevintcoughlin.smodr.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import com.kevintcoughlin.smodr.R;
import com.kevintcoughlin.smodr.models.Channel;
import com.kevintcoughlin.smodr.models.Item;
import com.kevintcoughlin.smodr.services.MediaPlayerService;
import com.kevintcoughlin.smodr.ui.fragments.ChannelsFragment;
import com.kevintcoughlin.smodr.ui.fragments.EpisodesFragment;

public class ChannelsActivity extends ActionBarActivity implements ChannelsFragment.Callbacks,
    EpisodesFragment.Callbacks {

    private static final String TAG = "ChannelsView";
    private Toolbar mToolbar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_channels);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ChannelsFragment channelsFragment = new ChannelsFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, channelsFragment, channelsFragment.TAG)
                .commit();
    }

    @Override
    public void onChannelSelected(Channel channel) {
        EpisodesFragment episodesFragment = new EpisodesFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack("channels")
                .replace(R.id.container, episodesFragment)
                .commit();
    }

    @Override
    public void onEpisodeSelected(Item episode) {
        Intent intent = new Intent(getApplicationContext(), MediaPlayerService.class);
        intent.setAction(MediaPlayerService.ACTION_PLAY);
        intent.putExtra("episode", episode);
        startService(intent);
    }

}
