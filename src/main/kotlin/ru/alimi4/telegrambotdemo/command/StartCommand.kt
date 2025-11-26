package ru.alimi4.telegrambotdemo.command

import org.springframework.stereotype.Component
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender

@Component
class StartCommand(
    private val messageCache: ru.alimi4.telegrambotdemo.cache.MessageCache
) : BotCommand("/start", "Начать новый диалог") {

    override fun execute(absSender: AbsSender, user: User, chat: Chat, arguments: Array<out String>) {
        messageCache.clearMessages(chat.id)
        absSender.execute(createMessage(chat.id.toString(), "Начинаем диалог!"))
    }

    private fun createMessage(chatId: String, text: String): SendMessage {
        return SendMessage(chatId, text)
            .apply { enableMarkdown(true) }
            .apply { disableWebPagePreview() }
    }
}