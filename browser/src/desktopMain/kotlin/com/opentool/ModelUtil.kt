package com.opentool

import ai.koog.prompt.executor.clients.anthropic.AnthropicModels
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.llm.LLModel


fun AppSettings.toModel(): LLModel {
    when (this.connectionType) {
        ConnectionType.OPENAI -> {
            return when (this.openAIModel) {
                OpenAIModel.GPT4O -> OpenAIModels.Chat.GPT4o

                OpenAIModel.GPT4O_MINI -> OpenAIModels.CostOptimized.GPT4oMini
            }
        }

        ConnectionType.ANTHROPIC -> {
            return when (this.anthropicModel) {
                AnthropicModel.CLAUDE_3_7_SONNET -> AnthropicModels.Sonnet_3_7
                AnthropicModel.CLAUDE_4_SONNET -> AnthropicModels.Sonnet_4
            }
        }
    }
}