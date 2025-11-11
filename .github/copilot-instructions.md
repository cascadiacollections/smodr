# GitHub Copilot Instructions

## Project Context

This is **Smodr**, an Android podcast player application written in Kotlin.

## Architecture & Patterns

- **Language**: Kotlin
- **UI Framework**: Android SDK with RecyclerView-based architecture
- **Design Pattern**: Custom adapter pattern using `BinderRecyclerAdapter`
- **Database**: Room for local data persistence
- **Async**: Kotlin Coroutines
- **Build System**: Gradle with multiple modules

## Code Style Guidelines

### Kotlin Conventions

- Use Kotlin idioms and conventions
- Prefer `val` over `var` when possible
- Use data classes for model objects
- Leverage Kotlin extension functions
- Use `lateinit` for Android components initialized in lifecycle methods
- Prefer `by lazy` for expensive initializations

### Android Best Practices

- Follow Android Architecture Components guidelines
- Use ViewBinding for view access
- Implement proper lifecycle awareness
- Handle configuration changes appropriately
- Use proper resource management (strings, dimensions, colors in XML)

### Custom Patterns

- Use `BinderRecyclerAdapter` with `ViewHolderBinder` for RecyclerView implementations
- Configure adapters with `BinderRecyclerAdapterConfig.Builder`
- Extend `BinderRecyclerFragment` for list-based fragments

## Module Structure

- `Smodr/` - Main application module
- `common-android/` - Shared Android components and utilities

## Key Components

### Adapters

```kotlin
// Always provide both type arguments when creating adapter configs
BinderRecyclerAdapterConfig.Builder<ItemType, ViewHolderType>()
    .enableDiffUtil(true)
    .build()
```

### ProGuard/R8

- Keep rules are defined in `proguard-rules.pro`
- Custom adapter classes require keep rules to prevent shrinking

## Dependencies

- AndroidX libraries for modern Android development
- Firebase Crashlytics for crash reporting
- Kotlin Coroutines for async operations
- Material Design Components

## Testing Approach

- Unit tests for business logic
- Instrumentation tests for UI components
- Follow AAA pattern (Arrange, Act, Assert)

## Common Pitfalls to Avoid

1. Don't forget type arguments for generic builders
2. Always handle null safely with Kotlin null-safety features
3. Properly manage Activity/Fragment lifecycle
4. Clean up resources in `onDestroy()` or `onDestroyView()`
5. Use `requireContext()` instead of nullable `context` in Fragments when context is required

## Suggestions for Copilot

- When suggesting RecyclerView implementations, use the `BinderRecyclerAdapter` pattern
- Suggest DiffUtil for list updates
- Recommend proper ProGuard rules for custom classes
- Follow Material Design guidelines for UI suggestions
- Suggest Kotlin Coroutines for async operations instead of callbacks

## Development Setup

### Requirements

- **Java**: JDK 17 (Eclipse Temurin distribution recommended)
- **Android SDK**: Compile SDK 35, Min SDK 33, Target SDK 35
- **Build Tool**: Gradle 8.11.1+ (wrapper included)
- **IDE**: Android Studio or IntelliJ IDEA with Android plugin

### Building the Project

Build commands using Gradle wrapper:

```bash
# Build debug APK
./gradlew assembleDebug --stacktrace

# Build release APK (may require signing keys)
./gradlew assembleRelease --stacktrace

# Clean build
./gradlew clean build --stacktrace
```

### Running Tests

```bash
# Run unit tests
./gradlew testDebugUnitTest --stacktrace

# Run instrumentation tests (requires emulator or device)
./gradlew connectedDebugAndroidTest --stacktrace

# Run all checks
./gradlew check --stacktrace
```

### Linting and Code Quality

```bash
# Run Android Lint
./gradlew lintDebug --stacktrace

# Run Detekt (static analysis)
./gradlew detekt --stacktrace

# Run ktlint (code style)
./gradlew ktlintCheck --stacktrace
```

## CI/CD Information

- **Primary CI**: GitHub Actions (`.github/workflows/android.yml`)
- **Build Environment**: macOS runner with Java 17
- **Automated Checks**: Build, unit tests, lint, static analysis
- **Timeout**: 45 minutes for main build job
- **Test Strategy**: Unit tests always run; instrumentation tests currently disabled

## Troubleshooting

### Common Issues

1. **Gradle sync failures**: Ensure Java 17 is installed and `JAVA_HOME` is set correctly
2. **Build failures**: Run `./gradlew clean` before rebuilding
3. **Dependency conflicts**: Check `build.gradle` for resolution strategies
4. **ProGuard issues**: Verify keep rules in `proguard-rules.pro`
5. **Room schema changes**: Schema files are stored in `Smodr/schemas/`

### Memory Settings

The project requires significant memory for builds:
- Gradle JVM args: `-Xmx4g -XX:MaxMetaspaceSize=512m`
- These are configured in CI and may need to be set locally for large builds

