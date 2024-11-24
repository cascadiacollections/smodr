package com.kevintcoughlin.smodr.viewholders

import android.annotation.SuppressLint
import android.graphics.Color
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import com.cascadiacollections.jamoka.adapter.BinderRecyclerAdapter
import com.kevintcoughlin.smodr.R
import com.kevintcoughlin.smodr.databinding.ItemListEpisodeLayoutBinding
import com.kevintcoughlin.smodr.models.Item
import java.text.SimpleDateFormat
import java.util.Locale

class EpisodeView : BinderRecyclerAdapter.Binder<Item?, EpisodeViewHolder?> {

    override fun bind(model: Item, viewHolder: EpisodeViewHolder) {
        with(viewHolder) {
            mTitle.text = model.title
            mDescription.text = Html.fromHtml(model.summary, Html.FROM_HTML_MODE_LEGACY)
            mMetadata.text = mMetadata.context.getString(
                R.string.metadata,
                formatDate(model.pubDate),
                model.duration
            )

            val textColor = if (model.completed) COLOR_GRAY else COLOR_BLACK
            mTitle.setTextColor(textColor)
            mDescription.setTextColor(textColor)
            mMetadata.setTextColor(textColor)
        }
    }

    override fun createViewHolder(parent: ViewGroup): EpisodeViewHolder {
        val binding = ItemListEpisodeLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EpisodeViewHolder(binding)
    }

    companion object {
        private val DATE_FORMAT_INPUT = SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.US)
        @SuppressLint("ConstantLocale")
        private val DATE_FORMAT_OUTPUT = SimpleDateFormat("dd MMM", Locale.getDefault())

        private const val COLOR_BLACK = Color.BLACK
        private val COLOR_GRAY = Color.rgb(150, 150, 150)

        /**
         * Formats a date string to a localized display format.
         * Returns an empty string if parsing fails.
         */
        fun formatDate(dateTimeString: String?): String {
            return try {
                val date = dateTimeString?.let { DATE_FORMAT_INPUT.parse(it) }
                date?.let { DATE_FORMAT_OUTPUT.format(it) } ?: ""
            } catch (e: Exception) {
                ""
            }
        }
    }
}