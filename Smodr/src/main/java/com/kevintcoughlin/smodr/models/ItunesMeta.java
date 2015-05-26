package com.kevintcoughlin.smodr.models;

import org.parceler.Parcel;
import org.simpleframework.xml.Attribute;

@Parcel
public final class ItunesMeta {
	@Attribute(required = false)
	private String href;

	public ItunesMeta() {
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}
}
