package com.sealedstack.track

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * A repository class responsible for managing track data operations such as
 * creating, reading, updating, deleting, and searching for tracks.
 */
class TrackRepository(val localDatabase: com.sealedstack.data.LocalDatabase): com.sealedstack.data.Repository<YmeTrack> {
    private val scope = CoroutineScope(Dispatchers.IO)

    val latestRepoTracks = localDatabase.latestDatabaseTracks

    init {
        scope.launch {
            latestRepoTracks.collect { tracks ->
                tracks.forEach { println(it) }
            }
        }
    }

    override suspend fun create(item: YmeTrack) {
        localDatabase.createTrack(item = item)
    }

    override suspend fun read(item: YmeTrack): YmeTrack {
        TODO("Not yet implemented")
    }

    override suspend fun update(item: YmeTrack) {
        TODO("Not yet implemented")

    }

    override suspend fun delete(item: YmeTrack) {
        TODO("Not yet implemented")
    }
}