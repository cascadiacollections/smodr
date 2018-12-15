package com.kevintcoughlin.smodr.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.kevintcoughlin.smodr.R;
import com.kevintcoughlin.smodr.models.Item;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A detail view for a given {@link Item} containing cover art
 * and a @link List<Episode>}.
 *
 * @author kevincoughlin
 */
public final class DetailActivity extends AppCompatActivity {
    /**
     * Key for a {@link com.kevintcoughlin.smodr.models.Item}'s title.
     */
    public static final String EXTRA_NAME = ".name";
//    /**
//     * Key for a {@link com.kevintcoughlin.smodr.models.Item}'s image url.
//     */
//    public static final String EXTRA_IMAGE_URL = ".image_url";
    /**
     * Displays a {@link List<Item>}.
     */
    @Bind(R.id.list)
    RecyclerView mRecyclerView;
    /**
     * Displays the screen's title.
     */
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    /**
     * The display title.
     */
    @NonNull
    private String mChannelName = "Smodcast";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_layout);
        ButterKnife.bind(this);

        final Intent intent = getIntent();
        if (intent != null) {
            mChannelName = intent.getStringExtra(EXTRA_NAME);
//            final SimpleDraweeView imageView = ButterKnife.findById(this, R.id.backdrop);
//            imageView.setImageURI(Uri.parse(intent.getStringExtra(EXTRA_IMAGE_URL)));
        }

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        final CollapsingToolbarLayout collapsingToolbar = ButterKnife.findById(this, R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(mChannelName);

//        BinderAdapter mAdapter = new BinderAdapter(this);
//		mAdapter.registerViewType(R.layout.item_list_episode_layout, new EpisodeViewBinder(), Item.class);
//        mAdapter.setOnItemClickListener(this);
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        mRecyclerView.setAdapter(mAdapter);
    }

//    @Override
//    public void onItemClick(@NonNull Object item) {
//        final Item episode = (Item) item;
//        final Intent intent = new Intent(this, MediaPlaybackService.class);
//        intent.setAction(MediaPlaybackService.ACTION_PLAY);
//        intent.putExtra(MediaPlaybackService.INTENT_EPISODE_URL, episode.enclosure.url);
//        intent.putExtra(MediaPlaybackService.INTENT_EPISODE_TITLE, episode.title);
//        intent.putExtra(MediaPlaybackService.INTENT_EPISODE_DESCRIPTION, episode.description);
//        startService(intent);
//    }
}
