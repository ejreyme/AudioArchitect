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
    PLAYLIST,
}

class AudioLibraryViewModel(
    private val audioLibraryService: AudioLibraryService
) {
    val scope = CoroutineScope(Dispatchers.IO)
    var selectedPlaylist: MutableState<YmePlaylist> = mutableStateOf(YmePlaylist(id = 0, name = "Library"))
    var trackCollection: MutableState<List<YmeTrack>> = mutableStateOf(emptyList())
    var playlistCollection: MutableState<List<YmePlaylist>> = mutableStateOf(emptyList())
    var currentScreen: MutableState<NavEventType> = mutableStateOf(NavEventType.LIBRARY)

    init {
        refreshLibrary()
    }
    
    fun onNavigationEvent(event: NavEvent) {
        when (event.type) {
            NavEventType.EXPLORE -> onNavExploreEvent()
            NavEventType.LIBRARY -> onNavLibraryEvent()
            NavEventType.HOME -> onNavHomeEvent()
            NavEventType.PLAYLIST -> onNavPlaylistEvent()
        }
    }

    fun onPlaylistEvent(event: PlaylistEvent) {
        when (event.type) {
            PlaylistEventType.CREATE -> onPlaylistCreateEvent(event)
            PlaylistEventType.READ -> onPlaylistReadEvent(event)
            PlaylistEventType.ADD_TRACK -> onPlaylistAddTrackEvent(event)
            PlaylistEventType.DELETE -> onPlaylistDeleteEvent(event)
            PlaylistEventType.EXPORT -> onPlaylistExportEvent(event)
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
                trackCollection.value = audioLibraryService.searchTracks(query)
            }
        }
    }

    private fun onPlaylistExportEvent(event: PlaylistEvent) {
        println("Export playlist: ${event.playlist.name}")
        audioLibraryService.exportPlaylist(event.playlist)
    }

    private fun onPlaylistDeleteEvent(event: PlaylistEvent) {
        audioLibraryService.deletePlaylist(event.playlist)
    }
    
    private fun onNavLibraryEvent() {
        currentScreen.value = NavEventType.LIBRARY
        scope.launch {
            refreshTracks()
        }
    }

    private fun onNavPlaylistEvent() {
        currentScreen.value = NavEventType.PLAYLIST
    }

    private fun onNavExploreEvent() {
        currentScreen.value = NavEventType.EXPLORE
    }

    private fun onNavHomeEvent() {
        currentScreen.value = NavEventType.HOME
    }
    
    private fun onPlaylistCreateEvent(event: PlaylistEvent) {
        println("Create playlist: ${event.playlist.name}")
        audioLibraryService.createPlaylist(event.playlist)
    }

    private fun onPlaylistAddTrackEvent(event: PlaylistEvent) {
        println("Add track to playlist: ${event.playlist.name}")
        audioLibraryService.updatePlaylist(event.playlist, event.track)
    }

    private fun onPlaylistReadEvent(event: PlaylistEvent) {
        println("View playlist: ${event.playlist.name}")
        currentScreen.value = NavEventType.PLAYLIST
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
