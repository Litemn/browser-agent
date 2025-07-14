package com.opentool.agent

import ai.koog.agents.core.agent.session.AIAgentLLMWriteSession
import ai.koog.agents.core.dsl.builder.AIAgentNodeDelegateBase
import ai.koog.agents.core.dsl.builder.AIAgentSubgraphBuilderBase
import ai.koog.agents.core.dsl.builder.forwardTo
import ai.koog.agents.core.dsl.builder.strategy
import ai.koog.agents.core.dsl.extension.*
import ai.koog.agents.core.environment.ReceivedToolResult
import ai.koog.prompt.message.Message
import com.opentool.playwright.PlaywrightAgentTools


object AgentStrategy {

    fun createStrategy() = strategy("browser-default") {
        val nodeCallLLM by nodeLLMRequest("sendInput")
        val nodeExecuteTool by nodeExecuteTool("nodeExecuteTool")
        val nodeSendToolResult by nodeLLMSendToolResult("nodeSendToolResult")
        val nodeCompressHistory by compressHistory<ReceivedToolResult>("compress")
        edge(nodeStart forwardTo nodeCallLLM)
        edge(nodeCallLLM forwardTo nodeExecuteTool onToolCall { true })
        edge(nodeCallLLM forwardTo nodeFinish onAssistantMessage { true })
        edge(nodeExecuteTool forwardTo nodeCompressHistory)
        edge(nodeCompressHistory forwardTo nodeSendToolResult)
        edge(nodeSendToolResult forwardTo nodeFinish onAssistantMessage { true })
        edge(nodeSendToolResult forwardTo nodeExecuteTool onToolCall { true })
    }
}

private fun <T> AIAgentSubgraphBuilderBase<*, *>.compressHistory(
    name: String? = null,
): AIAgentNodeDelegateBase<T, T> =
    node(name) { input ->
        llm.writeSession {
            filterOutSnapshotMessages()
        }
        input
    }

private fun AIAgentLLMWriteSession.filterOutSnapshotMessages(toolName: String = PlaywrightAgentTools::getSnapshot.name) {
    prompt = prompt.withMessages { messages ->
        val snapshotMessages = messages.filterIsInstance<Message.Tool>()
            .filter { it.tool == toolName }
        if (snapshotMessages.size <= 2) {
            return@withMessages messages
        }
        var remainingToSkip = snapshotMessages.size - 1
        messages.filter { message ->
            if (message is Message.Tool && message.tool == toolName && remainingToSkip > 0) {
                remainingToSkip--
                false
            } else {
                true
            }
        }
    }
}
