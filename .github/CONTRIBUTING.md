# Contributing to Smodr

Thank you for your interest in contributing to Smodr! We welcome contributions from the community.

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [Development Setup](#development-setup)
- [How to Contribute](#how-to-contribute)
- [Coding Standards](#coding-standards)
- [Commit Guidelines](#commit-guidelines)
- [Pull Request Process](#pull-request-process)
- [Reporting Bugs](#reporting-bugs)
- [Suggesting Features](#suggesting-features)

## Code of Conduct

This project adheres to a Code of Conduct. By participating, you are expected to uphold this code.
Please read [CODE_OF_CONDUCT.md](../CODE_OF_CONDUCT.md) before contributing.

## Getting Started

1. **Fork the repository** on GitHub
2. **Clone your fork** locally
   ```bash
   git clone https://github.com/YOUR-USERNAME/smodr.git
   cd smodr
   ```
3. **Add the upstream repository**
   ```bash
   git remote add upstream https://github.com/KevinTCoughlin/smodr.git
   ```
4. **Create a feature branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

## Development Setup

### Prerequisites

- **Android Studio**: Latest stable version
- **JDK**: Version 17 or higher
- **Android SDK**: API level 21 or higher
- **Kotlin**: Latest stable version

### Building the Project

1. Open the project in Android Studio
2. Sync Gradle files
3. Build the project:
   ```bash
   ./gradlew build
   ```

### Running Tests

```bash
# Run all tests
./gradlew test

# Run unit tests
./gradlew testDebugUnitTest

# Run instrumentation tests
./gradlew connectedAndroidTest
```

## How to Contribute

### Types of Contributions

- **Bug fixes**: Fix issues reported in the issue tracker
- **Features**: Implement new functionality
- **Documentation**: Improve or add documentation
- **Tests**: Add or improve test coverage
- **Refactoring**: Improve code quality without changing functionality

### Before You Start

1. Check if an issue already exists for your change
2. For major changes, open an issue first to discuss your approach
3. Make sure your idea aligns with the project's goals

## Coding Standards

### Kotlin Style Guide

- Follow
  the [official Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use meaningful variable and function names
- Prefer `val` over `var` when possible
- Use data classes for simple data holders
- Leverage Kotlin's null safety features

### Android Best Practices

-
Follow [Android Architecture Components guidelines](https://developer.android.com/topic/architecture)
- Use ViewBinding for view access (no findViewById)
- Implement proper lifecycle awareness
- Handle configuration changes appropriately
- Use string resources for all user-facing text

### Project-Specific Patterns

- Use `BinderRecyclerAdapter` for RecyclerView implementations
- Always provide both type arguments for `BinderRecyclerAdapterConfig.Builder<T, VH>()`
- Extend `BinderRecyclerFragment` for list-based fragments
- Use Kotlin Coroutines for async operations

### Code Formatting

- Use Android Studio's built-in formatter
- Run "Reformat Code" (Cmd+Option+L on Mac, Ctrl+Alt+L on Windows/Linux) before committing
- Configure your IDE to organize imports automatically

## Commit Guidelines

We follow the [Conventional Commits](https://www.conventionalcommits.org/) specification:

```
<type>(<scope>): <subject>

<body>

<footer>
```

### Types

- `feat`: A new feature
- `fix`: A bug fix
- `docs`: Documentation only changes
- `style`: Code style changes (formatting, missing semi-colons, etc)
- `refactor`: Code changes that neither fix bugs nor add features
- `perf`: Performance improvements
- `test`: Adding or updating tests
- `chore`: Changes to build process or auxiliary tools

### Examples

```
feat(player): add playback speed control

- Implement speed adjustment UI
- Add playback speed settings
- Update player service to handle speed changes

Closes #123
```

```
fix(feed): resolve crash when parsing invalid RSS feed

Add null safety checks for missing feed elements

Fixes #456
```

## Pull Request Process

1. **Update your branch** with the latest upstream changes:
   ```bash
   git fetch upstream
   git rebase upstream/main
   ```

2. **Ensure all tests pass** locally:
   ```bash
   ./gradlew test
   ```

3. **Commit your changes** following the commit guidelines

4. **Push to your fork**:
   ```bash
   git push origin feature/your-feature-name
   ```

5. **Create a Pull Request** on GitHub
    - Use the PR template
    - Provide a clear description of the changes
    - Reference any related issues
    - Add screenshots for UI changes

6. **Respond to review feedback** promptly

7. **Once approved**, a maintainer will merge your PR

### PR Checklist

- [ ] Code follows the project's style guidelines
- [ ] Self-review completed
- [ ] Comments added for complex code
- [ ] Documentation updated (if needed)
- [ ] No new warnings generated
- [ ] Tests added/updated
- [ ] All tests passing
- [ ] PR description is clear and complete

## Reporting Bugs

Use the [Bug Report template](.github/ISSUE_TEMPLATE/bug_report.yml) to report bugs. Include:

- Clear description of the bug
- Steps to reproduce
- Expected vs actual behavior
- App version, Android version, and device
- Relevant logs or screenshots

## Suggesting Features

Use the [Feature Request template](.github/ISSUE_TEMPLATE/feature_request.yml) to suggest features.
Include:

- Problem statement
- Proposed solution
- Alternative approaches considered
- Priority level

## Questions?

If you have questions about contributing, feel free to:

- Open a [Discussion](https://github.com/KevinTCoughlin/smodr/discussions)
- Reach out to the maintainers

Thank you for contributing to Smodr! 🎉

