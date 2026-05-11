package com.kevintcoughlin.smodr

import com.google.firebase.crashlytics.FirebaseCrashlytics

/**
 * Firebase Crashlytics crash reporter.
 * Forwards crash data and logs to Firebase for production monitoring.
 *
 * Collection is disabled in debug builds so that local test sessions do not
 * pollute production dashboards; it is enabled only for non-debug (release)
 * builds via [BuildConfig.DEBUG].
 */
object CrashReporter {
    fun init(@Suppress("UNUSED_PARAMETER") app: android.app.Application) {
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
    }

    fun log(message: String) {
        FirebaseCrashlytics.getInstance().log(message)
    }

    fun recordException(throwable: Throwable) {
        FirebaseCrashlytics.getInstance().recordException(throwable)
    }
}
