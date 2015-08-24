package com.kevintcoughlin.smodr.models;

import org.parceler.Parcel;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Model representing an RSS feed.
 *
 * @author kevincoughlin
 */
@Parcel
@Root(name = "rss", strict = false)
public final class Rss {
    @Element(name="channel") private Channel channel;
    @Attribute private String version;

	public Rss() {
	}

	public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

}

