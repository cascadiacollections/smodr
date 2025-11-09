package com.kevintcoughlin.smodr.models

import android.net.Uri
import android.os.Bundle

/**
 * Sealed interface representing any media that can be played.
 */
sealed interface MediaPlayback {
    val uri: Uri?
}

/**
 * Extension function to convert any object to a Bundle using a mapper function.
 *
 * @param T The type of the object to convert
 * @param mapper Lambda function that defines how to map object properties to Bundle entries
 * @return A Bundle containing the mapped data
 */
inline fun <reified T> T.toBundle(mapper: Bundle.(T) -> Unit): Bundle {
    return Bundle().apply { mapper(this@toBundle) }
}

/**
 * Represents a podcast episode or media item.
 *
 * @property guid Unique identifier for the item
 * @property title The title of the episode
 * @property pubDate Publication date string
 * @property description Full description of the episode
 * @property duration Duration string (e.g., "45 min")
 * @property summary HTML summary of the episode
 * @property origEnclosureLink URL to the media file
 * @property completed Whether the episode has been fully played
 */
data class Item @JvmOverloads constructor(
    val guid: String = "",
    val title: String? = null,
    val pubDate: String? = null,
    val description: String? = null,
    val duration: String? = null,
    val summary: String? = null,
    val origEnclosureLink: String? = null,
    val completed: Boolean = false
) : MediaPlayback {
    
    /**
     * Parses the origEnclosureLink and returns a valid HTTPS Uri, or null if invalid.
     * HTTP links are automatically upgraded to HTTPS.
     */
    override val uri: Uri?
        get() = origEnclosureLink?.run {
            when {
                startsWith("https://") -> Uri.parse(this)
                startsWith("http://") -> Uri.parse("https://${substring(7)}")
                else -> null
            }
        }

    /**
     * Converts this Item into a Bundle for event logging or serialization.
     *
     * @return Bundle containing all item properties
     */
    fun toEventBundle(): Bundle = toBundle {
        putString("guid", guid)
        putString("title", title)
        putString("pubDate", pubDate)
        putString("description", description)
        putString("duration", duration)
        putString("summary", summary)
        putString("origEnclosureLink", origEnclosureLink)
        putBoolean("completed", completed)
    }
}