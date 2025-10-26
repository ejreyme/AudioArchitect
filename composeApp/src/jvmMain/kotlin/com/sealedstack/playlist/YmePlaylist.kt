package com.sealedstack.playlist

import com.sealedstack.track.YmeTrack
import kotlin.random.Random

data class YmePlaylist(
    var id: Int = 0,
    val name: String = "New playlist-" + Random.nextInt(1000),
    val ymeTracks: List<YmeTrack> = emptyList()
)

data class PlaylistEvent(
    val playlist: YmePlaylist = YmePlaylist(),
    val track: YmeTrack = YmeTrack(),
    val type: PlaylistEventType = PlaylistEventType.DEFAULT,
    val exportType: PlaylistExportType = PlaylistExportType.M3U,
)

enum class PlaylistEventType {
    DEFAULT,
    CREATE,
    READ,
    DELETE,
    ADD_TRACK,
    EXPORT,
}

enum class PlaylistExportType {
    M3U,
}