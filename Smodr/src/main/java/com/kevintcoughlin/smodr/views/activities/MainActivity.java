package com.kevintcoughlin.smodr.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kevintcoughlin.smodr.BuildConfig;
import com.kevintcoughlin.smodr.R;
import com.kevintcoughlin.smodr.SmodrApplication;
import com.kevintcoughlin.smodr.models.Channel;
import com.kevintcoughlin.smodr.models.Item;
import com.kevintcoughlin.smodr.services.MediaPlaybackService;
import com.kevintcoughlin.smodr.views.fragments.ChannelsFragment;
import com.kevintcoughlin.smodr.views.fragments.EpisodesFragment;
import hotchemi.android.rate.AppRate;

/**
 * The primary activity containing a single {@link android.support.v4.app.Fragment}.
 *
 * @author kevincoughlin
 */
public final class MainActivity extends AppCompatActivity implements ChannelsFragment.Callbacks, EpisodesFragment.Callbacks {
	/**
	 * The primary {@link Toolbar}.
	 */
	@Nullable
	@Bind(R.id.toolbar)
	Toolbar mToolbar;
	/**
	 * The {@link AdView} displayed.
	 */
	@Nullable
	@Bind(R.id.ad)
	AdView mAdView;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_layout);
		ButterKnife.bind(this);

		final AdRequest adRequest = new AdRequest.Builder().addTestDevice("C6D397172C2598AF256CF30C6393FBFC").build();
		if (mAdView != null) {
			mAdView.loadAd(adRequest);
		}
		setSupportActionBar(mToolbar);

        if (savedInstanceState == null) {
            final FragmentManager fm = getSupportFragmentManager();
            final ChannelsFragment fragment = new ChannelsFragment();
            fm.beginTransaction()
                    .add(R.id.channels_container, fragment, ChannelsFragment.TAG)
                    .commit();
        }

		AppRate.with(this).setDebug(BuildConfig.DEBUG);
		AppRate.showRateDialogIfMeetsConditions(this);
	}

	@Override
	public void onChannelSelected(@NonNull final Channel channel) {
		trackChannelSelected(channel);
	    final EpisodesFragment fragment = new EpisodesFragment();
	    final Bundle args = new Bundle();
	    args.putString(EpisodesFragment.ARG_CHANNEL_NAME, channel.getShortName());
	    fragment.setArguments(args);
	    getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.channels_container, fragment)
            .addToBackStack(ChannelsFragment.TAG)
            .commit();
    }

	@Override
	public void onEpisodeSelected(@NonNull final Item item) {
		final Intent intent = new Intent(this, MediaPlaybackService.class);
		intent.setAction(MediaPlaybackService.ACTION_PLAY);
		intent.putExtra(MediaPlaybackService.INTENT_EPISODE_URL, item.getEnclosure().getUrl());
		intent.putExtra(MediaPlaybackService.INTENT_EPISODE_TITLE, item.getTitle());
		intent.putExtra(MediaPlaybackService.INTENT_EPISODE_DESCRIPTION, item.getDescription());
		startService(intent);
		trackEpisodeSelected(item.getTitle());
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
        final Tracker t = ((SmodrApplication) getApplication()).getTracker(
                SmodrApplication.TrackerName.APP_TRACKER);
        t.send(new HitBuilders.EventBuilder()
		        .setCategory("CHANNEL")
		        .setAction("SELECTED")
		        .setLabel(channel.getShortName())
		        .build());
    }

	private void trackEpisodeSelected(@NonNull final String episodeTitle) {
		final Tracker t = ((SmodrApplication) getApplication()).getTracker(
				SmodrApplication.TrackerName.APP_TRACKER);
		t.send(new HitBuilders.EventBuilder()
				.setCategory("EPISODE")
				.setAction("SELECTED")
				.setLabel(episodeTitle)
				.build());
	}

}
