package com.kevintcoughlin.smodr.views.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kevintcoughlin.smodr.R;
import com.kevintcoughlin.smodr.SmodrApplication;
import com.kevintcoughlin.smodr.views.fragments.ChannelsFragment;
import com.kevintcoughlin.smodr.views.fragments.EpisodesFragment;

/**
 * SModcast Channels Activity
 */
public class ChannelsActivity extends FragmentActivity implements ChannelsFragment.Callbacks, FragmentManager.OnBackStackChangedListener {
    private static final String TAG = "ChannelsView";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.channels_view_layout);

        setTitle("Smodr");

        getSupportFragmentManager().addOnBackStackChangedListener(this);
        shouldDisplayHomeUp();

        if (savedInstanceState == null) {
            final FragmentManager fm = getSupportFragmentManager();
            final ChannelsFragment fragment = new ChannelsFragment();
            fm.beginTransaction()
                    .add(R.id.channels_container, fragment, ChannelsFragment.TAG)
                    .commit();
        }

        loadAd();
        track();
    }

    @Override
    public void onChannelSelected(String shortName, String photoUrl) {
        trackChannelSelected(shortName);

        Bundle arguments = new Bundle();
        arguments.putString(EpisodesFragment.ARG_CHANNEL_NAME, shortName);
        arguments.putString(EpisodesFragment.ARG_CHANNEL_PHOTO_URL, photoUrl);

        EpisodesFragment fragment = new EpisodesFragment();
        fragment.setArguments(arguments);

        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.channels_container, fragment)
            .addToBackStack(ChannelsFragment.TAG)
            .commit();
    }

    @Override
    public void onBackStackChanged() {
        shouldDisplayHomeUp();
    }

    public void shouldDisplayHomeUp() {
        boolean canback = getSupportFragmentManager().getBackStackEntryCount() > 0;
        getActionBar().setDisplayHomeAsUpEnabled(canback);
    }

    @Override
    public boolean onNavigateUp() {
        getSupportFragmentManager().popBackStack();
        return true;
    }

    private void loadAd() {
        AdView adView = (AdView) this.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    private void track() {
        Tracker t = ((SmodrApplication) getApplication())
                .getTracker(SmodrApplication.TrackerName.APP_TRACKER);

        t.setScreenName(TAG);
        t.send(new HitBuilders.AppViewBuilder().build());
    }

    private void trackChannelSelected(String shortName) {
        Tracker t = ((SmodrApplication) getApplication()).getTracker(
                SmodrApplication.TrackerName.APP_TRACKER);

        t.send(new HitBuilders.EventBuilder()
                .setCategory("CHANNEL")
                .setAction("SELECTED")
                .setLabel(shortName)
                .build());
    }
}
