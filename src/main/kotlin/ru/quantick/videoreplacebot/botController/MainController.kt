package ru.quantick.videoreplacebot.botController

import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.annotations.CommandHandler
import eu.vendeli.tgbot.annotations.InputHandler
import eu.vendeli.tgbot.annotations.UnprocessedHandler
import eu.vendeli.tgbot.api.deleteMessage
import eu.vendeli.tgbot.api.media.video
import eu.vendeli.tgbot.types.User
import eu.vendeli.tgbot.types.internal.ProcessedUpdate
import org.springframework.stereotype.Component
import ru.quantick.videoreplacebot.youtubedl.Config
import ru.quantick.videoreplacebot.youtubedl.YoutubeDL

@Component
class MainController(
    private val youtubeDL: YoutubeDL
) {
    @UnprocessedHandler()
    suspend fun start(update: ProcessedUpdate, user: User, bot: TelegramBot) {
        val msg = update.update.message?.text
        val regex = Regex("""^(?:https?://)?(?:www\.)?(?:tiktok\.com/|vm\.tiktok\.com/)(\w{32})""")
        if (true) {
            val url = youtubeDL.execute(
                Config.builder()
                    .skipDownload()
                    .url(msg!!)
            )?.formats?.first()?.url

            if (url != null) {
                val messageId = update.update.message?.messageId
                if (messageId != null) {
                    deleteMessage(messageId).send(user, bot)
                }
                video { url }.send(
                    user,
                    bot
                )
            }
        }

    }
}
