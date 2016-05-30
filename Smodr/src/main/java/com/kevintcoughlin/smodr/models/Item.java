package com.kevintcoughlin.smodr.models;

import com.google.auto.value.AutoValue;

import java.util.List;

@AutoValue
public abstract class Item {
    public abstract String title();
    public abstract String description();
    public abstract String link();
    public abstract String author();
    public abstract String date();
    public abstract Image image();
    public abstract List<Enclosure> enclosures();
}
