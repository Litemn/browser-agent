package com.opentool

import com.russhwolf.settings.Settings

/**
 * Interface for platform-specific settings storage.
 */
interface SettingsStorage {
    /**
     * Saves the settings to persistent storage.
     *
     * @param settings The settings to save
     */
    fun saveSettings(settings: AppSettings)

    /**
     * Loads the settings from persistent storage.
     *
     * @return The loaded settings, or null if no settings were found
     */
    fun loadSettings(): AppSettings?
}

/**
 * Implementation of SettingsStorage using multiplatform-settings.
 */
class MultiplatformSettingsStorage(private val settings: Settings) : SettingsStorage {
    companion object {
        private const val KEY_API_KEY = "apiKey"
        private const val KEY_MAX_ITERATIONS = "maxIterations"
        private const val KEY_HOST = "host"
        private const val KEY_CONNECTION_TYPE = "connectionType"
        private const val KEY_OPENAI_MODEL = "openAIModel"
        private const val KEY_ANTHROPIC_MODEL = "anthropicModel"
        private const val KEY_SYSTEM_PROMPT = "systemPrompt"
        private const val KEY_HEADLESS = "headless"
    }

    override fun saveSettings(settings: AppSettings) {
        this.settings.putString(KEY_API_KEY, settings.apiKey)
        this.settings.putInt(KEY_MAX_ITERATIONS, settings.maxIterations)
        this.settings.putString(KEY_HOST, settings.host ?: "")
        this.settings.putString(KEY_CONNECTION_TYPE, settings.connectionType.name)
        this.settings.putString(KEY_OPENAI_MODEL, settings.openAIModel.name)
        this.settings.putString(KEY_ANTHROPIC_MODEL, settings.anthropicModel.name)
        this.settings.putString(KEY_SYSTEM_PROMPT, settings.systemPrompt)
        this.settings.putBoolean(KEY_HEADLESS, settings.headless)
    }

    override fun loadSettings(): AppSettings? {
        if (!this.settings.hasKey(KEY_API_KEY)) {
            return null
        }

        return try {
            AppSettings(
                apiKey = this.settings.getString(KEY_API_KEY, ""),
                maxIterations = this.settings.getInt(KEY_MAX_ITERATIONS, 50),
                host = this.settings.getString(KEY_HOST, "").takeIf { it.isNotBlank() },
                connectionType = ConnectionType.valueOf(
                    this.settings.getString(KEY_CONNECTION_TYPE, ConnectionType.OPENAI.name)
                ),
                openAIModel = OpenAIModel.valueOf(
                    this.settings.getString(KEY_OPENAI_MODEL, OpenAIModel.GPT4O_MINI.name)
                ),
                anthropicModel = AnthropicModel.valueOf(
                    this.settings.getString(KEY_ANTHROPIC_MODEL, AnthropicModel.CLAUDE_3_7_SONNET.name)
                ),
                systemPrompt = this.settings.getString(KEY_SYSTEM_PROMPT, AppSettings().systemPrompt),
                headless = this.settings.getBoolean(KEY_HEADLESS, false)
            )
        } catch (e: Exception) {
            null
        }
    }
}

/**
 * Gets the platform-specific settings storage implementation.
 */
expect fun getSettingsStorage(): SettingsStorage
