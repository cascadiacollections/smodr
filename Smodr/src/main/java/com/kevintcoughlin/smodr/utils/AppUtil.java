package com.kevintcoughlin.smodr.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Utility for common application tasks.
 */
public final class AppUtil {
	public static String[] getStrings(Context context, int id) {
		return context.getResources().getStringArray(id);
	}

	public static void toast(Context context, String message) {
		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}
}
