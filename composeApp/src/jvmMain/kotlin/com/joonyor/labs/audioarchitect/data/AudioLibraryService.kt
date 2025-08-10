package com.joonyor.labs.audioarchitect.data

import kotlinx.coroutines.flow.Flow

interface AudioLibraryService {
    val latestPlaylistCollection: Flow<List<YmePlaylist>>
    val latestTrackCollection: Flow<List<YmeTrack>>
    fun addPlaylist(playlist: YmePlaylist)
}