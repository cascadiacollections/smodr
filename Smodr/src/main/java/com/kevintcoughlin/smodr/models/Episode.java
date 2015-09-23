package com.kevintcoughlin.smodr.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Model for a {@link Channel}'s episode.
 *
 * @author kevincoughlin
 */
@ParseClassName("Item")
public final class Episode extends ParseObject {
	/**
	 * Key for a {@link Episode}'s RSS feed title.
	 */
	public static final String FEED_TITLE = "feed_title";
	/**
	 * Key for a {@link Episode}'s publish date.
	 */
	public static final String PUB_DATE = "pubDate";

	public Episode() {
	}

	public String getTitle() {
		return getString("title");
	}

	public String getDescription() {
		return getString("description");
	}

	public String getEnclosureUrl() {
		return getString("enclosure_url");
	}
}
