package com.kevintcoughlin.smodr.models

import android.net.Uri
import android.os.Bundle
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml
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
) : IMediaPlayback {
    override val uri: Uri?
        get() = origEnclosureLink?.let { Uri.parse(it.replace("http://", "https://")) }
    /**
     * Creates a bundle with the item's GUID and title for event handling.
     */
    fun toEventBundle(): Bundle = Bundle().apply {
        putString("guid", guid)
        putString("title", title)
    }
}