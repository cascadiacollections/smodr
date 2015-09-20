package com.kevintcoughlin.smodr.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Channel")
public final class Channel extends ParseObject {

	public Channel() {
	}

	public String getImageUrl() {
		return getString("image_url");
	}

	public String getTitle() {
		return getString("title");
	}
}

