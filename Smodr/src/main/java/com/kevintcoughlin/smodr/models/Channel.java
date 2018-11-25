package com.kevintcoughlin.smodr.models;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Channel {
    public abstract String imageUrl();
    public abstract String title();
}
