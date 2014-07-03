package com.kevintcoughlin.smodr.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(name = "enclosure", strict = false)
public class Enclosure implements Parcelable {

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.url);
    }

    public static final Parcelable.Creator<Enclosure> CREATOR = new Parcelable.Creator<Enclosure>() {
        public Enclosure createFromParcel(Parcel in) {
            return new Enclosure(in);
        }

        public Enclosure[] newArray(int size) {
            return new Enclosure[size];
        }
    };

    private Enclosure(Parcel in) {
        this.url = in.readString();
    }

}
