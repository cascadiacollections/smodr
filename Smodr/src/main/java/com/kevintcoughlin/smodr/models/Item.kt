package com.kevintcoughlin.smodr.models

import android.net.Uri
import android.os.Build
import android.text.Html
import android.text.Spanned
import org.simpleframework.xml.Element
import org.simpleframework.xml.Namespace
import org.simpleframework.xml.Root
import java.util.*

@Root(name = "item", strict = false)
class Item : IMediaPlayback {
    private val _http = "http://"
    private val _https = "https://"

    fun sanitizedHtml(): Spanned {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(summary, Html.FROM_HTML_MODE_COMPACT)
        } else {
            Html.fromHtml(summary)
        }
    }

    @field:Element(required = false)
    var guid: String? = null

    @field:Element(required = false)
    var title: String? = null

    @field:Element(required = false)
    var pubDate: String? = null

    @field:Element(required = false)
    var description: String? = null

    @field:Element
    @field:Namespace(prefix = "itunes")
    var duration: String? = null

    @field:Element
    @field:Namespace(prefix = "itunes")
    var summary: String? = null

    @field:Element
    @field:Namespace(prefix = "feedburner")
    var origEnclosureLink: String? = null

    var completed: Boolean = false;

    override val uri: Uri?
        get() {
            if (origEnclosureLink != null) {
                // @todo
                val uriString = origEnclosureLink!!.replace(_http, _https)
                return Uri.parse(uriString)
            }
            return null
        }

    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }
        if (javaClass != other.javaClass) {
            return false
        }
        val item = other as Item
        return title == item.title &&
                pubDate == item.pubDate &&
                description == item.description &&
                duration == item.duration &&
                summary == item.summary &&
                origEnclosureLink == item.origEnclosureLink
    }

    override fun hashCode(): Int {
        return Objects.hash(title, pubDate, description, duration, summary, origEnclosureLink)
    }

    companion object {
        @JvmStatic
        fun create(item: Item, completed: Boolean): Item {
            val newItem = Item()

            newItem.title = item.title;
            newItem.description = item.description;
            newItem.duration = item.duration;
            newItem.origEnclosureLink = item.origEnclosureLink;
            newItem.pubDate = item.pubDate;
            newItem.summary = item.summary;
            newItem.completed = completed;

            return newItem;
        }
    }
}