package com.kevintcoughlin.smodr.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

@Root(name="channel", strict = false)
public class Channel {
    @Element
    public String title;

    @Path("description")
    @Text(required = false)
    public String description;

    @Element
    public String pubDate;

    @Element
    public String lastBuildDate;

    @Element(required = false)
    public String link;

    @Path("image")
    public Image image;

    public String getTitle() {
        return this.title;
    }
}