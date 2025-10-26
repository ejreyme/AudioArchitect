import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sealedstack.playlist.PlaylistEvent
import com.sealedstack.playlist.PlaylistEventType
import com.sealedstack.playlist.YmePlaylist

@Composable
fun PlaylistRow(
    playlist: YmePlaylist = YmePlaylist(),
    onPlaylistEvent: (PlaylistEvent) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .combinedClickable(
                onClick = {
                    onPlaylistEvent.invoke(
                        PlaylistEvent(
                            playlist = playlist,
                            type = PlaylistEventType.READ
                        )
                    )
                }
            )
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.CenterVertically)
        ) {
            Text(playlist.name)
        }
    }
}