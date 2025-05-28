# Browser Agent

A Kotlin-based project for browser automation and agent-based interaction with web browsers.

## Project Overview

The Browser Agent project provides a framework for automating browser interactions using agent-based architecture. It leverages Playwright for browser automation and the Koog agents framework for agent functionality.

## Project Structure

The project is organized into several modules:

- **agent**: Core agent functionality, including strategy
- **playwright**: Playwright tool implementation
- **tools**: Agent tools interfaces
- **example**: Example of using the agent

## Build/Configuration Instructions

This project uses [Gradle](https://gradle.org/) for build automation.

### Build Commands

```bash
# Build the entire project
./gradlew build

# Build a specific module
./gradlew :agent:build
./gradlew :tools:build
./gradlew :playwright:build

# Run the application
./gradlew run

# Run all checks, including tests
./gradlew check

# Clean all build outputs
./gradlew clean
```

Note the usage of the Gradle Wrapper (`./gradlew`).
This is the suggested way to use Gradle in production projects.

[Learn more about the Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html).

[Learn more about Gradle tasks](https://docs.gradle.org/current/userguide/command_line_interface.html#common_tasks).

### Build Configuration

The project uses Gradle with Kotlin DSL for build configuration. Key configuration files:

- Module-specific `build.gradle.kts` files
- `gradle.properties` for project-wide properties
- Shared build logic in `buildSrc/src/main/kotlin/kotlin-jvm.gradle.kts`
- Version catalog (see `gradle/libs.versions.toml`) to declare and version dependencies

### Dependencies

- Kotlin JVM (Java 17 toolchain)
- Playwright for browser automation
- Koog agents framework, version defined in `gradle.properties`
- JUnit 5 for testing
