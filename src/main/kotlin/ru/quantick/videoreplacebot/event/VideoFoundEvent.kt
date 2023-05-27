package ru.quantick.videoreplacebot.event

import org.springframework.context.ApplicationEvent

class VideoFoundEvent(
    source: Any,
    val replyTo: Long,
    val chatId: Long,
    val videoUrl: String
) : ApplicationEvent(source)
