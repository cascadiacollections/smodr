package com.kevintcoughlin.smodr.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "image", strict = false)
public class Image {

    Image(String url) {
        this.url = url;
    }

    @Element(required = false)
    public String url;
}