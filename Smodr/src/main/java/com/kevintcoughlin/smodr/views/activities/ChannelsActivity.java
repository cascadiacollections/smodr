package com.kevintcoughlin.smodr.views.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kevintcoughlin.smodr.R;
import com.kevintcoughlin.smodr.SmodrApplication;
import com.kevintcoughlin.smodr.models.Channel;
import com.kevintcoughlin.smodr.views.fragments.ChannelsFragment;
import com.kevintcoughlin.smodr.views.fragments.EpisodesFragment;

public final class ChannelsActivity extends FragmentActivity implements ChannelsFragment.Callbacks {
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.channels_view_layout);
        mTitle = getTitle();
        if (savedInstanceState == null) {
            final FragmentManager fm = getSupportFragmentManager();
            final ChannelsFragment fragment = new ChannelsFragment();
            fm.beginTransaction()
                    .add(R.id.channels_container, fragment, ChannelsFragment.TAG)
                    .commit();
        }
    }

    @Override
    public void onChannelSelected(final Channel channel) {
        trackChannelSelected(channel);
	    final EpisodesFragment fragment = new EpisodesFragment();
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.channels_container, fragment)
            .addToBackStack(ChannelsFragment.TAG)
            .commit();
    }

    @Override
    public void setTitle(CharSequence title) {
	    if (getActionBar() != null) {
		    mTitle = title;
		    getActionBar().setTitle(mTitle);
	    }
    }

    @Override
    public boolean onNavigateUp() {
        getSupportFragmentManager().popBackStack();
        return true;
    }

    private void trackChannelSelected(final Channel channel) {
        final Tracker t = ((SmodrApplication) getApplication()).getTracker(
                SmodrApplication.TrackerName.APP_TRACKER);
        t.send(new HitBuilders.EventBuilder()
                .setCategory("CHANNEL")
                .setAction("SELECTED")
                .setLabel(channel.getShortName())
                .build());
    }
}
