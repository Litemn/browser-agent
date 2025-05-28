package com.opentool.playwright

import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.agents.core.tools.annotations.Tool
import ai.koog.agents.core.tools.reflect.ToolSet
import com.opentool.Keyboard
import com.opentool.Mouse
import com.opentool.Navigation
import com.opentool.Snapshot

private const val refDescription =
    "Element ref number from the page snapshot, Format: ```[ref=eNUMBER]```, example: ```[ref=e1]``` or ```[ref=e35]```"

/**
 * Implementation of browser automation tools using Playwright.
 * 
 * This class provides concrete implementations of the Snapshot, Mouse, Keyboard,
 * and Navigation interfaces using the Playwright browser automation library.
 * It serves as the main entry point for browser automation functionality in the agent.
 * 
 * The class uses the CurrentBrowser singleton to manage browser state and interact
 * with the Playwright API.
 */
@LLMDescription("Tools for browser automation based on Playwright and snapshots")
class PlaywrightAgentTools : Snapshot, Mouse, Keyboard, Navigation, ToolSet {
    /**
     * Closes the browser and releases all associated resources.
     * 
     * This method should be called when the browser is no longer needed to free up
     * system resources. It's important to call this method to prevent memory leaks.
     * 
     * @return A string indicating the result of the operation
     */
    @Tool
    @LLMDescription("Close the browser, use when you are done with the browser actions")
    fun closeBrowser(): String {
        return CurrentBrowser.executeSafely("Error: Failed to close browser") {
            CurrentBrowser.closeBrowser()
        }
    }

    /**
     * Starts a new browser instance.
     * 
     * This method must be called before any other browser operations can be performed.
     * It initializes the Playwright engine and creates a new browser instance.
     * 
     * @return A string indicating the result of the operation
     */
    @Tool
    @LLMDescription("Start browser, use it before any other action")
    fun startBrowser(): String {
        return CurrentBrowser.executeSafely("Error: Failed to start browser") {
            CurrentBrowser.startBrowser()
        }
    }

    /**
     * Captures and returns a snapshot of the current page state.
     * 
     * The snapshot includes the visible content of the page and reference IDs for
     * elements that can be used with other methods like clickByRef().
     * 
     * @return A string representation of the current page state
     */
    @Tool
    @LLMDescription("Current page state snapshot")
    override fun getSnapshot(): String {
        return CurrentBrowser.executeSafely("Error: Failed to get page snapshot") {
            CurrentBrowser.getSnapshot()
        }
    }

    /**
     * Clicks at the specified coordinates in the browser.
     * 
     * @param x The x-coordinate of the click position
     * @param y The y-coordinate of the click position
     * @return A string describing the result of the operation
     */
    @Tool
    @LLMDescription("Click by coordinates")
    override fun click(x: Int, y: Int): String {
        // Validate coordinates
        if (x < 0 || y < 0) {
            return "Error: Invalid coordinates - x and y must be non-negative"
        }

        return CurrentBrowser.executeSafely("Error: Failed to click at coordinates ($x, $y)") {
            CurrentBrowser.getPage().execute { page ->
                val mouse = page.mouse() ?: throw IllegalStateException("Mouse interface is not available")
                mouse.click(x.toDouble(), y.toDouble())
                return@execute "Success: Clicked at coordinates ($x, $y)"
            }
        }
    }

    /**
     * Clicks on an element identified by its reference ID.
     * 
     * The reference ID is obtained from a page snapshot and has the format [ref=eNUMBER],
     * for example [ref=e1] or [ref=e35].
     * 
     * @param ref The reference ID of the element to click
     * @return A string describing the result of the operation
     */
    @Tool
    @LLMDescription("Click on an element by its ref")
    fun clickByRef(@LLMDescription(refDescription) ref: String): String {
        // Validate reference
        if (ref.isNullOrBlank()) {
            return "Error: Element reference cannot be null or empty"
        }

        return CurrentBrowser.executeSafely("Error: Failed to click on element with reference $ref") {
            CurrentBrowser.toLocator(ref).execute { locator ->
                try {
                    // Check if element is visible before clicking
                    if (!locator.isVisible()) {
                        return@execute "Warning: Element $ref is not visible, click may fail"
                    }

                    locator.click()
                    "Success: Clicked on element $ref"
                } catch (e: Exception) {
                    "Error: Failed to click on $ref - ${e.message ?: "Unknown error"}"
                }
            }
        }
    }

    /**
     * Types the specified text using the keyboard.
     * 
     * This method simulates keyboard input and types the given text into the
     * currently focused element.
     * 
     * @param text The text to type
     * @return A string describing the result of the operation
     */
    @Tool
    @LLMDescription("Type text using the keyboard")
    override fun typeText(text: String): String {
        // Validate text
        if (text.isNullOrEmpty()) {
            return "Error: Text to type cannot be null or empty"
        }

        return CurrentBrowser.executeSafely("Error: Failed to type text: $text") {
            CurrentBrowser.getPage().execute { page ->
                val keyboard = page.keyboard() ?: throw IllegalStateException("Keyboard interface is not available")

                try {
                    keyboard.type(text)
                    return@execute "Success: Typed text \"$text\""
                } catch (e: Exception) {
                    return@execute "Error: Failed to type text - ${e.message ?: "Unknown error"}"
                }
            }
        }
    }

    /**
     * Navigates the browser to the specified URL.
     * 
     * This method loads the page at the given URL and waits for it to be ready.
     * 
     * @param url The URL to navigate to
     * @return A string describing the result of the navigation operation
     */
    @Tool
    @LLMDescription("Open url")
    override fun navigateTo(url: String): String {
        // Validate URL
        if (url.isNullOrBlank()) {
            return "Error: URL cannot be null or empty"
        }

        // Basic URL format validation
        if (!url.startsWith("http://") && !url.startsWith("https://") && !url.startsWith("file://")) {
            return "Warning: URL should start with http://, https://, or file:// - attempting to navigate anyway"
        }

        return CurrentBrowser.executeSafely("Error: Failed to navigate to URL: $url") {
            CurrentBrowser.openLink(url)
        }
    }
}
