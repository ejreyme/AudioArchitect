package com.sealedstack.data

import com.sealedstack.config.DbSettings
import com.sealedstack.playlist.PlaylistEntity
import com.sealedstack.playlist.PlaylistsTable
import com.sealedstack.playlist.YmePlaylist
import com.sealedstack.playlist.toPlaylist
import com.sealedstack.track.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class LocalDatabase {
    init {
        try {
            DbSettings.h2Db
            transaction {
                SchemaUtils.drop(TracksTable)
                SchemaUtils.create(TracksTable)
                SchemaUtils.drop(PlaylistsTable)
                SchemaUtils.create(PlaylistsTable)
            }
        } catch (e: Exception) {
            println("db setup failure: ${e.message}")
        }
    }

    val latestDatabaseTracks: Flow<List<YmeTrack>> = flow {
        val items = transaction {
            TrackEntity
                .all()
                .map(::toTrack)
                .toList()
        }

        emit(value = items)
    }


    val latestDatabasePlaylists: Flow<List<YmePlaylist>> = flow {
        val items = transaction {
            PlaylistEntity
                .all()
                .map(::toPlaylist)
                .toList()
        }

        emit(value = items)
    }

    suspend fun insertPlaylist(item: YmePlaylist) {
        TODO("Not yet implemented")
    }

    fun selectPlaylist(item: YmePlaylist): YmePlaylist {
        TODO("Not yet implemented")
    }

    fun updatePlaylist(item: YmePlaylist) {
        TODO("Not yet implemented")
    }

    fun deletePlaylist(item: YmePlaylist) {
        TODO("Not yet implemented")
    }

    fun updateTrack(item: YmeTrack) {

    }

    fun createTrack(item: YmeTrack) = transaction {
        insertTrack(item)
    }
}