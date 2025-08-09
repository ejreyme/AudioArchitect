package com.joonyor.labs.audioarchitect

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.joonyor.labs.audioarchitect.player.AudioPlayerService
import com.joonyor.labs.audioarchitect.player.AudioPlayerEvent
import com.joonyor.labs.audioarchitect.player.AudioPlayerEventType
import com.joonyor.labs.audioarchitect.player.YmeAudioPlayer
import com.joonyor.labs.audioarchitect.playlist.PlaylistScreen
import com.joonyor.labs.audioarchitect.track.TrackDetailScreen
import com.joonyor.labs.audioarchitect.track.TrackListScreen

@Composable
fun AudioArchitectApp(viewModel: LibraryViewModel) {
    MaterialTheme {
        val selectedTrack = viewModel.selectedTrack
        val currentTrackPlaying = viewModel.currentTrackPlaying
        val tracks = viewModel.tracks
        val playlists = viewModel.playlists
        val isPlaying = viewModel.isPlaying
        val volumeSliderPosition = viewModel.volumeSliderPosition
        val searchQuery = viewModel.searchQuery

        Scaffold(
            topBar = {
                TextField(
                    value = searchQuery,
                    onValueChange = { viewModel.onSearchQuery(it) },
                    label = { Text("Search Tracks") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            bottomBar = {
                BottomAppBar {
                    YmeAudioPlayer(
                        selectedTrack = selectedTrack.value,
                        currentTrackPlaying = currentTrackPlaying,
                        isPlaying = isPlaying,
                        onAudioPlayerEvent = { viewModel.onMediaPlayerEvent(it) },
                    )
                }
            }
        ) {
            Row(modifier = Modifier.fillMaxSize().padding(top = 50.dp)) {
                PlaylistScreen(
                    modifier = Modifier.weight(0.2f)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding( 10.dp),
                    playlists = playlists,
                )
                TrackListScreen(
                    modifier = Modifier.weight(0.6f)
                        .safeContentPadding()
                        .fillMaxSize()
                        .padding(top = 10.dp, start = 10.dp, end = 10.dp, bottom = 100.dp),
                    tracks = tracks,
                    selectedTrack = selectedTrack,
                    onMediaPlayerEvent = { viewModel.onMediaPlayerEvent(it) },
                    isPlaying = isPlaying,
                )
                TrackDetailScreen(
                    modifier = Modifier.weight(0.2f)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding( 10.dp),
                    selectedTrack = selectedTrack.value,
                )
            }
        }
    }
}

class LibraryViewModel(
    private val audioPlayerService: AudioPlayerService,
    audioLibraryService: AudioLibraryService
) {
    val volumeSliderPosition: Float by mutableStateOf(0.0f)
    var searchQuery: String by mutableStateOf("")
    var tracks: List<YmeTrack> by mutableStateOf(emptyList())
    var playlists: List<YmePlaylist> by mutableStateOf(emptyList())
    var selectedTrack: MutableState<YmeTrack> = mutableStateOf(YmeTrack())
    var currentTrackPlaying: YmeTrack? by mutableStateOf(YmeTrack())
    var isPlaying: Boolean by mutableStateOf(false)

    init {
        tracks = audioLibraryService.loadTracks()
        playlists = audioLibraryService.loadPlaylists()
    }

    fun onPlayClick(track: YmeTrack) {
        audioPlayerService.play(track.filePath)
        selectedTrack.value = track
        currentTrackPlaying = track
        isPlaying = true
    }

    fun onStopClick() {
        audioPlayerService.stop()
        selectedTrack.value = YmeTrack()
        currentTrackPlaying = null
        isPlaying = false
    }

    fun onPauseClick() {
        audioPlayerService.pause()
        isPlaying = false
    }

    fun onMediaPlayerEvent(event: AudioPlayerEvent) {
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

    fun onVolumeChange(value: Float) {
        audioPlayerService.volumeChange(value)
    }

    fun onSearchQuery(it: String) {
        searchQuery = it
        tracks = tracks.filter { track -> track.title.contains(it, ignoreCase = true) }
    }
}