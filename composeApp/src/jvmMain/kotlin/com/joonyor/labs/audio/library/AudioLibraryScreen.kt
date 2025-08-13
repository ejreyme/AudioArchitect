package com.joonyor.labs.audio.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.TopAppBar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.joonyor.labs.audio.player.AudioPlayerScreen
import com.joonyor.labs.audio.player.AudioPlayerViewModel
import com.joonyor.labs.audio.playlist.PlaylistScreen
import com.joonyor.labs.audio.track.TrackDetailScreen
import com.joonyor.labs.audio.track.TrackListScreen

@Composable
fun AudioLibraryScreen(
    libraryViewModel: AudioLibraryViewModel,
    audioPlayerViewModel: AudioPlayerViewModel
) {
    MaterialTheme {
        var searchQuery by remember { mutableStateOf("") }
        Scaffold(
            topBar = {
                TopAppBar {
                    Column(
                        modifier = Modifier
                            .weight(.2f)
                            .padding(start = 10.dp),
                    ) {
                        Text("Audio Architect")
                    }
                    Column(
                        modifier = Modifier
                            .weight(.6f)
                    ) {
                        TextField(
                            value = searchQuery,
                            onValueChange = {
                                searchQuery = it
                                libraryViewModel.onSearchQuery(it)
                            },
                            label = { Text("Search Tracks") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Column(
                        modifier = Modifier
                            .weight(.2f)
                            .padding(start = 10.dp),
                        horizontalAlignment = Alignment.Start
                    )  {
                        Text("Profile")
                    }
                }
            },
            bottomBar = {
                BottomAppBar {
                    AudioPlayerScreen(
                        selectedTrack = audioPlayerViewModel.selectedTrack.value,
                        currentTrackPlaying = audioPlayerViewModel.currentTrackPlaying.value,
                        isPlaying = audioPlayerViewModel.isPlaying.value,
                        trackPosition = audioPlayerViewModel.trackPosition,
                        onAudioPlayerEvent = { audioPlayerViewModel.onAudioPlayerEvent(it) },
                    )
                }
            },
        ) {
            Row(modifier = Modifier.fillMaxSize().padding(top = 50.dp)) {
                PlaylistScreen(
                    modifier = Modifier.weight(0.2f)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(10.dp),
                    playlistCollection = libraryViewModel.playlistCollection.value,
                    onNavEvent = { libraryViewModel.onNavigationEvent(it) },
                    onPlaylistEvent = { libraryViewModel.onPlaylistEvent(it) }
                )
                TrackListScreen(
                    modifier = Modifier.weight(0.6f)
                        .safeContentPadding()
                        .fillMaxSize()
                        .padding(top = 10.dp, start = 10.dp, end = 10.dp, bottom = 100.dp),
                    trackCollection = libraryViewModel.trackCollection.value,
                    playlistCollection = libraryViewModel.playlistCollection.value,
                    selectedTrack = audioPlayerViewModel.selectedTrack,
                    onAudioPlayerEvent = { audioPlayerViewModel.onAudioPlayerEvent(it) },
                    onPlaylistEvent = { libraryViewModel.onPlaylistEvent(it) },
                    isPlaying = audioPlayerViewModel.isPlaying.value,
                    currentTrackPlaying = audioPlayerViewModel.currentTrackPlaying.value,
                    selectedPlaylist = libraryViewModel.selectedPlaylist.value,
                )
                TrackDetailScreen(
                    modifier = Modifier.weight(0.2f)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(10.dp),
                    selectedTrack = audioPlayerViewModel.selectedTrack.value,
                )
            }
        }
    }
}

@Composable
fun Sidebar(
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(MaterialTheme.colorScheme.primaryContainer),
        content = content
    )
}