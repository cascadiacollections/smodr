package com.cascadiacollections.smodr.views

import android.text.format.DateUtils
import android.widget.TextView

fun TextView.setElapsedTime(milliseconds: Int) {
    this.text = DateUtils.formatElapsedTime(milliseconds.toLong() / 1000);
}