package ru.quantick.videoreplacebot.controller

import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.api.media.video
import eu.vendeli.tgbot.api.message
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import ru.quantick.videoreplacebot.youtubedl.Config
import ru.quantick.videoreplacebot.youtubedl.YoutubeDL

@RestController
class TestController(
    private val youtubedl: YoutubeDL,
    private val bot: TelegramBot
) {
//    @GetMapping("/")
//    fun test(): Any {
//
//        return youtubedl.execute(
//            Config.builder()
//                .skipDownload()
//                .noSimulate()
//                .url("https://www.youtube.com/watch?v=FUKmyRLOlAA")
//        )
//    }
}
