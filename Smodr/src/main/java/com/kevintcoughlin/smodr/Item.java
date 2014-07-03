package com.kevintcoughlin.smodr;

import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;

import com.kevintcoughlin.smodr.util.MediaType;
import com.kevintcoughlin.smodr.util.Playable;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "item", strict = false)
public class Item implements Playable {

    @Element(name = "title")
    public String title;

    @Element(name = "link", required = false)
    public String link;

    @Element(name = "guid", required = false)
    public String guid;

    @Element(name = "pubDate", required = false)
    public String pubDate;

    @Element(name = "description", required = false)
    public String description;

    @Element(name="enclosure", required = false)
    public Enclosure enclosure;

    @Attribute(name="url", required = false)
    public String url;

    public Item() {

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

    @Override
    public void writeToPreferences(SharedPreferences.Editor prefEditor) {

    }

    @Override
    public void loadMetadata() throws PlayableException {

    }

    @Override
    public String getEpisodeTitle() {
        return this.title;
    }

    @Override
    public String getWebsiteLink() {
        return "http://smodcast.com";
    }

    @Override
    public String getFeedTitle() {
        return null;
    }

    @Override
    public Object getIdentifier() {
        return null;
    }

    @Override
    public int getDuration() {
        return 0;
    }

    @Override
    public int getPosition() {
        return 0;
    }

    @Override
    public MediaType getMediaType() {
        return MediaType.AUDIO;
    }

    @Override
    public String getLocalMediaUrl() {
        return null;
    }

    @Override
    public String getStreamUrl() {
        return this.enclosure.url;
    }

    @Override
    public boolean localFileAvailable() {
        return false;
    }

    @Override
    public boolean streamAvailable() {
        return false;
    }

    @Override
    public void saveCurrentPosition(SharedPreferences pref, int newPosition) {

    }

    @Override
    public void setPosition(int newPosition) {

    }

    @Override
    public void setDuration(int newDuration) {

    }

    @Override
    public void onPlaybackStart() {

    }

    @Override
    public void onPlaybackCompleted() {

    }

    @Override
    public int getPlayableType() {
        return 0;
    }

}