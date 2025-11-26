package ru.alimi4.telegrambotdemo.service

import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.openai.OpenAiChatOptions
import org.springframework.stereotype.Service
import org.springframework.ai.chat.messages.Message
import org.springframework.ai.openai.api.ResponseFormat
import org.springframework.beans.factory.annotation.Value

@Service
class OpenAiService(
    private val chatModel: ChatModel
) {
    @Value($$"${spring.ai.openai.model}")
    private lateinit var model: String


    fun sendMessages(messages: List<Message>): List<String> {
        val prompt = Prompt(
            messages,
            OpenAiChatOptions.builder()
                .model(model)
                .temperature(0.3)
                .maxTokens(2000)
                .streamUsage(true)
                .responseFormat(ResponseFormat.builder().type(ResponseFormat.Type.TEXT).build())
                .build()
        )

        val response: ChatResponse = chatModel.call(prompt)
        return response.results
            ?.map { it.output.text }
            ?: emptyList()
    }
}