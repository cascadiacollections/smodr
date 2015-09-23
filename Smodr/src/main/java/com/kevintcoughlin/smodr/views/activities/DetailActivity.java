package com.kevintcoughlin.smodr.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kevintcoughlin.smodr.R;
import com.kevintcoughlin.smodr.SmodrApplication;
import com.kevintcoughlin.smodr.adapters.BinderAdapter;
import com.kevintcoughlin.smodr.models.Episode;
import com.kevintcoughlin.smodr.utils.AppUtil;
import com.kevintcoughlin.smodr.viewholders.EpisodeViewBinder;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * {@link com.kevintcoughlin.smodr.models.Channel} detail
 * @author kevincoughlin
 */
public final class DetailActivity extends AppCompatActivity {

	public static final String EXTRA_NAME = ".name";
	public static final String EXTRA_IMAGE_URL = ".image_url";
	@Bind(R.id.list) RecyclerView mRecyclerView;
	@Nullable private BinderAdapter mAdapter;
	@NonNull private String mChannelName = "Smodcast";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail_layout);
		ButterKnife.bind(this);

		final Intent intent = getIntent();
		if (intent != null) {
			mChannelName = intent.getStringExtra(EXTRA_NAME);
			loadBackdrop(intent.getStringExtra(EXTRA_IMAGE_URL));
		}

		final Toolbar toolbar = ButterKnife.findById(this, R.id.toolbar);
		setSupportActionBar(toolbar);
		if (getSupportActionBar() != null) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}

		final CollapsingToolbarLayout collapsingToolbar = ButterKnife.findById(this, R.id.collapsing_toolbar);
		collapsingToolbar.setTitle(mChannelName);

		mAdapter = new BinderAdapter(this);
		mAdapter.registerViewType(R.layout.item_list_episode_layout, new EpisodeViewBinder(), Episode.class);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		mRecyclerView.setAdapter(mAdapter);
		refresh(mChannelName);
	}

	private void loadBackdrop(final String url) {
		final ImageView imageView = ButterKnife.findById(this, R.id.backdrop);
		Glide.with(this).load(url).centerCrop().into(imageView);
	}

	/**
	 * Refreshes the channel's feed of episodes.
	 *
	 * @param name
	 *      the name of the channel to refresh.
	 */
	private void refresh(@NonNull final String name) {
		ParseQuery.getQuery(Episode.class)
				.whereEqualTo("feed_title", name)
				.orderByDescending("pubDate")
				.fromLocalDatastore()
				.setLimit(1000)
				.findInBackground((episodes, e) -> {
					if (e == null && mAdapter != null && episodes != null && !episodes.isEmpty()) {
						mAdapter.setItems(episodes);
					}
				});

		ParseQuery.getQuery(Episode.class)
				.whereEqualTo("feed_title", name)
				.orderByDescending("pubDate")
				.setLimit(1000)
				.findInBackground((episodes, e) -> {
					if (e == null && mAdapter != null && episodes != null && !episodes.isEmpty()) {
						ParseObject.pinAllInBackground(episodes);
						mAdapter.setItems(episodes);
					} else if (e != null) {
						AppUtil.toast(this, e.getLocalizedMessage());
					}
				});
	}

	private void trackEpisodeSelected(@NonNull final String episodeTitle) {
		final Tracker t = ((SmodrApplication) getApplication()).getTracker();
		t.send(new HitBuilders.EventBuilder()
				.setCategory("EPISODE")
				.setAction("SELECTED")
				.setLabel(episodeTitle)
				.build());
	}

//	final Intent intent = new Intent(this, MediaPlaybackService.class);
//	intent.setAction(MediaPlaybackService.ACTION_PLAY);
//	intent.putExtra(MediaPlaybackService.INTENT_EPISODE_URL, item.getEnclosureUrl());
//	intent.putExtra(MediaPlaybackService.INTENT_EPISODE_TITLE, item.getTitle());
//	intent.putExtra(MediaPlaybackService.INTENT_EPISODE_DESCRIPTION, item.getDescription());
//	startService(intent);
//	trackEpisodeSelected(item.getString("title"));
}
