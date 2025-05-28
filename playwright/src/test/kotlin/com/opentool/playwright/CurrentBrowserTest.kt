package com.opentool.playwright

import com.microsoft.playwright.*
import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.lang.reflect.Field
import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi

/**
 * Unit tests for the CurrentBrowser singleton.
 * 
 * These tests use MockK to mock the Playwright API and reflection to access
 * private fields in the CurrentBrowser singleton.
 */
@OptIn(ExperimentalAtomicApi::class)
class CurrentBrowserTest {
    
    // Mocks for Playwright classes
    private val mockPlaywright = mockk<Playwright>(relaxed = true)
    private val mockBrowserType = mockk<BrowserType>(relaxed = true)
    private val mockBrowser = mockk<Browser>(relaxed = true)
    private val mockPage = mockk<Page>(relaxed = true)
    private val mockLocator = mockk<Locator>(relaxed = true)
    
    // Fields for reflection access
    private lateinit var playwrightField: Field
    private lateinit var browserField: Field
    private lateinit var pageField: Field
    private lateinit var lastSnapshotField: Field
    
    @BeforeEach
    fun setUp() {
        // Clear all mocks before each test
        clearAllMocks()
        
        // Set up reflection to access private fields
        val currentBrowserClass = CurrentBrowser::class.java
        
        playwrightField = currentBrowserClass.getDeclaredField("playwright")
        playwrightField.isAccessible = true
        
        browserField = currentBrowserClass.getDeclaredField("browser")
        browserField.isAccessible = true
        
        pageField = currentBrowserClass.getDeclaredField("page")
        pageField.isAccessible = true
        
        lastSnapshotField = currentBrowserClass.getDeclaredField("lastSnapshot")
        lastSnapshotField.isAccessible = true
        
        // Reset the CurrentBrowser state
        resetCurrentBrowser()
        
        // Set up common mock behavior
        every { mockPlaywright.chromium() } returns mockBrowserType
        every { mockBrowserType.launch(any()) } returns mockBrowser
        every { mockBrowser.newPage() } returns mockPage
    }
    
    @AfterEach
    fun tearDown() {
        // Reset the CurrentBrowser state after each test
        resetCurrentBrowser()
    }
    
    /**
     * Resets the CurrentBrowser singleton to its initial state.
     */
    private fun resetCurrentBrowser() {
        playwrightField.set(CurrentBrowser, AtomicReference<Playwright?>(null))
        browserField.set(CurrentBrowser, AtomicReference<Browser?>(null))
        pageField.set(CurrentBrowser, AtomicReference<Page?>(null))
        lastSnapshotField.set(CurrentBrowser, null)
    }
    
    /**
     * Sets up the CurrentBrowser with mock objects for testing.
     */
    private fun setupMockBrowser() {
        playwrightField.set(CurrentBrowser, AtomicReference(mockPlaywright))
        browserField.set(CurrentBrowser, AtomicReference(mockBrowser))
        pageField.set(CurrentBrowser, AtomicReference(mockPage))
    }
    
    @Test
    fun `startBrowser should initialize Playwright and browser`() {
        // Arrange
        mockkStatic(Playwright::class)
        every { Playwright.create() } returns mockPlaywright
        
        // Act
        val result = CurrentBrowser.startBrowser()
        
        // Assert
        assertEquals("Success: Browser started", result)
        verify { mockPlaywright.chromium() }
        verify { mockBrowserType.launch(any()) }
        verify { mockBrowser.newPage() }
    }
    
    @Test
    fun `getPage should return Failure when page is not initialized`() {
        // Act
        val result = CurrentBrowser.getPage()
        
        // Assert
        assertTrue(result is Failure)
        assertEquals("Page is not initialized, use start browser before", (result as Failure).error)
    }
    
    @Test
    fun `getPage should return Success with page when initialized`() {
        // Arrange
        setupMockBrowser()
        
        // Act
        val result = CurrentBrowser.getPage()
        
        // Assert
        assertTrue(result is Success)
        assertEquals(mockPage, (result as Success).page)
    }
    
