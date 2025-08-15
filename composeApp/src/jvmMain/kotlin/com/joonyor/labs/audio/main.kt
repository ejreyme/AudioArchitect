package com.joonyor.labs.audio

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.joonyor.labs.audio.config.AppConfiguration.APP_NAME
import com.joonyor.labs.audio.library.AudioLibraryScreen
import com.joonyor.labs.audio.library.AudioLibraryService
import com.joonyor.labs.audio.library.AudioLibraryViewModel
import com.joonyor.labs.audio.player.AudioPlayerViewModel
import com.joonyor.labs.audio.player.VlcAudioPlayerService
import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun <T> loggerFor(clazz: Class<T>): Logger = LoggerFactory.getLogger(clazz)

fun main() = application {
    val audioPlayerService = VlcAudioPlayerService()
    val audioLibraryService = AudioLibraryService()

    Window(
        onCloseRequest = { onExit(this, audioPlayerService) },
        title = APP_NAME,
        state = rememberWindowState(width = 1440.dp, height = 1080.dp)
    ) {
        AudioLibraryScreen(
            libVM = AudioLibraryViewModel(
                audioLibraryService = audioLibraryService
            ),
            apVM = AudioPlayerViewModel(
                audioPlayerService = audioPlayerService
            )
        )
    }
}

fun onExit(
    applicationScope: ApplicationScope,
    audioPlayerService: VlcAudioPlayerService
) {
    audioPlayerService.exit()
    applicationScope.exitApplication()
}