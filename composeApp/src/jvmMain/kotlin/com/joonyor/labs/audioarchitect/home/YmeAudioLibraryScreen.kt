package com.joonyor.labs.audioarchitect.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.joonyor.labs.audioarchitect.player.YmeAudioPlayerScreen
import com.joonyor.labs.audioarchitect.playlist.PlaylistScreen
import com.joonyor.labs.audioarchitect.track.TrackDetailScreen
import com.joonyor.labs.audioarchitect.track.TrackListScreen

@Composable
fun AudioLibraryScreen(viewModel: AudioLibraryViewModel) {
    MaterialTheme {
        val selectedTrack = viewModel.selectedTrack
        val currentTrackPlaying = viewModel.currentTrackPlaying
        val isPlaying = viewModel.isPlaying
        var playlistCollection = viewModel.playlistCollection

        var searchQuery by remember { mutableStateOf("") }

        Scaffold(
            topBar = {
                TextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        viewModel.onSearchQuery(it)
                    },
                    label = { Text("Search Tracks") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            bottomBar = {
                BottomAppBar {
                    YmeAudioPlayerScreen(
                        selectedTrack = selectedTrack.value,
                        currentTrackPlaying = currentTrackPlaying.value,
                        isPlaying = isPlaying.value,
                        trackPosition = viewModel.trackPosition,
                        onAudioPlayerEvent = { viewModel.onAudioPlayerEvent(it) },
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
                    playlistCollection = playlistCollection.value,
                    onPlaylistEvent = { viewModel.onPlaylistEvent(it) }
                )
                TrackListScreen(
                    modifier = Modifier.weight(0.6f)
                        .safeContentPadding()
                        .fillMaxSize()
                        .padding(top = 10.dp, start = 10.dp, end = 10.dp, bottom = 100.dp),
                    trackCollection = viewModel.trackCollection.value,
                    playlistCollection = playlistCollection.value,
                    selectedTrack = selectedTrack,
                    onMediaPlayerEvent = { viewModel.onAudioPlayerEvent(it) },
                    onPlaylistEvent = { viewModel.onPlaylistEvent(it) },
                    isPlaying = isPlaying.value,
                    currentTrackPlaying = currentTrackPlaying.value,
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