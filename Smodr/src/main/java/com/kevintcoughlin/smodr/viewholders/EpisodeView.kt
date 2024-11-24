package com.kevintcoughlin.smodr.viewholders

import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.cascadiacollections.jamoka.adapter.BinderRecyclerAdapter
import com.kevintcoughlin.smodr.R
import com.kevintcoughlin.smodr.databinding.ItemListEpisodeLayoutBinding
import com.kevintcoughlin.smodr.models.Item
import com.kevintcoughlin.smodr.utils.StringResourceUtilities.getString
import java.text.ParseException
import java.util.Date
import java.util.Locale

class EpisodeView :
    BinderRecyclerAdapter.Binder<Item?, EpisodeViewHolder?> {
    override fun bind(model: Item, viewHolder: EpisodeViewHolder) {
        viewHolder.mTitle.text = model.title
        viewHolder.mDescription.text =
            Html.fromHtml(model.summary, Html.FROM_HTML_MODE_LEGACY)
        viewHolder.mMetadata.text = getString(
            viewHolder.mMetadata.context,
            R.string.metadata,
            formatDate(model.pubDate),
            model.duration
        )

        if (model.completed) {
            // @todo extension method?
            viewHolder.mTitle.setTextColor(COLOR_GRAY)
            viewHolder.mDescription.setTextColor(COLOR_GRAY)
            viewHolder.mMetadata.setTextColor(COLOR_GRAY)
        } else {
            viewHolder.mTitle.setTextColor(COLOR_BLACK)
            viewHolder.mDescription.setTextColor(COLOR_BLACK)
            viewHolder.mMetadata.setTextColor(COLOR_BLACK)
        }
    }

    override fun createViewHolder(parent: ViewGroup): EpisodeViewHolder {
        return EpisodeViewHolder(
            ItemListEpisodeLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    companion object {
        // @todo: Date format locale aware
        private val format = SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.US)

        // @todo: Date format locale aware
        private val format2 = SimpleDateFormat("dd MMM", Locale.US)

        // @todo: theme
        private val COLOR_BLACK = Color.rgb(0, 0, 0)
        private val COLOR_GRAY = Color.rgb(222, 222, 222)

        private fun formatDate(dateTimeString: String?): String {
            // @todo: optimize
            var date: Date? = null
            var dateString = ""
            try {
                date = format.parse(dateTimeString)
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            if (date != null) {
                dateString = format2.format(date)
            }

            return dateString
        }
    }
}