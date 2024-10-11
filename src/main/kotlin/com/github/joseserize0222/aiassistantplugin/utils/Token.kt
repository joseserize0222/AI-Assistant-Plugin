package com.github.joseserize0222.aiassistantplugin.utils

import com.intellij.util.EnvironmentUtil

@JvmInline
value class Token(val value: String)


fun getToken() : Token{
    val envToken = EnvironmentUtil.getValue("OPENAI_API_KEY")
    checkNotNull(envToken) {
        "No environment OPENAI_API_KEY was found"
    }
    return Token(envToken)
}

