package com.joonyor.labs.audio.library

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LibraryMusic
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
    libVM: AudioLibraryViewModel,
    apVM: AudioPlayerViewModel
) {
    MaterialTheme {
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
                        var searchQuery by remember { mutableStateOf("") }
                        TextField(
                            value = searchQuery,
                            onValueChange = {
                                searchQuery = it
                                libVM.onSearchQuery(it)
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
                        playerState = apVM.playerState,
                        onAudioPlayerEvent = { apVM.onAudioPlayerEvent(it) },
                    )
                }
            },
        ) {
            // main content (row) [nav/playlist (col) | track list (col)| track detail (col) ]
            Row(modifier = Modifier.fillMaxSize().padding(top = 50.dp)) {
                // nav/playlist (col)
                Column(
                    modifier = Modifier
                        .weight(0.2f)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(10.dp)
                ) {
                    SideNavScreen(onNavEvent = { libVM.onNavigationEvent(it) })
                    Divider()
                    PlaylistScreen(
                        playlists = libVM.libState.playlists.value,
                        onPlaylistEvent = { libVM.onPlaylistEvent(it) }
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(0.8f)
                ) {
                    Row(modifier = Modifier.fillMaxSize()) {
                        when (libVM.libState.activeScreen.value) {
                            NavEventType.HOME -> HomeScreen()
                            NavEventType.EXPLORE -> ExploreScreen()
                            NavEventType.LIBRARY, NavEventType.PLAYLIST -> {
                                // track list (col)
                                TrackListScreen(
                                    modifier = Modifier
                                    .weight(0.8f)
                                        .safeContentPadding()
                                        .fillMaxSize()
                                        .padding(top = 10.dp, start = 10.dp, end = 10.dp, bottom = 100.dp),
                                    selectedTrack = apVM.playerState.activeTrack,
                                    isPlaying = apVM.playerState.isPlaying.value,
                                    trackPlaying = apVM.playerState.trackPlaying.value,
                                    tracks = libVM.libState.tracks.value,
                                    playlists = libVM.libState.playlists.value,
                                    activePlaylist = libVM.libState.activePlaylist.value,
                                    onPlaylistEvent = { libVM.onPlaylistEvent(it) },
                                    onAudioPlayerEvent = { apVM.onAudioPlayerEvent(it) },
                                )
                                // track detail (col)
                                TrackDetailScreen(
                                    modifier = Modifier
                                        .weight(0.2f)
                                        .fillMaxHeight()
                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                        .padding(10.dp),
                                    selectedTrack = apVM.playerState.activeTrack.value,
                                    onTrackEvent = { libVM.onTrackEvent(it) },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SideNavScreen(
    onNavEvent: (NavEvent) -> Unit,
) {
    Row(modifier = Modifier.fillMaxWidth()
        .combinedClickable(
            onClick = {
                onNavEvent.invoke(
                    NavEvent(
                        type = NavEventType.HOME
                    )
                )
            }
        )
    ) {
        Icon(
            imageVector = Icons.Outlined.Home,
            contentDescription = "Home",
        )
        Text("Home")
    }
    Row(modifier = Modifier.fillMaxWidth()
        .combinedClickable(
            onClick = {
                onNavEvent.invoke(
                    NavEvent(
                        type = NavEventType.EXPLORE
                    )
                )
            }
        )
    ) {
        Icon(
            imageVector = Icons.Outlined.Explore,
            contentDescription = "Explore",
        )
        Text("Explore")
    }
    Row(modifier = Modifier.fillMaxWidth()
        .combinedClickable(
            onClick = {
                onNavEvent.invoke(
                    NavEvent(
                        type = NavEventType.LIBRARY
                    )
                )
            }
        )
    ) {
        Icon(
            imageVector = Icons.Outlined.LibraryMusic,
            contentDescription = "Library",
        )
        Text("Library")
    }
}

@Composable
fun HomeScreen() {
    Column{
        Text("Home")
    }
}

@Composable
fun ExploreScreen() {
    Column{
        Text("Explore")
    }
}