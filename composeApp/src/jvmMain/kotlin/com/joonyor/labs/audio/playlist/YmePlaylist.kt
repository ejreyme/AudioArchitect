package com.joonyor.labs.audio.playlist

import com.joonyor.labs.audio.track.YmeTrack
import kotlin.random.Random

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