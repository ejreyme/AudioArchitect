package com.joonyor.labs.audio.player

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.joonyor.labs.audio.track.YmeTrack

@Composable
fun AudioPlayerScreen(
    playerState: AudioPlayerState,
    onAudioPlayerEvent: (AudioPlayerEvent) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(modifier = Modifier.weight(0.2f)) {
            Slider(
                enabled = playerState.trackPlaying.value.isNotNew,
                value = playerState.trackPosition.value,
                onValueChange = {
                    playerState.trackPosition.value = it
                    onAudioPlayerEvent.invoke(
                        AudioPlayerEvent(
                            type = AudioPlayerEventType.TRACK_POSITION,
                            trackPosition = it
                        )
                    )
                },
                valueRange = 0.0f..1.0f,
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colors.primaryVariant,
                    activeTrackColor = MaterialTheme.colors.primaryVariant
                )
            )
        }
        Row(modifier = Modifier.weight(0.8f)) {
            Column(modifier = Modifier.weight(0.3f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    AudioPlayerControls(
                        selectedTrack = playerState.activeTrack.value,
                        isPlaying = playerState.isPlaying.value,
                        onAudioPlayerEvent = { onAudioPlayerEvent.invoke(it) }
                    )
                }
            }
            Column(modifier = Modifier.weight(0.5f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Column {
                        Text(text = playerState.trackPlaying.value.title)
                        Text(text = playerState.trackPlaying.value.artist)
                    }
                }
            }
            Column(modifier = Modifier.weight(0.2f)) {
                Row {
                    var volumePosition by remember { mutableStateOf(0.5f) }
                    Slider(
                        value = volumePosition,
                        onValueChange = {
                            volumePosition = it
                            onAudioPlayerEvent.invoke(
                                AudioPlayerEvent(
                                    type = AudioPlayerEventType.VOLUME,
                                    volume = it
                                )
                            )
                        },
                        valueRange = 0.0f..1.0f,
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colors.primaryVariant,
                            activeTrackColor = MaterialTheme.colors.primaryVariant
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun AudioPlayerControls(
    selectedTrack: YmeTrack = YmeTrack(),
    isPlaying: Boolean,
    onAudioPlayerEvent: (AudioPlayerEvent) -> Unit
) {
    Row {
        IconButton(
            enabled = !selectedTrack.isNew,
            onClick = {
                onAudioPlayerEvent.invoke(
                    AudioPlayerEvent(
                        type = AudioPlayerEventType.STOP
                    )
                )
            }
        ) {
            Icon(
                imageVector = Icons.Filled.Stop,
                contentDescription = "Stop",
            )
        }
        IconButton(
            enabled = !selectedTrack.isNew,
            onClick = {
                if (isPlaying) {
                    onAudioPlayerEvent.invoke(
                        AudioPlayerEvent(
                            type = AudioPlayerEventType.PAUSE
                        )
                    )
                } else {
                    onAudioPlayerEvent.invoke(
                        AudioPlayerEvent(
                            track = selectedTrack,
                            type = AudioPlayerEventType.PLAY
                        )
                    )
                }
            }) {
            Icon(
                imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play",
            )

        }
        var repeat by remember { mutableStateOf(false) }
        IconButton(
            enabled = !selectedTrack.isNew,
            onClick = {
                repeat = !repeat
                onAudioPlayerEvent.invoke(
                    AudioPlayerEvent(
                        type = AudioPlayerEventType.REPEAT,
                        isRepeat = repeat
                    )
                )
            }) {
            Icon(
                imageVector = if (repeat) Icons.Filled.RepeatOn else Icons.Filled.Repeat,
                contentDescription = if (repeat) "Repeat On" else "Repeat",
            )
        }
    }
}