package com.opentool

/**
 * Interface for capturing snapshots of the browser state.
 * 
 * This interface provides methods for capturing the current state of the browser,
 * such as the visible content of the page, which can be used for analysis or debugging.
 */
interface Snapshot {
    /**
     * Captures and returns a snapshot of the current browser state.
     * 
     * @return A string representation of the current browser state
     */
    fun getSnapshot(): String
}
