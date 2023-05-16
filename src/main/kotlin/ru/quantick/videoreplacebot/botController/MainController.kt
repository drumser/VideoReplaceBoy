package ru.quantick.videoreplacebot.botController

import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.annotations.UnprocessedHandler
import eu.vendeli.tgbot.api.media.video
import eu.vendeli.tgbot.types.User
import eu.vendeli.tgbot.types.internal.ProcessedUpdate
import mu.KLogging
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import ru.quantick.videoreplacebot.common.Constants.VIDEO_URL_REGEX
import ru.quantick.videoreplacebot.event.VideoFoundEvent
import ru.quantick.videoreplacebot.youtubedl.Config
import ru.quantick.videoreplacebot.youtubedl.YoutubeDL

@Component
class MainController(
    private val eventPublisher: ApplicationEventPublisher,
) {
    @UnprocessedHandler()
    suspend fun start(update: ProcessedUpdate, user: User, bot: TelegramBot) {
        val userMessage = update.update.message?.text.orEmpty()
        val videoUrl = VIDEO_URL_REGEX.find(userMessage)?.value.orEmpty()
        logger.info { "Got new message" }
        if (videoUrl.isNotEmpty()) {
            val messageId = update.update.message?.messageId
            val chatId = update.update.message?.chat?.id
            if (chatId != null && messageId != null) {
                eventPublisher.publishEvent(
                    VideoFoundEvent(
                        this, messageId, chatId, videoUrl
                    )
                )
            }

        }
    }

    companion object : KLogging()
}
