package com.kevintcoughlin.smodr.models

/**
 * Represents a podcast channel containing metadata and episodes.
 *
 * @property title The title of the podcast channel
 * @property link The URL link to the podcast channel
 * @property items The list of episodes/items in this channel
 */
data class Channel(
    val title: String? = null,
    val link: String? = null,
    val items: List<Item> = emptyList()
)