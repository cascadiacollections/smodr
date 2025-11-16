package com.kevintcoughlin.smodr.viewholders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import com.cascadiacollections.jamoka.adapter.BinderRecyclerAdapter
import com.kevintcoughlin.smodr.R
import com.kevintcoughlin.smodr.databinding.ItemListEpisodeLayoutBinding
import com.kevintcoughlin.smodr.models.Item
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Binds an Episode (Item) to an EpisodeViewHolder for display in a RecyclerView.
 *
 * This binder uses ViewBinding and Material Design guidelines. It formats episode metadata
 * and summary using HTML compatibility and localized date formatting. The adapter is intended
 * for use with BinderRecyclerAdapter and supports DiffUtil for efficient list updates.
 *
 * @see BinderRecyclerAdapter
 * @see EpisodeViewHolder
 * @see Item
 */
class EpisodeView : BinderRecyclerAdapter.ViewHolderBinder<Item, EpisodeViewHolder> {

    /**
     * Binds the episode data to the view holder's views.
     *
     * @param model The episode item to bind.
     * @param viewHolder The view holder containing the views.
     * @param position The position in the adapter.
     */
    override fun bind(model: Item, viewHolder: EpisodeViewHolder, position: Int) =
        viewHolder.binding.render(model)

    /**
     * Creates a new EpisodeViewHolder using ViewBinding.
     *
     * @param parent The parent ViewGroup.
     * @param viewType The view type (unused).
     * @return A new EpisodeViewHolder instance.
     */
    override fun createViewHolder(parent: ViewGroup, viewType: Int): EpisodeViewHolder {
        val binding = ItemListEpisodeLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EpisodeViewHolder(binding)
    }

    private fun ItemListEpisodeLayoutBinding.render(model: Item) {
        title.text = model.title
        description.text = HtmlCompat.fromHtml(model.summary.orEmpty(), HtmlCompat.FROM_HTML_MODE_LEGACY)
        metadata.text = root.resources.getString(
            R.string.metadata,
            formatDate(model.pubDate),
            model.duration.orEmpty(),
        )
    }

    companion object {
        private val dateFormatInput = DateTimeFormatter.RFC_1123_DATE_TIME

        private fun dateFormatOutput(): DateTimeFormatter =
            DateTimeFormatter.ofPattern("dd MMM", Locale.getDefault())

        /**
         * Formats a date string to a localized display format (e.g., "15 Nov").
         * Returns an empty string if parsing fails or input is null/empty.
         *
         * @param dateTimeString The RFC822 date string to format.
         * @return A localized date string or empty string if invalid.
         */
        fun formatDate(dateTimeString: String?): String {
            if (dateTimeString.isNullOrBlank()) return ""
            return runCatching {
                ZonedDateTime.parse(dateTimeString, dateFormatInput).format(dateFormatOutput())
            }.getOrDefault("")
        }
    }
}
