package com.kevintcoughlin.smodr.models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;


@Root(name = "item", strict = false)
public class Item {
    @Element(required = false)
    @Nullable
    public String guid;

    @Element(required = false)
    @Nullable
    public String title;

    @Element(required = false)
    @Nullable
    public String pubDate;

    @Element(required = false)
    @Nullable
    public String link;

    @Element(required = false)
    @Nullable
    public String description;

    @Element(required = false)
    @Nullable
    public Enclosure enclosure;

    @Element
    @Namespace(prefix="feedburner")
    @NonNull
    public String origEnclosureLink;
}