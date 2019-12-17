package com.kevintcoughlin.smodr.views.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.kevintcoughlin.smodr.R;
import com.kevintcoughlin.smodr.models.Channel;
import com.kevintcoughlin.smodr.views.fragments.EpisodesFragment;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * The primary activity that displays a {@link EpisodesFragment}.
 *
 * @author kevincoughlin
 */
public final class MainActivity extends AppCompatActivity {
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.ad)
    AdView mAdView;

    private final static String APP_CENTER_ID = "4933507b-9621-4fe6-87c6-150a352d7f47";
    private final static String AD_ID = "ca-app-pub-6967310132431626~6673311196";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCenter.start(getApplication(), APP_CENTER_ID,
                Analytics.class, Crashes.class);

        setContentView(R.layout.activity_main_layout);
        ButterKnife.bind(this);

        MobileAds.initialize(this, AD_ID);
        final AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        setSupportActionBar(mToolbar);

        final Channel channel = new Channel("Tell 'Em Steve-Dave", "https://feeds.feedburner.com/TellEmSteveDave", "https://i1.sndcdn.com/avatars-000069229441-16gxj6-original.jpg");
        if (savedInstanceState == null) {
            final FragmentManager fm = getSupportFragmentManager();
            final Fragment fragment = EpisodesFragment.create(channel);

            fm.beginTransaction()
                    .add(R.id.coordinator_layout, fragment, EpisodesFragment.TAG)
                    .commit();
        }
    }
}
