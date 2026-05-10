package com.kevintcoughlin.smodr

import com.google.firebase.crashlytics.FirebaseCrashlytics

/**
 * Firebase Crashlytics crash reporter.
 * Forwards crash data and logs to Firebase for production monitoring.
 */
object CrashReporter {
    fun init(@Suppress("UNUSED_PARAMETER") app: android.app.Application) {
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
    }

    fun log(message: String) {
        FirebaseCrashlytics.getInstance().log(message)
    }

    fun recordException(throwable: Throwable) {
        FirebaseCrashlytics.getInstance().recordException(throwable)
    }
}
