package com.kevintcoughlin.smodr.models

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml
class Rss {
    @field:Element
    var channel: Channel? = null
}