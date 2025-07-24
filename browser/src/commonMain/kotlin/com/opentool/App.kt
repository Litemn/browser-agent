package com.opentool

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    val coroutineScope = rememberCoroutineScope()
    var messages by remember { mutableStateOf(listOf<ChatMessage>()) }
    var inputText by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }

    // Get the current settings
    val currentSettings = remember { SettingsManager.getSettings() }
    var apiKey by remember { mutableStateOf(currentSettings.apiKey) }
    var maxIterations by remember { mutableStateOf(currentSettings.maxIterations.toString()) }
    var host by remember { mutableStateOf(currentSettings.host ?: "") }
    var connectionType by remember { mutableStateOf(currentSettings.connectionType) }
    var openAIModel by remember { mutableStateOf(currentSettings.openAIModel) }
    var anthropicModel by remember { mutableStateOf(currentSettings.anthropicModel) }
    var systemPrompt by remember { mutableStateOf(currentSettings.systemPrompt) }

    val updateListener = remember { object: UpdateListener {
        override fun onUpdate(message: String) {
            messages = messages.filterNot { it.isTool } + ChatMessage(message, isFromUser = false, isLoading = false, isTool = true)
        }
    } }
    // Get the chat service for the current platform
    val chatService = remember { getChatService(updateListener) }

    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Title and settings button
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Browser Agent Chat",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Button(
                    onClick = { showSettings = true }
                ) {
                    Text("Settings")
                }
            }

            // Settings dialog
            if (showSettings) {
                Dialog(onDismissRequest = { showSettings = false }) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Settings",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            // Buttons
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(
                                    onClick = { showSettings = false }
                                ) {
                                    Text("Cancel")
                                }

                                Button(
                                    onClick = {
                                        val newSettings = AppSettings(
                                            apiKey = apiKey,
                                            maxIterations = maxIterations.toIntOrNull() ?: 50,
                                            host = if (host.isBlank()) null else host,
                                            connectionType = connectionType,
                                            openAIModel = openAIModel,
                                            anthropicModel = anthropicModel,
                                            systemPrompt = systemPrompt
                                        )
                                        SettingsManager.updateSettings(newSettings)
                                        showSettings = false
                                    },
                                    modifier = Modifier.padding(start = 8.dp)
                                ) {
                                    Text("Save")
                                }
                            }

                            // API Key
                            OutlinedTextField(
                                value = apiKey,
                                onValueChange = { apiKey = it },
                                label = { Text("API Key") },
                                modifier = Modifier.fillMaxWidth(),
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                            )

                            // Max Iterations
                            OutlinedTextField(
                                value = maxIterations,
                                onValueChange = { maxIterations = it },
                                label = { Text("Max Iterations") },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )

                            // Host (optional)
                            OutlinedTextField(
                                value = host,
                                onValueChange = { host = it },
                                label = { Text("Host (optional)") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Connection Type
                            Text("Connection Type:", modifier = Modifier.padding(top = 8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                ConnectionType.entries.forEach { type ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(end = 8.dp)
                                    ) {
                                        RadioButton(
                                            selected = connectionType == type,
                                            onClick = { connectionType = type }
                                        )
                                        Text(type.name)
                                    }
                                }
                            }

                            // Model selection based on connection type
                            Text("Model:", modifier = Modifier.padding(top = 8.dp))

                            // OpenAI model selection (only shown when OpenAI is selected)
                            if (connectionType == ConnectionType.OPENAI) {
                                Column {
                                    OpenAIModel.entries.forEach { model ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        ) {
                                            RadioButton(
                                                selected = openAIModel == model,
                                                onClick = { openAIModel = model }
                                            )
                                            Text(model.toString())
                                        }
                                    }
                                }
                            }

                            // Anthropic model selection (only shown when Anthropic is selected)
                            if (connectionType == ConnectionType.ANTHROPIC) {
                                Column {
                                    AnthropicModel.entries.forEach { model ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        ) {
                                            RadioButton(
                                                selected = anthropicModel == model,
                                                onClick = { anthropicModel = model }
                                            )
                                            Text(model.toString())
                                        }
                                    }
                                }
                            }

                            // System Prompt
                            Text("System Prompt:", modifier = Modifier.padding(top = 8.dp))
                            val scrollState = rememberScrollState()
                            OutlinedTextField(
                                value = systemPrompt,
                                onValueChange = { systemPrompt = it },
                                label = { Text("System Prompt") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 200.dp)
                                    .verticalScroll(scrollState),
                                minLines = 5,
                                maxLines = 20
                            )


                        }
                    }
                }
            }

            // Chat messages area
            val listState = rememberLazyListState()

            // Find the last user message index
            val lastUserMessageIndex = messages.indexOfLast { it.isFromUser }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                    .padding(8.dp),
                state = listState,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages.withIndex().toList()) { (index, message) ->
                    val isLastUserMessage = index == lastUserMessageIndex

                    ChatMessageItem(
                        message = message,
                        isLastUserMessage = isLastUserMessage,
                        onRetry = {
                            // Only proceed if this is the last user message, processing is not in progress,
                            // and the lastUserMessageIndex is valid
                            if (isLastUserMessage && !isProcessing && lastUserMessageIndex >= 0) {
                                // Get the last user message content
                                val lastUserMessage = messages[lastUserMessageIndex]
                                val userRequest = lastUserMessage.content

                                // Add loading message
                                val loadingMessage = ChatMessage("Processing...", false, true)
                                messages = messages + loadingMessage
                                isProcessing = true

                                // Use the chat service to send the message
                                chatService.sendMessage(
                                    userRequest,
                                    onResponse = { response ->
                                        // Remove loading message and add response
                                        messages = messages.filterNot { it.isLoading  }.filterNot { it.isTool } +
                                            ChatMessage(response, false)
                                        isProcessing = false
                                    },
                                    onError = { errorMessage ->
                                        // Remove loading message and add error
                                        messages = messages.filterNot { it.isLoading }.filterNot { it.isTool } +
                                            ChatMessage("Error: $errorMessage", false)
                                        isProcessing = false
                                    },
                                    onStatus = { statusMessage ->
                                        // Update the loading message with the status
                                        messages = messages.map {
                                            if (it.isLoading) ChatMessage(statusMessage, false, true) else it
                                        }
                                    }
                                )
                            }
                        }
                    )
                }
            }

            // Scroll to bottom when new messages are added
            LaunchedEffect(messages.size) {
                if (messages.isNotEmpty()) {
                    listState.animateScrollToItem(messages.size - 1)
                }
            }

            // Input area
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    placeholder = { Text("Type your request...") },
                    enabled = !isProcessing
                )

                Button(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            val userMessage = ChatMessage(inputText, true)
                            val loadingMessage = ChatMessage("Processing...", false, true)
                            messages = messages + userMessage + loadingMessage

                            val userRequest = inputText
                            inputText = ""
                            isProcessing = true

                            // Use the chat service to send the message
                            chatService.sendMessage(
                                userRequest,
                                onResponse = { response ->
                                    // Remove loading message and add response
                                    messages = messages.filterNot { it.isLoading }.filterNot { it.isTool } +
                                        ChatMessage(response, false)
                                    isProcessing = false
                                },
                                onError = { errorMessage ->
                                    // Remove loading message and add error
                                    messages = messages.filterNot { it.isLoading }.filterNot { it.isTool } +
                                        ChatMessage("Error: $errorMessage", false)
                                    isProcessing = false
                                },
                                onStatus = { statusMessage ->
                                    // Update the loading message with the status
                                    messages = messages.map {
                                        if (it.isLoading) ChatMessage(statusMessage, false, true) else it
                                    }
                                }
                            )
                        }
                    },
                    enabled = inputText.isNotBlank() && !isProcessing
                ) {
                    Text("Send")
                }
            }

            // Service availability warning if needed
            if (!chatService.isAvailable()) {
                Text(
                    chatService.getUnavailableReason() ?: "Service is not available",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun ChatMessageItem(
    message: ChatMessage,
    isLastUserMessage: Boolean = false,
    onRetry: () -> Unit = {}
) {
    val backgroundColor = when {
        message.isLoading -> MaterialTheme.colorScheme.surfaceVariant
        message.isFromUser -> MaterialTheme.colorScheme.primaryContainer
        message.isTool -> MaterialTheme.colorScheme.secondaryContainer
        else -> MaterialTheme.colorScheme.secondaryContainer
    }

    val alignment = if (message.isFromUser) Alignment.End else Alignment.Start

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (message.isFromUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Column(
            horizontalAlignment = if (message.isFromUser) Alignment.End else Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 8.dp,
                            topEnd = 8.dp,
                            bottomStart = if (message.isFromUser) 8.dp else 0.dp,
                            bottomEnd = if (message.isFromUser) 0.dp else 8.dp
                        )
                    )
                    .background(backgroundColor)
                    .padding(12.dp)
            ) {
                if (message.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    if (message.isTool) {
                        Text(
                            message.content,
                            color = MaterialTheme.colorScheme.outline,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    } else {
                        Text(message.content)
                    }
                }
            }

            // Show retry button only for the last user message
            if (isLastUserMessage && !message.isLoading) {
                TextButton(
                    onClick = onRetry,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text("Retry", fontSize = 12.sp)
                }
            }
        }
    }
}
