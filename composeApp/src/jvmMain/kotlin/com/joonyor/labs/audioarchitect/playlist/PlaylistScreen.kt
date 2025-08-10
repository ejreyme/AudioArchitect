package com.joonyor.labs.audioarchitect.playlist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import com.joonyor.labs.audioarchitect.PlaylistEvent
import com.joonyor.labs.audioarchitect.PlaylistEventType
import com.joonyor.labs.audioarchitect.YmePlaylist
import kotlin.random.Random

@Composable
fun PlaylistScreen(
    modifier: Modifier = Modifier,
    playlistCollection: List<YmePlaylist>,
    onPlaylistEvent: (PlaylistEvent) -> Unit,
) {
    var showNewPlaylistForm by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
    ) {
        Button(onClick = { showNewPlaylistForm = !showNewPlaylistForm}) {
            Text("New Playlist")
        }
        Divider()
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
        LazyColumn {
            items(
                count = playlistCollection.size,
                key = { index -> playlistCollection.getOrNull(index)?.id ?: index}
            ) { index ->
                playlistCollection.getOrNull(index)?.let { playlist ->
                    Text(playlist.name)
                }
            }
        }
    }
}