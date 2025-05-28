package com.opentool.agent.core

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.config.AIAgentConfig
import ai.koog.agents.core.agent.entity.AIAgentStrategy
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.local.features.eventHandler.feature.EventHandler
import ai.koog.prompt.executor.clients.LLMClient
import ai.koog.prompt.executor.llms.SingleLLMPromptExecutor
import ai.koog.prompt.executor.model.PromptExecutor
import com.opentool.agent.BrowserAgentSettings
import com.opentool.plugin.ToolPluginRegistry

/**
 * Core class for the Browser Agent.
 * 
 * This class provides the core functionality of the Browser Agent,
 * independent of the specific browser automation implementation.
 * It uses the plugin system to load tools from registered plugins.
 */
class BrowserAgentCore(
    private val llmClient: LLMClient,
    private val strategy: AIAgentStrategy,
    private val agentConfig: AIAgentConfig,
    private val pluginRegistry: ToolPluginRegistry = ToolPluginRegistry.getInstance()
) {
    /**
     * Create an AIAgent with the configured settings and tools.
     * 
     * @return An AIAgent instance.
     */
    fun createAgent(): AIAgent {
        // Discover plugins if they haven't been discovered yet
        if (pluginRegistry.getAllPlugins().isEmpty()) {
            pluginRegistry.discoverPlugins()
        }

        // Get all tools from all registered plugins
        val tools = pluginRegistry.getAllTools()

        // Create prompt executor
        val promptExecutor: PromptExecutor = SingleLLMPromptExecutor(
            llmClient = llmClient
        )

        // Create tool registry
        val toolRegistry = ToolRegistry {
            tools(tools)
        }

        // Create and return the agent
        return AIAgent(
            promptExecutor = promptExecutor,
            strategy = strategy,
            agentConfig = agentConfig,
            toolRegistry = toolRegistry
        ) {
            install(EventHandler) {
                onToolCall = { tool, arg ->
                    println("Tool call: ${tool.name} with args: $arg")
                }
                onAgentRunError = { error, conf ->
                    println("Error: $error $conf")
                }
                onToolCallResult = { tool, toolArgs, result ->
                    println("Result: ${tool.name}, $toolArgs, ${result?.toStringDefault()}")
                }
                onAgentFinished = { strategyName, result ->
                    println("Agent finished: $strategyName, $result")
                }
            }
        }
    }

    /**
     * Create a BrowserAgentCore from BrowserAgentSettings.
     * 
     * @param settings The settings to use.
     * @return A BrowserAgentCore instance.
     */
    companion object {
        fun fromSettings(settings: BrowserAgentSettings): BrowserAgentCore {
            return BrowserAgentCore(
                llmClient = settings.llmClient,
                strategy = settings.strategy,
                agentConfig = settings.agentConfig
            )
        }
    }
}
