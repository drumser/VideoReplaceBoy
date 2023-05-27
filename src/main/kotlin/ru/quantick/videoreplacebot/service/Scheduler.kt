package ru.quantick.videoreplacebot.service

import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.types.Message
import eu.vendeli.tgbot.types.internal.ActionContext
import kotlinx.coroutines.runBlocking
import mu.KLogging
import org.springframework.context.ApplicationEventPublisher
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import ru.quantick.videoreplacebot.common.Constants
import ru.quantick.videoreplacebot.event.VideoFoundEvent


@Service
class Scheduler(
    private val bot: TelegramBot,
    private val eventPublisher: ApplicationEventPublisher,
) {
    companion object : KLogging()

    @Retryable(maxAttempts = Integer.MAX_VALUE)
    fun scheduleHandleUpdate() {
        logger.info { "Run scheduling" }

        bot.update.stopListener()
        try {
            runBlocking {
                bot.handleUpdates {
                    onMessage {
                        val userMessage = this.update.message?.text.orEmpty()
                        val videoUrl = Constants.VIDEO_URL_REGEX.find(userMessage)?.value.orEmpty()
                        logger.info { "Got new message ${userMessage}, videourl=${videoUrl}" }

                        if (videoUrl.isNotEmpty()) {
                            publishEvent(videoUrl)
                        }
                    }
                }
            }
        } catch (e: Throwable) {
            logger.error { "Scheduling failed. e=$e" }
            throw e
        }
    }

    private fun ActionContext<Message>.publishEvent(videoUrl: String) {
        val messageId = this.update.message?.messageId
        val chatId = this.update.message?.chat?.id
        if (chatId != null && messageId != null) {
            eventPublisher.publishEvent(
                VideoFoundEvent(
                    this, messageId, chatId, videoUrl
                )
            )
        }
    }
}
