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

