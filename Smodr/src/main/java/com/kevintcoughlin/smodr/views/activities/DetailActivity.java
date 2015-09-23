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
import com.kevintcoughlin.smodr.models.Channel;
import com.kevintcoughlin.smodr.models.Episode;
import com.kevintcoughlin.smodr.services.MediaPlaybackService;
import com.kevintcoughlin.smodr.utils.AppUtil;
import com.kevintcoughlin.smodr.viewholders.EpisodeViewBinder;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

/**
 * {@link com.kevintcoughlin.smodr.models.Channel} detail view.
 *
 * @author kevincoughlin
 */
public final class DetailActivity extends AppCompatActivity implements BinderAdapter.OnItemClickListener {
	/**
	 * Key for a {@link com.kevintcoughlin.smodr.models.Channel}'s title.
	 */
	public static final String EXTRA_NAME = ".name";
	/**
	 * Key for a {@link com.kevintcoughlin.smodr.models.Channel}'s image url.
	 */
	public static final String EXTRA_IMAGE_URL = ".image_url";
	/**
	 * Displays a {@link List<Episode>}.
	 */
	@Bind(R.id.list) RecyclerView mRecyclerView;
	/**
	 * Displays the screen's title.
	 */
	@Bind(R.id.toolbar) Toolbar mToolbar;
	/**
	 * Contains a {@link List<Episode>} for {@link #mRecyclerView}.
	 */
	@Nullable private BinderAdapter mAdapter;
	/**
	 * The display title.
	 */
	@NonNull private String mChannelName = "Smodcast";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail_layout);
		ButterKnife.bind(this);

		final Intent intent = getIntent();
		if (intent != null) {
			mChannelName = intent.getStringExtra(EXTRA_NAME);
			final ImageView imageView = ButterKnife.findById(this, R.id.backdrop);
			Glide.with(this).load(intent.getStringExtra(EXTRA_IMAGE_URL)).centerCrop().into(imageView);
		}

		setSupportActionBar(mToolbar);
		if (getSupportActionBar() != null) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}

		final CollapsingToolbarLayout collapsingToolbar = ButterKnife.findById(this, R.id.collapsing_toolbar);
		collapsingToolbar.setTitle(mChannelName);

		mAdapter = new BinderAdapter(this);
		mAdapter.registerViewType(R.layout.item_list_episode_layout, new EpisodeViewBinder(), Episode.class);
		mAdapter.setOnItemClickListener(this);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		mRecyclerView.setAdapter(mAdapter);
		refresh(mChannelName);
	}

	@Override
	public void onItemClick(@NonNull Object item) {
		final Episode episode = (Episode) item;
		final Intent intent = new Intent(this, MediaPlaybackService.class);
		intent.setAction(MediaPlaybackService.ACTION_PLAY);
		intent.putExtra(MediaPlaybackService.INTENT_EPISODE_URL, episode.getEnclosureUrl());
		intent.putExtra(MediaPlaybackService.INTENT_EPISODE_TITLE, episode.getTitle());
		intent.putExtra(MediaPlaybackService.INTENT_EPISODE_DESCRIPTION, episode.getDescription());
		startService(intent);
		trackEpisodeSelected(episode.getTitle());
	}

	/**
	 * Refreshes the {@link #mAdapter}'s {@link List<Episode>}.
	 *
	 * @param name
	 *     the {@link Channel} name to query.
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

	/**
	 * Sends an event when an {@link Episode} is selected.
	 *
	 * @param episodeTitle
	 *      the title of the {@link Episode} selected.
	 */
	private void trackEpisodeSelected(@NonNull final String episodeTitle) {
		final Tracker t = ((SmodrApplication) getApplication()).getTracker();
		t.send(new HitBuilders.EventBuilder()
				.setCategory("EPISODE")
				.setAction("SELECTED")
				.setLabel(episodeTitle)
				.build());
	}
}
