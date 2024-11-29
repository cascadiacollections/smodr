package com.kevintcoughlin.smodr.views

import android.content.Context
import android.util.Log
import android.widget.TextView
import androidx.annotation.CheckResult
import androidx.annotation.StringRes
import com.kevintcoughlin.smodr.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration
import java.util.Locale

/**
 * Sets the text of the TextView to display elapsed time in the format HH:mm:ss or MM:ss.
 *
 * @param milliseconds The elapsed time in milliseconds. Must be non-negative.
 * @param locale The desired locale for formatting the elapsed time. Defaults to the system's default locale.
 * @param fallbackTextResId The resource ID for the fallback text if the time is invalid.
 * @param formatter A custom formatter function for elapsed time.
 */
inline fun TextView.setElapsedTime(
    milliseconds: Long,
    locale: Locale = Locale.getDefault(),
    @StringRes fallbackTextResId: Int? = null,
    formatter: (Duration, Locale) -> String = ::defaultElapsedTimeFormatter
) {
    val fallbackText = fallbackTextResId?.let { context.getString(it) }
        ?: context.getString(R.string.elapsed_time_fallback)

    this.text = if (milliseconds >= 0) {
        formatter(milliseconds.toDuration(DurationUnit.MILLISECONDS), locale)
    } else {
        Log.w("setElapsedTime", "Invalid duration: $milliseconds")
        fallbackText
    }
}

/**
 * Default formatter for elapsed time.
 *
 * Formats elapsed time in the format HH:mm:ss or MM:ss. For durations exceeding 1 day, it includes days.
 *
 * @param duration The elapsed time as a [Duration].
 * @param locale The locale to use for formatting.
 * @return A formatted elapsed time string.
 */
@CheckResult
fun defaultElapsedTimeFormatter(duration: Duration, locale: Locale): String {
    val totalSeconds = duration.toLong(DurationUnit.SECONDS)
    val days = totalSeconds / 86400
    val hours = (totalSeconds % 86400) / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return when {
        days > 0 -> String.format(locale, "%d days, %d:%02d:%02d", days, hours, minutes, seconds)
        hours > 0 -> String.format(locale, "%d:%02d:%02d", hours, minutes, seconds)
        else -> String.format(locale, "%d:%02d", minutes, seconds)
    }
}

/**
 * Coroutine-based extension to update a TextView with elapsed time periodically.
 *
 * The update stops when the TextView is detached from the window.
 *
 * @param startTimeMillis The starting time in milliseconds since epoch.
 * @param locale The desired locale for formatting the elapsed time. Defaults to the system's default locale.
 * @param intervalMillis The interval between updates in milliseconds.
 */
suspend fun TextView.updateElapsedTime(
    startTimeMillis: Long,
    locale: Locale = Locale.getDefault(),
    intervalMillis: Long = 1000L
) = withContext(Dispatchers.Main) {
    while (isAttachedToWindow) {
        val elapsedMillis = System.currentTimeMillis() - startTimeMillis
        setElapsedTime(elapsedMillis, locale)
        delay(intervalMillis)
    }
}

/**
 * Formats elapsed time as a string without requiring a TextView.
 *
 * Useful for non-UI use cases such as logging or notifications.
 *
 * @param duration The elapsed time as a [Duration].
 * @param locale The desired locale for formatting the elapsed time. Defaults to the system's default locale.
 * @return A formatted elapsed time string.
 */
@CheckResult
fun Duration.formatElapsedTime(locale: Locale = Locale.getDefault()): String {
    return defaultElapsedTimeFormatter(this, locale)
}

/**
 * Utility to retrieve and cache fallback text for a given string resource ID.
 */
private val fallbackTextCache = mutableMapOf<Int, String>()

fun Context.getFallbackText(@StringRes resId: Int): String {
    return fallbackTextCache.getOrPut(resId) { getString(resId) }
}
