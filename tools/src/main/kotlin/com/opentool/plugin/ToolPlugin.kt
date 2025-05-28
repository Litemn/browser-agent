package com.opentool.plugin

import ai.koog.agents.core.tools.Tool

/**
 * Interface for tool plugins in the Browser Agent.
 * 
 * A tool plugin provides a set of tools that can be used by the agent.
 * Plugins can be registered with the ToolPluginRegistry to make them
 * available to the agent.
 */
interface ToolPlugin {
    /**
     * Get the unique identifier for this plugin.
     * This ID should be unique across all plugins.
     */
    val id: String
    
    /**
     * Get a human-readable name for this plugin.
     */
    val name: String
    
    /**
     * Get a description of this plugin.
     */
    val description: String
    
    /**
     * Get the tools provided by this plugin.
     * 
     * @return A list of Tool instances that can be used by the agent.
     */
    fun getTools(): List<Tool<*, *>>
}