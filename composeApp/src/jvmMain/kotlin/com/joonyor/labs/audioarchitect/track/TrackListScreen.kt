package com.joonyor.labs.audioarchitect.track

import androidx.compose.foundation.ContextMenuDataProvider
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.joonyor.labs.audioarchitect.data.PlaylistEvent
import com.joonyor.labs.audioarchitect.data.PlaylistEventType
import com.joonyor.labs.audioarchitect.data.YmePlaylist
import com.joonyor.labs.audioarchitect.data.YmeTrack
import com.joonyor.labs.audioarchitect.player.AudioPlayerEvent
import com.joonyor.labs.audioarchitect.player.AudioPlayerEventType

@Composable
fun TrackListScreen(
    modifier: Modifier = Modifier,
    trackCollection: List<YmeTrack> = emptyList(),
    playlistCollection: List<YmePlaylist> = emptyList(),
    selectedTrack: MutableState<YmeTrack>,
    currentTrackPlaying: YmeTrack,
    onMediaPlayerEvent: (AudioPlayerEvent) -> Unit,
    onPlaylistEvent: (PlaylistEvent) -> Unit,
    isPlaying: Boolean = false,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LazyColumn {
            items(trackCollection.size) { trackIndex ->
                val track = trackCollection.getOrNull(trackIndex) ?: YmeTrack()
                ContextMenuDataProvider(
                    items = {
                        trackListMenu(
                            track = track,
                            playListCollection = playlistCollection,
                            onMediaPlayerEvent = onMediaPlayerEvent,
                            onPlaylistEvent = onPlaylistEvent
                        )
                    }
                ) {
                    SelectionContainer {
                        TrackRowScreen(
                            modifier = Modifier.fillMaxWidth(),
                            trackRow = track,
                            selectedTrack = selectedTrack,
                            currentTrackPlaying = currentTrackPlaying,
                            onMediaPlayerEvent = onMediaPlayerEvent,
                            isPlaying = isPlaying,
                        )
                        Divider()
                    }
                }
            }
        }
    }
}

fun trackListMenu(
    track: YmeTrack = YmeTrack(),
    playListCollection: List<YmePlaylist> = emptyList(),
    onMediaPlayerEvent: (AudioPlayerEvent) -> Unit,
    onPlaylistEvent: (PlaylistEvent) -> Unit,
): List<ContextMenuItem> {
    val queueItem = ContextMenuItem(
        label = "Queue",
        onClick = {
            onMediaPlayerEvent.invoke(
                AudioPlayerEvent(
                    track = track,
                    type = AudioPlayerEventType.QUEUE
                )
            )
        }
    )

    val addToPlaylistHeader = ContextMenuItem("Add to Playlists") {}

    val availablePlaylistItems = createPlaylistMenuItems(
        track = track,
        playListCollection = playListCollection,
        onPlaylistEvent = onPlaylistEvent
    )

    return listOf(queueItem, addToPlaylistHeader) + availablePlaylistItems
}

private fun createPlaylistMenuItems(
    track: YmeTrack,
    playListCollection: List<YmePlaylist>,
    onPlaylistEvent: (PlaylistEvent) -> Unit
): List<ContextMenuItem> {
    return playListCollection
        .filter { playlist -> !playlist.tracks.contains(track) }
        .map { playlist ->
            ContextMenuItem(
                label = playlist.name,
                onClick = {
                    onPlaylistEvent.invoke(
                        PlaylistEvent(
                            track = track,
                            playlist = playlist,
                            type = PlaylistEventType.ADD_TRACK
                        )
                    )
                }
            )
        }
}

@Composable
fun TrackRowScreen(
    trackRow: YmeTrack = YmeTrack(),
    modifier: Modifier = Modifier,
    selectedTrack: MutableState<YmeTrack>,
    currentTrackPlaying: YmeTrack,
    onMediaPlayerEvent: (AudioPlayerEvent) -> Unit,
    isPlaying: Boolean = false,
) {
    Row(
        modifier = modifier
            .combinedClickable(
                onClick = {
                    selectedTrack.value = trackRow
                },
                onDoubleClick = {
                    selectedTrack.value = trackRow
                    onMediaPlayerEvent.invoke(
                        AudioPlayerEvent(
                            track = selectedTrack.value,
                            type = if (isPlaying) AudioPlayerEventType.PAUSE else AudioPlayerEventType.PLAY
                        )
                    )
                }
            )
    ) {
        Column(modifier = Modifier.weight(0.2f)) {
            Button(
                onClick = {
                    selectedTrack.value = trackRow
                    onMediaPlayerEvent.invoke(
                        AudioPlayerEvent(
                            track = selectedTrack.value,
                            type = if (isPlaying) AudioPlayerEventType.PAUSE else AudioPlayerEventType.PLAY
                        )
                    )
                }
            ) {
                Text(
                    text = if (isPlaying && currentTrackPlaying == trackRow) "Pause" else "Play"
                )
            }
        }
        Column(modifier = Modifier.weight(0.6f)) {
            if (trackRow.title.isNotEmpty() && trackRow.artist.isNotEmpty()) {
                Row {
                    Text(trackRow.title)
                }
                Row {
                    Text(trackRow.artist)
                }
            } else {
                Text("Unknown track")
            }
        }
        Column(modifier = Modifier.weight(0.2f)) {
            if (trackRow.duration.isNotEmpty()) {
                Text(trackRow.duration)
            } else {
                Text("Unknown duration")
            }
        }
    }
}

@Composable
fun TrackDetailScreen(
    modifier: Modifier = Modifier,
    selectedTrack: YmeTrack
) {
    Column(
        modifier = modifier.padding(10.dp)
    ) {
        Text(selectedTrack.title)
        Text(selectedTrack.artist)
    }
}