package com.joonyor.labs.audio.track

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class TrackDataRepository {
    // read/write-only
    val dataSource = MutableStateFlow<List<YmeTrack>>(emptyList())
    // external read-only
    val latestTrackCollection: Flow<List<YmeTrack>> = dataSource.asStateFlow()

    // CREATE
    fun createTrack(track: YmeTrack) {
        dataSource.value = dataSource.value.toMutableList().apply {
            add(track)
        }
    }

    // READ
    fun readTrack(filePath: String): YmeTrack? {
        return dataSource.value.find { it.filePath == filePath }
    }

    // UPDATE
    fun updateTrack(track: YmeTrack) {
        readTrack(track.filePath)?.let {
            dataSource.value = dataSource.value.toMutableList().apply {
                set(indexOf(it), track)
            }
        }
    }

    // DELETE
    fun deleteTrack(track: YmeTrack) {
        dataSource.value = dataSource.value.toMutableList().apply {
            remove(track)
        }
    }

    // SEARCH
    fun search(query: String): List<YmeTrack> {
        return dataSource.value.filter { track -> track.title.contains(query, ignoreCase = true) }
    }
}

data class YmeTrack(
    val filePath: String = "",
    val title: String = "Unknown title",
    val artist: String = "Unknown artist",
    val duration: String = "00:00",
    val tags: Set<YmeTag> = emptySet()
) {
    val isNew = filePath.isEmpty()
    val isNotNew = !isNew
}

data class YmeTag(val name: String)

enum class TrackEventType {
    ADD_TAG, REMOVE_TAG, DEFAULT
}