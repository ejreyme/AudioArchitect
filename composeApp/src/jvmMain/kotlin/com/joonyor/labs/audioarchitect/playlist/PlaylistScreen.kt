package com.joonyor.labs.audioarchitect.playlist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.joonyor.labs.audioarchitect.YmePlaylist

@Composable
fun PlaylistScreen(
    modifier: Modifier = Modifier,
    playlists: List<YmePlaylist>
) {
    Column(
        modifier = modifier
    ) {
        Button(
            onClick = {

            }
        ) {
            Text("New Playlist")
        }
        LazyColumn {
            items(playlists.size) { playlist ->
                Row {
                    Text(playlists[playlist].name)
                }
            }
        }
    }
}