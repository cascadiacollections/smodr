package com.kevintcoughlin.smodr.viewholders

import BinderRecyclerAdapter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import com.kevintcoughlin.smodr.R
import com.kevintcoughlin.smodr.databinding.ItemListEpisodeLayoutBinding
import com.kevintcoughlin.smodr.models.Item
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Implementation of ViewHolderBinder for binding Episode (Item) data to EpisodeViewHolder.
 */
class EpisodeView : BinderRecyclerAdapter.ViewHolderBinder<Item, EpisodeViewHolder> {

    override fun bind(model: Item, viewHolder: EpisodeViewHolder, position: Int) {
        viewHolder.binding.title.text = model.title
        viewHolder.binding.description.text = HtmlCompat.fromHtml(model.summary.orEmpty(), HtmlCompat.FROM_HTML_MODE_LEGACY)
        viewHolder.binding.metadata.text = viewHolder.itemView.context.getString(
            R.string.metadata,
            formatDate(model.pubDate),
            model.duration
        )
    }

    override fun createViewHolder(parent: ViewGroup, viewType: Int): EpisodeViewHolder {
        val binding = ItemListEpisodeLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EpisodeViewHolder(binding)
    }

    companion object {
        private val dateFormatInput = ThreadLocal.withInitial {
            SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.US)
        }

        private val dateFormatOutput = ThreadLocal.withInitial {
            SimpleDateFormat("dd MMM", Locale.getDefault())
        }

        /**
         * Formats a date string to a localized display format.
         * Returns an empty string if parsing fails.
         */
        fun formatDate(dateTimeString: String?): String {
            if (dateTimeString.isNullOrEmpty()) return ""

            return try {
                dateFormatInput.get()?.parse(dateTimeString)?.let { date ->
                    dateFormatOutput.get()?.format(date)
                } ?: ""
            } catch (e: Exception) {
                "" // Handle parsing errors gracefully
            }
        }
    }
}