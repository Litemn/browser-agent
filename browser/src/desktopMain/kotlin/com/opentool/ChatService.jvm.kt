package com.opentool

import ai.koog.agents.core.agent.config.AIAgentConfig
import ai.koog.agents.core.tools.reflect.ToolFromCallable
import ai.koog.agents.features.eventHandler.feature.EventHandlerConfig
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.clients.LLMClient
import ai.koog.prompt.executor.clients.anthropic.AnthropicClientSettings
import ai.koog.prompt.executor.clients.anthropic.AnthropicLLMClient
import ai.koog.prompt.executor.clients.openai.OpenAIClientSettings
import ai.koog.prompt.executor.clients.openai.OpenAILLMClient
import com.opentool.agent.BrowserAgent
import com.opentool.agent.BrowserAgentSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.swing.Swing

/**
 * JVM implementation of ChatService that simulates agent behavior.
 *
 * Note: This is a mock implementation for demonstration purposes.
 * In a real implementation, this would use the BrowserAgent.
 */

class JvmChatService(private val updateListener: UpdateListener) : ChatService {
    private val coroutineScope = CoroutineScope(Dispatchers.Swing)

    val eventHandler: EventHandlerConfig.() -> Unit = {
        onToolCall {
            updateListener.onUpdate("Tool call: ${it.tool.name} with args: ${it.toolArgs}")
        }
    }



    private fun createLLMClient(): LLMClient {

        val appSettings = SettingsManager.getSettings()

        return when (appSettings.connectionType) {
            ConnectionType.OPENAI -> {
                OpenAILLMClient(
                    apiKey = appSettings.apiKey,
                    settings = appSettings.host?.let {
                        OpenAIClientSettings(baseUrl = it)
                    } ?: OpenAIClientSettings()
                )
            }

            ConnectionType.ANTHROPIC -> {
                AnthropicLLMClient(
                    apiKey = appSettings.apiKey,
                    settings = appSettings.host?.let {
                        AnthropicClientSettings(baseUrl = it)
                    } ?: AnthropicClientSettings()
                )
            }
        }
    }

    private fun createBrowserAgentSettings(): BrowserAgentSettings {
        val appSettings = SettingsManager.getSettings()
        val llmClient = createLLMClient()

        return BrowserAgentSettings(
            llmClient = llmClient,
            eventHandler = eventHandler,
            agentConfig = AIAgentConfig(
                prompt = prompt("browser-agent") {
                    system(appSettings.systemPrompt)
                },
                model = appSettings.toModel(),
                maxAgentIterations = appSettings.maxIterations,
            )
        )
    }

    private val settings by lazy { createBrowserAgentSettings() }
    private val agent by lazy { BrowserAgent(settings) }

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

        // Report initial status
        coroutineScope.launch(Dispatchers.Swing) {
            onStatus("Starting agent execution...")
        }
        // Execute agent
        coroutineScope.launch(Dispatchers.IO) {
            try {
                // Report status before running the agent
                coroutineScope.launch(Dispatchers.Swing) {
                    onStatus("Processing your request...")
                }

                val response = agent.agent.run(message)

                // Report completion
                coroutineScope.launch(Dispatchers.Swing) {
                    onStatus("Agent execution completed")
                    onResponse(response)
                }
            } catch (e: Exception) {
                coroutineScope.launch(Dispatchers.Swing) {
                    onStatus("Error during agent execution: ${e.message}")
                    onError("Error: ${e.message}")
                }
            }
        }
    }

    override fun isAvailable(): Boolean {
        val appSettings = SettingsManager.getSettings()
        // Check if API key is provided
        return appSettings.apiKey.isNotBlank()
    }

    override fun getUnavailableReason(): String? {
        val appSettings = SettingsManager.getSettings()
        return when {
            appSettings.apiKey.isBlank() -> "API key is not configured. Please go to Settings to configure it."
            else -> null
        }
    }
}

/**
 * Gets the chat service implementation for the JVM platform.
 */
actual fun getChatService(listener: UpdateListener): ChatService = JvmChatService(listener)
