# Special ProGuard rules specifically for androidx.window classes

# Keep the androidx.window.extensions classes that might be missing
-keep class androidx.window.extensions.** { *; }
-keep class androidx.window.extensions.embedding.** { *; }
-keep class androidx.window.extensions.layout.** { *; }
-keep class androidx.window.sidecar.** { *; }

# Specifically keep the referenced classes causing issues
-keep class androidx.window.extensions.embedding.ActivityEmbeddingComponent { *; }
-keep class androidx.window.extensions.layout.WindowLayoutComponent { *; }
-keep class androidx.window.sidecar.SidecarDeviceState { *; }
-keep class androidx.window.sidecar.SidecarInterface { *; }

# Keep all interfaces from these packages
-keep interface androidx.window.** { *; }
-keep interface androidx.window.extensions.** { *; }
-keep interface androidx.window.extensions.embedding.** { *; }
-keep interface androidx.window.extensions.layout.** { *; }
-keep interface androidx.window.sidecar.** { *; }

# Don't warn about missing classes - they are stub interfaces provided at runtime
-dontwarn androidx.window.**
-dontwarn androidx.window.extensions.**
-dontwarn androidx.window.extensions.embedding.**
-dontwarn androidx.window.extensions.layout.**
-dontwarn androidx.window.sidecar.**

# Disable optimizations that might cause issues
-optimizations !class/unboxing/enum,!code/allocation/variable