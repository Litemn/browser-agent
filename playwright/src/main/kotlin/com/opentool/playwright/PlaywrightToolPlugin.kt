//package com.opentool.playwright
//
//import ai.koog.agents.core.tools.reflect.ToolSet
//import com.opentool.plugin.AbstractToolPlugin
//
///**
// * Plugin that provides Playwright-based browser automation tools.
// *
// * This plugin wraps the PlaywrightAgentTools class and makes it available
// * through the plugin system.
// */
//class PlaywrightToolPlugin : AbstractToolPlugin() {
//    override val id: String = "playwright"
//    override val name: String = "Playwright Browser Tools"
//    override val description: String = "Browser automation tools based on Playwright"
//
//    /**
//     * Get the ToolSet instances provided by this plugin.
//     *
//     * @return A list containing the PlaywrightAgentTools instance.
//     */
//    override fun getToolSets(): List<ToolSet> {
//        return listOf(PlaywrightAgentTools())
//    }
//}