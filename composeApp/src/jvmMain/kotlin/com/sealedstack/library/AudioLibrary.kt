package com.sealedstack.library

import ExploreScreen
import TrackDetail
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.TopAppBar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sealedstack.player.AudioPlayer
import com.sealedstack.player.AudioPlayerViewModel
import com.sealedstack.playlist.PlaylistCollection
import com.sealedstack.track.TrackCollection

@Composable
fun AudioLibrary(
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
                    AudioPlayer(
                        playerState = apVM.playerState,
                        onAudioPlayerEvent = { apVM.onAudioPlayerEvent(it) },
                    )
                }
            },
        ) {
            Row(modifier = Modifier.fillMaxSize().padding(top = 50.dp)) {
                Column(
                    modifier = Modifier
                        .weight(0.2f)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(10.dp)
                ) {
                    SideNav(onNavEvent = { libVM.onNavigationEvent(it) })
                    Divider()
                    PlaylistCollection(
                        playlists = libVM.libState.playlistCollection.value,
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
                                TrackCollection(
                                    modifier = Modifier
                                        .weight(0.8f)
                                        .safeContentPadding()
                                        .fillMaxSize()
                                        .padding(top = 10.dp, start = 10.dp, end = 10.dp, bottom = 100.dp),
                                    selectedTrack = apVM.playerState.activeTrack,
                                    isPlaying = apVM.playerState.isPlaying.value,
                                    trackPlaying = apVM.playerState.trackPlaying.value,
                                    tracks = libVM.libState.trackCollection.value,
                                    playlistCollection = libVM.libState.playlistCollection.value,
                                    activePlaylist = libVM.libState.activePlaylist.value,
                                    onPlaylistEvent = { libVM.onPlaylistEvent(it) },
                                    onAudioPlayerEvent = { apVM.onAudioPlayerEvent(it) },
                                    mediaPlayerState = apVM.playerState.mediaState.value
                                )
                                TrackDetail(
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