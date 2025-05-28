# Browser Agent Project Guidelines

This document provides guidelines and information for developers working on the Browser Agent project.

## Build/Configuration Instructions

### Project Structure

The project is organized into several modules:

- **agent**: Core agent functionality, including strategy
- **playwright**: Playwright tool implementation
- **tools**: Agent tools interfaces
- **example**: Example of using the agent

### Build Configuration

The project uses Gradle with Kotlin DSL for build configuration. Key configuration files:

- Module-specific `build.gradle.kts` files
- `gradle.properties` for project-wide properties
- Shared build logic in `buildSrc/src/main/kotlin/kotlin-jvm.gradle.kts`

### Build Commands

```bash
# Build the entire project
./gradlew build

# Build a specific module
./gradlew :agent:build
./gradlew :tools:build
./gradlew :playwright:build
```

### Dependencies

- Kotlin JVM (Java 17 toolchain)
- Playwright for browser automation
- Koog agents framework, version defined in `gradle.properties`
- JUnit 5 for testing

## Testing Information

### Test Configuration

The project uses JUnit 5 for testing. Test configuration is specified in each module's `build.gradle.kts` file:

```kotlin
tasks.test {
    useJUnitPlatform()
}
```

### Running Tests

```bash
# Run all tests
./gradlew test

# Run tests for a specific module
./gradlew :tools:test
./gradlew :agent:test
./gradlew :playwright:test

# Run a specific test class
./gradlew :tools:test --tests "com.opentool.KeyboardTest"

# Run a specific test method
./gradlew :tools:test --tests "com.opentool.KeyboardTest.testMockKeyboard"
```

### Adding New Tests

1. Create test classes in the `src/test/kotlin` directory of the appropriate module
2. Use JUnit 5 annotations (`@Test`, etc.)
3. Follow the naming convention: `*Test.kt` for test files
4. Place tests in the same package as the code being tested

### Example Test

Here's a simple test for the `Keyboard` interface:

Note: The following code requires these imports:

- `import org.junit.jupiter.api.Test`
- `import org.junit.jupiter.api.Assertions.assertEquals`

```kotlin
class KeyboardTest {

    @Test
    fun testMockKeyboard() {
        // Create a mock implementation of the Keyboard interface
        val mockKeyboard = object : Keyboard {
            override fun typeText(text: String): String {
                return "Typed: $text"
            }
        }

        // Test the mock implementation
        val result = mockKeyboard.typeText("Hello, World!")
        assertEquals("Typed: Hello, World!", result)
    }
}
```

## Additional Development Information

### Code Style

- Follow Kotlin coding conventions
- Use interfaces for defining tool capabilities (see `tools` module)
- Implement interfaces in concrete classes (e.g., `BrowserTool`)
- Each tool function must return a `String`
- Use annotations for tool descriptions:
    - `@Tool` to mark methods as tools
    - `@LLMDescription` to provide descriptions for tools and parameters

### Browser Automation

The project uses Playwright for browser automation:

- Browser initialization is handled in `BrowserTool.startBrowser()`
- Page navigation in `BrowserTool.openPage()`
- Element interaction through locators (see `BrowserTool.toLocator()`)

### Error Handling

- Use try-catch blocks for operations that might fail
- Return meaningful error messages
- Check for null values and throw appropriate exceptions

### Tool Development

When adding new tools:

1. Define the interface in the `tools` module
2. Implement the interface in the appropriate module
3. Add `@Tool` and `@LLMDescription` annotations
4. Write tests for the new functionality
