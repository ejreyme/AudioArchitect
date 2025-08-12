package com.joonyor.labs.audio.track

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class TrackDataRepository {
    // write-only
    private val _trackDataSource = MutableStateFlow<List<YmeTrack>>(emptyList())
    // read-only
    val latestPlaylistCollection: Flow<List<YmeTrack>> = _trackDataSource.asStateFlow()


    fun addTrack(track: YmeTrack) {
        _trackDataSource.value = _trackDataSource.value.toMutableList().apply {
            add(track)
        }
    }
}

data class YmeTrack(
    val filePath: String = "",
    val title: String = "Unknown title",
    val artist: String = "Unknown artist",
    val duration: String = "00:00"
) {
    val isNew = filePath.isEmpty()
}