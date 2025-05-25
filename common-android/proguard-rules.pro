# Add project-specific ProGuard rules here.
# By default, the flags in this file are appended to the standard Android ProGuard configuration.
# For more details, see https://developer.android.com/studio/build/shrink-code

# If your project uses WebView with JS, specify the fully qualified class name of the JavaScript interface
# Uncomment and replace fqcn.of.javascript.interface.for.webview with the correct class name
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-keep class androidx.core.** { *; }
-keep interface androidx.core.** { *; }

# Keep classes that are causing R8 compilation failures
-keep class com.google.common.util.concurrent.ListenableFuture { *; }
-keepnames class com.google.common.util.concurrent.ListenableFuture

# Completely disable class merging for problematic classes to prevent R8 issues
-optimizations !class/merging/vertical,!class/merging/horizontal

# androidx.window extensions and core - special handling for system stub libraries
-dontwarn androidx.window.**
-keep class androidx.window.** { *; }

# Explicitly keep all the missing reference classes
-keep class androidx.window.extensions.embedding.ActivityEmbeddingComponent { *; }
-keep class androidx.window.extensions.layout.WindowLayoutComponent { *; }
-keep class androidx.window.sidecar.SidecarDeviceState { *; }
-keep class androidx.window.sidecar.SidecarInterface { *; }

# Explicitly handle all the missing class references from androidx.window.extensions
-dontwarn androidx.window.extensions.**
-dontwarn androidx.window.extensions.embedding.**
-dontwarn androidx.window.extensions.layout.**
-dontwarn androidx.window.sidecar.**

# Keep all interfaces and abstract methods from these packages
-keep interface androidx.window.extensions.** { *; }
-keep interface androidx.window.sidecar.** { *; }

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
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod,Exceptions
-keepclassmembers class ** {
    @androidx.annotation.Keep *;
}

# Keep BinderRecyclerAdapter and all its inner classes
-keep class com.cascadiacollections.jamoka.adapter.BinderRecyclerAdapter { *; }
-keepclassmembers class com.cascadiacollections.jamoka.adapter.BinderRecyclerAdapter$Binder { *; }

# Keep BinderRecyclerFragment and all its inner classes
-keep class com.cascadiacollections.jamoka.fragment.BinderRecyclerFragment { *; }
-keepclassmembers class com.cascadiacollections.jamoka.fragment.BinderRecyclerFragment$OnItemSelected { *; }

# Keep BinderRecyclerAdapter and its inner classes
-keep class com.cascadiacollections.jamoka.adapter.BinderRecyclerAdapter { *; }
-keep class com.cascadiacollections.jamoka.adapter.BinderRecyclerAdapter$Binder { *; }

# Keep BinderRecyclerFragment and its inner classes
-keep class com.cascadiacollections.jamoka.fragment.BinderRecyclerFragment { *; }
-keep class com.cascadiacollections.jamoka.fragment.BinderRecyclerFragment$OnItemSelected { *; }

# Keep all attributes to avoid issues with R8 optimizations
-keepattributes *