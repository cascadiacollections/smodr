package com.kevintcoughlin.smodr.models;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;


@Root(name = "item", strict = false)
public class Item implements IMediaPlayback {
    @Element(required = false)
    @Nullable
    public String title;

    @Element(required = false)
    @Nullable
    public String pubDate;

    @Element(required = false)
    @Nullable
    public String description;

    @Element(required = false)
    @Nullable
    public Enclosure enclosure;

    @Element
    @Namespace(prefix="itunes")
    @Nullable
    public String duration;

    @Element
    @Namespace(prefix="itunes")
    @Nullable
    public String summary;

    @Element
    @Namespace(prefix="feedburner")
    @Nullable
    private String origEnclosureLink;

    @NonNull
    private static final String HTTP_PROTOCOL = "http://";
    @NonNull
    private static final String HTTPS_PROTOCOL = "https://";

    @Override
    @Nullable
    public Uri getUri() {
        if (this.origEnclosureLink != null) {
            final String uriString = this.origEnclosureLink.replace(HTTP_PROTOCOL, HTTPS_PROTOCOL);
            return Uri.parse(uriString);
        }
        return null;
    }

    @Override
    // @todo
    public boolean equals(@Nullable Object obj) {
        return super.equals(obj);
    }

    @NonNull
    @Override
    // @todo
    public String toString() {
        return super.toString();
    }
}