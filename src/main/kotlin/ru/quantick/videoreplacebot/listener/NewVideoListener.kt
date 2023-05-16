package ru.quantick.videoreplacebot.listener

import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.api.media.video
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mu.KLogging
import org.springframework.context.ApplicationListener
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import ru.quantick.videoreplacebot.event.VideoFoundEvent
import ru.quantick.videoreplacebot.youtubedl.Config
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

    @Scheduled(fixedDelay = 5000)
    fun sendNewAdMessages() {
        logger.info { "run listener" }

        runBlocking {
            var counter = 100
            while (counter == 0 || !newAdQueue.isEmpty()) {
                val foundVideo = newAdQueue.remove()
                launch {
                    logger.info { "Found videourl ${foundVideo.videoUrl}" }
                    val url = youtubeDL.execute(
                        Config.builder().skipDownload().url(foundVideo.videoUrl)
                    )?.formats?.first()?.url

                    if (url != null) {
                        video { url }
                            .caption { "Загружено ✅" }
                            .options { replyToMessageId = foundVideo.replyTo }
                            .send(foundVideo.chatId, bot)
                    }
                }
                delay(100)
                counter--
            }
        }
    }

    companion object : KLogging()
}
