package com.sealedstack

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.sealedstack.config.AppConfiguration.APP_NAME
import com.sealedstack.data.LocalDatabase
import com.sealedstack.library.AudioLibrary
import com.sealedstack.library.AudioLibraryService
import com.sealedstack.library.AudioLibraryViewModel
import com.sealedstack.player.AudioPlayerViewModel
import com.sealedstack.player.VlcAudioPlayerService
import com.sealedstack.playlist.PlaylistRepository
import com.sealedstack.track.TrackRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun <T> loggerFor(clazz: Class<T>): Logger = LoggerFactory.getLogger(clazz)

fun main() = application {
    val database = LocalDatabase()
    val trackRepository = TrackRepository(database)
    val playlistRepository = PlaylistRepository(database)

    val audioLibraryService = AudioLibraryService(trackRepository,playlistRepository)
    val audioPlayerService = VlcAudioPlayerService()

    Window(
        onCloseRequest = { onExit(this, audioPlayerService) },
        title = APP_NAME,
        state = rememberWindowState(width = 1440.dp, height = 1080.dp),
    ) {
        AudioLibrary(
            libVM = AudioLibraryViewModel(audioLibraryService),
            apVM = AudioPlayerViewModel(audioPlayerService)
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