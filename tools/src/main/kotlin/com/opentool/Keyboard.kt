package com.opentool

/**
 * Interface for keyboard operations in the browser.
 * 
 * This interface provides methods for simulating keyboard input in a browser,
 * such as typing text into input fields or forms.
 */
interface Keyboard {
    /**
     * Types the specified text into the currently focused element.
     * 
     * @param text The text to type
     * @return A string describing the result of the operation
     */
    fun typeText(text: String): String
}
