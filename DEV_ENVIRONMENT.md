# Smodr Development Environment

This document describes the modern development environment setup for the Smodr Android podcast player.

## Quick Start

### Option 1: Development Container (Recommended)

The easiest way to get started is using the devcontainer:

1. Install [Docker](https://www.docker.com/) and [VS Code](https://code.visualstudio.com/)
2. Install the [Dev Containers extension](https://marketplace.visualstudio.com/items?itemName=ms-vscode-remote.remote-containers)
3. Clone this repository
4. Open in VS Code and click "Reopen in Container" when prompted
5. Wait for the container to build and dependencies to install

The devcontainer includes:
- Java 17 (Temurin distribution)
- Android SDK with API 34
- Gradle 8.11.1
- All recommended VS Code extensions
- Pre-configured development settings

### Option 2: Local Development

If you prefer local development:

1. Install [Java 17](https://adoptium.net/)
2. Install [Android Studio](https://developer.android.com/studio) or [Android SDK](https://developer.android.com/studio#cmdline-tools)
3. Install [VS Code](https://code.visualstudio.com/) with recommended extensions
4. Open the workspace file: `smodr.code-workspace`

## Development Tools

### VS Code Configuration

The project includes comprehensive VS Code configuration:

- **Workspace file**: `smodr.code-workspace` - Open this for the best experience
- **Settings**: Optimized for Android/Kotlin development
- **Extensions**: Automatic recommendations for essential tools
- **Tasks**: Pre-configured build, test, and lint tasks
- **File nesting**: Organized project structure in the explorer

### Recommended Extensions

The following extensions are automatically suggested:

- **Kotlin Language Support**: `ms-vscode.vscode-kotlin`
- **Java Extension Pack**: `vscjava.vscode-java-pack`
- **Gradle for Java**: `ms-vscode.gradle-for-java`
- **GitLens**: `eamodio.gitlens`
- **GitHub Copilot**: `github.copilot` (if you have access)

### Build Tasks

Access via `Ctrl+Shift+P` → "Tasks: Run Task":

- **Build Debug**: Build debug APK
- **Build Release**: Build release APK
- **Clean Build**: Clean and rebuild project
- **Run Tests**: Execute unit tests
- **Run Lint**: Android lint analysis
- **Install Debug APK**: Install debug build to connected device

## Project Structure

```
smodr/
├── .devcontainer/          # Development container configuration
├── .github/                # GitHub Actions workflows
├── .vscode/                # VS Code settings and extensions
├── Smodr/                  # Main Android application module
├── common-android/         # Shared Android library
├── smodr.code-workspace    # VS Code workspace configuration
└── README.md              # This file
```

## GitHub Actions

The project includes several automated workflows:

- **Android CI**: Main build and test pipeline
- **CodeQL Analysis**: Security and code quality scanning
- **Development CI**: Validation for pull requests

## Dependency Management

[Dependabot](https://github.com/dependabot) is configured to automatically:
- Update GitHub Actions weekly (Mondays)
- Update Gradle dependencies weekly (Tuesdays)
- Group related updates for easier review
- Maintain security with automatic security updates

## Getting Help

If you encounter issues:

1. Check the [GitHub Issues](https://github.com/cascadiacollections/smodr/issues)
2. Review the build logs in GitHub Actions
3. Ensure you're using the recommended development environment

## Contributing

When contributing:

1. Use the devcontainer or VS Code workspace for consistency
2. Run `./gradlew lint` before submitting
3. Ensure all GitHub Actions pass
4. Follow the existing code style (enforced by EditorConfig)

The development environment is designed to provide a consistent, productive experience across all contributors.