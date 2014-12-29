package com.kevintcoughlin.smodr.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.kevintcoughlin.smodr.R;
import com.kevintcoughlin.smodr.models.Channel;
import com.kevintcoughlin.smodr.ui.ChannelView;
import com.kevintcoughlin.smodr.ui.presenters.ChannelPresenter;
import com.kevintcoughlin.smodr.ui.presenters.ChannelPresenterImpl;
import com.kevintcoughlin.smodr.ui.presenters.mapper.IndividualChannelMapper;
import com.kevintcoughlin.smodr.util.PaletteBitmapTarget;
import com.kevintcoughlin.smodr.util.PaletteBitmapTranscoder;
import com.kevintcoughlin.smodr.util.PaletteBitmapWrapper;
import com.kevintcoughlin.smodr.util.PaletteUtils;
import com.melnykov.fab.FloatingActionButton;

import org.parceler.Parcels;

public class ChannelActivity extends ActionBarActivity implements ChannelView, IndividualChannelMapper {
    public static final String TAG = ChannelActivity.class.getSimpleName();
    private ChannelPresenter mChannelPresenter;
    private Toolbar mToolbar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ListView mListView;
    private View mHeaderInfoView;
    private ImageView mHeaderImageView;
    private TextView mDescriptionTextView;
    private FloatingActionButton mFavouriteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);

        mChannelPresenter = new ChannelPresenterImpl(this, this);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
        mListView = (ListView) findViewById(R.id.listView);

        mHeaderInfoView = LayoutInflater.from(this).inflate(R.layout.header_channel_info, null);
        mHeaderImageView = (ImageView) mHeaderInfoView.findViewById(R.id.headerImageView);
        mDescriptionTextView = (TextView) mHeaderInfoView.findViewById(R.id.descriptionTextView);
        mFavouriteButton = (FloatingActionButton) mHeaderInfoView.findViewById(R.id.favouriteButton);

        mChannelPresenter.handleInitialData(getIntent());
        mChannelPresenter.initializeViews();
        mChannelPresenter.initializeData();
    }

    @Override
    public void initializeToolbar() {
        mToolbar.setTitle(R.string.app_name);
        mToolbar.setBackgroundColor(getResources().getColor(R.color.orange));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void initializeSwipeRefreshLayout() {
        mSwipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 64, getResources().getDisplayMetrics()));
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.orange));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mChannelPresenter.onSwipeRefresh();
            }
        });
    }

    @Override
    public void initializeFavouriteButton(boolean isFavourite) {
        if (isFavourite) {
            mFavouriteButton.setImageResource(R.drawable.ic_favourite_white_24dp);
        } else {
            mFavouriteButton.setImageResource(R.drawable.ic_favourite_outline_white_24dp);
        }
        mFavouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChannelPresenter.onFavourite();
            }
        });
    }

    @Override
    public void showRefreshing() {
        mSwipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void hideRefreshing() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void setTitle(String title) {
        mToolbar.setTitle(title);
    }

    @Override
    public void setDescription(String description) {
        mDescriptionTextView.setText(description);
    }

    @Override
    public void setThumbnail(String url) {
        if (mHeaderImageView != null) {
            // @TODO: Fix and move this channel access
            final Channel channel = Parcels.unwrap(getIntent().getParcelableExtra("channel"));
            final int coverPhotoResource = getResources().getIdentifier(channel.getShortName().replace("-", ""), "drawable", getPackageName());
            Glide.with(this)
                    .load(coverPhotoResource)
                    .asBitmap()
                    .transcode(new PaletteBitmapTranscoder(), PaletteBitmapWrapper.class)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .animate(android.R.anim.fade_in)
                    .into(new PaletteBitmapTarget(mHeaderImageView) {
                        @Override
                        public void onResourceReady(PaletteBitmapWrapper resource, GlideAnimation<? super PaletteBitmapWrapper> glideAnimation) {
                            super.onResourceReady(resource, glideAnimation);
                            mHeaderImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        }
                    });
        }
    }

    @Override
    public void setFavouriteButton(boolean isFavourite) {
        if (isFavourite) {
            mFavouriteButton.setImageResource(R.drawable.ic_action_pause);
        } else {
            mFavouriteButton.setImageResource(R.drawable.ic_action_play);
        }
    }

    @Override
    public void toastMangaError() {
        Toast.makeText(this, "Error loading channel.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public Context getContext() {
        return this;
    }

    // @TODO: RecyclerView
    @Override
    public void initializeRecyclerView() {
        mListView.addHeaderView(mHeaderInfoView, null, false);
    }

    @Override
    public void registerAdapter(ListAdapter adapter) {
        mListView.setAdapter(adapter);
    }
}
