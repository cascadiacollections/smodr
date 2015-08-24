package com.kevintcoughlin.smodr.models;

import org.parceler.Parcel;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 * Model representing an RSS feed item's media enclosure.
 *
 * @author kevincoughlin
 */
@Parcel
@Root(name = "enclosure", strict = false)
public final class Enclosure {
    @Attribute(name = "url", required = false)
    private String url;

	public Enclosure() {
	}

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
