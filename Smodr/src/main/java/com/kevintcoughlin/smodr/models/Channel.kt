package com.kevintcoughlin.smodr.models

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "channel")
data class Channel(
    @field:PropertyElement var title: String? = null,
    @field:PropertyElement var link: String? = null,
    @field:Element var items: List<Item> = emptyList()
)