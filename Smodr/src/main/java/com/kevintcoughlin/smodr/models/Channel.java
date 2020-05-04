package com.kevintcoughlin.smodr.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

import java.util.List;

@Root(name = "channel", strict = false)
public class Channel {
    Channel() {

    }

    public Channel(String title, String link, String imageUriString) {
        this.title = title;
        this.link = link;
        this.image = new Image(imageUriString);
    }

    @Element
    public String title;

    @Element(required = false)
    public String link;

    @Path("image")
    public Image image;

    @ElementList(inline = true, entry = "item")
    public List<Item> item;
}