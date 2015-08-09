package com.kevintcoughlin.smodr.models;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(name = "image", strict = false)
public final class Image {

	@Attribute(name = "href", required = false)
	public String href;

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}
}
