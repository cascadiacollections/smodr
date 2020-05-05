package com.kevintcoughlin.smodr.views.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
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
import com.google.android.gms.ads.RequestConfiguration;
import com.kevintcoughlin.smodr.R;
import com.kevintcoughlin.smodr.models.Channel;
import com.kevintcoughlin.smodr.services.MediaService;
import com.kevintcoughlin.smodr.views.fragments.EpisodesFragment;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

import java.util.Arrays;

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
    private MediaService mService;
    private boolean mBound = false;
    private final static String APP_CENTER_ID = "4933507b-9621-4fe6-87c6-150a352d7f47";
    private final static String AD_ID = "ca-app-pub-6967310132431626/8150044399";
    private final Channel mChannel = new Channel(
            "Tell 'Em Steve-Dave",
            "https://feeds.feedburner.com/TellEmSteveDave",
            "https://i1.sndcdn.com/avatars-000069229441-16gxj6-original.jpg"
    );

    @Override
    protected void onStart() {
        super.onStart();

        final Intent intent = new Intent(this, MediaService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();

        unbindService(connection);
        mBound = false;
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            final MediaService.MediaServiceBinder binder = (MediaService.MediaServiceBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCenter.start(getApplication(), APP_CENTER_ID, Analytics.class, Crashes.class);
        setContentView(R.layout.activity_main_layout);
        ButterKnife.bind(this);
        initializeAds();

        if (savedInstanceState == null) {
            final FragmentManager fm = getSupportFragmentManager();
            final Fragment fragment = EpisodesFragment.create(mChannel);

            fm.beginTransaction()
                    .add(R.id.coordinator_layout, fragment, EpisodesFragment.TAG)
                    .commit();
        }
    }

    @OnClick(R.id.play)
    public void onTogglePlaybackClick(@NonNull View view) {
        if (mBound) {
            if (this.mService.isPlaying()) {
                this.mService.pausePlayback();
            } else {
                this.mService.resumePlayback();
            }
        }
    }

    @OnClick(R.id.forward)
    public void onForwardClick(@NonNull View view) {
        if (mBound) {
            this.mService.seekTo(MediaService.THIRTY_SECONDS_IN_MILLISECONDS);
        }
    }

    @OnClick(R.id.replay)
    public void onRewindClick(@NonNull View view) {
        if (mBound) {
            this.mService.seekTo(-MediaService.THIRTY_SECONDS_IN_MILLISECONDS);
        }
    }

    private void initializeAds() {
        mAdView = findViewById(R.id.ad);
        adView = new AdView(this);
        adView.setAdUnitId(AD_ID);
        mAdView.addView(adView);

        final RequestConfiguration configuration = new RequestConfiguration.Builder()
                .setTestDeviceIds(Arrays.asList(AdRequest.DEVICE_ID_EMULATOR))
                .build();
        MobileAds.setRequestConfiguration(configuration);
        MobileAds.initialize(this);
        final AdRequest adRequest = new AdRequest.Builder().build();
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
