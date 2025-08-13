package com.joonyor.labs.audio.track

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * A repository class responsible for managing track data operations such as
 * creating, reading, updating, deleting, and searching for tracks.
 */
class TrackDataRepository {
    // read/write-only TODO replace with local or network data source
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
    fun updateTrack(track: YmeTrack, tag: YmeTag) {
        readTrack(track.filePath)?.let {

            val updatedTags = it.tags.toMutableSet()
            if (tag.active) {
                updatedTags.add(tag)
            } else {
                updatedTags.remove(tag)
            }

            val updatedTrack = YmeTrack(
                filePath = it.filePath,
                title = it.title,
                artist = it.artist,
                tags = updatedTags.toSet(),
                duration = it.duration,
            )

            dataSource.value = dataSource.value.toMutableList().apply {
                set(indexOf(it), updatedTrack)
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