package com.joonyor.labs.audioarchitect.player

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOn
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.joonyor.labs.audioarchitect.data.YmePlaylist
import com.joonyor.labs.audioarchitect.data.YmeTrack

@Composable
fun YmeAudioPlayerScreen(
    selectedTrack: YmeTrack = YmeTrack(),
    currentTrackPlaying: YmeTrack = YmeTrack(),
    isPlaying: Boolean = false,
    onAudioPlayerEvent: (AudioPlayerEvent) -> Unit,
    trackPosition: MutableState<Float>,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(modifier = Modifier.weight(0.2f)) {
            Slider(
                enabled = !currentTrackPlaying.isNew && isPlaying,
                value = trackPosition.value,
                onValueChange = {
                    trackPosition.value = it
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
                        selectedTrack = selectedTrack,
                        isPlaying = isPlaying,
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
                        Text(text = currentTrackPlaying.title)
                        Text(text = currentTrackPlaying.artist)
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
        IconButton(onClick = {
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
        IconButton(onClick = {
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

data class AudioPlayerEvent(
    val playlist: YmePlaylist = YmePlaylist(),
    val track: YmeTrack = YmeTrack(),
    val type: AudioPlayerEventType = AudioPlayerEventType.STOP,
    val volume: Float = 0.0f,
    val trackPosition: Float = 0.0f,
    val isRepeat: Boolean = false,
)

enum class AudioPlayerEventType {
    PLAY, PAUSE, SKIP_FORWARD, SKIP_BACK, STOP, QUEUE, VOLUME, TRACK_POSITION, REPEAT,
}