package com.opentool

/**
 * Interface for mouse operations in the browser.
 * 
 * This interface provides methods for simulating mouse input in a browser,
 * such as clicking on elements at specific coordinates.
 */
interface Mouse {
    /**
     * Clicks at the specified coordinates in the browser.
     * 
     * @param x The x-coordinate of the click position
     * @param y The y-coordinate of the click position
     * @return A string describing the result of the operation
     */
    fun click(x: Int, y: Int): String
}
