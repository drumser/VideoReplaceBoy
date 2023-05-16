package ru.quantick.videoreplacebot.common

object Constants {
    private const val VIDEO_URL_REGEX_VALUE = "(?:https?://)?(?:(?:www\\.)?youtube\\.com/(?:watch\\?v=|embed/)|youtu\\.be/|(?:www\\.)?(?:vt\\.)?tiktok\\.com.*|www\\.facebook\\.com/.+?/videos/|www\\.instagram\\.com/p/|www\\.instagram\\.com/reel/)([\\w-]+)(?:[?&]\\S*)?"
    val VIDEO_URL_REGEX = VIDEO_URL_REGEX_VALUE.toRegex()
}
