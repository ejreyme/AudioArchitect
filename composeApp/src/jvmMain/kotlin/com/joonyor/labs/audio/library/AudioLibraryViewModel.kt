package com.joonyor.labs.audio.library

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.joonyor.labs.audio.player.AudioPlayerEvent
import com.joonyor.labs.audio.player.AudioPlayerEventType
import com.joonyor.labs.audio.player.AudioPlayerService
import com.joonyor.labs.audio.playlist.PlaylistEvent
import com.joonyor.labs.audio.playlist.PlaylistEventType
import com.joonyor.labs.audio.playlist.YmePlaylist
import com.joonyor.labs.audio.track.YmeTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

class AudioLibraryViewModel(
    private val audioPlayerService: AudioPlayerService,
    private val audioLibraryService: AudioLibraryService
) {
    val scope = CoroutineScope(Dispatchers.IO)
    var isPlaying: MutableState<Boolean> = mutableStateOf(false)
    var currentTrackPlaying: MutableState<YmeTrack> = mutableStateOf(YmeTrack())
    var selectedTrack: MutableState<YmeTrack> = mutableStateOf(YmeTrack())
    var selectedPlaylist: MutableState<YmePlaylist> = mutableStateOf(YmePlaylist(id = 0, name = "Library"))
    var trackCollection: MutableState<List<YmeTrack>> = mutableStateOf(emptyList())
    var playlistCollection: MutableState<List<YmePlaylist>> = mutableStateOf(emptyList())
    var trackPosition: MutableState<Float> = mutableStateOf(0.0f)
    val trackQueue = Channel<YmeTrack>()
    init {
        refreshLibrary()
        audioPlayerEventHandlers()
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
            PlaylistEventType.VIEW -> {
                println("View playlist: ${event.playlist.name}")
                trackCollection.value = event.playlist.tracks
                selectedPlaylist.value = event.playlist
            }
            PlaylistEventType.LIBRARY -> {
                scope.launch {
                    refreshTracks()
                }
            }
            else -> {
                println("Unknown playlist event")
            }
        }
    }

    fun onSearchQuery(query: String) {
        scope.launch {
            if (query.isEmpty()) {
                refreshTracks()
            } else {
                trackCollection.value = audioLibraryService.search(query)
            }
        }
    }

    private fun refreshLibrary() {
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

    /**
     * Handles various audio player events dispatched by the user or system.
     *
     * @param event The event representing the type of interaction with the audio player, such as play, pause, stop,
     * volume change, track position change, or repeat toggle. Contains associated data depending on the event type,
     * such as the track, volume level, track position, or repeat state.
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
            AudioPlayerEventType.QUEUE -> {
                onQueueEvent(event.track)
            }
            else -> {
                println("Unknown audio player event")
            }
        }
    }

    private fun onQueueEvent(track: YmeTrack) {
        println("onQueueEvent: $track")
        scope.launch { trackQueue.send(track) }
    }

    private fun audioPlayerEventHandlers() {
        scope.launch {
            audioPlayerService.trackPosition.collect {
                println("collecting trackPosition: $it")
                trackPosition.value = it
            }
        }

        scope.launch {
            audioPlayerService.isPlaying.collect {
                println("collecting isPlaying: $it")
                isPlaying.value = it
                if (!it) {
                    playNextTrack()
                }
            }
        }
    }

    private suspend fun playNextTrack() {
        val nextTrack = trackQueue.receive()
        if (nextTrack.isNotNew) {
            onPlayClick(nextTrack)
        }
    }

    private fun onRepeatClick(isRepeat: Boolean) {
        audioPlayerService.repeat(isRepeat)
    }

    private fun onTrackPositionChange(position: Float) {
        audioPlayerService.trackPositionChange(position)
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


    // TODO add scroll pagination
}