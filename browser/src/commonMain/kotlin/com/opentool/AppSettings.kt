package com.opentool

/**
 * Enum representing the type of LLM connection to use.
 */
enum class ConnectionType {
    OPENAI,
    ANTHROPIC
}

/**
 * Enum representing OpenAI models.
 */
enum class OpenAIModel() {
    GPT4O_MINI(),
    GPT4O;

    override fun toString(): String {
        return when (this) {
            GPT4O_MINI -> "GPT-4o Mini"
            GPT4O -> "GPT-4o"
        }
    }
}

/**
 * Enum representing Anthropic models.
 */
enum class AnthropicModel {
    CLAUDE_3_7_SONNET,
    CLAUDE_4_SONNET;

    override fun toString(): String {
        return when (this) {
            CLAUDE_3_7_SONNET -> "Claude 3.7 Sonnet"
            CLAUDE_4_SONNET -> "Claude 4 Sonnet"
        }
    }
}

/**
 * Settings for the browser agent application.
 *
 * @property apiKey The API key to use for the LLM service
 * @property maxIterations The maximum number of iterations for the agent
 * @property host Optional host URL for the LLM service (if null, uses the default)
 * @property connectionType The type of LLM connection to use (OpenAI or Anthropic)
 * @property openAIModel The OpenAI model to use (only applicable when connectionType is OPENAI)
 * @property anthropicModel The Anthropic model to use (only applicable when connectionType is ANTHROPIC)
 * @property systemPrompt The system prompt to use for the agent
 */
data class AppSettings(
    val apiKey: String = "",
    val maxIterations: Int = 50,
    val host: String? = null,
    val connectionType: ConnectionType = ConnectionType.OPENAI,
    val openAIModel: OpenAIModel = OpenAIModel.GPT4O_MINI,
    val anthropicModel: AnthropicModel = AnthropicModel.CLAUDE_3_7_SONNET,
    val systemPrompt: String = """
        You are an AI agent designed to automate browser tasks. Your goal is to accomplish the ultimate task following the rules

        1. Element Interaction
        – Interact only using element references.

        2. Navigation & Errors
        – If no elements found, use alternative methods (back, search, refresh, etc).
        – Handle popups/cookies (accept/close).
        – Scroll to locate elements.
        – Wait if page isn't fully loaded.

        3. Task Completion
        – Call done only when the task is complete or at the final step.
        – Include all related gathered info in the result
        – Track repetitions (e.g. "for each", "x times") using memory; don't stop early.
        – Never hallucinate actions.

        4. Visual Context
        – Use provided images to understand layout.
        – Use bounding box labels for element indexing.

        5. Forms
        – Handle field suggestions/popups that appear after input.

        6. Long Tasks
        – Track status/progress in memory.
        – Use procedural memory summaries to stay on track and avoid repeating steps.
    """.trimIndent()
)

/**
 * Singleton object to store and manage application settings.
 */
object SettingsManager {
    private var _settings: AppSettings
    private val storage = getSettingsStorage()

    init {
        // Load settings from storage or use defaults
        _settings = storage.loadSettings() ?: AppSettings()
    }

    /**
     * Gets the current application settings.
     */
    fun getSettings(): AppSettings = _settings

    /**
     * Updates the application settings and saves them to storage.
     *
     * @param settings The new settings to use
     */
    fun updateSettings(settings: AppSettings) {
        _settings = settings
        storage.saveSettings(settings)
    }
}
