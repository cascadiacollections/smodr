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

# Install Gradle dependencies (this will also download and set up Gradle wrapper)
echo "📥 Installing project dependencies..."
./gradlew dependencies --quiet > /dev/null 2>&1 || echo "⚠️  Dependencies installation completed with warnings (this is normal for first setup)"

# Set up Git hooks if they exist
if [ -d ".git/hooks" ]; then
    echo "🔗 Setting up Git hooks..."
    # This would be where we'd set up any pre-commit hooks, etc.
fi

echo "✅ Development environment setup complete!"
echo ""
echo "🎯 Quick start commands:"
echo "  ./gradlew build          - Build the project"
echo "  ./gradlew lint           - Run Android lint"
echo "  ./gradlew test           - Run tests"
echo "  ./gradlew assembleDebug  - Build debug APK"
echo ""
echo "🔍 Check README.md for more detailed instructions."