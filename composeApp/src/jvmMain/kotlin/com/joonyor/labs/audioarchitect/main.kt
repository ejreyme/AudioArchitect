package com.joonyor.labs.audioarchitect

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.joonyor.labs.audioarchitect.AppConfiguration.APP_NAME
import com.joonyor.labs.audioarchitect.player.YmeAudioPlayerService

object AppConfiguration {
    const val APP_NAME = "Audio Architect"
    const val LIBRARY_ROOT_PATH = "/Users/ejreyme/Music/yME"
}

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = APP_NAME,
    ) {
        AudioArchitectApp(
            viewModel = LibraryViewModel(
                audioPlayerService = YmeAudioPlayerService(),
                audioLibraryService = YmeAudioLibraryService()
            )
        )
    }
}