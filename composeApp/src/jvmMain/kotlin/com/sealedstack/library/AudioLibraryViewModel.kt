package com.sealedstack.library

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.sealedstack.library.NavEventType.*
import com.sealedstack.playlist.PlaylistEvent
import com.sealedstack.playlist.PlaylistEventType.*
import com.sealedstack.playlist.YmePlaylist
import com.sealedstack.track.TrackEvent
import com.sealedstack.track.TrackEventType.ADD_TAG
import com.sealedstack.track.YmeTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
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
    var activePlaylist: MutableState<YmePlaylist> = mutableStateOf(YmePlaylist(id = 0, name = "Library")),
    var trackCollection: MutableState<List<YmeTrack>> = mutableStateOf(emptyList()),
    var playlistCollection: MutableState<List<YmePlaylist>> = mutableStateOf(emptyList()),
    var activeScreen: MutableState<NavEventType> = mutableStateOf(LIBRARY),
)

class AudioLibraryViewModel(
    val audioLibraryService: AudioLibraryService
) {
    val scope = CoroutineScope(Dispatchers.IO)
    val libState = AudioLibraryState()
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()


    init {
        refreshLibrary()
    }

    fun onSearchQuery(query: String) {
        scope.launch {
            _searchQuery.value = query
            when(libState.activeScreen.value) {
                HOME -> refreshTracks()
                EXPLORE -> refreshTracks()
                LIBRARY -> {
                    when(searchQuery.value.isEmpty()) {
                        true -> refreshLibrary()
                        false -> filterTracks()
                    }
                }
                PLAYLIST -> {
                    when(searchQuery.value.isEmpty()) {
                        true -> libState.trackCollection.value = libState.activePlaylist.value.tracks
                        false -> refreshTracks()
                    }
                }
            }
        }
    }

    fun onNavigationEvent(event: NavEvent) {
        when (event.type) {
            EXPLORE -> libState.activeScreen.value = EXPLORE
            LIBRARY -> {
                libState.activeScreen.value = LIBRARY
                scope.launch { refreshTracks() }
            }
            HOME -> {
                libState.activeScreen.value = HOME
            }
            PLAYLIST ->  libState.activeScreen.value = PLAYLIST
        }
    }

    fun onPlaylistEvent(event: PlaylistEvent) {
        scope.launch {
            when (event.type) {
                CREATE -> audioLibraryService.newPlaylist(event.playlist)
                READ -> {
                    libState.activeScreen.value = PLAYLIST
                    libState.trackCollection.value = event.playlist.tracks
                    libState.activePlaylist.value = event.playlist
                }
                ADD_TRACK -> audioLibraryService.updatePlaylist(event.playlist, event.track)
                DELETE -> audioLibraryService.deletePlaylist(event.playlist)
                EXPORT -> audioLibraryService.exportPlaylist(event.playlist)
                else -> println("Unknown playlist event")
            }
        }
    }

    fun onTrackEvent(event: TrackEvent) {
        scope.launch {
            when (event.type) {
                ADD_TAG -> audioLibraryService.updateTrack(event.ymeTrack, event.tag)
                else -> println("Unknown track event")
            }
        }
    }

    private suspend fun filterTracks() {
        combine(
            audioLibraryService.latestLibraryTracks,
            searchQuery
        ) { items, query ->
            if (query.isBlank()) {
                items
            } else {
                items.filter { item ->
                    item.artist.contains(query, ignoreCase = true) ||
                            item.title.contains(query, ignoreCase = true)
                }
            }
        }.collect {
            libState.trackCollection.value = it
        }
    }
    
    private fun refreshLibrary() {
        scope.launch { refreshPlaylists() }
        scope.launch { refreshTracks() }
    }

    private suspend fun refreshPlaylists() {
        audioLibraryService.latestLibraryPlaylists.collect {
            libState.playlistCollection.value = it
        }
    }

    private suspend fun refreshTracks() {
        audioLibraryService.latestLibraryTracks.collect {
            libState.trackCollection.value = it
        }
    }
}