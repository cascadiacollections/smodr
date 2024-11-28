package com.kevintcoughlin.smodr.models

import android.net.Uri
import android.os.Bundle
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

sealed interface MediaPlayback {
    val uri: Uri?
}

inline fun <reified T> T.toBundle(mapper: Bundle.(T) -> Unit): Bundle {
    return Bundle().apply { mapper(this@toBundle) }
}

@Xml(name = "item")
data class Item(
    @field:Element var guid: String = "",
    @field:Element var title: String? = null,
    @field:Element var pubDate: String? = null,
    @field:Element var description: String? = null,
    @field:Element var duration: String? = null,
    @field:Element var summary: String? = null,
    @field:Element var origEnclosureLink: String? = null,
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