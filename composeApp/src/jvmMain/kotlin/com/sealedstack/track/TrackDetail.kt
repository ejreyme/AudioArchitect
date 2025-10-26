import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sealedstack.track.TrackEvent
import com.sealedstack.track.TrackTag
import com.sealedstack.track.YmeTag
import com.sealedstack.track.YmeTrack

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TrackDetail(
    modifier: Modifier = Modifier,
    selectedTrack: YmeTrack = YmeTrack(),
    onTrackEvent: (TrackEvent) -> Unit,
) {
    Column(
        modifier = modifier.padding(10.dp)
    ) {
        val activeTags = selectedTrack.tags.subtract(tagRepository())
        val availableTags = tagRepository().subtract(activeTags)
        var showEditTag by remember { mutableStateOf(false) }

        Text(selectedTrack.title)
        Text(selectedTrack.artist)
        Divider()
        Text("Duration: ${selectedTrack.durationDisplay()}")
        Button(onClick = { showEditTag = !showEditTag }) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = "Edit Tags",
            )
            Text("Edit Tags")
        }
        AnimatedVisibility(showEditTag) {
            Column {
                Text("Active Tags")
                TrackTag(
                    selectedTrack = selectedTrack,
                    onTrackEvent = onTrackEvent,
                    tags = activeTags
                )
                Divider()
                Text("Available Tags")
                TrackTag(
                    selectedTrack = selectedTrack,
                    onTrackEvent = onTrackEvent,
                    tags = availableTags
                )
            }
        }
    }
}



private fun tagRepository() : Set<YmeTag> {
    return setOf("Rock", "Metal", "Pop", "Folk", "Jazz")
        .stream()
        .map { YmeTag(it, false) }
        .toList()
        .toSet()
}