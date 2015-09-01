package com.kevintcoughlin.smodr.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.Toast;

/**
 * Contains common {@link android.app.Application} utilities.
 *
 * @author kevincoughlin
 */
public final class AppUtil {
	/**
	 * Returns a {@link String[]} for the given resource id.
	 *
	 * @param context
	 * 		the context to use.
	 * @param id
	 * 		the resource id to fetch.
	 * @return the {@link String[]} for the given resource id.
	 */
	public static String[] getStrings(@Nullable final Context context, final int id) {
		if (context == null) {
			return new String[] {};
		}
		return context.getResources().getStringArray(id);
	}

	/**
	 * Displays a {@link Toast} with the given {@link String} message.
	 *
	 * @param context
	 *      the context to use.
	 * @param message
	 *      the {@link String} message to display.
	 */
	public static void toast(@Nullable final Context context, @NonNull final String message) {
		if (context == null) {
			return;
		}
		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}

	/**
	 * Displays a {@link Toast} with the given string resource identifier's contents.
	 *
	 * @param context
	 *      the context to use.
	 * @param id
	 *      the string resource identifier to use.
	 */
	public static void toast(@Nullable final Context context, @StringRes final int id) {
		if (context == null) {
			return;
		}
		Toast.makeText(context, context.getResources().getText(id), Toast.LENGTH_LONG).show();
	}

	/**
	 * Toggle a {@link View}'s visibility.
	 *
	 * @param v
	 *      the {@link View} to manipulate.
	 * @param visible
	 *      if true then {@link View#VISIBLE} else {@link View#INVISIBLE}.
	 */
	public static void setVisible(@Nullable View v, final boolean visible) {
		if (v != null) {
			v.setVisibility(visible ? View.VISIBLE : View.GONE);
		}
	}
}
