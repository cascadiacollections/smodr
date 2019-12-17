package com.kevintcoughlin.smodr.utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.google.android.material.snackbar.Snackbar;

public final class StringResourceUtilities {
    public static String getString(@Nullable final Context context, @StringRes final int id, Object... args) {
        return context != null ? context.getResources().getString(id, args) : "";
    }
}
