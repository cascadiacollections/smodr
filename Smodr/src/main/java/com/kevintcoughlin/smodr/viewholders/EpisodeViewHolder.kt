package com.kevintcoughlin.smodr.viewholders

import androidx.recyclerview.widget.RecyclerView
import com.kevintcoughlin.smodr.databinding.ItemListEpisodeLayoutBinding

class EpisodeViewHolder(
    binding: ItemListEpisodeLayoutBinding
) : RecyclerView.ViewHolder(binding.root) {
    val mTitle = binding.title
    val mDescription = binding.description
    val mMetadata = binding.metadata
}