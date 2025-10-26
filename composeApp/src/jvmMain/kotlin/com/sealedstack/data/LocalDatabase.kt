package com.sealedstack.data

import com.sealedstack.config.DbSettings
import com.sealedstack.playlist.YmePlaylist
import com.sealedstack.track.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class LocalDatabase {

    init {
        DbSettings.h2Db
        transaction {
            SchemaUtils.drop(TracksTable)
            SchemaUtils.create(TracksTable)
        }
    }

    val latestDatabaseTracks: Flow<List<YmeTrack>> = flow {
        emit(
            transaction {
                TrackEntity
                    .all()
                    .map(::toTrack)
                    .toList()
            }
        )
    }



    val latestDatabasePlaylists: Flow<List<YmePlaylist>> = MutableSharedFlow(1)

    suspend fun create(item: YmePlaylist) {
        TODO("Not yet implemented")
    }

    fun read(item: YmePlaylist): YmePlaylist {
        TODO("Not yet implemented")
    }

    fun updatePlaylist(item: YmePlaylist) {
        TODO("Not yet implemented")
    }

    fun updateTrack(item: YmeTrack) {

    }

    fun delete(item: YmePlaylist) {
        TODO("Not yet implemented")
    }

    fun createTrack(item: YmeTrack) = transaction {
        println("createTrack: $item")
        insertTrack(item)
    }
}