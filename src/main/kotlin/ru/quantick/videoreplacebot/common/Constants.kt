package ru.quantick.videoreplacebot.common

object Constants {
    private const val VIDEO_URL_REGEX_VALUE = "(?:https?://)?((?:www\\.)?(?:vt\\.)?tiktok\\.com.*)([\\w-]+)(?:[?&]\\S*)?"
    val VIDEO_URL_REGEX = VIDEO_URL_REGEX_VALUE.toRegex()
}
