package com.kevintcoughlin.smodr.models;

import com.google.auto.value.AutoValue;

import java.util.List;

@AutoValue
abstract class Item {
    abstract String title();
    abstract String description();
    abstract String link();
    abstract String author();
    abstract String date();
    abstract Image image();
    abstract List<Enclosure> enclosures();
}
