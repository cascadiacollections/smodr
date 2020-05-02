package com.kevintcoughlin.smodr.viewholders;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.kevintcoughlin.smodr.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class EpisodeViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.title)
    TextView mTitle;
    @Bind(R.id.metadata)
    TextView mMetadata;
    @Bind(R.id.description)
    TextView mDescription;

    EpisodeViewHolder(final View view) {
        super(view);
        ButterKnife.bind(this, view);
    }
}
