package com.joonyor.labs.audioarchitect

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.IconButton
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier

@Composable
fun YmeMediaPlayer(
    selectedTrack: YmeTrack = YmeTrack(),
    currentTrackPlaying: YmeTrack? = null,
    isPlaying: Boolean = false,
    volumeSliderPosition: Float = 0.0f,
    trackSliderPosition: Float = 0.0f,
    onMediaPlayerEvent: (MediaPlayerEvent) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
//        Row { MediaPlayerSlider(trackSliderPosition) }
        Row {
            Column(modifier = Modifier.weight(0.34f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    PlayerControls(
                        selectedTrack = selectedTrack,
                        isPlaying = isPlaying,
                        onPlayerEvent = { onMediaPlayerEvent.invoke(it) }
                    )
                }
            }
            Column(modifier = Modifier.weight(0.33f)) {
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
            Column(modifier = Modifier.weight(0.33f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    VolumeControls(sliderPosition = volumeSliderPosition)
                }
            }
        }
    }
}

@Composable
fun VolumeControls(
    sliderPosition: Float = 0.0f,
) {
    Row  (
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Text(text = "Volume")
        MediaPlayerSlider(
            inSliderPosition = sliderPosition
        )
    }
}

@Composable
fun PlayerControls(
    selectedTrack: YmeTrack = YmeTrack(),
    isPlaying: Boolean,
    onPlayerEvent: (MediaPlayerEvent) -> Unit
) {
    Row {
        IconButton(
            onClick = {
                onPlayerEvent.invoke(
                    MediaPlayerEvent(
                        type = MediaPlayerEventType.SKIP_BACK
                    )
                )
            }
        ) {
            Text("Back")
        }
        IconButton(onClick = {
            if (isPlaying) {
                onPlayerEvent.invoke(
                    MediaPlayerEvent(
                        type = MediaPlayerEventType.PAUSE
                    )
                )
            } else {
                onPlayerEvent.invoke(
                    MediaPlayerEvent(
                        track = selectedTrack,
                        type = MediaPlayerEventType.PLAY
                    )
                )
            }
        }) {
            if (isPlaying) Text("Pause") else Text("Play")
        }
        IconButton(onClick = {
            onPlayerEvent.invoke(
                MediaPlayerEvent(
                    type = MediaPlayerEventType.SKIP_FORWARD
                )
            )
        }) {
            Text("Forward")
        }
    }
}

@Composable
fun MediaPlayerSlider(
    inSliderPosition: Float
) {
    val sliderPosition  = mutableStateOf(inSliderPosition)
    Slider(
        value = sliderPosition.value,
        onValueChange = { sliderPosition.value = it },
        valueRange = 0.0f..1.0f,
        colors = SliderDefaults.colors(
            thumbColor = androidx.compose.material.MaterialTheme.colors.primaryVariant,
            activeTrackColor = androidx.compose.material.MaterialTheme.colors.primaryVariant
        )
    )
}

data class MediaPlayerEvent(
    val playlist: YmePlaylist = YmePlaylist(),
    val track: YmeTrack = YmeTrack(),
    val type: MediaPlayerEventType = MediaPlayerEventType.STOP,
)

enum class MediaPlayerEventType {
    PLAY, PAUSE, SKIP_FORWARD, SKIP_BACK, STOP, QUEUE
}