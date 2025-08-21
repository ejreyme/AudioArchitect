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
    fun updateTrackTags(track: YmeTrack, tag: YmeTag) {
        readTrack(track.filePath)?.let { existingTrack ->
            val updatedTrack = existingTrack.copy(
                tags = updateTagsForTrack(existingTrack.tags, tag)
            )
            replaceTrackInDataSource(existingTrack, updatedTrack)
        }
    }

    private fun updateTagsForTrack(currentTags: Set<YmeTag>, tag: YmeTag): Set<YmeTag> {
        return currentTags.toMutableSet().apply {
            if (tag.active) {
                add(tag)
            } else {
                remove(tag)
            }
        }.toSet()
    }

    private fun replaceTrackInDataSource(oldTrack: YmeTrack, newTrack: YmeTrack) {
        dataSource.value = dataSource.value.toMutableList().apply {
            set(indexOf(oldTrack), newTrack)
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