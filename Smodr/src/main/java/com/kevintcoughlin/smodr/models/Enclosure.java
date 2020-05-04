package com.kevintcoughlin.smodr.models;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(name = "enclosure", strict = false)
public class Enclosure {
    @Attribute
    public int length;
}