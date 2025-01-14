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
 * Implementation of ViewHolderBinder for binding Episode data to EpisodeViewHolder.
 */
class EpisodeView : BinderRecyclerAdapter.ViewHolderBinder<Item, EpisodeViewHolder> {

    override fun bind(model: Item, viewHolder: EpisodeViewHolder) = with(viewHolder.binding) {
        title.text = model.title
        description.text = HtmlCompat.fromHtml(model.summary.orEmpty(), HtmlCompat.FROM_HTML_MODE_LEGACY)
        metadata.text = metadata.context.getString(
            R.string.metadata,
            formatDate(model.pubDate),
            model.duration
        )
    }

    override fun createViewHolder(parent: ViewGroup, viewType: Int): EpisodeViewHolder =
        EpisodeViewHolder(ItemListEpisodeLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    companion object {
        private val dateFormatInput: ThreadLocal<SimpleDateFormat> = ThreadLocal.withInitial {
            SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.US)
        }

        private val dateFormatOutput: ThreadLocal<SimpleDateFormat> = ThreadLocal.withInitial {
            SimpleDateFormat("dd MMM", Locale.getDefault())
        }

        /**
         * Formats a date string to a localized display format.
         * Returns an empty string if parsing fails.
         */
        fun formatDate(dateTimeString: String?): String {
            if (dateTimeString.isNullOrEmpty()) return ""

            return dateFormatInput.get()?.let { inputFormatter ->
                dateFormatOutput.get()?.let { outputFormatter ->
                    runCatching {
                        inputFormatter.parse(dateTimeString)?.let { date ->
                            outputFormatter.format(date)
                        }
                    }.getOrDefault("")
                }
            } ?: ""
        }
    }
}