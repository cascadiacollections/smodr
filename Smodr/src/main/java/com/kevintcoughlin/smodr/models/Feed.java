package com.kevintcoughlin.smodr.models;

import com.google.auto.value.AutoValue;

import java.util.List;

@AutoValue
abstract class Feed {
    abstract String title();
    abstract String url();
    abstract List<Item> items();
}
