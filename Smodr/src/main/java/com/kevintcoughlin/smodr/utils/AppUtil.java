package com.kevintcoughlin.smodr.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.Toast;

/**
 * Utility for common application tasks.
 */
public final class AppUtil {
	public static String[] getStrings(@NonNull final Context context, final int id) {
		return context.getResources().getStringArray(id);
	}

	public static void toast(@NonNull final Context context, @NonNull final String message) {
		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}

	public static void toast(@NonNull final Context context, @StringRes final int id) {
		Toast.makeText(context, context.getResources().getText(id), Toast.LENGTH_LONG).show();
	}

	public static void setVisible(final View v, final boolean visible) {
		if (v != null) {
			v.setVisibility(visible ? View.VISIBLE : View.GONE);
		}
	}
}
