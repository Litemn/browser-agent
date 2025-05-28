package com.opentool.playwright

import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

/**
 * Unit tests for the PlaywrightAgentTools class.
 * 
 * These tests use MockK to mock the CurrentBrowser singleton and verify that
 * the PlaywrightAgentTools methods correctly delegate to the CurrentBrowser methods.
 */
class PlaywrightAgentToolsTest {
    
    private lateinit var tools: PlaywrightAgentTools
    
    @BeforeEach
    fun setUp() {
        // Clear all mocks before each test
        clearAllMocks()
        
        // Mock the CurrentBrowser singleton
        mockkObject(CurrentBrowser)
        
        // Create a new instance of PlaywrightAgentTools for each test
        tools = PlaywrightAgentTools()
    }
    
    @Test
    fun `closeBrowser should delegate to CurrentBrowser`() {
        // Arrange
        every { CurrentBrowser.executeSafely(any(), any()) } returns "Success: Browser closed"
        
        // Act
        val result = tools.closeBrowser()
        
        // Assert
        assertEquals("Success: Browser closed", result)
        verify { CurrentBrowser.executeSafely(eq("Error: Failed to close browser"), any()) }
    }
    
    @Test
    fun `startBrowser should delegate to CurrentBrowser`() {
        // Arrange
        every { CurrentBrowser.executeSafely(any(), any()) } returns "Success: Browser started"
        
        // Act
        val result = tools.startBrowser()
        
        // Assert
        assertEquals("Success: Browser started", result)
        verify { CurrentBrowser.executeSafely(eq("Error: Failed to start browser"), any()) }
    }
    
    @Test
    fun `getSnapshot should delegate to CurrentBrowser`() {
        // Arrange
        every { CurrentBrowser.executeSafely(any(), any()) } returns "Success: Snapshot captured\nSnapshot content"
        
        // Act
        val result = tools.getSnapshot()
        
        // Assert
        assertEquals("Success: Snapshot captured\nSnapshot content", result)
        verify { CurrentBrowser.executeSafely(eq("Error: Failed to get page snapshot"), any()) }
    }
    
    @Test
    fun `click should validate coordinates`() {
        // Act
        val result = tools.click(-1, 10)
        
        // Assert
        assertEquals("Error: Invalid coordinates - x and y must be non-negative", result)
        verify(exactly = 0) { CurrentBrowser.executeSafely(any(), any()) }
    }
    
    @Test
    fun `click should delegate to CurrentBrowser when coordinates are valid`() {
        // Arrange
        val x = 100
        val y = 200
        val mockPage = mockk<Success<com.microsoft.playwright.Page>>(relaxed = true)
        
        every { CurrentBrowser.getPage() } returns mockPage
        every { CurrentBrowser.executeSafely(any(), any()) } answers { 
            secondArg<() -> String>().invoke()
        }
        every { mockPage.execute(any()) } returns "Success: Clicked at coordinates ($x, $y)"
        
        // Act
        val result = tools.click(x, y)
        
        // Assert
        assertEquals("Success: Clicked at coordinates ($x, $y)", result)
        verify { CurrentBrowser.executeSafely(eq("Error: Failed to click at coordinates ($x, $y)"), any()) }
        verify { CurrentBrowser.getPage() }
        verify { mockPage.execute(any()) }
    }
    
    @Test
    fun `clickByRef should validate reference`() {
        // Act
        val result = tools.clickByRef("")
        
        // Assert
        assertEquals("Error: Element reference cannot be null or empty", result)
        verify(exactly = 0) { CurrentBrowser.executeSafely(any(), any()) }
    }
    
    @Test
    fun `clickByRef should delegate to CurrentBrowser when reference is valid`() {
        // Arrange
        val ref = "[ref=e1]"
        val mockLocator = mockk<Success<com.microsoft.playwright.Locator>>(relaxed = true)
        
        every { CurrentBrowser.toLocator(ref) } returns mockLocator
        every { CurrentBrowser.executeSafely(any(), any()) } answers { 
            secondArg<() -> String>().invoke()
        }
        every { mockLocator.execute(any()) } returns "Success: Clicked on element $ref"
        
        // Act
        val result = tools.clickByRef(ref)
        
        // Assert
        assertEquals("Success: Clicked on element $ref", result)
        verify { CurrentBrowser.executeSafely(eq("Error: Failed to click on element with reference $ref"), any()) }
        verify { CurrentBrowser.toLocator(ref) }
        verify { mockLocator.execute(any()) }
    }
    
    @Test
    fun `typeText should validate text`() {
        // Act
        val result = tools.typeText("")
        
        // Assert
        assertEquals("Error: Text to type cannot be null or empty", result)
        verify(exactly = 0) { CurrentBrowser.executeSafely(any(), any()) }
    }
    
    @Test
    fun `typeText should delegate to CurrentBrowser when text is valid`() {
        // Arrange
        val text = "Hello, World!"
        val mockPage = mockk<Success<com.microsoft.playwright.Page>>(relaxed = true)
        
        every { CurrentBrowser.getPage() } returns mockPage
        every { CurrentBrowser.executeSafely(any(), any()) } answers { 
            secondArg<() -> String>().invoke()
        }
        every { mockPage.execute(any()) } returns "Success: Typed text \"$text\""
        
        // Act
        val result = tools.typeText(text)
        
        // Assert
        assertEquals("Success: Typed text \"$text\"", result)
        verify { CurrentBrowser.executeSafely(eq("Error: Failed to type text: $text"), any()) }
        verify { CurrentBrowser.getPage() }
        verify { mockPage.execute(any()) }
    }
    
    @Test
    fun `navigateTo should validate URL`() {
        // Act
        val result = tools.navigateTo("")
        
        // Assert
        assertEquals("Error: URL cannot be null or empty", result)
        verify(exactly = 0) { CurrentBrowser.executeSafely(any(), any()) }
    }
    
    @Test
    fun `navigateTo should warn about invalid URL format`() {
        // Act
        val result = tools.navigateTo("example.com")
        
        // Assert
        assertTrue(result.startsWith("Warning:"))
        assertTrue(result.contains("URL should start with http://, https://, or file://"))
        verify(exactly = 0) { CurrentBrowser.executeSafely(any(), any()) }
    }
    
    @Test
    fun `navigateTo should delegate to CurrentBrowser when URL is valid`() {
        // Arrange
        val url = "https://example.com"
        
        every { CurrentBrowser.executeSafely(any(), any()) } returns "Success: Link opened"
        
        // Act
        val result = tools.navigateTo(url)
        
        // Assert
        assertEquals("Success: Link opened", result)
        verify { CurrentBrowser.executeSafely(eq("Error: Failed to navigate to URL: $url"), any()) }
    }
}