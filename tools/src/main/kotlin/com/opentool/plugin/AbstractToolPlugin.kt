package com.opentool.plugin

import ai.koog.agents.core.tools.Tool
import ai.koog.agents.core.tools.reflect.ToolSet
import ai.koog.agents.core.tools.reflect.asTools

/**
 * Abstract base class for tool plugins.
 * 
 * This class provides common functionality and default implementations
 * for the ToolPlugin interface. It makes it easier to implement new plugins
 * by handling the conversion of ToolSet instances to Tool instances.
 */
abstract class AbstractToolPlugin : ToolPlugin {
    /**
     * Get the tools provided by this plugin.
     * 
     * This default implementation converts ToolSet instances to Tool instances
     * using the asTools() extension function. Override this method if you need
     * custom tool creation logic.
     * 
     * @return A list of Tool instances that can be used by the agent.
     */
    override fun getTools(): List<Tool<*, *>> {
        return getToolSets().flatMap { it.asTools() }
    }
    
    /**
     * Get the ToolSet instances provided by this plugin.
     * 
     * Override this method to provide the ToolSet instances that contain
     * the tools for this plugin.
     * 
     * @return A list of ToolSet instances.
     */
    protected abstract fun getToolSets(): List<ToolSet>
}