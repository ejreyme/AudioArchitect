package com.joonyor.labs.audio.track

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ContextMenuDataProvider
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.joonyor.labs.audio.player.AudioPlayerEvent
import com.joonyor.labs.audio.player.AudioPlayerEventType
import com.joonyor.labs.audio.playlist.PlaylistEvent
import com.joonyor.labs.audio.playlist.PlaylistEventType
import com.joonyor.labs.audio.playlist.YmePlaylist

@Composable
fun TrackListScreen(
    modifier: Modifier = Modifier,
    isPlaying: Boolean = false,
    selectedTrack: MutableState<YmeTrack>,
    selectedPlaylist: YmePlaylist,
    currentTrackPlaying: YmeTrack,
    trackCollection: List<YmeTrack> = emptyList(),
    playlistCollection: List<YmePlaylist> = emptyList(),
    onAudioPlayerEvent: (AudioPlayerEvent) -> Unit,
    onPlaylistEvent: (PlaylistEvent) -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row {
            if (selectedPlaylist.name.isEmpty()) {
                Text("Library")
            } else {
                Text(selectedPlaylist.name)
            }
        }
        Divider()
        LazyColumn {
            items(trackCollection.size) { trackIndex ->
                val track = trackCollection.getOrNull(trackIndex) ?: YmeTrack()
                ContextMenuDataProvider(
                    items = {
                        trackListMenu(
                            track = track,
                            playListCollection = playlistCollection,
                            onAudioPlayerEvent = onAudioPlayerEvent,
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
                            onAudioPlayerEvent = onAudioPlayerEvent,
                            isPlaying = isPlaying,
                        )
                        Divider()
                    }
                }
            }
        }
    }
}

@Composable
fun TrackRowScreen(
    trackRow: YmeTrack = YmeTrack(),
    modifier: Modifier = Modifier,
    selectedTrack: MutableState<YmeTrack>,
    currentTrackPlaying: YmeTrack,
    onAudioPlayerEvent: (AudioPlayerEvent) -> Unit,
    isPlaying: Boolean = false,
) {
    Row(
        modifier = modifier
            .combinedClickable(
                onClick = { selectedTrack.value = trackRow },
                onDoubleClick = {
                    selectedTrack.value = trackRow
                    onAudioPlayerEvent.invoke(
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
                    onAudioPlayerEvent.invoke(
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
            Text(trackRow.durationDisplay())
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TrackDetailScreen(
    modifier: Modifier = Modifier,
    selectedTrack: YmeTrack,
    onTrackEvent: (TrackEvent) -> Unit,
) {
    Column(
        modifier = modifier.padding(10.dp)
    ) {
        val activeTags = selectedTrack.tags.subtract(tagRepository())
        val availableTags = tagRepository().subtract(activeTags)
        var showEditTag by remember { mutableStateOf(false) }

        Text(selectedTrack.title)
        Text(selectedTrack.artist)
        Divider()
        Text("Duration: ${selectedTrack.durationDisplay()}")
        Button(onClick = { showEditTag = !showEditTag }) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = "Edit Tags",
            )
            Text("Edit Tags")
        }
        AnimatedVisibility(showEditTag) {
            Column {
                Text("Active Tags")
                TrackTagScreen(
                    selectedTrack = selectedTrack,
                    onTrackEvent = onTrackEvent,
                    tags = activeTags
                )
                Divider()
                Text("Available Tags")
                TrackTagScreen(
                    selectedTrack = selectedTrack,
                    onTrackEvent = onTrackEvent,
                    tags = availableTags
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TrackTagScreen(
    selectedTrack: YmeTrack,
    onTrackEvent: (TrackEvent) -> Unit,
    tags: Set<YmeTag>
) {
    tags.forEach {
        Row {
            var active by remember { mutableStateOf(false) }
            Chip(
                colors =
                    if (active)
                        ChipDefaults.chipColors(backgroundColor = MaterialTheme.colors.onPrimary)
                    else
                        ChipDefaults.chipColors(backgroundColor = MaterialTheme.colors.secondaryVariant),
                onClick = {
                    active = !active
                    onTrackEvent.invoke(
                        TrackEvent(
                            track = selectedTrack,
                            type = TrackEventType.ADD_TAG,
                            tag = YmeTag(it.name, active)
                        )
                    )
                }
            ) {
                Text(it.name)
            }
        }
    }
}

private fun trackListMenu(
    track: YmeTrack = YmeTrack(),
    playListCollection: List<YmePlaylist> = emptyList(),
    onAudioPlayerEvent: (AudioPlayerEvent) -> Unit,
    onPlaylistEvent: (PlaylistEvent) -> Unit,
): List<ContextMenuItem> {
    val queueItem = ContextMenuItem(
        label = "Queue Track",
        onClick = {
            onAudioPlayerEvent.invoke(
                AudioPlayerEvent(
                    track = track,
                    type = AudioPlayerEventType.QUEUE
                )
            )
        }
    )

    val availablePlaylistItems = createPlaylistMenuItems(
        track = track,
        playListCollection = playListCollection,
        onPlaylistEvent = onPlaylistEvent
    )

    return listOf(queueItem) + availablePlaylistItems
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
                label = "${playlist.name} +",
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

private fun tagRepository() : Set<YmeTag> {
    return setOf("Rock", "Metal", "Pop", "Folk", "Jazz")
        .stream()
        .map { YmeTag(it, false) }
        .toList()
        .toSet()
}