package com.sealedstack.track

import TrackRow
import androidx.compose.foundation.ContextMenuDataProvider
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sealedstack.player.AudioPlayerEvent
import com.sealedstack.player.AudioPlayerEventType
import com.sealedstack.player.MediaPlayerState
import com.sealedstack.playlist.PlaylistEvent
import com.sealedstack.playlist.PlaylistEventType
import com.sealedstack.playlist.YmePlaylist

@Composable
fun TrackCollection(
    modifier: Modifier = Modifier,
    isPlaying: Boolean = false,
    selectedTrack: MutableState<YmeTrack>,
    activePlaylist: YmePlaylist,
    trackPlaying: YmeTrack,
    tracks: List<YmeTrack> = emptyList(),
    playlistCollection: List<YmePlaylist> = emptyList(),
    onAudioPlayerEvent: (AudioPlayerEvent) -> Unit,
    onPlaylistEvent: (PlaylistEvent) -> Unit,
    mediaPlayerState: MediaPlayerState
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row {
            if (activePlaylist.name.isEmpty()) {
                Text("Library")
            } else {
                Text(activePlaylist.name)
            }
        }
        Divider()
        LazyColumn {
            items(tracks.size) { trackIndex ->
                val track = tracks.getOrNull(trackIndex) ?: YmeTrack()
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
                        TrackRow(
                            modifier = Modifier.fillMaxWidth(),
                            trackRow = track,
                            selectedTrack = selectedTrack,
                            trackPlaying = trackPlaying,
                            onAudioPlayerEvent = onAudioPlayerEvent,
                            isPlaying = isPlaying,
                            mediaPlayerState = mediaPlayerState
                        )
                        Divider()
                    }
                }
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
