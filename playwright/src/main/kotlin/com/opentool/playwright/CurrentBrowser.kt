package com.opentool.playwright

import com.microsoft.playwright.*
import java.security.MessageDigest
import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi

/**
 * Singleton object that manages the browser state for Playwright automation.
 *
 * This object is responsible for creating and managing the Playwright browser instance,
 * handling page navigation, and providing access to browser functionality. It uses
 * atomic references to ensure thread safety when accessing browser resources.
 *
 * The object maintains the state of the browser, page, and snapshot, and provides
 * methods for interacting with these resources.
 */
@OptIn(ExperimentalAtomicApi::class)
object CurrentBrowser {
    private val playwright: AtomicReference<Playwright?> = AtomicReference(null)
    private val browser: AtomicReference<Browser?> = AtomicReference(null)
    private val page: AtomicReference<Page?> = AtomicReference(null)
    private var lastSnapshot: String? = null
    private val pageHashes = mutableMapOf<String, String>()

    fun page(): Page? {
        return page.load()
    }

    /**
     * Starts a new browser instance.
     *
     * This method initializes the Playwright engine, launches a Chrome browser,
     * and creates a new page. It should be called before any other browser operations.
     *
     * @return A string indicating the result of the operation
     */
    fun startBrowser(headless: Boolean): String {
        return executeSafely {
            playwright.compareAndSet(null, Playwright.create())
            val playwrightInstance = playwright.load() ?: throw IllegalStateException("Failed to initialize Playwright")
            browser.exchange(playwrightInstance.chromium().launch(BrowserType.LaunchOptions().setHeadless(headless)))
            val browserInstance = browser.load() ?: throw IllegalStateException("Failed to launch browser")
            page.exchange(browserInstance.newPage())
            "Success: Browser started"
        }
    }

    /**
     * Gets the current page instance.
     *
     * This internal method is used by other methods to access the current page.
     * It returns a BrowserResult that either contains the page or an error message.
     *
     * @return A BrowserResult containing either the Page instance or an error message
     */
    internal fun getPage(): BrowserResult<Page> {
        val load = page.load() ?: return Failure("Page is not initialized, use start browser before")
        val url = load.url()
        pageHashes[url] = hashOfPage(load)
        return Success(load)
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun hashOfPage(page: Page): String {
        val instance = MessageDigest.getInstance("MD5")
        val s = instance.digest(page.content()?.toByteArray())
        return s.toHexString()
    }

    fun isChanged(page: Page): Boolean {
        val hash = pageHashes[page.url()] ?: return false
        return hash != hashOfPage(page)
    }

    /**
     * Closes the browser and releases all associated resources.
     *
     * This method closes the current page, browser, and Playwright instance,
     * releasing all system resources. It should be called when the browser is
     * no longer needed to prevent memory leaks.
     *
     * The method uses try-finally blocks to ensure resources are properly closed
     * even if exceptions occur during the cleanup process.
     *
     * @return A string indicating the result of the operation
     */
    fun closeBrowser(): String {
        return executeSafely {
            // Close resources in reverse order of creation using try-finally blocks
            // to ensure proper cleanup even if exceptions occur
            try {
                // Close page if it exists
                val currentPage = page.load()
                if (currentPage != null) {
                    try {
                        currentPage.close()
                    } finally {
                        page.exchange(null) // Always clear the reference
                    }
                }
            } finally {
                try {
                    // Close browser if it exists
                    val currentBrowser = browser.load()
                    if (currentBrowser != null) {
                        try {
                            currentBrowser.close()
                        } finally {
                            browser.exchange(null) // Always clear the reference
                        }
                    }
                } finally {
                    // Close playwright if it exists
                    val currentPlaywright = playwright.load()
                    if (currentPlaywright != null) {
                        try {
                            currentPlaywright.close()
                        } finally {
                            playwright.exchange(null) // Always clear the reference
                        }
                    }
                }
            }

            "Success: Browser closed"
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
    fun openLink(url: String): String {
        return executeSafely("Error: Failed to open link $url") {
            val currentPage = page.load()
                ?: throw IllegalStateException("Page is not initialized, use startBrowser before opening a link")
            currentPage.navigate(url)
            "Success: Link opened"
        }
    }

    /**
     * Captures and returns a snapshot of the current page state.
     *
     * The snapshot includes the visible content of the page and reference IDs for
     * elements that can be used with other methods like toLocator().
     *
     * @return A string representation of the current page state
     */
    fun getSnapshot(): String {
        return getPage().execute { page ->
            val bodyLocator = page.locator("body")
            val snapshot = bodyLocator.ariaSnapshot(Locator.AriaSnapshotOptions().setRef(true))

            if (snapshot.isNullOrEmpty()) {
                throw IllegalStateException("Failed to capture page snapshot: empty snapshot returned")
            }

            lastSnapshot = snapshot
            "Success: Snapshot captured\n$snapshot"
        }
    }

    /**
     * Executes an action safely, catching any exceptions.
     *
     * This utility method is used by other methods to execute actions and handle
     * exceptions in a consistent way.
     *
     * @param defaultError The error message to return if an exception occurs and has no message
     * @param action The action to execute
     * @return The result of the action, or an error message if an exception occurs
     */
    fun executeSafely(defaultError: String = "Error: Unknown error", action: () -> String): String {
        return try {
            action.invoke()
        } catch (e: Exception) {
            // Ensure error messages follow the standardized format
            val errorMessage = e.message ?: defaultError
            if (!errorMessage.startsWith("Error:") && !errorMessage.startsWith("Warning:") && !errorMessage.startsWith("Success:")) {
                "Error: $errorMessage"
            } else {
                errorMessage
            }
        }
    }

    /**
     * Converts a reference ID to a Playwright Locator.
     *
     * This internal method is used to convert a reference ID from a snapshot to a
     * Playwright Locator that can be used to interact with the element.
     *
     * @param ref The reference ID of the element
     * @return A BrowserResult containing either the Locator or an error message
     */
    internal fun toLocator(ref: String): BrowserResult<Locator> {
        // Check if snapshot exists
        if (lastSnapshot == null) {
            return Failure("You need to run the `getSnapshot` tool first.")
        }

        // Validate reference format
        if (ref.isBlank()) {
            return Failure("Element reference cannot be null or empty.")
        }

        // Check if reference exists in snapshot
        if (!lastSnapshot!!.contains(ref)) {
            return Failure("The ref you provided is not found in the snapshot, get new page state with `getSnapshot`")
        }

        // Extract the reference ID
        val cleanRef = ref.trim().removePrefix("[ref=").removeSuffix("]")
        if (cleanRef.isBlank()) {
            return Failure("Invalid reference format. Expected format: [ref=eNUMBER]")
        }

        val locatorSelector = "aria-ref=$cleanRef"

        // Get page and create locator
        val page = getPage()
        return when (page) {
            is Failure -> Failure("Failed to get page: ${page.error}, start browser before")
            is Success -> {
                val locator = page.page.locator(locatorSelector)
                // Verify locator exists on page
                if (locator == null) {
                    Failure("Failed to create locator for reference: $ref")
                } else {
                    Success(locator)
                }
            }
        }
    }
}
