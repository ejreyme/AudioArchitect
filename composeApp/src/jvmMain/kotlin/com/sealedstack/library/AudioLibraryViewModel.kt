package com.sealedstack.library

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.sealedstack.playlist.PlaylistEvent
import com.sealedstack.playlist.PlaylistEventType
import com.sealedstack.playlist.YmePlaylist
import com.sealedstack.track.TrackEvent
import com.sealedstack.track.TrackEventType
import com.sealedstack.track.YmeTrack
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

data class AudioLibraryState(
    var activeYmePlaylist: MutableState<YmePlaylist> = mutableStateOf(YmePlaylist(id = 0, name = "Library")),
    var tracks: MutableState<List<YmeTrack>> = mutableStateOf(emptyList()),
    var playlists: MutableState<List<YmePlaylist>> = mutableStateOf(emptyList()),
    var activeScreen: MutableState<NavEventType> = mutableStateOf(NavEventType.LIBRARY)
)

class AudioLibraryViewModel(val audioLibraryService: AudioLibraryService) {
    val scope = CoroutineScope(Dispatchers.IO)
    val libState = AudioLibraryState()

    init {
        refreshLibrary()
    }

    fun onSearchQuery(query: String) {
        scope.launch {
            if (query.isEmpty()) {
                when (libState.activeScreen.value) {
                    NavEventType.LIBRARY -> refreshTracks()
                    NavEventType.PLAYLIST -> {
                        libState.tracks.value = libState.activeYmePlaylist.value.ymeTracks
                    }
                    else -> refreshTracks()
                }
            } else {
                libState.tracks.value = audioLibraryService.searchTracks(query)
            }
        }
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
        scope.launch {
            when (event.type) {
                PlaylistEventType.CREATE -> onPlaylistCreateEvent(event)
                PlaylistEventType.READ -> onPlaylistReadEvent(event)
                PlaylistEventType.ADD_TRACK -> onPlaylistAddTrackEvent(event)
                PlaylistEventType.DELETE -> onPlaylistDeleteEvent(event)
                PlaylistEventType.EXPORT -> onPlaylistExportEvent(event)
                else -> println("Unknown playlist event")
            }
        }
    }

    fun onTrackEvent(event: TrackEvent) {
        scope.launch {
            when (event.type) {
                TrackEventType.ADD_TAG -> onTrackAddTagEvent(event)
                else -> println("Unknown track event")
            }
        }
    }

    private suspend fun onTrackAddTagEvent(event: TrackEvent) {
        println("Add tag to track: ${event.ymeTrack.title}")
        audioLibraryService.updateTrack(event.ymeTrack, event.tag)
    }

    private fun onPlaylistExportEvent(event: PlaylistEvent) {
        println("Export playlist: ${event.playlist.name}")
        audioLibraryService.exportPlaylist(event.playlist, event.exportType)
    }

    private suspend fun onPlaylistDeleteEvent(event: PlaylistEvent) {
        audioLibraryService.deletePlaylist(event.playlist)
    }
    
    private fun onNavLibraryEvent() {
        libState.activeScreen.value = NavEventType.LIBRARY
        scope.launch { refreshTracks() }
    }

    private fun onNavPlaylistEvent() {
        libState.activeScreen.value = NavEventType.PLAYLIST
    }

    private fun onNavExploreEvent() {
        libState.activeScreen.value = NavEventType.EXPLORE
    }

    private fun onNavHomeEvent() {
        libState.activeScreen.value = NavEventType.HOME
    }
    
    private suspend fun onPlaylistCreateEvent(event: PlaylistEvent) {
        println("Create playlist: ${event.playlist.name}")
        audioLibraryService.createPlaylist(event.playlist)
    }

    private suspend fun onPlaylistAddTrackEvent(event: PlaylistEvent) {
        println("Add track to playlist: ${event.playlist.name}")
        audioLibraryService.addTrackToPlaylist(event.playlist, event.track)
    }

    private fun onPlaylistReadEvent(event: PlaylistEvent) {
        println("View playlist: ${event.playlist.name}")
        libState.activeScreen.value = NavEventType.PLAYLIST
        libState.tracks.value = event.playlist.ymeTracks
        libState.activeYmePlaylist.value = event.playlist
    }
    
    private fun refreshLibrary() {
        scope.launch { refreshPlaylists() }
        scope.launch { refreshTracks() }
    }

    private suspend fun refreshPlaylists() {
        println("refreshPlaylists")
        audioLibraryService.latestLibraryPlaylists.collect {
            println("refreshPlaylists: ${it.size}")
            libState.playlists.value = it
        }
    }

    private suspend fun refreshTracks() {
        audioLibraryService.latestLibraryTracks.collect {
            println("refreshTracks: ${it.size}")
            libState.tracks.value = it
        }
    }
}