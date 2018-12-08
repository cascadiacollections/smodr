package com.kevintcoughlin.smodr.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import javax.annotation.Nullable;

@Root(name = "item", strict = false)
public class Item {
    @Element(required = false)
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
}