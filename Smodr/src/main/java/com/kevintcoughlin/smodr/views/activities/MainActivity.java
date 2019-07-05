package com.kevintcoughlin.smodr.views.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.kevintcoughlin.smodr.R;
import com.kevintcoughlin.smodr.models.Channel;
import com.kevintcoughlin.smodr.utils.AppUtil;
import com.kevintcoughlin.smodr.views.fragments.ChannelsFragment;
import com.kevintcoughlin.smodr.views.fragments.EpisodesFragment;

import java.util.Objects;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.net.ConnectivityManager.*;

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
    /**
     * Receiver for network connectivity events.
     */
    @Nullable
    private NetworkStateReceiver mNetworkStateReceiver;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_layout);
        ButterKnife.bind(this);

        mNetworkStateReceiver = new NetworkStateReceiver();
        registerReceiver(mNetworkStateReceiver, new IntentFilter(CONNECTIVITY_ACTION));

        final AdRequest adRequest = new AdRequest.Builder().addTestDevice(getString(R.string.test_device_id)).build();
        mAdView.loadAd(adRequest);
        setSupportActionBar(mToolbar);

        if (savedInstanceState == null) {
            final FragmentManager fm = getSupportFragmentManager();
            final Fragment fragment = new ChannelsFragment();
            fm.beginTransaction()
                    .add(R.id.container, fragment, ChannelsFragment.TAG)
                    .commit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mNetworkStateReceiver);
    }

    @Override
    public void setTitle(final CharSequence title) {
        if (getActionBar() != null) getActionBar().setTitle(title );
    }

    private void onNetworkConnected() {
        AppUtil.snackbar(findViewById(R.id.coordinator_layout), R.string.on_network_connected);
    }

    private void onNetworkDisconnected() {
        AppUtil.snackbar(findViewById(R.id.coordinator_layout), R.string.on_network_disconnected);
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
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
    public void onItemSelected(Channel item) {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        final Fragment fragment = EpisodesFragment.create(item);

        if (mToolbar != null) {
            mToolbar.setTitle(item.title);
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        if (fragmentManager != null) {
            fragmentManager
                    .beginTransaction()
                    .add(R.id.container, fragment, EpisodesFragment.TAG)
                    .commit();
        }
    }

    /**
     * Receiver for network connectivity events.
     *
     * @author kevintcoughlin
     */
    public final class NetworkStateReceiver extends BroadcastReceiver {

        private boolean mLastConnectivityState = true;

        public void onReceive(Context context, Intent intent) {
            if (intent == null || context == null || !CONNECTIVITY_ACTION.equals(intent.getAction())) {
                return;
            }
            if (intent.getAction().equals(CONNECTIVITY_ACTION) && (intent.getExtras() != null)) {
                final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
                final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo != null
                        && (networkInfo.getType() == TYPE_MOBILE
                        || networkInfo.getType() == TYPE_WIFI)) {
                    onConnectivityChange(context);
                }
            }
        }

        private void onConnectivityChange(final Context context) {
            final boolean isNetworkAvailable = isNetworkAvailable(context);
            if (isNetworkAvailable && !mLastConnectivityState) {
                mLastConnectivityState = true;
                onNetworkConnected();
            } else if (!isNetworkAvailable && mLastConnectivityState) {
                mLastConnectivityState = false;
                onNetworkDisconnected();
            }
        }

        private boolean isNetworkAvailable(final Context context) {
            final ConnectivityManager cm = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
            final NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnected();
        }
    }

}
