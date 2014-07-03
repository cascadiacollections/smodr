package com.kevintcoughlin.smodr;

import android.os.Parcel;
import android.os.Parcelable;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "rss", strict = false)
public class Rss implements Parcelable {

    @Element(name="channel")
    private Channel channel;

    @Attribute
    private String version;

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

    public Rss() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeParcelable(this.channel, flags);
        out.writeString(this.version);
    }

    public static final Parcelable.Creator<Rss> CREATOR = new Parcelable.Creator<Rss>() {
        public Rss createFromParcel(Parcel in) {
            return new Rss(in);
        }

        public Rss[] newArray(int size) {
            return new Rss[size];
        }
    };

    private Rss(Parcel in) {
        this.channel = in.readParcelable(Channel.class.getClassLoader());
        this.version = in.readString();
    }

}