    @Test
    fun `closeBrowser should close all resources`() {
        // Arrange
        setupMockBrowser()
        
        // Act
        val result = CurrentBrowser.closeBrowser()
        
        // Assert
        assertEquals("Success: Browser closed", result)
        verify { mockPage.close() }
        verify { mockBrowser.close() }
        verify { mockPlaywright.close() }
        
        // Verify references are cleared
        val playwrightRef = playwrightField.get(CurrentBrowser) as AtomicReference<*>
        val browserRef = browserField.get(CurrentBrowser) as AtomicReference<*>
        val pageRef = pageField.get(CurrentBrowser) as AtomicReference<*>
        
        assertNull(playwrightRef.load())
        assertNull(browserRef.load())
        assertNull(pageRef.load())
    }
    
    @Test
    fun `openLink should navigate to URL`() {
        // Arrange
        setupMockBrowser()
        val testUrl = "https://example.com"
        
        // Act
        val result = CurrentBrowser.openLink(testUrl)
        
        // Assert
        assertEquals("Success: Link opened", result)
        verify { mockPage.navigate(testUrl) }
    }
    
    @Test
    fun `openLink should return error when page is not initialized`() {
        // Act
        val result = CurrentBrowser.openLink("https://example.com")
        
        // Assert
        assertTrue(result.startsWith("Error:"))
        assertTrue(result.contains("Page is not initialized"))
    }
    
    @Test
    fun `getSnapshot should capture page snapshot`() {
        // Arrange
        setupMockBrowser()
        val testSnapshot = "Test snapshot content"
        val mockBodyLocator = mockk<Locator>(relaxed = true)
        
        every { mockPage.locator("body") } returns mockBodyLocator
        every { mockBodyLocator.ariaSnapshot() } returns testSnapshot
        
        // Act
        val result = CurrentBrowser.getSnapshot()
        
        // Assert
        assertTrue(result.contains("Success: Snapshot captured"))
        assertTrue(result.contains(testSnapshot))
        assertEquals(testSnapshot, lastSnapshotField.get(CurrentBrowser))
        verify { mockPage.locator("body") }
        verify { mockBodyLocator.ariaSnapshot() }
    }
    
    @Test
    fun `executeSafely should return action result on success`() {
        // Act
        val result = CurrentBrowser.executeSafely { "Success: Test action" }
        
        // Assert
        assertEquals("Success: Test action", result)
    }
    
    @Test
    fun `executeSafely should return error message on exception`() {
        // Act
        val result = CurrentBrowser.executeSafely { throw RuntimeException("Test exception") }
        
        // Assert
        assertEquals("Error: Test exception", result)
    }
    
    @Test
    fun `toLocator should return Failure when snapshot is null`() {
        // Act
        val result = CurrentBrowser.toLocator("[ref=e1]")
        
        // Assert
        assertTrue(result is Failure)
        assertEquals("You need to run the `getSnapshot` tool first.", (result as Failure).error)
    }
    
    @Test
    fun `toLocator should return Failure when reference is not found in snapshot`() {
        // Arrange
        setupMockBrowser()
        lastSnapshotField.set(CurrentBrowser, "Snapshot without the reference")
        
        // Act
        val result = CurrentBrowser.toLocator("[ref=e1]")
        
        // Assert
        assertTrue(result is Failure)
        assertEquals("The ref you provided is not found in the snapshot.", (result as Failure).error)
    }
    
    @Test
    fun `toLocator should return Success with locator when reference is valid`() {
        // Arrange
        setupMockBrowser()
        lastSnapshotField.set(CurrentBrowser, "Snapshot with [ref=e1] reference")
        
        every { mockPage.locator("aria-ref=e1") } returns mockLocator
        
        // Act
        val result = CurrentBrowser.toLocator("[ref=e1]")
        
        // Assert
        assertTrue(result is Success)
        assertEquals(mockLocator, (result as Success).page)
        verify { mockPage.locator("aria-ref=e1") }
    }
}