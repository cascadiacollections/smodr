package com.kevintcoughlin.smodr.models;

import com.google.auto.value.AutoValue;

import java.util.List;

@AutoValue
public abstract class Feed {
    public abstract String title();
    public abstract String url();
    public abstract List<Item> items();
}
