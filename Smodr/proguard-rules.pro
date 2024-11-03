# Add project-specific ProGuard rules here.
# By default, the flags in this file are appended to the standard Android ProGuard configuration.
# For more details, see https://developer.android.com/studio/build/shrink-code

# If your project uses WebView with JS, specify the fully qualified class name of the JavaScript interface
# Uncomment and replace fqcn.of.javascript.interface.for.webview with the correct class name
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# --- TikXML ProGuard Rules ---
-keep class com.tickaroo.tikxml.** { *; }
-keep @com.tickaroo.tikxml.annotation.Xml public class *
-keep class **$$TypeAdapter { *; }

-keepclasseswithmembernames class * {
    @com.tickaroo.tikxml.* <fields>;
}

-keepclasseswithmembernames class * {
    @com.tickaroo.tikxml.* <methods>;
}

# --- Firebase Crashlytics ---
# Keep necessary Firebase Crashlytics classes for crash reporting
-keepattributes *Annotation*
-keep class com.google.firebase.crashlytics.** { *; }
-dontwarn com.google.firebase.crashlytics.**

# --- Firebase Analytics ---
# Keep necessary Firebase Analytics classes
-keep class com.google.firebase.analytics.** { *; }
-dontwarn com.google.firebase.analytics.**

# --- Retrofit ---
# If using Retrofit for networking, keep generated adapters and Gson support
-keep class com.squareup.retrofit2.** { *; }
-dontwarn com.squareup.retrofit2.**

# --- LeakCanary (for Debug Builds) ---
# Keep LeakCanary if it's enabled for debugging
# Use this only in the debug configuration and exclude from release
-keep class com.squareup.leakcanary.** { *; }
-dontwarn com.squareup.leakcanary.**

# --- Kotlin Coroutines ---
# Keep necessary Kotlin Coroutine classes to avoid stripping coroutine functionality
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**

# --- RxJava 3 ---
# Keep RxJava classes if you're using RxJava 3 for reactive programming
-keep class io.reactivex.rxjava3.** { *; }
-dontwarn io.reactivex.rxjava3.**

# --- Common Annotations ---
-keepattributes *Annotation*
-keepclassmembers class ** {
    @androidx.annotation.Keep *;
}