package com.cascadiacollections.smodr.models

import android.net.Uri
import android.os.Bundle
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.simpleframework.xml.Element
import org.simpleframework.xml.Namespace
import org.simpleframework.xml.Root
import java.util.*

@Entity
@Root(name = "item", strict = false)
class Item : IMediaPlayback {
    @PrimaryKey
    @ColumnInfo(name = "guid")
    @field:Element(required = false)
    @NonNull
    var guid: String? = null

    @ColumnInfo(name = "title")
    @field:Element(required = false)
    @NonNull
    var title: String? = null

    @ColumnInfo(name ="pubDate")
    @field:Element(required = false)
    @NonNull
    var pubDate: String? = null

    @ColumnInfo(name ="description")
    @field:Element(required = false)
    @NonNull
    var description: String? = null

    @ColumnInfo(name ="duration")
    @field:Element
    @field:Namespace(prefix = "itunes")
    @NonNull
    var duration: String? = null

    @ColumnInfo(name ="summary")
    @field:Element
    @field:Namespace(prefix = "itunes")
    @NonNull
    var summary: String? = null

    @ColumnInfo(name ="origEnclosureLink")
    @field:Element
    @field:Namespace(prefix = "feedburner")
    @NonNull
    var origEnclosureLink: String? = null

    @ColumnInfo(name ="completed")
    @NonNull
    var completed: Boolean = false

    override val uri: Uri?
        @Nullable
        get() {
            if (origEnclosureLink != null) {
                // @todo
                val uriString = origEnclosureLink!!.replace("http://", "https://")
                return Uri.parse(uriString)
            }
            return null
        }

    @NonNull
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

    @NonNull
    override fun hashCode(): Int {
        return Objects.hash(title, pubDate, description, duration, summary, origEnclosureLink)
    }

    fun eventBundle(): Bundle {
        val bundle = Bundle()

        bundle.putString("guid", guid)
        bundle.putString("title", title)

        return bundle
    }

    companion object {
        @JvmStatic
        @NonNull
        fun create(item: Item, completed: Boolean): Item {
            val newItem = Item()

            newItem.title = item.title
            newItem.description = item.description
            newItem.duration = item.duration
            newItem.origEnclosureLink = item.origEnclosureLink
            newItem.pubDate = item.pubDate
            newItem.summary = item.summary
            newItem.completed = completed

            return newItem
        }
    }
}