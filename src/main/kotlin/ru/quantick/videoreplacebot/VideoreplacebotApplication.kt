package ru.quantick.videoreplacebot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.retry.annotation.EnableRetry
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
@EnableRetry
class VideoreplacebotApplication

fun main(args: Array<String>) {
	runApplication<VideoreplacebotApplication>(*args)
}
