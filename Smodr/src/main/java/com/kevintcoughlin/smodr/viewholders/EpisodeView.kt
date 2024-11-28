package com.kevintcoughlin.smodr.viewholders

import android.annotation.SuppressLint
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import com.kevintcoughlin.smodr.R
import com.kevintcoughlin.smodr.databinding.ItemListEpisodeLayoutBinding
import com.kevintcoughlin.smodr.models.Item
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Implementation of ItemBinder for binding Episode data to EpisodeViewHolder.
 */
class EpisodeView : BinderRecyclerAdapter.ItemBinder<Item, EpisodeViewHolder> {

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

    override fun createViewHolder(parent: ViewGroup, viewType: Int): EpisodeViewHolder {
        val binding = ItemListEpisodeLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EpisodeViewHolder(binding)
    }

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