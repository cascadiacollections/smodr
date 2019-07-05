package com.kevintcoughlin.smodr.models;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(name = "enclosure", strict = false)
public class Enclosure {
//    @Attribute(required = false)
//    @Nullable
//    public String url;

    @Attribute
    public int length;

    @Attribute
    public String type;
}