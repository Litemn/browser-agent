package com.opentool

/**
 * Represents a message in the chat.
 *
 * @property content The text content of the message
 * @property isFromUser Whether the message is from the user (true) or the agent (false)
 * @property isLoading Whether the message is in a loading state
 */
data class ChatMessage(
    val content: String,
    val isFromUser: Boolean,
    val isLoading: Boolean = false
)

/**
 * Interface for chat service that handles communication with the agent.
 */
interface ChatService {
    /**
     * Sends a message to the agent and returns the response.
     *
     * @param message The message to send
     * @param onResponse Callback for when a response is received
     * @param onError Callback for when an error occurs
     */
    fun sendMessage(
        message: String,
        onResponse: (String) -> Unit,
        onError: (String) -> Unit
    )

    /**
     * Checks if the service is available.
     *
     * @return true if the service is available, false otherwise
     */
    fun isAvailable(): Boolean

    /**
     * Returns a message explaining why the service is not available, if applicable.
     *
     * @return An error message or null if the service is available
     */
    fun getUnavailableReason(): String?
}

/**
 * Gets the chat service implementation for the current platform.
 */
expect fun getChatService(): ChatService