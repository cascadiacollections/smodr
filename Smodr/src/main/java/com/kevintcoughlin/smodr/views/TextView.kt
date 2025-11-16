package com.kevintcoughlin.smodr.views

import android.content.Context
import android.util.Log
import android.widget.TextView
import androidx.annotation.CheckResult
import androidx.annotation.StringRes
import com.kevintcoughlin.smodr.BuildConfig
import com.kevintcoughlin.smodr.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * Extension functions for [TextView] to display and manage elapsed time formatting.
 *
 * Provides utilities for formatting durations in human-readable formats (HH:mm:ss, MM:ss, etc.)
 * with support for:
 * - Multiple locales
 * - Custom formatters
 * - Coroutine-based automatic updates
 * - Thread-safe caching
 */

// Constants for time calculations
private const val SECONDS_PER_MINUTE = 60L
private const val SECONDS_PER_HOUR = 3600L
private const val SECONDS_PER_DAY = 86400L
private const val DEFAULT_UPDATE_INTERVAL_MS = 1000L

/** Tag for logging - public for inline function access */
const val TAG = "TextViewExtensions"

/**
 * Sets the text of the TextView to display elapsed time in a human-readable format.
 *
 * Formats:
 * - Less than 1 hour: `MM:ss` (e.g., "5:42")
 * - 1 hour or more: `HH:mm:ss` (e.g., "1:05:42")
 * - 1 day or more: `D days, HH:mm:ss` (e.g., "2 days, 3:15:30")
 *
 * @param milliseconds The elapsed time in milliseconds. Negative values display fallback text.
 * @param locale The locale for formatting. Defaults to the system locale.
 * @param fallbackTextResId Optional resource ID for fallback text when time is invalid.
 * @param formatter Custom formatter function. Defaults to [defaultElapsedTimeFormatter].
 *
 * @see defaultElapsedTimeFormatter
 * @see updateElapsedTime
 */
inline fun TextView.setElapsedTime(
    milliseconds: Long,
    locale: Locale = Locale.getDefault(),
    @StringRes fallbackTextResId: Int? = null,
    formatter: (Duration, Locale) -> String = ::defaultElapsedTimeFormatter,
) {
    text = when {
        milliseconds >= 0 -> formatter(milliseconds.milliseconds, locale)
        else -> {
            if (BuildConfig.DEBUG) {
                Log.w(TAG, "Invalid elapsed time: $milliseconds ms (must be non-negative)")
            }
            fallbackTextResId?.let { context.getString(it) }
                ?: context.getString(R.string.elapsed_time_fallback)
        }
    }
}

/**
 * Default formatter for elapsed time with locale-aware formatting.
 *
 * Format examples:
 * - 42 seconds: `"0:42"`
 * - 5 minutes 30 seconds: `"5:30"`
 * - 1 hour 15 minutes: `"1:15:00"`
 * - 2 days 3 hours: `"2 days, 3:00:00"`
 *
 * @param duration The elapsed time duration.
 * @param locale The locale for number formatting.
 * @return A formatted time string.
 */
@CheckResult
fun defaultElapsedTimeFormatter(duration: Duration, locale: Locale): String {
    val totalSeconds = duration.inWholeSeconds

    // Early return for zero or negative durations
    if (totalSeconds <= 0) return "0:00"

    val days = totalSeconds / SECONDS_PER_DAY
    val hours = (totalSeconds % SECONDS_PER_DAY) / SECONDS_PER_HOUR
    val minutes = (totalSeconds % SECONDS_PER_HOUR) / SECONDS_PER_MINUTE
    val seconds = totalSeconds % SECONDS_PER_MINUTE

    return when {
        days > 0 -> String.format(locale, "%d days, %d:%02d:%02d", days, hours, minutes, seconds)
        hours > 0 -> String.format(locale, "%d:%02d:%02d", hours, minutes, seconds)
        else -> String.format(locale, "%d:%02d", minutes, seconds)
    }
}

/**
 * Automatically updates a TextView with elapsed time at regular intervals using coroutines.
 *
 * The update loop:
 * - Runs on the Main dispatcher (UI-safe)
 * - Stops when the TextView is detached from the window
 * - Stops when the coroutine is cancelled
 * - Is suspendable and non-blocking
 *
 * Usage:
 * ```kotlin
 * lifecycleScope.launch {
 *     textView.updateElapsedTime(startTimeMillis = startTime)
 * }
 * ```
 *
 * @param startTimeMillis The reference time in milliseconds since epoch (from [System.currentTimeMillis]).
 * @param locale The locale for formatting. Defaults to the system locale.
 * @param intervalMillis Update interval in milliseconds. Defaults to 1000ms (1 second).
 *
 * @see setElapsedTime
 */
suspend fun TextView.updateElapsedTime(
    startTimeMillis: Long,
    locale: Locale = Locale.getDefault(),
    intervalMillis: Long = DEFAULT_UPDATE_INTERVAL_MS,
) = withContext(Dispatchers.Main) {
    require(intervalMillis > 0) { "Update interval must be positive, got $intervalMillis ms" }

    while (isAttachedToWindow && isActive) {
        val elapsedMillis = System.currentTimeMillis() - startTimeMillis
        setElapsedTime(elapsedMillis, locale)
        delay(intervalMillis)
    }
}

/**
 * Formats a [Duration] as an elapsed time string without requiring a TextView.
 *
 * Useful for non-UI contexts such as:
 * - Logging
 * - Notifications
 * - Data serialization
 * - Testing
 *
 * Example:
 * ```kotlin
 * val duration = 90.seconds
 * val formatted = duration.formatElapsedTime() // "1:30"
 * ```
 *
 * @param locale The locale for formatting. Defaults to the system locale.
 * @return A formatted elapsed time string.
 *
 * @see defaultElapsedTimeFormatter
 */
@CheckResult
fun Duration.formatElapsedTime(locale: Locale = Locale.getDefault()): String =
    defaultElapsedTimeFormatter(this, locale)

/**
 * Thread-safe cache for fallback text resources.
 * Uses [ConcurrentHashMap] to prevent race conditions in multi-threaded contexts.
 */
private val fallbackTextCache = ConcurrentHashMap<Int, String>()

/**
 * Retrieves and caches fallback text for a string resource ID.
 *
 * Caching improves performance by avoiding repeated [Context.getString] calls
 * for the same resource ID.
 *
 * Thread-safe implementation using [ConcurrentHashMap].
 *
 * @param resId The string resource ID.
 * @return The localized string value.
 */
fun Context.getFallbackText(@StringRes resId: Int): String =
    fallbackTextCache.getOrPut(resId) { getString(resId) }
