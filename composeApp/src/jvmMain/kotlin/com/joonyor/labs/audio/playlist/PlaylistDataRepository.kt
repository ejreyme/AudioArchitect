package com.joonyor.labs.audio.playlist

import com.joonyor.labs.audio.track.YmeTrack
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Repository class responsible for managing playlist data operations such as creation, retrieval, updating, and deletion.
 * It leverages a reactive data source to expose and manipulate playlist collections.
 */
class PlaylistDataRepository {
    // read/write-only TODO replace with local or network data source
    val dataSource = MutableStateFlow<List<YmePlaylist>>(emptyList())
    // external read-only
    val latestPlaylistCollection: Flow<List<YmePlaylist>> = dataSource.asStateFlow()

    // CREATE
    fun createPlaylist(playlist: YmePlaylist) {
        dataSource.value = dataSource.value.toMutableList().apply {
            add(playlist)
        }
    }

    // READ
    fun readPlaylist(id: Int): YmePlaylist? {
        return dataSource.value.find { it.id == id }
    }

    // UPDATE
    fun updatePlaylist(playlist: YmePlaylist) {
        readPlaylist(playlist.id)?.let {
            dataSource.value = dataSource.value.toMutableList().apply {
                set(indexOf(it), playlist)
            }
        }
    }

    // DELETE
    fun deletePlaylist(playlist: YmePlaylist) {
        dataSource.value = dataSource.value.toMutableList().apply {
            remove(playlist)
        }
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