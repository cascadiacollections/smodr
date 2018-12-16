package com.kevintcoughlin.smodr.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

import java.util.List;

@Root(name = "channel", strict = false)
public class Channel {

    public Channel(String title, String imageUriString) {
        this.title = title;
        this.image = new Image(imageUriString);
    }

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

    @ElementList(inline = true, entry = "item")
    public List<Item> item;

    public String getTitle() {
        return this.title;
    }
}