package com.kevintcoughlin.smodr.viewholders;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.kevintcoughlin.smodr.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public final class EpisodeViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.metadata)
    TextView mMetadata;
    @BindView(R.id.description)
    TextView mDescription;

    EpisodeViewHolder(final View view) {
        super(view);
        ButterKnife.bind(this, view);
    }
}
