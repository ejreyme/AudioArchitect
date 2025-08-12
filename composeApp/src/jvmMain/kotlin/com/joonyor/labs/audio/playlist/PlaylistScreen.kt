package com.joonyor.labs.audio.playlist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import kotlin.random.Random

@Composable
fun PlaylistScreen(
    modifier: Modifier = Modifier,
    playlistCollection: List<YmePlaylist>,
    onPlaylistEvent: (PlaylistEvent) -> Unit,
) {
    var showNewPlaylistForm by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Row(modifier = Modifier.fillMaxWidth()
            .combinedClickable(
                onClick = {
                    onPlaylistEvent.invoke(
                        PlaylistEvent(
                            playlist = YmePlaylist(),
                            type = PlaylistEventType.HOME
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
                    onPlaylistEvent.invoke(
                        PlaylistEvent(
                            playlist = YmePlaylist(),
                            type = PlaylistEventType.EXPLORE
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
                    onPlaylistEvent.invoke(
                        PlaylistEvent(
                            playlist = YmePlaylist(),
                            type = PlaylistEventType.LIBRARY
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
        Divider()
        Row {
            Button(onClick = { showNewPlaylistForm = !showNewPlaylistForm}) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = "New Playlist",
                )
                Text("New Playlist")
            }
        }
        Row {
            AnimatedVisibility(showNewPlaylistForm) {
                val playlistName = remember { mutableStateOf("") }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    TextField(
                        label = { Text("Playlist name") },
                        value = playlistName.value,
                        onValueChange = { playlistName.value = it },
                        modifier = Modifier.onKeyEvent {
                            when (it.key) {
                                Key.Enter -> {
                                    onPlaylistEvent.invoke(
                                        PlaylistEvent(
                                            playlist = YmePlaylist(id = Random.nextInt(1,1000),name = playlistName.value),
                                            type = PlaylistEventType.CREATE
                                        )
                                    )
                                    showNewPlaylistForm = false
                                    true
                                }
                                Key.Escape -> {
                                    showNewPlaylistForm = false
                                    true
                                }
                                else -> {
                                    false
                                }
                            }
                        }
                    )
                }
            }
        }
        Row {
            LazyColumn {
                items(
                    count = playlistCollection.size,
                    key = { index -> playlistCollection.getOrNull(index)?.id ?: index}
                ) { index ->
                    playlistCollection.getOrNull(index)?.let { playlist ->
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .combinedClickable(
                                    onClick = {
                                        onPlaylistEvent.invoke(
                                            PlaylistEvent(
                                                playlist = playlist,
                                                type = PlaylistEventType.VIEW
                                            )
                                        )
                                    }
                                )
                        ) {
                            Column(
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                            ) {
                                Text(playlist.name)
                            }
                        }
                    }
                }
            }
        }
    }
}