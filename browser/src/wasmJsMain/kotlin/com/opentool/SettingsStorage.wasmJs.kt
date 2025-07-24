package com.opentool

import kotlinx.browser.localStorage

/**
 * WasmJs implementation of settings storage using localStorage.
 */
class WasmJsSettingsStorage : SettingsStorage {
    private val settingsKey = "browser-agent-settings"

    override fun saveSettings(settings: AppSettings) {
        // Create a simple JSON-like string representation of the settings
        val settingsJson = """
            {
                "apiKey": "${settings.apiKey}",
                "maxIterations": ${settings.maxIterations},
                "host": "${settings.host ?: ""}",
                "connectionType": "${settings.connectionType.name}",
                "openAIModel": "${settings.openAIModel.name}",
                "anthropicModel": "${settings.anthropicModel.name}",
                "systemPrompt": "${settings.systemPrompt.replace("\"", "\\\"").replace("\n", "\\n")}"
            }
        """.trimIndent()

        // Save to localStorage
        localStorage.setItem(settingsKey, settingsJson)
    }

    override fun loadSettings(): AppSettings? {
        // Get from localStorage
        val settingsJson = localStorage.getItem(settingsKey) ?: return null

        // Parse the JSON-like string
        // This is a simple implementation without a proper JSON parser
        // In a real application, you would use a proper JSON parser

        // Extract values using regex
        val apiKey = extractValue(settingsJson, "apiKey") ?: ""
        val maxIterations = extractValue(settingsJson, "maxIterations")?.toIntOrNull() ?: 50
        val host = extractValue(settingsJson, "host")?.takeIf { it.isNotBlank() }
        val connectionTypeName = extractValue(settingsJson, "connectionType") ?: ConnectionType.OPENAI.name
        val openAIModelName = extractValue(settingsJson, "openAIModel") ?: OpenAIModel.GPT4O_MINI.name
        val anthropicModelName = extractValue(settingsJson, "anthropicModel") ?: AnthropicModel.CLAUDE_3_7_SONNET.name
        val systemPrompt = extractValue(settingsJson, "systemPrompt")?.replace("\\\"", "\"")?.replace("\\n", "\n") ?: AppSettings().systemPrompt

        return try {
            AppSettings(
                apiKey = apiKey,
                maxIterations = maxIterations,
                host = host,
                connectionType = ConnectionType.valueOf(connectionTypeName),
                openAIModel = OpenAIModel.valueOf(openAIModelName),
                anthropicModel = AnthropicModel.valueOf(anthropicModelName),
                systemPrompt = systemPrompt
            )
        } catch (e: Exception) {
            // If there's any error parsing the settings, return null
            null
        }
    }

    // Helper function to extract a value from the JSON-like string
    private fun extractValue(json: String, key: String): String? {
        val regex = """"$key"\s*:\s*"?([^"]*)"?""".toRegex()
        val matchResult = regex.find(json) ?: return null
        return matchResult.groupValues[1]
    }
}

/**
 * Gets the WasmJs-specific settings storage implementation.
 */
actual fun getSettingsStorage(): SettingsStorage = WasmJsSettingsStorage()
