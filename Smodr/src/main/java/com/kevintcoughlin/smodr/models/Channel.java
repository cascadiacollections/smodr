package com.kevintcoughlin.smodr.models;

import org.parceler.Parcel;
import org.simpleframework.xml.*;

import java.util.ArrayList;

@Parcel
@Root(name = "channel", strict = false)
public final class Channel {
	private String shortName;

    @Element(name = "title", required = false)
    private String title;

    @Element(name = "description", required = false)
    private String description;

    @ElementList(name = "item", required = false, inline = true)
    private ArrayList<Item> items = new ArrayList<>();

	@Namespace(prefix = "itunes")
	@Element(name = "author", required = false)
	private String itunesAuthor;

	@Namespace(prefix = "itunes")
	@Element(name = "duration", required = false)
	private String itunesDuration;

	@Element(name ="pubDate", required = false)
	private String pubDate;

	public Channel() {
	}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

	public String getItunesAuthor() {
		return itunesAuthor;
	}

	public void setItunesAuthor(String itunesAuthor) {
		this.itunesAuthor = itunesAuthor;
	}

	public String getItunesDuration() {
		return itunesDuration;
	}

	public void setItunesDuration(String itunesDuration) {
		this.itunesDuration = itunesDuration;
	}
}