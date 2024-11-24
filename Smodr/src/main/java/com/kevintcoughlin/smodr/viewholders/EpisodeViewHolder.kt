package com.kevintcoughlin.smodr.viewholders

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kevintcoughlin.smodr.databinding.ItemListEpisodeLayoutBinding

class EpisodeViewHolder internal constructor(binding: ItemListEpisodeLayoutBinding) :
    RecyclerView.ViewHolder(binding.root) {
    @JvmField
    var mTitle: TextView = binding.title
    @JvmField
    var mMetadata: TextView = binding.metadata
    @JvmField
    var mDescription: TextView = binding.description
}
