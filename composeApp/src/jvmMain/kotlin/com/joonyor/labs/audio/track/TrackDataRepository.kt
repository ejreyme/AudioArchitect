package com.joonyor.labs.audio.track

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class TrackDataRepository {
    // read/write-only
    val trackDataSource = MutableStateFlow<List<YmeTrack>>(emptyList())
    // external read-only
    val latestPlaylistCollection: Flow<List<YmeTrack>> = trackDataSource.asStateFlow()


    fun addTrack(track: YmeTrack) {
        trackDataSource.value = trackDataSource.value.toMutableList().apply {
            add(track)
        }
    }

    fun search(query: String): List<YmeTrack> {
        return trackDataSource.value.filter { track -> track.title.contains(query, ignoreCase = true) }
    }
}

data class YmeTrack(
    val filePath: String = "",
    val title: String = "Unknown title",
    val artist: String = "Unknown artist",
    val duration: String = "00:00"
) {
    val isNew = filePath.isEmpty()
    val isNotNew = !isNew
}