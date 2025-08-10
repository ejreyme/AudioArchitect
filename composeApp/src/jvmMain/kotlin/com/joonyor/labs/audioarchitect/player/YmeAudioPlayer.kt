package com.joonyor.labs.audioarchitect.player

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.joonyor.labs.audioarchitect.data.YmePlaylist
import com.joonyor.labs.audioarchitect.data.YmeTrack

@Composable
fun YmeAudioPlayer(
    selectedTrack: YmeTrack = YmeTrack(),
    currentTrackPlaying: YmeTrack = YmeTrack(),
    isPlaying: Boolean = false,
    onAudioPlayerEvent: (AudioPlayerEvent) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row {
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
                        Text(text = currentTrackPlaying?.title ?: "No title")
                        Text(text = currentTrackPlaying?.artist ?: "No artist")
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
                        steps = 4,
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
                        type = AudioPlayerEventType.SKIP_BACK
                    )
                )
            }
        ) {
            Text("Back")
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
            if (isPlaying) Text("Pause") else Text("Play")
        }
        IconButton(onClick = {
            onAudioPlayerEvent.invoke(
                AudioPlayerEvent(
                    type = AudioPlayerEventType.SKIP_FORWARD
                )
            )
        }) {
            Text("Forward")
        }
    }
}

data class AudioPlayerEvent(
    val playlist: YmePlaylist = YmePlaylist(),
    val track: YmeTrack = YmeTrack(),
    val type: AudioPlayerEventType = AudioPlayerEventType.STOP,
    val volume: Float = 0.0f,
)

enum class AudioPlayerEventType {
    PLAY, PAUSE, SKIP_FORWARD, SKIP_BACK, STOP, QUEUE, VOLUME
}