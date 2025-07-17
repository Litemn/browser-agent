package com.opentool.agent

import ai.koog.agents.core.agent.AIAgent
import com.opentool.agent.core.BrowserAgentCore

/**
 * Main class for the Browser Agent.
 *
 * This class uses the BrowserAgentCore to create and manage the agent,
 * providing a clear separation between core agent logic and browser automation implementation.
 */
class BrowserAgent(private val settings: BrowserAgentSettings) {

    /**
     * The core agent logic, independent of the browser automation implementation.
     */
    private val core = BrowserAgentCore.fromSettings(settings)

    /**
     * The AIAgent instance created by the core.
     */
    val agent: AIAgent<String, String> by lazy { core.createAgent() }
}
