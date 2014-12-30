package com.kevintcoughlin.smodr.models;

import org.parceler.Parcel;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.Date;

@Parcel
@Root(name = "channel", strict = false)
public class Channel {
    private Long _id;
    private String shortName;

    @Element(name = "title", required = false)
    private String title;

    @Element(name = "description", required = false)
    private String description;

    @Element(name = "pubDate", required = false)
    private Date pubDate;

    @ElementList(name = "item", required = false, inline = true)
    private ArrayList<Item> items = new ArrayList<>();

    public Channel() {}

    // @TODO: fix with database id
    public Channel(int _id, String shortName, String title) {
        this._id = Long.valueOf(_id);
        this.shortName = shortName;
        this.title = title;
    }

    public Long getId() {
        return _id;
    }

    public void setId(Long _id) {
        this._id = _id;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getImageUrl() {
        return this.shortName.replace("-", "") + ".jpg";
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

    public Date getPubDate() {
        return pubDate;
    }

    public void setPubDate(Date pubDate) {
        this.pubDate = pubDate;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }
 {}
}