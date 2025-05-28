package com.opentool.playwright

/**
 * Sealed class representing the result of a browser operation.
 * 
 * This class is used to handle the result of operations that might fail,
 * such as getting a page or locator. It has two subclasses: Success and Failure.
 * 
 * @param T The type of the result value
 * @param page The result value, or null if the operation failed
 */
internal sealed class BrowserResult<T>(private val page: T?) {
    /**
     * Executes an action on the result if it's a Success, or returns the error message if it's a Failure.
     * 
     * This method provides a convenient way to handle both success and failure cases
     * with a single function call.
     * 
     * @param action The action to execute on the result value if this is a Success
     * @return The result of the action, or the error message if this is a Failure
     */
    fun execute(action: (T) -> String): String {
        return when (this) {
            is Success -> {
                if (page == null) {
                    return "Error: Page or element is not initialized"
                }
                try {
                    action.invoke(page)
                } catch (e: Exception) {
                    "Error executing action: ${e.message ?: "Unknown error"}"
                }
            }
            is Failure -> this.error
        }
    }
}

/**
 * Represents a failed browser operation.
 * 
 * @param T The type of the result value that would have been returned if the operation succeeded
 * @param error The error message describing why the operation failed
 */
internal class Failure<T>(val error: String) : BrowserResult<T>(null)

/**
 * Represents a successful browser operation.
 * 
 * @param T The type of the result value
 * @param page The result value
 */
internal class Success<T>(val page: T) : BrowserResult<T>(page)
