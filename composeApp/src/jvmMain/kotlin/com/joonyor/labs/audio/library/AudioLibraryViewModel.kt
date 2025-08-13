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

class AudioLibraryViewModel(private val audioLibraryService: AudioLibraryService) {
    private val logger = loggerFor(javaClass)
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
                trackCollection.value = audioLibraryService.searchTracks(query)
            }
        }
    }

    private fun onPlaylistExportEvent(event: PlaylistEvent) {
        logger.debug("Export playlist: ${event.playlist.name}")
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
        logger.debug("Create playlist: ${event.playlist.name}")
        audioLibraryService.createPlaylist(event.playlist)
    }

    private fun onPlaylistAddTrackEvent(event: PlaylistEvent) {
        logger.debug("Add track to playlist: ${event.playlist.name}")
        audioLibraryService.updatePlaylist(event.playlist, event.track)
    }

    private fun onPlaylistReadEvent(event: PlaylistEvent) {
        logger.debug("View playlist: ${event.playlist.name}")
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
        logger.debug("refreshPlaylists")
        audioLibraryService.latestPlaylistCollection.collect {
            logger.debug("refreshPlaylists: ${it.size}")
            playlistCollection.value = it
        }
    }

    private suspend fun refreshTracks() {
        audioLibraryService.latestTrackCollection.collect {
            logger.debug("refreshTracks: ${it.size}")
            trackCollection.value = it
        }
    }
    // TODO add scroll pagination
}
