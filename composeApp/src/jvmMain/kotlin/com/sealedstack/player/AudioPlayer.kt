package com.sealedstack.player

import AudioPlayerControls
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
fun AudioPlayer(
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
                            type = AudioPlayerEventType.POSITION,
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
