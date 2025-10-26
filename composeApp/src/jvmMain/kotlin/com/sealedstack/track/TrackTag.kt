package com.sealedstack.track

import androidx.compose.foundation.layout.Row
import androidx.compose.material.Chip
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TrackTag(
    selectedTrack: YmeTrack,
    onTrackEvent: (TrackEvent) -> Unit,
    tags: Set<YmeTag>
) {
    tags.forEach {
        Row {
            var active by remember { mutableStateOf(false) }
            Chip(
                colors =
                    if (active)
                        ChipDefaults.chipColors(backgroundColor = MaterialTheme.colors.onPrimary)
                    else
                        ChipDefaults.chipColors(backgroundColor = MaterialTheme.colors.secondaryVariant),
                onClick = {
                    active = !active
                    onTrackEvent.invoke(
                        TrackEvent(
                            ymeTrack = selectedTrack,
                            type = TrackEventType.ADD_TAG,
                            tag = YmeTag(it.name, active)
                        )
                    )
                }
            ) {
                Text(it.name)
            }
        }
    }
}