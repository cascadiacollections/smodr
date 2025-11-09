package com.kevintcoughlin.smodr.models

/**
 * Represents a podcast feed containing a channel with episodes.
 *
 * @property channel The channel information and episodes for this feed
 */
data class Feed(
    val channel: Channel = Channel()
)