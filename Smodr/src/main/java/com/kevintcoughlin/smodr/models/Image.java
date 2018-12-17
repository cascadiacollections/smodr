package com.kevintcoughlin.smodr.models;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "image", strict = false)
public class Image {

    Image(String url) {
        this.url = url;
    }

    @Element(required = false)
    public String title;

    @Element(required = false)
    public String url;

    @Element(required = false)
    public String link;

    @Attribute
    public String href;
}