package ru.quantick.videoreplacebot.listener

import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.api.media.video
import eu.vendeli.tgbot.api.message
import eu.vendeli.tgbot.types.ParseMode
import eu.vendeli.tgbot.types.internal.onFailure
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mu.KLogging
import org.springframework.context.ApplicationListener
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import ru.quantick.videoreplacebot.event.VideoFoundEvent
import ru.quantick.videoreplacebot.youtubedl.Config
import ru.quantick.videoreplacebot.youtubedl.Format
import ru.quantick.videoreplacebot.youtubedl.YoutubeDL
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

@Component
class NewVideoListener(
    private val bot: TelegramBot,
    private val youtubeDL: YoutubeDL,
) : ApplicationListener<VideoFoundEvent> {
    val newAdQueue: Queue<VideoFoundEvent> = ConcurrentLinkedQueue()

    override fun onApplicationEvent(event: VideoFoundEvent) {
        logger.info { "new event" }
        newAdQueue.add(event)
    }

    @Scheduled(fixedDelay = 1000)
    fun sendNewAdMessages() {
        runBlocking {
            var counter = 10
            while (counter == 0 || !newAdQueue.isEmpty()) {
                val foundVideo = newAdQueue.remove()
                launch {
                    logger.info { "Found videourl ${foundVideo.videoUrl}" }
                    val format = recognizeVideoFormat(foundVideo)

                    if (format != null) {
                        val fileSizeInMb = format.filesize / 1024 / 1024;
                        logger.info { "Found video format size=${fileSizeInMb}mb; url=${format.url}" }

                        if (fileSizeInMb < 15) {
                            sendVideoMessage(format, foundVideo)

                        } else {
                            sendFallbackMessage(format, foundVideo)
                        }
                    }
                }
                delay(100)
                counter--
            }
        }
    }

    private fun recognizeVideoFormat(foundVideo: VideoFoundEvent): Format? {
        val result = youtubeDL.execute(
            Config.builder().skipDownload().url(foundVideo.videoUrl)
        )
        return result?.formats?.firstOrNull { it.format_note == "Download video, watermarked" }
            ?: result?.formats?.firstOrNull { it.format_note != "storyboard" }
    }

    private suspend fun NewVideoListener.sendVideoMessage(
        format: Format, foundVideo: VideoFoundEvent
    ) {
        video { format.url }
            .caption { foundVideo.videoUrl }
            .options { replyToMessageId = foundVideo.replyTo }
            .sendAsync(foundVideo.chatId, bot)
            .await()
            .onFailure {
                sendFallbackMessage(format, foundVideo)
            }
    }

    private suspend fun sendFallbackMessage(
        format: Format, foundVideo: VideoFoundEvent
    ) {
        message {
            "Video size too big for me :( Take the <a href='${format.url}'>link</a>"
        }.options {
            parseMode = ParseMode.HTML
            replyToMessageId = foundVideo.replyTo
        }.send(foundVideo.chatId, bot)
    }

    companion object : KLogging()
}
