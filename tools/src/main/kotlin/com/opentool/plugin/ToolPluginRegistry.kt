package com.opentool.plugin

import ai.koog.agents.core.tools.Tool
import java.util.ServiceLoader

/**
 * Registry for tool plugins in the Browser Agent.
 * 
 * This class manages the registration and discovery of tool plugins.
 * Plugins can be registered manually or discovered automatically using
 * the Java ServiceLoader mechanism.
 */
class ToolPluginRegistry {
    private val plugins = mutableMapOf<String, ToolPlugin>()
    
    /**
     * Register a plugin with the registry.
     * 
     * @param plugin The plugin to register.
     * @throws IllegalArgumentException If a plugin with the same ID is already registered.
     */
    fun registerPlugin(plugin: ToolPlugin) {
        if (plugins.containsKey(plugin.id)) {
            throw IllegalArgumentException("Plugin with ID ${plugin.id} is already registered")
        }
        plugins[plugin.id] = plugin
    }
    
    /**
     * Unregister a plugin from the registry.
     * 
     * @param pluginId The ID of the plugin to unregister.
     * @return True if the plugin was unregistered, false if it wasn't registered.
     */
    fun unregisterPlugin(pluginId: String): Boolean {
        return plugins.remove(pluginId) != null
    }
    
    /**
     * Get a plugin by its ID.
     * 
     * @param pluginId The ID of the plugin to get.
     * @return The plugin, or null if no plugin with the given ID is registered.
     */
    fun getPlugin(pluginId: String): ToolPlugin? {
        return plugins[pluginId]
    }
    
    /**
     * Get all registered plugins.
     * 
     * @return A list of all registered plugins.
     */
    fun getAllPlugins(): List<ToolPlugin> {
        return plugins.values.toList()
    }
    
    /**
     * Get all tools from all registered plugins.
     * 
     * @return A list of all tools from all registered plugins.
     */
    fun getAllTools(): List<Tool<*, *>> {
        return plugins.values.flatMap { it.getTools() }
    }
    
    /**
     * Discover plugins using the Java ServiceLoader mechanism.
     * 
     * This method loads plugins that are registered using the ServiceLoader
     * mechanism. Plugins must implement the ToolPlugin interface and be
     * registered in a file named META-INF/services/com.opentool.plugin.ToolPlugin.
     * 
     * @return The number of plugins discovered and registered.
     */
    fun discoverPlugins(): Int {
        val serviceLoader = ServiceLoader.load(ToolPlugin::class.java)
        var count = 0
        
        for (plugin in serviceLoader) {
            try {
                registerPlugin(plugin)
                count++
            } catch (e: IllegalArgumentException) {
                // Plugin with this ID is already registered, skip it
            }
        }
        
        return count
    }
    
    companion object {
        private val instance = ToolPluginRegistry()
        
        /**
         * Get the singleton instance of the registry.
         * 
         * @return The singleton instance.
         */
        fun getInstance(): ToolPluginRegistry {
            return instance
        }
    }
}