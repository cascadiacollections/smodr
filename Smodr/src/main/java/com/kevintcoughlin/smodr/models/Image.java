package com.kevintcoughlin.smodr.models;

import org.parceler.Parcel;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 * Model representing an RSS feed item's image.
 *
 * @author kevincoughlin
 */
@Parcel
@Root(name = "image", strict = false)
public final class Image {
	@Attribute(name = "href", required = false)
	public String href;

	public Image() {
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}
}
