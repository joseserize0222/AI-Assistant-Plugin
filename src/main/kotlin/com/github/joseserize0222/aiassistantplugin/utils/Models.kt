package com.github.joseserize0222.aiassistantplugin.utils

import kotlinx.serialization.Serializable

@Serializable
data class OpenAIRequest(
    val model: String,
    val temperature: Double,
    val messages: List<Message>
)

@Serializable
data class Message(
    val role: String,
    val content: String
)

// Define la estructura del response
@Serializable
data class OpenAIResponse(
    val choices: List<Choice>
)

@Serializable
data class Choice(
    val message: Message
)