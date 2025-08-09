package com.joonyor.labs.audioarchitect

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.joonyor.labs.audioarchitect.AppConfiguration.APP_NAME

object AppConfiguration {
    const val APP_NAME = "Audio Architect"
    const val LIBRARY_ROOT_PATH = "/Users/ejreyme/Music/yME"
}

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = APP_NAME,
    ) {
        MainScreen(
            viewModel = LibraryViewModel(
                audioPlayerService = YmeAudioPlayerService(),
                libraryManager = YmeLibraryManager()
            )
        )
    }
}