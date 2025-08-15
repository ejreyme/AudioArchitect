package com.joonyor.labs.audio.playlist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ContextMenuDataProvider
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
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
    playlists: List<YmePlaylist>,
    onPlaylistEvent: (PlaylistEvent) -> Unit,
) {
    var showNewPlaylistForm by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
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
                                            playlist = YmePlaylist(id = Random.nextInt(1,1000),name = sanitizePlaylistName(playlistName.value)),
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
                items(playlists.size) { index ->
                    val playlist = playlists.getOrNull(index) ?: YmePlaylist()
                    ContextMenuDataProvider(
                        items = {
                            playlistMenu(playlist = playlist, onPlaylistEvent = onPlaylistEvent)
                        }
                    ) {
                        SelectionContainer {
                            PlaylistRowScreen(playlist = playlist, onPlaylistEvent = onPlaylistEvent)
                        }
                    }
                }
            }
        }
    }
}



@Composable
fun PlaylistRowScreen(
    playlist: YmePlaylist = YmePlaylist(),
    onPlaylistEvent: (PlaylistEvent) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .combinedClickable(
                onClick = {
                    onPlaylistEvent.invoke(
                        PlaylistEvent(
                            playlist = playlist,
                            type = PlaylistEventType.READ
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

private fun playlistMenu(
    playlist: YmePlaylist = YmePlaylist(),
    onPlaylistEvent: (PlaylistEvent) -> Unit,
): List<ContextMenuItem> {
    return listOf(
        ContextMenuItem(
            label = "Export",
            onClick = {
                onPlaylistEvent.invoke(
                    PlaylistEvent(
                        playlist = playlist,
                        type = PlaylistEventType.EXPORT
                    )
                )
            }
        ),
        ContextMenuItem(
            label = "Delete",
            onClick = {
                onPlaylistEvent.invoke(
                    PlaylistEvent(
                        playlist = playlist,
                        type = PlaylistEventType.DELETE
                    )
                )
            }
        ),
    )
}

private fun sanitizePlaylistName(name: String): String {
    val nonAlphaNumRegex = "[^a-zA-Z0-9]".toRegex() // Matches any character that is NOT a letter or a digit
    return name.replace(nonAlphaNumRegex, "")
}