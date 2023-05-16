package ru.quantick.videoreplacebot.botController

import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.annotations.UnprocessedHandler
import eu.vendeli.tgbot.api.media.video
import eu.vendeli.tgbot.types.User
import eu.vendeli.tgbot.types.internal.ProcessedUpdate
import mu.KLogging
import org.springframework.stereotype.Component
import ru.quantick.videoreplacebot.common.Constants.VIDEO_URL_REGEX
import ru.quantick.videoreplacebot.youtubedl.Config
import ru.quantick.videoreplacebot.youtubedl.YoutubeDL

@Component
class MainController(
    private val youtubeDL: YoutubeDL
) {
    @UnprocessedHandler()
    suspend fun start(update: ProcessedUpdate, user: User, bot: TelegramBot) {
        val userMessage = update.update.message?.text.orEmpty()
        val videoUrl = VIDEO_URL_REGEX.find(userMessage)?.value.orEmpty()
        logger.info { "Got new message" }
        if (videoUrl.isNotEmpty()) {
            logger.info { "Found videourl ${videoUrl}" }
            val url = youtubeDL.execute(
                Config.builder()
                    .skipDownload()
                    .url(videoUrl)
            )?.formats?.first()?.url

            if (url != null) {
                logger.info { "Found video ${url}" }

                val messageId = update.update.message?.messageId
                if (messageId != null) {
                    video { url }
                        .caption { "Загружено ✅" }
                        .options { replyToMessageId = messageId }
                        .send(user, bot)
                }
            }
        }
    }

    companion object : KLogging()
}
