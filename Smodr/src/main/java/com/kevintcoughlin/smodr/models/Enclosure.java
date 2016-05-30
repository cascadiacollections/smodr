package com.kevintcoughlin.smodr.models;

import com.google.auto.value.AutoValue;

@AutoValue
abstract class Enclosure {
    abstract String url();
    abstract String type();
    abstract int length();
}
