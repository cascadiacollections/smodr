package com.cascadiacollections.smodr.models

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "channel", strict = false)
class Channel {
    internal constructor() {}
    constructor(title: String?, link: String?) {
        this.title = title
        this.link = link
    }

    @field:Element
    var title: String? = null

    @field:Element(required = false)
    var link: String? = null

    @field:ElementList(inline = true, entry = "item")
    var item: List<Item>? = null
}