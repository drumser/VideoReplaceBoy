package ru.quantick.videoreplacebot.implementation

import eu.vendeli.tgbot.TelegramBot
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mu.KLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class Scheduler(
    private val bot: TelegramBot
) {
    companion object : KLogging()

    @Scheduled(initialDelay = 5000, fixedDelay = 30000)
    fun metricActiveUsers() {
        logger.info { "Run scheduling" }

        GlobalScope.launch {
            bot.handleUpdates()
        }
    }
}
