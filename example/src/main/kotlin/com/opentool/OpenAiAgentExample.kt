package com.opentool

import ai.koog.agents.core.agent.config.AIAgentConfig
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.clients.openai.OpenAILLMClient
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import com.opentool.agent.BrowserAgent
import com.opentool.agent.BrowserAgentSettings

suspend fun main() {

    val system = """
        You are an AI agent designed to automate browser tasks. Your goal is to accomplish the ultimate task following the rules

        1. Element Interaction
        – Interact only using element references.

        2. Navigation & Errors
        – If no elements found, use alternative methods (back, search, refresh, etc).
        – Handle popups/cookies (accept/close).
        – Scroll to locate elements.
        – Wait if page isn’t fully loaded.

        3. Task Completion
        – Call done only when the task is complete or at the final step.
        – Include all related gathered info in the result
        – Track repetitions (e.g. “for each”, “x times”) using memory; don’t stop early.
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

    val settings = BrowserAgentSettings(
        agentConfig = AIAgentConfig(
            prompt = prompt("browser-agent") {
                system(system)
            }, model = OpenAIModels.CostOptimized.GPT4oMini, maxAgentIterations = 20
        ),
        llmClient = OpenAILLMClient(apiKey = System.getenv("OPENAI_API_KEY") ?: error("OPENAI_API_KEY not set")),
    )
    val agent = BrowserAgent(settings)
    agent.agent.runAndGetResult("1. Open github playwright repository\n" +
            "2. Open the pull requests\n" +
            "3. Find and open latest pull request\n" +
            "4. Give a short description of change")


}