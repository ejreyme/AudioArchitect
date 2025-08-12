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
import com.joonyor.labs.audio.playlist.PlaylistScreen
import com.joonyor.labs.audio.track.TrackDetailScreen
import com.joonyor.labs.audio.track.TrackListScreen

@Composable
fun AudioLibraryScreen(viewModel: AudioLibraryViewModel) {
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
                                viewModel.onSearchQuery(it)
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
                        selectedTrack = viewModel.selectedTrack.value,
                        currentTrackPlaying = viewModel.currentTrackPlaying.value,
                        isPlaying = viewModel.isPlaying.value,
                        trackPosition = viewModel.trackPosition,
                        onAudioPlayerEvent = { viewModel.onAudioPlayerEvent(it) },
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
                    playlistCollection = viewModel.playlistCollection.value,
                    onPlaylistEvent = { viewModel.onPlaylistEvent(it) }
                )
                TrackListScreen(
                    modifier = Modifier.weight(0.6f)
                        .safeContentPadding()
                        .fillMaxSize()
                        .padding(top = 10.dp, start = 10.dp, end = 10.dp, bottom = 100.dp),
                    trackCollection = viewModel.trackCollection.value,
                    playlistCollection = viewModel.playlistCollection.value,
                    selectedTrack = viewModel.selectedTrack,
                    onMediaPlayerEvent = { viewModel.onAudioPlayerEvent(it) },
                    onPlaylistEvent = { viewModel.onPlaylistEvent(it) },
                    isPlaying = viewModel.isPlaying.value,
                    currentTrackPlaying = viewModel.currentTrackPlaying.value,
                    selectedPlaylist = viewModel.selectedPlaylist.value,
                )
                TrackDetailScreen(
                    modifier = Modifier.weight(0.2f)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(10.dp),
                    selectedTrack = viewModel.selectedTrack.value,
                )
            }
        }
    }
}