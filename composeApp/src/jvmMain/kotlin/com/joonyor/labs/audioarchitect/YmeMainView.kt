package com.joonyor.labs.audioarchitect

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MainScreen(viewModel: LibraryViewModel) {
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
                    YmeMediaPlayer(
                        selectedTrack = selectedTrack,
                        currentTrackPlaying = currentTrackPlaying,
                        isPlaying = isPlaying,
                        volumeSliderPosition = volumeSliderPosition,
                        onMediaPlayerEvent = { viewModel.onMediaPlayerEvent(it) },
                    )
                }
            }
        ) {
            Row(modifier = Modifier.fillMaxSize().padding(top = 50.dp)) {
                PlaylistView(
                    modifier = Modifier.weight(0.2f),
                    playlists = playlists,
                )
                MainView(
                    modifier = Modifier.weight(0.6f)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .safeContentPadding()
                        .fillMaxSize().padding(bottom = 100.dp),
                    tracks = tracks,
                    onMediaPlayerEvent = { viewModel.onMediaPlayerEvent(it) },
                )
                DetailView(
                    modifier = Modifier.weight(0.2f),
                    selectedTrack = selectedTrack,
                )
            }
        }
    }
}

@Composable
fun PlaylistView(
    modifier: Modifier = Modifier,
    playlists: List<YmePlaylist>
) {
    Column(
        modifier = modifier
    ) {
        Text("Playlists")
        Button(
            onClick = {

            }
        ) {
            Text("New Playlist")
        }
        LazyColumn {
            items(playlists.size) { playlist ->
                Row {
                    Text(playlists[playlist].name)
                }
            }
        }
    }
}

@Composable
fun MainView(
    modifier: Modifier = Modifier,
    onMediaPlayerEvent: (MediaPlayerEvent) -> Unit,
    tracks: List<YmeTrack> = emptyList(),
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LazyColumn {
            items(tracks.size) { track ->
                Row {
                    Button(
                        onClick = {
                            onMediaPlayerEvent.invoke(
                                MediaPlayerEvent(
                                    type = MediaPlayerEventType.PLAY,
                                    track = tracks[track]
                                )
                            )
                        }
                    ) {
                        Text("Play")
                    }
                    Text("${tracks[track].title} - ${tracks[track].artist}")
                }
            }
        }
    }
}

@Composable
fun DetailView(
    modifier: Modifier = Modifier,
    selectedTrack: YmeTrack
) {
    Column(
        modifier = modifier
    ) {
        Text("Detail")
        Text(selectedTrack.title)
        Text(selectedTrack.artist)
    }
}

class LibraryViewModel(
    private val audioPlayerService: AudioPlayerService,
    libraryManager: LibraryManager
) {
    val volumeSliderPosition: Float by mutableStateOf(0.0f)
    val trackSliderPosition: Float by mutableStateOf(0.0f)
    var searchQuery: String by mutableStateOf("")
    var tracks: List<YmeTrack> by mutableStateOf(emptyList())
    var playlists: List<YmePlaylist> by mutableStateOf(emptyList())
    var selectedTrack: YmeTrack by mutableStateOf(YmeTrack("", "No selected track"))
    var currentTrackPlaying: YmeTrack? by mutableStateOf(YmeTrack())
    var isPlaying: Boolean by mutableStateOf(false)

    init {
        tracks = libraryManager.loadLibrary()
        playlists = libraryManager.loadPlaylists()
    }

    fun onPlayClick(track: YmeTrack) {
        audioPlayerService.play(track.filePath)
        selectedTrack = track
        currentTrackPlaying = track
        isPlaying = true
    }

    fun onStopClick() {
        audioPlayerService.stop()
        selectedTrack = YmeTrack()
        currentTrackPlaying = null
        isPlaying = false
    }

    fun onPauseClick() {
        audioPlayerService.pause()
        isPlaying = false
    }

    fun onMediaPlayerEvent(event: MediaPlayerEvent) {
        when (event.type) {
            MediaPlayerEventType.PLAY -> {
                onPlayClick(event.track)
            }
            MediaPlayerEventType.PAUSE -> {
                onPauseClick()
            }
            MediaPlayerEventType.STOP -> {
                onStopClick()
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