package com.github.joseserize0222.aiassistantplugin.utils

@JvmInline
value class Token(val value: String)


fun getToken() : Token{
    val envToken = System.getenv("OPENAI_API_KEY")
    checkNotNull(envToken) {
        "No environment OPENAI_API_KEY was found"
    }
    return Token(envToken)
}

