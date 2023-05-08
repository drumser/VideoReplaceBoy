package ru.quantick.videoreplacebot.youtubedl

import com.google.gson.Gson
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

class Config private constructor() {
    private val baseParameters = arrayOf(
        "--ignore-config",
        "--ignore-errors",
        "--dump-single-json"
    )

    private val parameters = mutableListOf<String>()
    private var url = ""

    companion object {
        fun builder(): Config {
            return Config()
        }
    }

    fun durationFilter(operator: String, value: Int): Config {
        return addOptionImmutable(arrayOf("--match-filter=duration$operator$value"))
    }

    fun yesPlaylist(): Config {
        return addOptionImmutable(arrayOf("--yes-playlist", "--flat-playlist"))
    }

    fun proxy(proxyUrl: String): Config {
        return addOptionImmutable(arrayOf("--proxy", proxyUrl))
    }

    fun format(format: String): Config {
        return addOptionImmutable(arrayOf("-f", format))
    }

    fun url(url: String): Config {
        val clone = Config()
        clone.url = url
        clone.parameters.addAll(parameters)
        return clone
    }

    fun skipDownload(): Config {
        return addOptionImmutable(arrayOf("--skip-download"))
    }

    fun playlistEnd(count: Int): Config {
        return addOptionImmutable(arrayOf("--playlist-end", count.toString()))
    }

    fun downloadPath(path: String): Config {
        return addOptionImmutable(arrayOf("--paths", path))
    }

    fun noSimulate(): Config {
        return addOptionImmutable(arrayOf("--no-simulate"))
    }

    fun compileOptions(): Array<String> {
        return baseParameters + parameters.toTypedArray() + url
    }

    private fun addOptionImmutable(options: Array<String>): Config {
        val clone = Config()
        clone.parameters.addAll(parameters)
        clone.parameters.addAll(options)
        return clone
    }
}

data class Result(
    val id: String,
    val formats: List<Format>
)

data class Format(
    val url: String
)

@Service
class YoutubeDL(
    @Value("\${app.youtubedl.path}") val path: String,
) {
    fun execute(config: Config, ignoreErrors: Boolean = true): Result? {
        val arguments = config.compileOptions()

        val cmd = mutableListOf<String>().apply {
            add(path)
            addAll(arguments)
        }.toTypedArray()

        return getCommandOutput(cmd, ignoreErrors)
    }

    fun getCommandOutput(cmd: Array<String>, ignoreErrors: Boolean): Result? {
        try {
            val process = ProcessBuilder(
                *cmd
            )
                .redirectErrorStream(true)
                .start()
            val stdOut = process.inputStream.bufferedReader().readText()
            process.waitFor()

            if (!ignoreErrors && process.exitValue() != 0) {
                throw Exception("Command execution failed with exit code: ${process.exitValue()}")
            }

            return Gson().fromJson(stdOut, Result::class.java)
        } catch (e: Exception) {
            if (ignoreErrors) {
                return null
            }
            throw e
        }
    }

}
