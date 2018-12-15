package com.kevintcoughlin.smodr.models;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "rss", strict = false)
public class Feed {
    @Element
    public Channel channel;
}