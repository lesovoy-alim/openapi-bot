package ru.alimi4.telegrambotdemo.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession
import ru.alimi4.telegrambotdemo.bot.TelegramBot

@Configuration
class BotConfig {

    @Bean
    fun telegramBotsApi(bot: TelegramBot): TelegramBotsApi {
        return TelegramBotsApi(DefaultBotSession::class.java).apply {
            registerBot(bot)
        }
    }
}