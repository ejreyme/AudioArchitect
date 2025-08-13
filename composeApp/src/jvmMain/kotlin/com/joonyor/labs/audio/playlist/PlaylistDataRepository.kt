package com.joonyor.labs.audio.playlist

import com.joonyor.labs.audio.track.YmeTrack
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

class PlaylistDataRepository {
    // read/write-only
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

    fun updatePlaylist(playlist: YmePlaylist, track: YmeTrack) {
        val updatedPlaylist = YmePlaylist(
            id = playlist.id,
            name = playlist.name,
            tracks = listOf(playlist.tracks, listOf(track)).flatten()
        )
        updatePlaylist(updatedPlaylist)
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

data class YmePlaylist(
    var id: Int = 0,
    val name: String = "New playlist-" + Random.nextInt(1000),
    val tracks: List<YmeTrack> = emptyList()
)

data class PlaylistEvent(
    val playlist: YmePlaylist = YmePlaylist(),
    val track: YmeTrack = YmeTrack(),
    val type: PlaylistEventType = PlaylistEventType.DEFAULT,
)

enum class PlaylistEventType {
    DEFAULT,
    CREATE,
    READ,
    UPDATE,
    DELETE,
    ADD_TRACK,
    REMOVE_TRACK,
    EXPORT,
}