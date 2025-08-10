package com.joonyor.labs.audioarchitect

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.joonyor.labs.audioarchitect.player.AudioPlayerEvent
import com.joonyor.labs.audioarchitect.player.AudioPlayerEventType
import com.joonyor.labs.audioarchitect.player.AudioPlayerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AudioLibraryViewModel(
    private val audioPlayerService: AudioPlayerService,
    private val audioLibraryService: AudioLibraryService
) {
    var scope = CoroutineScope(Dispatchers.IO)
    var isPlaying: MutableState<Boolean> = mutableStateOf(false)
    var currentTrackPlaying: MutableState<YmeTrack> = mutableStateOf(YmeTrack())
    var selectedTrack: MutableState<YmeTrack> = mutableStateOf(YmeTrack())
    var trackCollection: MutableState<List<YmeTrack>> = mutableStateOf(emptyList())
    var playlistCollection: MutableState<List<YmePlaylist>> = mutableStateOf(emptyList())

    init {
        refresh()
    }

    fun onPlaylistEvent(event: PlaylistEvent) {
        when (event.type) {
            PlaylistEventType.CREATE -> {
                scope.launch {
                    println("Create playlist: ${event.playlist.name}")
                    audioLibraryService.addPlaylist(event.playlist)
                    refreshPlaylists()
                }
            }
            else -> {}
        }
    }

    /**
     * Handles audio player events such as play, pause, stop, and volume adjustment.
     * Executes the corresponding actions based on the event type.
     *
     * @param event The audio player event containing details such as event type, track, and volume level.
     */
    fun onAudioPlayerEvent(event: AudioPlayerEvent) {
        when (event.type) {
            AudioPlayerEventType.PLAY -> {
                onPlayClick(event.track)
            }
            AudioPlayerEventType.PAUSE -> {
                onPauseClick()
            }
            AudioPlayerEventType.STOP -> {
                onStopClick()
            }
            AudioPlayerEventType.VOLUME -> {
                onVolumeChange(event.volume)
            }
            else -> {
                onStopClick()
            }
        }
    }

    private fun refresh() {
        refreshPlaylists()
        refreshTracks()
    }

    private fun refreshPlaylists() {
        println("refreshPlaylists")
        scope.launch {
            audioLibraryService.latestPlaylistCollection.collect {
                println("refreshPlaylists: $it")
                playlistCollection.value = it
            }
        }
    }

    private fun refreshTracks() {
        scope.launch {
            audioLibraryService.latestTrackCollection.collect {
                println("refreshTracks: $it")
                trackCollection.value = it
            }
        }
    }

    private fun onPlayClick(track: YmeTrack) {
        audioPlayerService.play(track.filePath)
        selectedTrack.value = track
        currentTrackPlaying.value = track
        isPlaying.value = true
    }

    private fun onStopClick() {
        audioPlayerService.stop()
        selectedTrack.value = YmeTrack()
        currentTrackPlaying.value = YmeTrack()
        isPlaying.value = false
    }

    private fun onPauseClick() {
        audioPlayerService.pause()
        isPlaying.value = false
    }

    private fun onVolumeChange(value: Float) {
        audioPlayerService.volumeChange(value)
    }

    // TODO performance test this need to reset without reload
    fun onSearchQuery(it: String) {
        scope.launch {
            if (it.isEmpty()) {
                refresh()
            } else {
                trackCollection.value = trackCollection.value.filter { track -> track.title.contains(it, ignoreCase = true) }
            }
        }
    }



    // TODO add scroll pagination
}