package com.opentool

import kotlinx.browser.localStorage

/**
 * WasmJs implementation of settings storage using localStorage.
 * This implementation uses individual localStorage keys for each setting,
 * similar to how multiplatform-settings works.
 */
class WasmJsSettingsStorage : SettingsStorage {
    companion object {
        private const val PREFIX = "browser-agent-"
        private const val KEY_API_KEY = "${PREFIX}apiKey"
        private const val KEY_MAX_ITERATIONS = "${PREFIX}maxIterations"
        private const val KEY_HOST = "${PREFIX}host"
        private const val KEY_CONNECTION_TYPE = "${PREFIX}connectionType"
        private const val KEY_OPENAI_MODEL = "${PREFIX}openAIModel"
        private const val KEY_ANTHROPIC_MODEL = "${PREFIX}anthropicModel"
        private const val KEY_SYSTEM_PROMPT = "${PREFIX}systemPrompt"
    }

    override fun saveSettings(settings: AppSettings) {
        // Save each setting as an individual key in localStorage
        localStorage.setItem(KEY_API_KEY, settings.apiKey)
        localStorage.setItem(KEY_MAX_ITERATIONS, settings.maxIterations.toString())
        localStorage.setItem(KEY_HOST, settings.host ?: "")
        localStorage.setItem(KEY_CONNECTION_TYPE, settings.connectionType.name)
        localStorage.setItem(KEY_OPENAI_MODEL, settings.openAIModel.name)
        localStorage.setItem(KEY_ANTHROPIC_MODEL, settings.anthropicModel.name)
        localStorage.setItem(KEY_SYSTEM_PROMPT, settings.systemPrompt)
    }

    override fun loadSettings(): AppSettings? {
        // Check if we have any settings stored
        if (localStorage.getItem(KEY_API_KEY) == null) {
            return null
        }

        return try {
            AppSettings(
                apiKey = localStorage.getItem(KEY_API_KEY) ?: "",
                maxIterations = localStorage.getItem(KEY_MAX_ITERATIONS)?.toIntOrNull() ?: 50,
                host = localStorage.getItem(KEY_HOST)?.takeIf { it.isNotBlank() },
                connectionType = ConnectionType.valueOf(
                    localStorage.getItem(KEY_CONNECTION_TYPE) ?: ConnectionType.OPENAI.name
                ),
                openAIModel = OpenAIModel.valueOf(
                    localStorage.getItem(KEY_OPENAI_MODEL) ?: OpenAIModel.GPT4O_MINI.name
                ),
                anthropicModel = AnthropicModel.valueOf(
                    localStorage.getItem(KEY_ANTHROPIC_MODEL) ?: AnthropicModel.CLAUDE_3_7_SONNET.name
                ),
                systemPrompt = localStorage.getItem(KEY_SYSTEM_PROMPT) ?: AppSettings().systemPrompt
            )
        } catch (e: Exception) {
            // If there's any error parsing the settings, return null
            null
        }
    }
}

/**
 * Gets the WasmJs-specific settings storage implementation.
 */
actual fun getSettingsStorage(): SettingsStorage = WasmJsSettingsStorage()
