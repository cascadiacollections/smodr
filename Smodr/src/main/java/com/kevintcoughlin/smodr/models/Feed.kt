package com.kevintcoughlin.smodr.models

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "rss")
class Feed {
    @field:Element
    var channel: Channel? = null
}