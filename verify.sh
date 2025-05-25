#!/bin/bash

echo "Verifying project structure..."

# Check for common Android structure
if [ -d "./Smodr" ] && [ -d "./common-android" ]; then
  echo "✓ Project structure looks good"
else
  echo "✗ Project structure has issues"
  exit 1
fi

# Check for specific files related to our fixes
if [ -f "./common-android/src/main/java/androidx/window/extensions/embedding/ActivityEmbeddingComponent.java" ] &&
   [ -f "./common-android/src/main/java/androidx/window/extensions/layout/WindowLayoutComponent.java" ] &&
   [ -f "./common-android/src/main/java/androidx/window/sidecar/SidecarDeviceState.java" ] &&
   [ -f "./common-android/src/main/java/androidx/window/sidecar/SidecarInterface.java" ] &&
   [ -f "./common-android/src/main/java/com/google/common/util/concurrent/ListenableFuture.java" ]; then
  echo "✓ Stub implementations for missing classes are in place"
else
  echo "✗ Missing one or more stub implementations"
  exit 1
fi

# Check gradle.properties for proper configurations
if grep -q "android.enableR8=false" "./gradle.properties"; then
  echo "✓ R8 properly disabled in gradle.properties"
else
  echo "✗ R8 configuration in gradle.properties is incorrect"
  exit 1
fi

# Check build configurations
if grep -q "minifyEnabled false" "./Smodr/build.gradle" &&
   grep -q "minifyEnabled false" "./common-android/build.gradle"; then
  echo "✓ Minification disabled in build.gradle files"
else
  echo "✗ Minification configuration in build.gradle is incorrect"
  exit 1
fi

echo "✓ All verifications passed successfully!"
exit 0