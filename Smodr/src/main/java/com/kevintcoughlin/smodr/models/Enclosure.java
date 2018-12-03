package com.kevintcoughlin.smodr.models;

import org.simpleframework.xml.Attribute;

public class Enclosure {
    @Attribute
    public String url;

    @Attribute
    public int length;

    @Attribute
    public String type;
}