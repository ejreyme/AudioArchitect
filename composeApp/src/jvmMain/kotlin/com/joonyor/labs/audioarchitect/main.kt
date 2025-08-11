package com.joonyor.labs.audioarchitect

import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.joonyor.labs.audioarchitect.home.AppConfiguration.APP_NAME
import com.joonyor.labs.audioarchitect.data.YmeAudioLibraryService
import com.joonyor.labs.audioarchitect.home.AudioLibraryScreen
import com.joonyor.labs.audioarchitect.home.AudioLibraryViewModel
import com.joonyor.labs.audioarchitect.player.VlcAudioPlayerService

fun main() = application {
    val audioPlayerService = VlcAudioPlayerService()
    val audioLibraryService = YmeAudioLibraryService()

    Window(
        onCloseRequest = { onExit(this, audioPlayerService) },
        title = APP_NAME,
    ) {
        AudioLibraryScreen(
            viewModel = AudioLibraryViewModel(
                audioPlayerService = audioPlayerService,
                audioLibraryService = audioLibraryService
            )
        )
    }
}

fun onExit(applicationScope: ApplicationScope, audioPlayerService: VlcAudioPlayerService) {
    println("Exiting")
    audioPlayerService.exit()
    applicationScope.exitApplication()
}