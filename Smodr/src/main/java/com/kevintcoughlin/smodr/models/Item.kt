package com.kevintcoughlin.smodr.models;

data class Item(val title: String, val description: String, val link: String, val author: String, val date: String, val image: Image, val enclosures: List<Enclosure>)
