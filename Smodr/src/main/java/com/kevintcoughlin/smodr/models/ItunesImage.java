package com.kevintcoughlin.smodr.models;

import org.parceler.Parcel;
import org.simpleframework.xml.Attribute;

@Parcel
public final class ItunesImage {

	@Attribute(required = false)
	private String href;

	public ItunesImage() {
	}

	public String getHref() {
		return href;
	}

}
