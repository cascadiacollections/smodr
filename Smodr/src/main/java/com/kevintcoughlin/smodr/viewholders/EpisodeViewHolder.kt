package com.kevintcoughlin.smodr.viewholders;

import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.kevintcoughlin.smodr.databinding.ItemListEpisodeLayoutBinding;

public final class EpisodeViewHolder extends RecyclerView.ViewHolder {
    protected TextView mTitle;
    protected TextView mMetadata;
    protected TextView mDescription;

    EpisodeViewHolder(final ItemListEpisodeLayoutBinding binding) {
        super(binding.getRoot());

        mTitle = binding.title;
        mMetadata = binding.metadata;
        mDescription = binding.description;
    }
}
