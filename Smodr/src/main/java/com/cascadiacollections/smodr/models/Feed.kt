package com.cascadiacollections.smodr.models

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "rss", strict = false)
class Feed {
    @field:Element
    var channel: Channel? = null
}