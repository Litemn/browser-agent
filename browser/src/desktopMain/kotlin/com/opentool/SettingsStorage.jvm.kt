package com.opentool

import java.io.File
import java.util.Properties

/**
 * JVM implementation of settings storage using Properties.
 */
class JvmSettingsStorage : SettingsStorage {
    private val settingsFile = File(System.getProperty("user.home"), ".browser-agent-settings.properties")

    override fun saveSettings(settings: AppSettings) {
        val properties = Properties()

        // Save all settings as properties
        properties.setProperty("apiKey", settings.apiKey)
        properties.setProperty("maxIterations", settings.maxIterations.toString())
        properties.setProperty("host", settings.host ?: "")
        properties.setProperty("connectionType", settings.connectionType.name)
        properties.setProperty("openAIModel", settings.openAIModel.name)
        properties.setProperty("anthropicModel", settings.anthropicModel.name)
        properties.setProperty("systemPrompt", settings.systemPrompt)

        // Save to file
        settingsFile.outputStream().use { 
            properties.store(it, "Browser Agent Settings") 
        }
    }

    override fun loadSettings(): AppSettings? {
        if (!settingsFile.exists()) {
            return null
        }

        val properties = Properties()

        // Load from file
        settingsFile.inputStream().use { 
            properties.load(it) 
        }

        // Parse properties into settings
        return try {
            AppSettings(
                apiKey = properties.getProperty("apiKey", ""),
                maxIterations = properties.getProperty("maxIterations", "50").toIntOrNull() ?: 50,
                host = properties.getProperty("host", "").takeIf { it.isNotBlank() },
                connectionType = properties.getProperty("connectionType", ConnectionType.OPENAI.name)
                    .let { ConnectionType.valueOf(it) },
                openAIModel = properties.getProperty("openAIModel", OpenAIModel.GPT4O_MINI.name)
                    .let { OpenAIModel.valueOf(it) },
                anthropicModel = properties.getProperty("anthropicModel", AnthropicModel.CLAUDE_3_7_SONNET.name)
                    .let { AnthropicModel.valueOf(it) },
                systemPrompt = properties.getProperty("systemPrompt", AppSettings().systemPrompt)
            )
        } catch (e: Exception) {
            // If there's any error parsing the settings, return null
            null
        }
    }
}

/**
 * Gets the JVM-specific settings storage implementation.
 */
actual fun getSettingsStorage(): SettingsStorage = JvmSettingsStorage()
