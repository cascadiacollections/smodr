package com.kevintcoughlin.smodr.views.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kevintcoughlin.smodr.R;
import com.kevintcoughlin.smodr.SmodrApplication;
import com.kevintcoughlin.smodr.models.Channel;
import com.kevintcoughlin.smodr.utils.AppUtil;
import com.kevintcoughlin.smodr.views.fragments.ChannelsFragment;

/**
 * The primary activity that displays a {@link ChannelsFragment}.
 *
 * @author kevincoughlin
 */
public final class MainActivity extends AppCompatActivity implements ChannelsFragment.OnChannelSelected {
	/**
	 * Action name for selecting an item.
	 */
	public static final String ACTION_SELECTED = "SELECTED";
	/**
	 * Displays the app name and menu actions.
	 */
	@Bind(R.id.toolbar) Toolbar mToolbar;
	/**
	 * Displays an ad below the {@link Fragment}.
	 */
	@Bind(R.id.ad) AdView mAdView;
	/**
	 * Receiver for network connectivity events.
	 */
	@Nullable private NetworkStateReceiver mNetworkStateReceiver;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_layout);
		ButterKnife.bind(this);

		mNetworkStateReceiver = new NetworkStateReceiver();
		registerReceiver(mNetworkStateReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

		final AdRequest adRequest = new AdRequest.Builder().addTestDevice(getString(R.string.test_device_id)).build();
		mAdView.loadAd(adRequest);
		setSupportActionBar(mToolbar);

        if (savedInstanceState == null) {
            final FragmentManager fm = getSupportFragmentManager();
            final ChannelsFragment fragment = new ChannelsFragment();
            fm.beginTransaction()
                    .add(R.id.container, fragment, ChannelsFragment.TAG)
                    .commit();
        }
	}

	@Override
	protected void onResume() {
		super.onResume();
		AppEventsLogger.activateApp(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		AppEventsLogger.deactivateApp(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mNetworkStateReceiver);
	}

	@Override
	public void onChannelSelected(@NonNull final Channel channel) {
		trackChannelSelected(channel);
		final Intent intent = new Intent(this, DetailActivity.class);
		intent.putExtra(DetailActivity.EXTRA_NAME, channel.getTitle());
		intent.putExtra(DetailActivity.EXTRA_IMAGE_URL, channel.getImageUrl());
		startActivity(intent);
    }

	@Override
	public void setTitle(final CharSequence title) {
	    if (getActionBar() != null) {
            getActionBar().setTitle(title);
        }
    }

	@Override
	public boolean onNavigateUp() {
		getSupportFragmentManager().popBackStack();
		return super.onNavigateUp();
	}

    private void trackChannelSelected(@NonNull final Channel channel) {
	    final Tracker t = ((SmodrApplication) getApplication()).getTracker();
	    t.send(new HitBuilders.EventBuilder()
			    .setCategory(Channel.class.getSimpleName().toUpperCase())
			    .setAction(ACTION_SELECTED)
			    .setLabel(channel.getTitle())
			    .build());
    }

	private void onNetworkConnected() {
		AppUtil.snackbar((ViewGroup) findViewById(R.id.coordinator_layout), R.string.on_network_connected);
	}

	private void onNetworkDisconnected() {
		AppUtil.snackbar((ViewGroup) findViewById(R.id.coordinator_layout), R.string.on_network_disconnected);
	}

	/**
	 * Receiver for network connectivity events.
	 *
	 * @author kevintcoughlin
	 */
	public final class NetworkStateReceiver extends BroadcastReceiver {

		private boolean mLastConnectivityState = true;

		public void onReceive(Context context, Intent intent) {
			if (intent == null || context == null) {
				return;
			} else if (!intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				return;
			}
			if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION) && intent.getExtras() != null) {
				final NetworkInfo networkInfo = (NetworkInfo) intent.getExtras().get(ConnectivityManager
						.EXTRA_NETWORK_INFO);
				if (networkInfo != null) {
					if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE
							|| networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
						onConnectivityChange(context);
					}
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
			final ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			final NetworkInfo netInfo = cm.getActiveNetworkInfo();
			return netInfo != null && netInfo.isConnected();
		}
	}

}
