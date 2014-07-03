package com.kevintcoughlin.smodr;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * SModcast Channels Activity
 */
public class ChannelsView extends FragmentActivity implements GridFragment.Callbacks, FragmentManager.OnBackStackChangedListener {
    private static final String TAG = "ChannelsView";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.channels_view_layout);

        setTitle("Smodr");

        getSupportFragmentManager().addOnBackStackChangedListener(this);
        shouldDisplayHomeUp();

        final FragmentManager fm = getSupportFragmentManager();

        if (fm.findFragmentById(android.R.id.content) == null) {
            final GridFragment fragment = new GridFragment();
            fm.beginTransaction().add(R.id.channels_container, fragment).commit();
        }

        loadAd();
        track();
    }

    @Override
    public void onChannelSelected(String shortName) {
        trackChannelSelected(shortName);

        Bundle arguments = new Bundle();
        arguments.putString(EpisodesFragment.ARG_CHANNEL_NAME, shortName);

        EpisodesFragment fragment = new EpisodesFragment();
        fragment.setArguments(arguments);

        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.channels_container, fragment)
            .addToBackStack(GridFragment.TAG)
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
        Tracker t = ((SmodrApplication) getApplication()).getTracker(
                SmodrApplication.TrackerName.APP_TRACKER);

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
