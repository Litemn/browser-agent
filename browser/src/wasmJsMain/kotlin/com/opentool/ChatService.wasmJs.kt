package com.opentool

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * WASM implementation of ChatService that simulates agent behavior.
 * 
 * Note: This is a mock implementation for demonstration purposes.
 * In a real implementation, this would use the BrowserAgent or a web API.
 */
class WasmChatService : ChatService {
    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    
    // Sample responses for demonstration
    private val sampleResponses = listOf(
        "I've navigated to the requested page and found the information you asked for.",
        "I've completed the task. Here's what I found: The latest version is 2.0.3, released yesterday.",
        "I've analyzed the webpage and found 5 relevant links that match your criteria.",
        "I've searched for the information but couldn't find exactly what you're looking for. Would you like me to try a different approach?",
        "I've completed the multi-step process you requested. The final result shows that the operation was successful."
    )

    override fun sendMessage(
        message: String,
        onResponse: (String) -> Unit,
        onUpdate: (String) -> Unit,
        onError: (String) -> Unit,
        onStatus: (String) -> Unit
    ) {
        if (!isAvailable()) {
            onError(getUnavailableReason() ?: "Service is not available")
            return
        }
        
        // Simulate processing time
        coroutineScope.launch {
            try {
                // Simulate thinking time
                delay(1500 + Random.nextLong(1000))
                
                // Generate a response based on the input
                val response = if (message.contains("error", ignoreCase = true)) {
                    // Simulate an error if the message contains "error"
                    throw Exception("Simulated error in processing")
                } else {
                    // Choose a random response or generate one based on the input
                    val baseResponse = sampleResponses[Random.nextInt(sampleResponses.size)]
                    
                    // Add some context from the user's message
                    val keywords = extractKeywords(message)
                    if (keywords.isNotEmpty()) {
                        "$baseResponse I noticed you mentioned ${keywords.joinToString(", ")}."
                    } else {
                        baseResponse
                    }
                }
                
                // Update the UI
                onResponse(response)
            } catch (e: Exception) {
                onError("Error: ${e.message}")
            }
        }
    }
    
    private fun extractKeywords(message: String): List<String> {
        // Simple keyword extraction for demonstration
        val commonWords = setOf("the", "a", "an", "and", "or", "but", "in", "on", "at", "to", "for", "with", "by")
        return message.split(" ", ".", ",", "!", "?")
            .filter { it.length > 3 && it.lowercase() !in commonWords }
            .take(2)
    }
    
    override fun isAvailable(): Boolean {
        // In a real implementation, this would check if the agent is properly initialized
        return true // For demo purposes, always available
    }
    
    override fun getUnavailableReason(): String? {
        // In a real implementation, this would return the reason why the agent is not available
        return null // Always available in WASM implementation
    }
}

/**
 * Gets the chat service implementation for the WASM platform.
 */
actual fun getChatService(listener: UpdateListener): ChatService = WasmChatService()