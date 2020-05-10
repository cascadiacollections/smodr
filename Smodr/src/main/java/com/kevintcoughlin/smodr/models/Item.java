package com.kevintcoughlin.smodr.models;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

import java.util.Objects;


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
    public boolean equals(@Nullable Object obj) {
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final Item item = (Item) obj;
        return Objects.equals(title, item.title) &&
                Objects.equals(pubDate, item.pubDate) &&
                Objects.equals(description, item.description) &&
                Objects.equals(duration, item.duration) &&
                Objects.equals(summary, item.summary) &&
                Objects.equals(origEnclosureLink, item.origEnclosureLink);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, pubDate, description, duration, summary, origEnclosureLink);
    }
}