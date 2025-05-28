package com.opentool.playwright

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

/**
 * Unit tests for the BrowserResult class and its subclasses.
 */
class BrowserResultTest {

    @Test
    fun `Success execute should return action result`() {
        // Arrange
        val testValue = "test"
        val success = Success(testValue)

        // Act
        val result = success.execute { "Success: $it processed" }

        // Assert
        assertEquals("Success: test processed", result)
    }

    @Test
    fun `Failure execute should return error message`() {
        // Arrange
        val errorMessage = "Error: Test error"
        val failure = Failure<String>(errorMessage)

        // Act
        val result = failure.execute { "This should not be returned" }

        // Assert
        assertEquals(errorMessage, result)
    }

    @Test
    fun `Success execute should handle exceptions in action`() {
        // Arrange
        val testValue = "test"
        val success = Success(testValue)

        // Act
        val result = success.execute { throw RuntimeException("Test exception") }

        // Assert
        assertTrue(result.startsWith("Error executing action:"))
        assertTrue(result.contains("Test exception"))
    }

    // We can't directly test the null page case because BrowserResult is a sealed class
    // and we can't create a subclass with a null value in the test.
    // This functionality is indirectly tested through the CurrentBrowser tests.
}
