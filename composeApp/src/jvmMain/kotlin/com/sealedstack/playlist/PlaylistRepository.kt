package com.sealedstack.playlist

import com.sealedstack.data.LocalDatabase
import com.sealedstack.data.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * Repository class responsible for managing playlist data operations such as creation, retrieval, updating, and deletion.
 * It leverages a reactive data source to expose and manipulate playlist collections.
 */
class PlaylistRepository(private val localDatabase: LocalDatabase): Repository<YmePlaylist> {
    private val scope = CoroutineScope(Dispatchers.IO)

    val latestRepoPlaylists: Flow<List<YmePlaylist>> = localDatabase.latestDatabasePlaylists

    init {
        scope.launch {
            latestRepoPlaylists.collect {
                it.forEach { (id, name, tracks) -> println("$id,$name,tracks:${tracks.size}") }
            }
        }
    }

    override suspend fun create(item: YmePlaylist) {
        localDatabase.insertPlaylist(item)
    }

    override suspend fun read(item: YmePlaylist): YmePlaylist {
        return localDatabase.selectPlaylist(item)
    }

    override suspend fun update(item: YmePlaylist) {
        localDatabase.updatePlaylist(item)
    }

    override suspend fun delete(item: YmePlaylist) {
        localDatabase.deletePlaylist(item)
    }
}