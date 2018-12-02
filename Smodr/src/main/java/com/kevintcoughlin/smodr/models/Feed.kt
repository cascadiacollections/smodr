package com.kevintcoughlin.smodr.models

data class Feed<T>(val title: String, val url: String, val items: List<T>)
