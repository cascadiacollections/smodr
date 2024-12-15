package com.kevintcoughlin.smodr.models

data class Channel(
    var title: String? = null,
    var link: String? = null,
    var items: List<Item> = emptyList()
)