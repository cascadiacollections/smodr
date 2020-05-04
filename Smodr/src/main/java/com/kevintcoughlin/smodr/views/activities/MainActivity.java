package com.kevintcoughlin.smodr.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.kevintcoughlin.smodr.R;
import com.kevintcoughlin.smodr.models.Channel;
import com.kevintcoughlin.smodr.services.MediaService;
import com.kevintcoughlin.smodr.views.fragments.EpisodesFragment;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public final class MainActivity extends AppCompatActivity {
    @BindView(R.id.play)
    ImageView mPlay;
    @BindView(R.id.replay)
    ImageView mReplay;
    @BindView(R.id.forward)
    ImageView mForward;
    @BindView(R.id.ad)
    RelativeLayout mAdView;
    AdView adView;

    private final static String APP_CENTER_ID = "4933507b-9621-4fe6-87c6-150a352d7f47";
    private final static String AD_ID = "ca-app-pub-6967310132431626/8150044399";
    private final Channel mChannel = new Channel(
            "Tell 'Em Steve-Dave",
            "https://feeds.feedburner.com/TellEmSteveDave",
            "https://i1.sndcdn.com/avatars-000069229441-16gxj6-original.jpg"
    );

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCenter.start(getApplication(), APP_CENTER_ID, Analytics.class, Crashes.class);
        setContentView(R.layout.activity_main_layout);
        ButterKnife.bind(this);

        MobileAds.initialize(this, initializationStatus -> { });

        initializeAds();

        if (savedInstanceState == null) {
            final FragmentManager fm = getSupportFragmentManager();
            final Fragment fragment = EpisodesFragment.create(mChannel);

            fm.beginTransaction()
                    .add(R.id.coordinator_layout, fragment, EpisodesFragment.TAG)
                    .commit();
        }
    }

    @OnClick(R.id.forward)
    public void onForwardClick(@NonNull View view) {
        final Intent intent = MediaService.createAction(
                this,
                MediaService.ACTION_FORWARD
        );

        startService(intent);
    }

    @OnClick(R.id.replay)
    public void onRewindClick(@NonNull View view) {
        final Intent intent = MediaService.createAction(
                this,
                MediaService.ACTION_REWIND
        );

        startService(intent);
    }

    private void initializeAds() {
        mAdView = findViewById(R.id.ad);
        adView = new AdView(this);
        adView.setAdUnitId(AD_ID);
        mAdView.addView(adView);

        final AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        final AdSize adSize = getAdSize();

        adView.setAdSize(adSize);
        adView.loadAd(adRequest);
    }

    private AdSize getAdSize() {
        final Display display = getWindowManager().getDefaultDisplay();
        final DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }
}
