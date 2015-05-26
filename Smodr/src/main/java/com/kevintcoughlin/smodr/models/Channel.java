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

    @Element(name = "pubDate", required = false)
    private String pubDate;

	@Namespace(prefix = "itunes")
	@Element(name = "subtitle", required = false)
	private String itunesSubtitle;

	@Namespace(prefix = "itunes")
	@Element(name = "author", required = false)
	private String itunesAuthor;

	@Namespace(prefix = "itunes")
	@Element(name = "image", required = false)
	private ItunesImage itunesImage;

    @ElementList(name = "item", required = false, inline = true)
    private ArrayList<Item> items = new ArrayList<>();

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

	public ItunesImage getmItunesImage() {
		return itunesImage;
	}

	public void setmItunesImage(ItunesImage mItunesImage) {
		this.itunesImage = mItunesImage;
	}
}