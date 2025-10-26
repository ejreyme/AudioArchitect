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
    val latestRepoPlaylists: Flow<List<YmePlaylist>> = localDatabase.latestDatabasePlaylists

    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        scope.launch {
            latestRepoPlaylists.collect {
                print("watching...$it")
            }
        }
    }

    override suspend fun create(item: YmePlaylist) {
        localDatabase.create(item)
    }

    override suspend fun read(item: YmePlaylist): YmePlaylist {
        return localDatabase.read(item)
    }

    override suspend fun update(item: YmePlaylist) {
        localDatabase.updatePlaylist(item)
    }

    override suspend fun delete(item: YmePlaylist) {
        localDatabase.delete(item)
    }

    private fun generatePlaylistList(): List<YmePlaylist> {
        return listOf(
            YmePlaylist(1, "Playlist 1"),
            YmePlaylist(2, "Playlist 2"),
            YmePlaylist(3, "Playlist 3"),
            YmePlaylist(4, "Playlist 4"),
            YmePlaylist(5, "Playlist 5"),
            YmePlaylist(6, "Playlist 6"),
        )
    }
}