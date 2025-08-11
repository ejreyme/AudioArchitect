package com.joonyor.labs.audioarchitect.home

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.joonyor.labs.audioarchitect.data.AudioLibraryService
import com.joonyor.labs.audioarchitect.data.PlaylistEvent
import com.joonyor.labs.audioarchitect.data.PlaylistEventType
import com.joonyor.labs.audioarchitect.data.YmePlaylist
import com.joonyor.labs.audioarchitect.data.YmeTrack
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
    val scope = CoroutineScope(Dispatchers.IO)
    var isPlaying: MutableState<Boolean> = mutableStateOf(false)
    var currentTrackPlaying: MutableState<YmeTrack> = mutableStateOf(YmeTrack())
    var selectedTrack: MutableState<YmeTrack> = mutableStateOf(YmeTrack())
    var trackCollection: MutableState<List<YmeTrack>> = mutableStateOf(emptyList())
    var playlistCollection: MutableState<List<YmePlaylist>> = mutableStateOf(emptyList())
    var trackPosition: MutableState<Float> = mutableStateOf(0.0f)

    init {
        refresh()
        scope.launch {
            audioPlayerService.trackPosition.collect {
                println("collecting trackPosition: $it")
                trackPosition.value = it
            }
        }
    }

    fun onPlaylistEvent(event: PlaylistEvent) {
        when (event.type) {
            PlaylistEventType.CREATE -> {
                println("Create playlist: ${event.playlist.name}")
                audioLibraryService.addPlaylist(event.playlist)
            }
            PlaylistEventType.ADD_TRACK -> {
                println("Add track to playlist: ${event.playlist.name}")
                audioLibraryService.updatePlaylist(event.playlist, event.track)
            }
            else -> {
                println("Unknown playlist event")
            }
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
            AudioPlayerEventType.TRACK_POSITION -> {
                onTrackPositionChange(event.trackPosition)
            }
            AudioPlayerEventType.REPEAT -> {
                onRepeatClick(event.isRepeat)
            }
            else -> {
                println("Unknown audio player event")
            }
        }
    }

    private fun onRepeatClick(isRepeat: Boolean) {
        audioPlayerService.repeat(isRepeat)
    }

    private fun onTrackPositionChange(position: Float) {
        audioPlayerService.trackPositionChange(position)
    }

    private fun refresh() {
        scope.launch {
            refreshPlaylists()
        }
        scope.launch {
            refreshTracks()
        }
    }

    private suspend fun refreshPlaylists() {
        println("refreshPlaylists")
        audioLibraryService.latestPlaylistCollection.collect {
            println("refreshPlaylists: $it")
            playlistCollection.value = it
        }
    }

    private suspend fun refreshTracks() {
        audioLibraryService.latestTrackCollection.collect {
            println("refreshTracks: $it")
            trackCollection.value = it
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