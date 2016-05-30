package com.kevintcoughlin.smodr.models;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Enclosure {
    public abstract String url();
    public abstract String type();
    public abstract int length();
}
