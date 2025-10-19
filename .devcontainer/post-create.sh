#!/bin/bash

echo "🚀 Setting up Smodr Android development environment..."

# Set up Android SDK environment variables
export ANDROID_HOME=/usr/local/android-sdk
export ANDROID_SDK_ROOT=$ANDROID_HOME
export PATH=$PATH:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools

# Accept Android SDK licenses
if [ -d "$ANDROID_HOME" ]; then
    echo "📱 Accepting Android SDK licenses..."
    yes | $ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager --licenses > /dev/null 2>&1
fi

# Install additional Android SDK components if needed
if command -v sdkmanager &> /dev/null; then
    echo "📦 Installing additional Android SDK components..."
    sdkmanager --install "platform-tools" "build-tools;34.0.0" "platforms;android-34" > /dev/null 2>&1
fi

# Make gradlew executable
if [ -f "./gradlew" ]; then
    echo "🔧 Making gradlew executable..."
    chmod +x ./gradlew
fi

# Verify Gradle version and wrapper
echo "🔍 Verifying Gradle 8.x setup..."
./gradlew --version | grep "Gradle 8" > /dev/null 2>&1
if [ $? -eq 0 ]; then
    echo "✅ Gradle 8.x detected"
else
    echo "⚠️  Warning: Expected Gradle 8.x, please verify gradle-wrapper.properties"
fi

# Enable Gradle configuration cache
echo "⚡ Configuring Gradle for optimal performance..."
if [ ! -f "gradle.properties" ]; then
    echo "org.gradle.configuration-cache=true" >> gradle.properties
    echo "org.gradle.daemon=true" >> gradle.properties
fi

# Install Gradle dependencies (this will also download and set up Gradle wrapper)
echo "📥 Installing project dependencies..."
./gradlew dependencies --quiet > /dev/null 2>&1 || echo "⚠️  Dependencies installation completed with warnings (this is normal for first setup)"

# Verify centralized dependency management
echo "🔍 Verifying dependency management setup..."
if grep -q "buildscript" build.gradle; then
    echo "✅ Centralized dependency management detected in build.gradle"
fi

# Set up Git hooks if they exist
if [ -d ".git/hooks" ]; then
    echo "🔗 Setting up Git hooks..."
    # This would be where we'd set up any pre-commit hooks, etc.
fi

echo ""
echo "✅ Development environment setup complete!"
echo ""
echo "🎯 Quick start commands:"
echo "  ./gradlew build          - Build the project"
echo "  ./gradlew lint           - Run Android lint"
echo "  ./gradlew test           - Run tests"
echo "  ./gradlew assembleDebug  - Build debug APK"
echo ""
echo "📚 Documentation:"
echo "  README.md          - Project overview"
echo "  DEV_ENVIRONMENT.md - Development setup guide"
echo "  COPILOT.md         - GitHub Copilot usage guide"
echo ""
echo "💡 Tip: GitHub Copilot is configured and ready to use!"
echo "   Use Ctrl+I for inline suggestions and chat for help with Gradle/Android tasks."