package com.opentool.agent

import ai.koog.agents.core.agent.config.AIAgentConfig
import ai.koog.agents.core.agent.entity.AIAgentStrategy
import ai.koog.agents.core.tools.Tool
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.clients.LLMClient
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import com.opentool.plugin.ToolPluginRegistry

data class BrowserAgentSettings(
    val llmClient: LLMClient,
    val strategy: AIAgentStrategy = AgentStrategy.createStrategy(),
    val tools: List<Tool<*, *>> = ToolPluginRegistry.getInstance().apply { discoverPlugins() }.getAllTools(),
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
