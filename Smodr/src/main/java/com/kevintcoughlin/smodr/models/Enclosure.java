package com.kevintcoughlin.smodr.models;

import android.support.annotation.Nullable;

import org.simpleframework.xml.Attribute;

public class Enclosure {
    @Attribute(required = false)
    @Nullable
    public String url;

    @Attribute
    public int length;

    @Attribute
    public String type;
}