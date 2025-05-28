package com.opentool

/**
 * Interface for navigation operations in the browser.
 * 
 * This interface provides methods for navigating to URLs and managing
 * browser navigation state.
 */
interface Navigation {
    /**
     * Navigates the browser to the specified URL.
     * 
     * @param url The URL to navigate to
     * @return A string describing the result of the navigation operation
     */
    fun navigateTo(url: String): String
}
