import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import com.sealedstack.player.*
import com.sealedstack.track.YmeTrack

@Composable
fun TrackRow(
    trackRow: YmeTrack = YmeTrack(),
    modifier: Modifier = Modifier,
    selectedTrack: MutableState<YmeTrack>,
    trackPlaying: YmeTrack,
    onAudioPlayerEvent: (AudioPlayerEvent) -> Unit,
    isPlaying: Boolean,
    mediaPlayerState: MediaPlayerState
) {
    Row(
        modifier = clickMods(
            track = trackRow,
            selectedTrack = selectedTrack,
            trackPlaying = trackPlaying,
            onAudioPlayerEvent = onAudioPlayerEvent,
            isPlaying = isPlaying
        )
    ) {
        Column(modifier = Modifier.weight(0.2f)) {
            Button(
                onClick = {
                    selectedTrack.value = trackRow

                    onAudioPlayerEvent.invoke(
                        buttonActionState(
                            selectedTrack = selectedTrack.value,
                            trackPlaying = trackPlaying,
                            mediaPlayerState = mediaPlayerState
                        )
                    )
                }
            ) {
                Text(
                    text = buttonTextState(
                        trackRow = trackRow,
                        trackPlaying = trackPlaying,
                        mediaPlayerState =mediaPlayerState
                    )
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

fun clickMods(
    track: YmeTrack = YmeTrack(),
    selectedTrack: MutableState<YmeTrack>,
    trackPlaying: YmeTrack,
    onAudioPlayerEvent: (AudioPlayerEvent) -> Unit,
    isPlaying: Boolean = false,
) : Modifier {
    return Modifier
        .combinedClickable(
            onClick = { selectedTrack.value = track },
            onDoubleClick = {
                selectedTrack.value = track
                onAudioPlayerEvent.invoke(
                    AudioPlayerEvent(
                        track = selectedTrack.value,
                        type = if (isPlaying) AudioPlayerEventType.PAUSE else AudioPlayerEventType.PLAY
                    )
                )
            }
        )
}