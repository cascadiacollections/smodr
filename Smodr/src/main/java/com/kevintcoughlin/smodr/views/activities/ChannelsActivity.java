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
import org.parceler.Parcels;

/**
 * SModcast Channels Activity
 */
public final class ChannelsActivity extends FragmentActivity implements ChannelsFragment.Callbacks {
    private static final String TAG = "ChannelsView";
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
        track();
    }

    @Override
    public void onChannelSelected(final Channel channel) {
        trackChannelSelected(channel);

        final Bundle bundle = new Bundle();
	    bundle.putParcelable(EpisodesFragment.ARG_CHANNEL_NAME, Parcels.wrap(channel));


        final EpisodesFragment fragment = new EpisodesFragment();
        fragment.setArguments(bundle);

        getFragmentManager()
            .beginTransaction()
            .replace(R.id.channels_container, fragment)
            .addToBackStack(ChannelsFragment.TAG)
            .commit();
    }

    public void shouldDisplayHomeUp() {
        boolean canback = getSupportFragmentManager().getBackStackEntryCount() > 0;
        if (!canback) {
            // @TODO: Move this
            setTitle(getText(R.string.app_name));
        }
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

    private void track() {
        Tracker t = ((SmodrApplication) getApplication())
                .getTracker(SmodrApplication.TrackerName.APP_TRACKER);

        t.setScreenName(TAG);
        t.send(new HitBuilders.AppViewBuilder().build());
    }

    private void trackChannelSelected(final Channel channel) {
        Tracker t = ((SmodrApplication) getApplication()).getTracker(
                SmodrApplication.TrackerName.APP_TRACKER);

        t.send(new HitBuilders.EventBuilder()
                .setCategory("CHANNEL")
                .setAction("SELECTED")
                .setLabel(channel.getShortName())
                .build());
    }
}
