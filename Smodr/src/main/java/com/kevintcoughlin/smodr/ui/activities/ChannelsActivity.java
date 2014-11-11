package com.kevintcoughlin.smodr.ui.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.kevintcoughlin.smodr.R;
import com.kevintcoughlin.smodr.ui.fragments.ChannelsFragment;
import com.kevintcoughlin.smodr.ui.fragments.EpisodesFragment;

public class ChannelsActivity extends ActionBarActivity implements ChannelsFragment.Callbacks,
    EpisodesFragment.Callbacks {

    private static final String TAG = "ChannelsView";
    private Toolbar mToolbar;
    private int mThemedStatusBarColor;
    private int mNormalStatusBarColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_channels);

        //mThemedStatusBarColor = getResources().getColor(R.color.theme_primary_dark);
        //mNormalStatusBarColor = mThemedStatusBarColor;

        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ChannelsFragment channelsFragment = new ChannelsFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, channelsFragment, channelsFragment.TAG)
                .commit();
    }

    public int getThemedStatusBarColor() {
        return mThemedStatusBarColor;
    }

    public void setNormalStatusBarColor(int color) {
        mNormalStatusBarColor = color;
    }

    @Override
    public void onChannelSelected(String channel) {
        EpisodesFragment episodesFragment = new EpisodesFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack("channels")
                .replace(R.id.container, episodesFragment)
                .commit();
    }

    @Override
    public void onEpisodeSelected(String episode) {

    }

}
