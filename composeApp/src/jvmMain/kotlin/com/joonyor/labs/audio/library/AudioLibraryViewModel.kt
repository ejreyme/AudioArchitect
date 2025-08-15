package com.joonyor.labs.audio.library

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.joonyor.labs.audio.loggerFor
import com.joonyor.labs.audio.playlist.PlaylistEvent
import com.joonyor.labs.audio.playlist.PlaylistEventType
import com.joonyor.labs.audio.playlist.YmePlaylist
import com.joonyor.labs.audio.track.TrackEvent
import com.joonyor.labs.audio.track.TrackEventType
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

data class AudioLibraryState(
    var activePlaylist: MutableState<YmePlaylist> = mutableStateOf(YmePlaylist(id = 0, name = "Library")),
    var tracks: MutableState<List<YmeTrack>> = mutableStateOf(emptyList()),
    var playlists: MutableState<List<YmePlaylist>> = mutableStateOf(emptyList()),
    var activeScreen: MutableState<NavEventType> = mutableStateOf(NavEventType.LIBRARY)
)

class AudioLibraryViewModel() {
    private val audioLibraryService = AudioLibraryService()
    private val logger = loggerFor(javaClass)
    val scope = CoroutineScope(Dispatchers.IO)
    val libState = AudioLibraryState()

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
                logger.debug("Unknown playlist event")
            }
        }
    }

    fun onTrackEvent(event: TrackEvent) {
        when (event.type) {
            TrackEventType.ADD_TAG -> onTrackAddTagEvent(event)
            else -> {
                logger.debug("Unknown track event")
            }
        }
    }

    private fun onTrackAddTagEvent(event: TrackEvent) {
        logger.debug("Add tag to track: ${event.track.title}")
        audioLibraryService.updateTrack(event.track, event.tag)
    }

    fun onSearchQuery(query: String) {
        scope.launch {
            if (query.isEmpty()) {
                refreshTracks()
            } else {
                libState.tracks.value = audioLibraryService.searchTracks(query)
            }
        }
    }

    private fun onPlaylistExportEvent(event: PlaylistEvent) {
        logger.debug("Export playlist: ${event.playlist.name}")
        audioLibraryService.exportPlaylist(event.playlist, event.exportType)
    }

    private fun onPlaylistDeleteEvent(event: PlaylistEvent) {
        audioLibraryService.deletePlaylist(event.playlist)
    }
    
    private fun onNavLibraryEvent() {
        libState.activeScreen.value = NavEventType.LIBRARY
        scope.launch {
            refreshTracks()
        }
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
    
    private fun onPlaylistCreateEvent(event: PlaylistEvent) {
        logger.debug("Create playlist: ${event.playlist.name}")
        audioLibraryService.createPlaylist(event.playlist)
    }

    private fun onPlaylistAddTrackEvent(event: PlaylistEvent) {
        logger.debug("Add track to playlist: ${event.playlist.name}")
        audioLibraryService.updatePlaylist(event.playlist, event.track)
    }

    private fun onPlaylistReadEvent(event: PlaylistEvent) {
        logger.debug("View playlist: ${event.playlist.name}")
        libState.activeScreen.value = NavEventType.PLAYLIST
        libState.tracks.value = event.playlist.tracks
        libState.activePlaylist.value = event.playlist
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
        logger.debug("refreshPlaylists")
        audioLibraryService.latestPlaylistCollection.collect {
            logger.debug("refreshPlaylists: ${it.size}")
            libState.playlists.value = it
        }
    }

    private suspend fun refreshTracks() {
        audioLibraryService.latestTrackCollection.collect {
            logger.debug("refreshTracks: ${it.size}")
            libState.tracks.value = it
        }
    }
    // TODO add scroll pagination
}
