package com.joonyor.labs.audioarchitect

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.joonyor.labs.audioarchitect.home.AppConfiguration.APP_NAME
import com.joonyor.labs.audioarchitect.data.YmeAudioLibraryService
import com.joonyor.labs.audioarchitect.home.AudioLibraryScreen
import com.joonyor.labs.audioarchitect.home.AudioLibraryViewModel
import com.joonyor.labs.audioarchitect.player.VlcAudioPlayerService

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = APP_NAME,
    ) {
        AudioLibraryScreen(
            viewModel = AudioLibraryViewModel(
                audioPlayerService = VlcAudioPlayerService(),
                audioLibraryService = YmeAudioLibraryService()
            )
        )
    }
}