package com.kevintcoughlin.smodr.models

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "channel")
class Channel {
    internal constructor() {}
    constructor(title: String?, link: String?) {
        this.title = title
        this.link = link
    }

    @field:PropertyElement
    var title: String? = null

    @field:PropertyElement
    var link: String? = null

    @field:Element
    var item: List<Item>? = null
}