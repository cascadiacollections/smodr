package com.kevintcoughlin.smodr.models;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

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
