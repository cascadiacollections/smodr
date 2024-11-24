package com.kevintcoughlin.smodr.models

import android.net.Uri
import android.os.Bundle
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml
import java.util.*

@Entity
@Xml(name = "item")
class Item : IMediaPlayback {
    @PrimaryKey
    @ColumnInfo(name = "guid")
    @field:Element
    var guid: String = ""

    @ColumnInfo(name = "title")
    @field:Element
    var title: String? = null

    @ColumnInfo(name ="pubDate")
    @field:Element
    var pubDate: String? = null

    @ColumnInfo(name ="description")
    @field:Element
    var description: String? = null

    @ColumnInfo(name ="duration")
    @field:Element
    var duration: String? = null

    @ColumnInfo(name ="summary")
    @field:Element
    var summary: String? = null

    @ColumnInfo(name ="origEnclosureLink")
    @field:Element
    var origEnclosureLink: String? = null

    @ColumnInfo(name ="completed")
    var completed: Boolean = false

    override val uri: Uri?
        get() {
            if (origEnclosureLink != null) {
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