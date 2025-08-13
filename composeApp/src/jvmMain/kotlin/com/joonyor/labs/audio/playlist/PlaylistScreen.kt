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
import com.joonyor.labs.audio.library.NavEvent
import com.joonyor.labs.audio.library.NavEventType
import com.joonyor.labs.audio.player.AudioPlayerEvent
import com.joonyor.labs.audio.player.AudioPlayerEventType
import com.joonyor.labs.audio.track.YmeTrack
import kotlin.random.Random

@Composable
fun PlaylistScreen(
    modifier: Modifier = Modifier,
    playlistCollection: List<YmePlaylist>,
    onPlaylistEvent: (PlaylistEvent) -> Unit,
    onNavEvent: (NavEvent) -> Unit,
) {
    var showNewPlaylistForm by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        SideBarNavScreen(onNavEvent = onNavEvent)
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
                items(playlistCollection.size) { index ->
                    val playlist = playlistCollection.getOrNull(index) ?: YmePlaylist()
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
//                items(
//                    count = playlistCollection.size,
//                    key = { index -> playlistCollection.getOrNull(index)?.id ?: index}
//                ) { index ->
//                    playlistCollection.getOrNull(index)?.let { playlist ->
//                        PlaylistRowScreen(playlist = playlist, onPlaylistEvent = onPlaylistEvent)
//                    }
//                }
            }
        }
    }
}

@Composable
fun SideBarNavScreen(
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