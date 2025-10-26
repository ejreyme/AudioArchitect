import androidx.compose.foundation.layout.Row
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import com.sealedstack.player.AudioPlayerEvent
import com.sealedstack.player.AudioPlayerEventType
import com.sealedstack.track.YmeTrack

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