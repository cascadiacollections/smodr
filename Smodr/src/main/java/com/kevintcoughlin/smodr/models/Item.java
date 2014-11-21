package com.kevintcoughlin.smodr.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "item", strict = false)
public class Item implements Parcelable {

    public Item() {
    }

    @Element(name = "title")
    private String title;

    @Element(name = "link", required = false)
    private String link;

    @Element(name = "guid", required = false)
    private String guid;

    @Element(name = "pubDate", required = false)
    private String pubDate;

    @Element(name = "description", required = false)
    private String description;

    @Element(name="enclosure", required = false)
    private Enclosure enclosure;

    @Attribute(name="url", required = false)
    private String url;

    public Enclosure getEnclosure() {
        return enclosure;
    }

    public void setEnclosure(Enclosure enclosure) {
        this.enclosure = enclosure;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.title);
        out.writeString(this.link);
        out.writeString(this.guid);
        out.writeString(this.pubDate);
        out.writeString(this.description);
        out.writeParcelable(this.enclosure, flags);
        out.writeString(this.url);
    }

    public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>() {
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    private Item(Parcel in) {
        this.title = in.readString();
        this.link = in.readString();
        this.guid = in.readString();
        this.pubDate = in.readString();
        this.description = in.readString();
        this.enclosure = in.readParcelable(Enclosure.class.getClassLoader());
        this.url = in.readString();
    }

}