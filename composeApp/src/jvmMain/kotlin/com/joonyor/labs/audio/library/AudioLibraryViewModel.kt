package com.joonyor.labs.audio.library

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.joonyor.labs.audio.playlist.PlaylistEvent
import com.joonyor.labs.audio.playlist.PlaylistEventType
import com.joonyor.labs.audio.playlist.YmePlaylist
import com.joonyor.labs.audio.track.YmeTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NavEvent(
    val type: NavEventType
)

enum class NavEventType {
    HOME,
    EXPLORE,
    LIBRARY,
}

class AudioLibraryViewModel(
    private val audioLibraryService: AudioLibraryService
) {
    val scope = CoroutineScope(Dispatchers.IO)
    var selectedPlaylist: MutableState<YmePlaylist> = mutableStateOf(YmePlaylist(id = 0, name = "Library"))
    var trackCollection: MutableState<List<YmeTrack>> = mutableStateOf(emptyList())
    var playlistCollection: MutableState<List<YmePlaylist>> = mutableStateOf(emptyList())

    init {
        refreshLibrary()
    }
    
    fun onNavigationEvent(event: NavEvent) {
        when (event.type) {
            NavEventType.EXPLORE -> {}
            NavEventType.LIBRARY -> onNavLibraryEvent()
            NavEventType.HOME -> {}
        }
    }

    fun onPlaylistEvent(event: PlaylistEvent) {
        when (event.type) {
            PlaylistEventType.CREATE -> onPlaylistCreateEvent(event)
            PlaylistEventType.READ -> onPlaylistViewEvent(event)
            PlaylistEventType.ADD_TRACK -> onPlaylistAddTrackEvent(event)
            PlaylistEventType.DELETE -> onPlaylistDeleteEvent(event)
            else -> {
                println("Unknown playlist event")
            }
        }
    }

    private fun onPlaylistDeleteEvent(event: PlaylistEvent) {
        audioLibraryService.deletePlaylist(event.playlist)
    }

    fun onSearchQuery(query: String) {
        scope.launch {
            if (query.isEmpty()) {
                refreshTracks()
            } else {
                trackCollection.value = audioLibraryService.searchTracks(query)
            }
        }
    }
    
    private fun onNavLibraryEvent() {
        scope.launch {
            refreshTracks()
        }
    }
    
    private fun onPlaylistCreateEvent(event: PlaylistEvent) {
        println("Create playlist: ${event.playlist.name}")
        audioLibraryService.createPlaylist(event.playlist)
    }

    private fun onPlaylistAddTrackEvent(event: PlaylistEvent) {
        println("Add track to playlist: ${event.playlist.name}")
        audioLibraryService.updatePlaylist(event.playlist, event.track)
    }

    private fun onPlaylistViewEvent(event: PlaylistEvent) {
        println("View playlist: ${event.playlist.name}")
        trackCollection.value = event.playlist.tracks
        selectedPlaylist.value = event.playlist
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
    // TODO add scroll pagination
}
