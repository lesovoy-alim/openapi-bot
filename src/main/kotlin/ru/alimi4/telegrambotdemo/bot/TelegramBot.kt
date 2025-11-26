package ru.alimi4.telegrambotdemo.bot

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.Message
import ru.alimi4.telegrambotdemo.cache.MessageCache
import ru.alimi4.telegrambotdemo.service.OpenAiService

@Component
class TelegramBot(
    private val openAiService: OpenAiService,
    private val messageCache: MessageCache,
    commands: Set<BotCommand>,
    @Value("\${telegram.token}")
    token: String,
) : TelegramLongPollingCommandBot(token) {

    @Value("\${telegram.botName}")
    private lateinit var botName: String

    init {
        registerAll(*commands.toTypedArray())
    }

    override fun processNonCommandUpdate(update: Update) {
        if (update.hasMessage()) {
            val message: Message = update.message
            val chatId = message.chatId
            if (message.hasText()) {
                val messageText = message.text
                
                // Получаем историю сообщений из кэша
                val messages = messageCache.getOrInitMessages(chatId).toMutableList()
                
                // Добавляем новое сообщение пользователя
                messages += org.springframework.ai.chat.messages.UserMessage(messageText)
                
                // Отправляем сообщения в OpenAI
                val assistantMessages = openAiService.sendMessages(messages)
                
                // Добавляем ответы от нейросети к истории сообщений
                val assistantMessageList = assistantMessages.map { org.springframework.ai.chat.messages.AssistantMessage(it) }
                messages.addAll(assistantMessageList)
                
                // Сохраняем обновленную историю в кэше
                messageCache.saveMessages(chatId, messages)
                
                // Отправляем ответ пользователю
                assistantMessages.forEach { 
                    execute(createMessage(chatId.toString(), it))
                }
            } else {
                execute(createMessage(chatId.toString(), "Я понимаю только текст!"))
            }
        }
    }

    private fun createMessage(chatId: String, text: String): SendMessage {
        return SendMessage(chatId, text)
            .apply { enableMarkdown(true) }
            .apply { disableWebPagePreview() }
    }

    override fun getBotUsername(): String = botName
}