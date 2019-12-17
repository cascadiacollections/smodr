package com.kevintcoughlin.smodr.views.activities;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.kevintcoughlin.smodr.R;
import com.kevintcoughlin.smodr.models.Channel;
import com.kevintcoughlin.smodr.views.fragments.ChannelsFragment;
import com.kevintcoughlin.smodr.views.fragments.EpisodesFragment;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

import java.util.Objects;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.kevintcoughlin.smodr.views.fragments.ChannelsFragment.CHANNEL_MAP;

/**
 * The primary activity that displays a {@link EpisodesFragment}.
 *
 * @author kevincoughlin
 */
public final class MainActivity extends AppCompatActivity implements ChannelsFragment.OnItemSelected<Channel> {
    /**
     * Displays the app name and menu actions.
     */
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    /**
     * Displays an ad below the {@link Fragment}.
     */
    @Bind(R.id.ad)
    AdView mAdView;
    private final static String TESD_CHANNEL_ID = "Tell 'Em Steve-Dave";
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

        final Channel channel = CHANNEL_MAP.get(TESD_CHANNEL_ID);
        if (savedInstanceState == null && channel != null) {
            final FragmentManager fm = getSupportFragmentManager();
            final Fragment fragment = EpisodesFragment.create(channel);

            fm.beginTransaction()
                    .add(R.id.coordinator_layout, fragment, EpisodesFragment.TAG)
                    .commit();
        }
    }

    @Override
    public void setTitle(final CharSequence title) {
        if (getActionBar() != null) getActionBar().setTitle(title );
    }

    @Override
    public void onAttachFragment(final @NonNull Fragment fragment) {
        if (fragment instanceof ChannelsFragment) {
            final ChannelsFragment channelsFragment = (ChannelsFragment) fragment;
            channelsFragment.setOnItemSelectedListener(this);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.remove(Objects.requireNonNull(getSupportFragmentManager().findFragmentByTag(EpisodesFragment.TAG)));
        fragmentTransaction.commit();
        getSupportFragmentManager().popBackStack();
        mToolbar.setTitle(getString(R.string.app_name));
        return super.onSupportNavigateUp();
    }

    @Override
    public void onItemSelected(final Channel item) {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        final Fragment fragment = EpisodesFragment.create(item);

        if (mToolbar != null) {
            mToolbar.setTitle(item.title);
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        fragmentManager
                .beginTransaction()
                .add(R.id.container, fragment, EpisodesFragment.TAG)
                .commit();
    }
}
