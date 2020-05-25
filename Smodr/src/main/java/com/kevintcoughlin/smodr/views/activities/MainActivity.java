package com.kevintcoughlin.smodr.views.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.kevintcoughlin.smodr.database.AppDatabase;
import com.kevintcoughlin.smodr.models.Channel;
import com.kevintcoughlin.smodr.models.Item;
import com.kevintcoughlin.smodr.services.MediaService;
import com.kevintcoughlin.smodr.views.TextViewKt;
import com.kevintcoughlin.smodr.views.fragments.EpisodesFragment;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.kevintcoughlin.smodr.R;
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
    private final static String AD_UNIT_ID = "ca-app-pub-6967310132431626/8145526941";
    private final static Channel mChannel = new Channel(
            "Tell 'Em Steve-Dave",
            "https://feeds.feedburner.com/TellEmSteveDave"
    );
    private final static String PRIVACY_POLICY_URL =
            "https://kevintcoughlin.blob.core.windows.net/smodr/privacy_policy.html";
    private final static String NEW_ISSUE_URL =
            "https://github.com/cascadiacollections/SModr/issues/new";
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
                    mPlay.setImageDrawable(getDrawable(R.drawable.ic_round_pause_24));
                    mSeekBar.setMax(mService.getDuration());
                    mPlayer.setVisibility(View.VISIBLE);

                    // @todo: dispose of timer
                    // @todo: worth backgrounding?
                    mUpdateProgress = () -> {
                        updateSeekProgress();
                        mHandler.postDelayed(mUpdateProgress, ONE_SECOND_IN_MS);
                    };
                    runOnUiThread(mUpdateProgress);

                    FirebaseAnalytics.getInstance(getApplicationContext()).logEvent("start_playback", safeGetEventBundle(mItem));
                }

                @Override
                public void onStopPlayback() {
                    mPlay.setImageDrawable(getDrawable(R.drawable.ic_round_play_arrow_24));

                    FirebaseAnalytics.getInstance(getApplicationContext()).logEvent("stop_playback", safeGetEventBundle(mItem));
                }

                @Override
                public void onCompletion() {
                    mSeekBar.setMax(0);
                    mSeekBar.setProgress(0);
                    mPlay.setImageDrawable(getDrawable(R.drawable.ic_round_play_arrow_24));

                    // @todo: also long click toggle
                    // @todo: cleanup
                    mItem.setCompleted(true);
                    AppDatabase.updateData(getApplicationContext(), mItem);
                    mBinderRecyclerFragment.markCompleted(mItem);

                    FirebaseAnalytics.getInstance(getApplicationContext()).logEvent("complete_playback", safeGetEventBundle(mItem));

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

    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        final int id = item.getItemId();

        switch (id) {
            case R.id.feedback:
                final Intent newIssue = new Intent(Intent.ACTION_VIEW);
                newIssue.setData(Uri.parse(NEW_ISSUE_URL));
                startActivity(newIssue);
                return true;
            case R.id.privacy_policy:
                final Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(PRIVACY_POLICY_URL));
                startActivity(i);
                return true;
            case R.id.third_party_notices:
                startActivity(new Intent(this, OssLicensesMenuActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

                FirebaseAnalytics.getInstance(this).logEvent("pause_playback", safeGetEventBundle(mItem));
            } else {
                this.mService.resumePlayback();

                FirebaseAnalytics.getInstance(this).logEvent("resume_playback", safeGetEventBundle(mItem));
            }
        }
    }

    @OnClick(R.id.forward)
    public final void onForwardClick(@NonNull View view) {
        if (mBound) {
            this.mService.forward();
            this.updateSeekProgress();
        }

        FirebaseAnalytics.getInstance(this).logEvent("forward_playback", safeGetEventBundle(mItem));
    }

    @OnClick(R.id.replay)
    public final void onRewindClick(@NonNull View view) {
        if (mBound) {
            this.mService.rewind();
            this.updateSeekProgress();
        }

        FirebaseAnalytics.getInstance(this).logEvent("rewind_playback", safeGetEventBundle(mItem));
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
        FirebaseAnalytics.getInstance(this).logEvent("selected_item", safeGetEventBundle(item));
    }

    private Bundle safeGetEventBundle(@Nullable Item item) {
        if (item == null) {
            return new Bundle();
        }

        return item.eventBundle();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        // @todo profile
        final Bundle bundle = safeGetEventBundle(mItem);
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
