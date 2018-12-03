package com.kevintcoughlin.smodr.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="item", strict = false)
public class Item {
    @Element
    public String guid;

    @Element(required = false)
    public String title;

    @Element
    public String pubDate;

    @Element
    public String link;

    @Element
    public String description;

    @Element
    public Enclosure enclosure;
}