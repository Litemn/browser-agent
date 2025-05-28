package com.opentool

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals

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