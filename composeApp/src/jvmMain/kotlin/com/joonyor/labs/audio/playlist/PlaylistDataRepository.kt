package com.joonyor.labs.audio.playlist

import com.joonyor.labs.audio.track.YmeTrack
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

class PlaylistDataRepository {
    // read/write-only
    val playlistDataSource = MutableStateFlow<List<YmePlaylist>>(emptyList())
    // external read-only
    val latestPlaylistCollection: Flow<List<YmePlaylist>> = playlistDataSource.asStateFlow()

    fun addPlaylist(playlist: YmePlaylist) {
        playlistDataSource.value = playlistDataSource.value.toMutableList().apply {
            add(playlist)
        }
    }

    fun updatePlaylist(playlist: YmePlaylist) {
        findPlaylistById(playlist.id)?.let {
            playlistDataSource.value = playlistDataSource.value.toMutableList().apply {
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

    fun findPlaylistById(id: Int): YmePlaylist? {
        return playlistDataSource.value.find { it.id == id }
    }

    fun generatePlaylistList(): List<YmePlaylist> {
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
    EXPORT, CREATE, DELETE, DEFAULT, ADD_TRACK, REMOVE_TRACK, VIEW,LIBRARY,HOME,EXPLORE
}