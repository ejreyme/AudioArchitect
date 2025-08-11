package com.joonyor.labs.audioarchitect.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PlaylistDataRepository {
    // write-only
    private val _playlistDataSource = MutableStateFlow<List<YmePlaylist>>(emptyList())
    // read-only
    val latestPlaylistCollection: Flow<List<YmePlaylist>> = _playlistDataSource.asStateFlow()

    init {
        _playlistDataSource.value = generatePlaylistList().toMutableList()
    }

    fun addPlaylist(playlist: YmePlaylist) {
        _playlistDataSource.value = _playlistDataSource.value.toMutableList().apply {
            add(playlist)
        }
    }

    fun updatePlaylist(playlist: YmePlaylist) {
        findPlaylistById(playlist.id)?.let {
            _playlistDataSource.value = _playlistDataSource.value.toMutableList().apply {
                set(indexOf(it), playlist)
            }
        }
    }

    fun updatePlaylist(playlist: YmePlaylist, track: YmeTrack) {
        val updatedPlaylist = YmePlaylist(
            id = playlist.id,
            name = playlist.name,
            tracks = listOf(playlist.tracks, listOf(track)).flatten())
        updatePlaylist(updatedPlaylist)
    }

    fun findPlaylistById(id: Int): YmePlaylist? {
        return _playlistDataSource.value.find { it.id == id }
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