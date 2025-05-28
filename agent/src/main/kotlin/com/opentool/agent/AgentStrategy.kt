package com.opentool.agent

import ai.koog.agents.core.dsl.builder.forwardTo
import ai.koog.agents.core.dsl.builder.strategy
import ai.koog.agents.core.dsl.extension.*
import ai.koog.agents.core.environment.ReceivedToolResult


object AgentStrategy {

    fun createStrategy() = strategy("browser-default") {
        val nodeCallLLM by nodeLLMRequest("sendInput")
        val nodeExecuteTool by nodeExecuteTool("nodeExecuteTool")
        val nodeSendToolResult by nodeLLMSendToolResult("nodeSendToolResult")
        val nodeCompressHistory by nodeLLMCompressHistory<ReceivedToolResult>()
        edge(nodeStart forwardTo nodeCallLLM)
        edge(nodeCallLLM forwardTo nodeExecuteTool onToolCall { true })
        edge(nodeCallLLM forwardTo nodeFinish onAssistantMessage { true })
        edge(nodeExecuteTool forwardTo nodeSendToolResult)
        edge(
            (nodeExecuteTool forwardTo nodeCompressHistory) onCondition { _ -> llm.readSession { prompt.messages.size > 10 } })

        edge(nodeCompressHistory forwardTo nodeSendToolResult)

        edge(nodeSendToolResult forwardTo nodeFinish onAssistantMessage { true })
        edge(nodeSendToolResult forwardTo nodeExecuteTool onToolCall { true })
    }
}