package com.kevintcoughlin.smodr.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Item")
public final class Episode extends ParseObject {

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
