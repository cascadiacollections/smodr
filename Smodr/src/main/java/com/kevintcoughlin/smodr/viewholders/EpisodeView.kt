package com.kevintcoughlin.smodr.viewholders

import android.annotation.SuppressLint
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import com.cascadiacollections.jamoka.adapter.BinderRecyclerAdapter
import com.kevintcoughlin.smodr.R
import com.kevintcoughlin.smodr.databinding.ItemListEpisodeLayoutBinding
import com.kevintcoughlin.smodr.models.Item
import java.text.SimpleDateFormat
import java.util.Locale

class EpisodeView : BinderRecyclerAdapter.Binder<Item, EpisodeViewHolder> {

    override fun bind(model: Item, viewHolder: EpisodeViewHolder) {
        with(viewHolder) {
            mTitle.text = model.title
            mDescription.text = Html.fromHtml(model.summary, Html.FROM_HTML_MODE_LEGACY)
            mMetadata.text = mMetadata.context.getString(
                R.string.metadata,
                formatDate(model.pubDate),
                model.duration
            )
        }
    }

    override fun createViewHolder(parent: ViewGroup): EpisodeViewHolder =
        ItemListEpisodeLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ).let { EpisodeViewHolder(it) }

    companion object {
        private val DATE_FORMAT_INPUT = SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.US)
        @SuppressLint("ConstantLocale")
        private val DATE_FORMAT_OUTPUT = SimpleDateFormat("dd MMM", Locale.getDefault())

        /**
         * Formats a date string to a localized display format.
         * Returns an empty string if parsing fails.
         */
        fun formatDate(dateTimeString: String?): String =
            dateTimeString?.let {
                runCatching { DATE_FORMAT_INPUT.parse(it) }
                    .mapCatching { DATE_FORMAT_OUTPUT.format(it!!) }
                    .getOrDefault("")
            } ?: ""
    }
}