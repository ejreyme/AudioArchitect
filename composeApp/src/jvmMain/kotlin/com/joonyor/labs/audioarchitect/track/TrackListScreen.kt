package com.joonyor.labs.audioarchitect.track

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.joonyor.labs.audioarchitect.YmeTrack
import com.joonyor.labs.audioarchitect.player.AudioPlayerEvent
import com.joonyor.labs.audioarchitect.player.AudioPlayerEventType

@Composable
fun TrackListScreen(
    modifier: Modifier = Modifier,
    tracks: List<YmeTrack> = emptyList(),
    selectedTrack: MutableState<YmeTrack>,
    onMediaPlayerEvent: (AudioPlayerEvent) -> Unit,
    isPlaying: Boolean = false,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LazyColumn {
            items(tracks.size) { track ->
                Row(
                    modifier = Modifier.fillParentMaxWidth()
                        .combinedClickable(
                            onClick = {
                                selectedTrack.value = tracks[track]
                            },
                            onDoubleClick = {
                                selectedTrack.value = tracks[track]
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
                                selectedTrack.value = tracks[track]
                                onMediaPlayerEvent.invoke(
                                    AudioPlayerEvent(
                                        track = selectedTrack.value,
                                        type = if (isPlaying) AudioPlayerEventType.PAUSE else AudioPlayerEventType.PLAY
                                    )
                                )
                            }
                        ) {
                            Text(
                                text = if (isPlaying && selectedTrack.value == tracks[track]) "Pause" else "Play"
                            )
                        }
                    }
                    Column(modifier = Modifier.weight(0.6f)) {
                        Row {
                            Text(tracks[track].title)
                        }
                        Row {
                            Text(tracks[track].artist)
                        }
                    }
                    Column(modifier = Modifier.weight(0.2f)) {
                        Text(tracks[track].duration)
                    }
                }
                Divider()
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