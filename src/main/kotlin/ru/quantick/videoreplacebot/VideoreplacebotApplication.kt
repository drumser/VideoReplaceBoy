package ru.quantick.videoreplacebot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class VideoreplacebotApplication

fun main(args: Array<String>) {
	runApplication<VideoreplacebotApplication>(*args)
}
