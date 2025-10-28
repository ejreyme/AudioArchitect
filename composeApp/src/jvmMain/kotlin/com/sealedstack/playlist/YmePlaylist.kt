package com.sealedstack.playlist

import com.sealedstack.track.YmeTrack
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import kotlin.random.Random

object PlaylistsTable: IntIdTable() {
    val name = varchar("name", 100)
}

class PlaylistEntity(id: EntityID<Int>): IntEntity(id) {
    companion object Companion : IntEntityClass<PlaylistEntity>(PlaylistsTable)
    var name by PlaylistsTable.name
}

fun toPlaylist(dao: PlaylistEntity) = YmePlaylist(
    name = dao.name,
)

data class YmePlaylist(
    var id: Int = 0,
    val name: String = "New playlist-" + Random.nextInt(1000),
    val tracks: List<YmeTrack> = emptyList()
) {
    fun addTrack(newTrack: YmeTrack): YmePlaylist {
        return this.copy(tracks = this.tracks.toMutableList().apply { add(newTrack) })
    }

    fun removeTrack(track: YmeTrack): YmePlaylist {
        return this.copy(
            tracks = this.tracks.filter {
                (it.title != track.title && it.artist != track.artist)
            }
        )
    }
}

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