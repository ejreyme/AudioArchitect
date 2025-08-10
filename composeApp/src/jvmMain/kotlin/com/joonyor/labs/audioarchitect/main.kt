package com.joonyor.labs.audioarchitect

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.joonyor.labs.audioarchitect.AppConfiguration.APP_NAME
import com.joonyor.labs.audioarchitect.player.YmeAudioPlayerService

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = APP_NAME,
    ) {
        AudioLibraryScreen(
            viewModel = AudioLibraryViewModel(
                audioPlayerService = YmeAudioPlayerService(),
                audioLibraryService = YmeAudioLibraryService()
            )
        )
    }
}