package com.opentool.agent

import ai.koog.agents.core.agent.config.AIAgentConfig
import ai.koog.agents.core.agent.entity.AIAgentStrategy
import ai.koog.agents.core.tools.Tool
import ai.koog.agents.core.tools.reflect.asTools
import ai.koog.agents.features.eventHandler.feature.EventHandlerConfig
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.clients.LLMClient
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import com.opentool.playwright.PlaywrightAgentTools

data class BrowserAgentSettings(
    val llmClient: LLMClient,
    val strategy: AIAgentStrategy<String, String> = AgentStrategy.createStrategy(),
    val eventHandler: EventHandlerConfig.() -> Unit = {},
    val headless: Boolean = false,
    val tools: List<Tool<*, *>> = PlaywrightAgentTools(headless).asTools(),
    val agentConfig: AIAgentConfig = AIAgentConfig(
        prompt = prompt("browser-agent") {
            system(
                "You are an AI agent designed to automate browser tasks. " +
                        "Your goal is to accomplish the ultimate task following the rules."
            )
        },
        model =
            OpenAIModels.CostOptimized.GPT4oMini,
        maxAgentIterations = 50
    )
)
