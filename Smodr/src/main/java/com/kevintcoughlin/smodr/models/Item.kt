package com.kevintcoughlin.smodr.models

import android.net.Uri
import android.os.Bundle

sealed interface MediaPlayback {
    val uri: Uri?
}

inline fun <reified T> T.toBundle(mapper: Bundle.(T) -> Unit): Bundle {
    return Bundle().apply { mapper(this@toBundle) }
}

data class Item(
    var guid: String = "",
    var title: String? = null,
    var pubDate: String? = null,
    var description: String? = null,
    var duration: String? = null,
    var summary: String? = null,
    var origEnclosureLink: String? = null,
    var completed: Boolean = false
) : MediaPlayback {
    override val uri: Uri?
        get() = origEnclosureLink?.let { link ->
            when {
                link.startsWith("https://") -> Uri.parse(link)
                link.startsWith("http://") -> Uri.parse("https://" + link.substring(7))
                else -> null
            }
        }

    /**
     * Converts the Item object into a Bundle using the generic toBundle function.
     */
    fun toEventBundle(): Bundle = toBundle { item ->
        putString("guid", item.guid)
        putString("title", item.title)
        putString("pubDate", item.pubDate)
        putString("description", item.description)
        putString("duration", item.duration)
        putString("summary", item.summary)
        putString("origEnclosureLink", item.origEnclosureLink)
        putBoolean("completed", item.completed)
    }
}