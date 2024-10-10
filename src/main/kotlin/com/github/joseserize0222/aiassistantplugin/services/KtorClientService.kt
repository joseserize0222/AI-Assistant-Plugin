package com.github.joseserize0222.aiassistantplugin.services

import com.github.joseserize0222.aiassistantplugin.utils.*
import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json

@Service(Service.Level.PROJECT)
class KtorClientService : Disposable {
    private val scope =
        CoroutineScope(Dispatchers.IO + SupervisorJob() + CoroutineExceptionHandler { _ , error -> throw error })
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    private var listener: ChatGptListener? = null

    fun postFunctionToOpenAi(functionBody: String, token: Token) {
        val request = OpenAIRequest(
            model = "gpt-3.5-turbo",
            messages = listOf(
                Message(role = "user", content = "Explain the following Kotlin function and its purpose\n" +
                        "\n" + "\n"
                        + functionBody
                )
            )
        )
        scope.launch {
            val response: HttpResponse = client.post("https://api.openai.com/v1/chat/completions") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer ${token.value}")
                    append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                }
                setBody(request)
            }

            if (response.status.isSuccess()) {
                val openAiResponse: OpenAIResponse = response.body()
                val answer = openAiResponse.choices.firstOrNull()?.message?.content ?: "No valid response received."
                update(answer)
            } else {
                val errorMessage = "Error: ${response.status.value} - ${response.bodyAsText()}"
                update(errorMessage)
            }
        }
    }


    fun addListener(new_listener: ChatGptListener) {
        listener = new_listener
    }

    private fun update(response: String) {
        listener?.callback(response)
    }

    override fun dispose() {
        client.close()
    }
}

