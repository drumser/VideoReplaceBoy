package ru.quantick.videoreplacebot.listener

import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import ru.quantick.videoreplacebot.service.Scheduler

@Service
class ApplicationListener(
    private val scheduler: Scheduler
) {
    @EventListener(ApplicationReadyEvent::class)
    fun doSomethingAfterStartup() {
        scheduler.scheduleHandleUpdate()
    }
}
