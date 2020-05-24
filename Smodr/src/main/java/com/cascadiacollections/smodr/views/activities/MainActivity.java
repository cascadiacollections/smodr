package com.cascadiacollections.smodr.views.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.cascadiacollections.smodr.R;
import com.cascadiacollections.smodr.database.AppDatabase;
import com.cascadiacollections.smodr.models.Channel;
import com.cascadiacollections.smodr.models.Item;
import com.cascadiacollections.smodr.services.MediaService;
import com.cascadiacollections.smodr.views.TextViewKt;
import com.cascadiacollections.smodr.views.fragments.EpisodesFragment;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public final class MainActivity extends AppCompatActivity implements EpisodesFragment.OnItemSelected<Item>, SeekBar.OnSeekBarChangeListener {
    @BindView(R.id.player)
    LinearLayout mPlayer;
    @BindView(R.id.play)
    ImageView mPlay;
    @BindView(R.id.replay)
    ImageView mReplay;
    @BindView(R.id.forward)
    ImageView mForward;
    @BindView(R.id.ad)
    RelativeLayout mAdView;
    AdView adView;
    @BindView(R.id.seekbar)
    SeekBar mSeekBar;
    @BindView(R.id.current_time)
    TextView mCurrentTime;
    @BindView(R.id.remaining_time)
    TextView mRemainingTime;

    private MediaService mService;
    private boolean mBound = false;
    private final static int ONE_SECOND_IN_MS = 1000;
    private final static String APP_CENTER_ID = "4933507b-9621-4fe6-87c6-150a352d7f47";
    private static final String AD_UNIT_ID = "ca-app-pub-6967310132431626/8145526941";
    private final static Channel mChannel = new Channel(
            "Tell 'Em Steve-Dave",
            "https://feeds.feedburner.com/TellEmSteveDave"
    );
    private Runnable mUpdateProgress;
    private Handler mHandler = new Handler();
    private EpisodesFragment mBinderRecyclerFragment;
    private Item mItem;

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

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            final MediaService.MediaServiceBinder binder = (MediaService.MediaServiceBinder) service;
            mService = binder.getService();
            mService.setPlaybackListener(new MediaService.IPlaybackListener() {
                @Override
                public void onStartPlayback() {
                    mPlay.setImageDrawable(getDrawable(R.drawable.baseline_pause_black_18dp));
                    mSeekBar.setMax(mService.getDuration());
                    mPlayer.setVisibility(View.VISIBLE);

                    // @todo: dispose of timer
                    // @todo: worth backgrounding?
                    mUpdateProgress = () -> {
                        updateSeekProgress();
                        mHandler.postDelayed(mUpdateProgress, ONE_SECOND_IN_MS);
                    };
                    runOnUiThread(mUpdateProgress);

                    FirebaseAnalytics.getInstance(getApplicationContext()).logEvent("start_playback", mItem.eventBundle());
                }

                @Override
                public void onStopPlayback() {
                    mPlay.setImageDrawable(getDrawable(R.drawable.round_play_arrow_black_18dp));

                    FirebaseAnalytics.getInstance(getApplicationContext()).logEvent("stop_playback", mItem.eventBundle());
                }

                @Override
                public void onCompletion() {
                    mSeekBar.setMax(0);
                    mSeekBar.setProgress(0);
                    mPlay.setImageDrawable(getDrawable(R.drawable.round_play_arrow_black_18dp));

                    // @todo: also long click toggle
                    // @todo: cleanup
                    mItem.setCompleted(true);
                    AppDatabase.updateData(getApplicationContext(), mItem);
                    mBinderRecyclerFragment.markCompleted(mItem);

                    FirebaseAnalytics.getInstance(getApplicationContext()).logEvent("complete_playback", mItem.eventBundle());

                    // @todo
                    mItem = null;

                    mInterstitialAd.loadAd(new AdRequest.Builder().build());
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    }
                    mInterstitialAd.setAdListener(new AdListener() {
                        @Override
                        public void onAdClosed() {
                            mInterstitialAd.loadAd(new AdRequest.Builder().build());
                        }

                    });
                }
            });
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    private InterstitialAd mInterstitialAd;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCenter.start(getApplication(), APP_CENTER_ID, Analytics.class, Crashes.class);
        setContentView(R.layout.activity_main_layout);
        ButterKnife.bind(this);

        initializeAds();
        mSeekBar.setOnSeekBarChangeListener(this);

        if (savedInstanceState == null) {
            final FragmentManager fm = getSupportFragmentManager();
            final Fragment fragment = EpisodesFragment.create(mChannel);

            fm.beginTransaction()
                    .add(R.id.coordinator_layout, fragment, EpisodesFragment.TAG)
                    .commit();
        }

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });
    }

    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        super.onAttachFragment(fragment);

        if (fragment instanceof EpisodesFragment) {
            mBinderRecyclerFragment = (EpisodesFragment) fragment;
            mBinderRecyclerFragment.setOnItemSelectedListener(this);
        }
    }

    @OnClick(R.id.play)
    public final void onTogglePlaybackClick(@NonNull View view) {
        if (mBound) {
            if (this.mService.isPlaying()) {
                this.mService.pausePlayback();

                FirebaseAnalytics.getInstance(this).logEvent("pause_playback", mItem.eventBundle());
            } else {
                this.mService.resumePlayback();

                FirebaseAnalytics.getInstance(this).logEvent("resume_playback", mItem.eventBundle());
            }
        }
    }

    @OnClick(R.id.forward)
    public final void onForwardClick(@NonNull View view) {
        if (mBound) {
            this.mService.forward();
            this.updateSeekProgress();
        }

        FirebaseAnalytics.getInstance(this).logEvent("forward_playback", mItem.eventBundle());
    }

    @OnClick(R.id.replay)
    public final void onRewindClick(@NonNull View view) {
        if (mBound) {
            this.mService.rewind();
            this.updateSeekProgress();
        }

        FirebaseAnalytics.getInstance(this).logEvent("rewind_playback", mItem.eventBundle());
    }

    private void updateSeekProgress() {
        if (!mBound) {
            return;
        }

        mSeekBar.setProgress(this.mService.getCurrentTime());
        TextViewKt.setElapsedTime(mCurrentTime, this.mService.getCurrentTime());
        TextViewKt.setElapsedTime(mRemainingTime, this.mService.getRemainingTime());
    }

    private void initializeAds() {
        final RequestConfiguration configuration = new RequestConfiguration.Builder()
                .setTestDeviceIds(Collections.singletonList(AdRequest.DEVICE_ID_EMULATOR))
                .build();

        MobileAds.setRequestConfiguration(configuration);
        MobileAds.initialize(this);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(AD_UNIT_ID);
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    @Override
    public final void onItemSelected(@NonNull Item item) {
        if (!mBound) {
            return;
        }

        // @todo: move into service
        mItem = item;
        mService.startPlayback(item.getUri());

        // @todo profile


        FirebaseAnalytics.getInstance(this).logEvent("selected_item", item.eventBundle());
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        // @todo profile
        final Bundle bundle = mItem.eventBundle();
        bundle.putInt("progress", progress);
        bundle.putBoolean("fromUser", fromUser);

        FirebaseAnalytics.getInstance(this).logEvent("seek_playback", bundle);

        if (fromUser && mService != null) {
            mService.seekTo(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
