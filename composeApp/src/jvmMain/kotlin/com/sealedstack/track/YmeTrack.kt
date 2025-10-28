package com.sealedstack.track

import org.jaudiotagger.audio.AudioFile
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.core.statements.InsertStatement
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import org.jetbrains.exposed.v1.jdbc.insert
import java.io.File

const val UNKNOWN_ARTIST = "unknown artist"
const val UNKNOWN_TITLE = "unknown title"
const val UNKNOWN_DURATION = "unknown duration"

object TracksTable: IntIdTable() {
    val filePath = varchar("file_path", 100)
    val title = varchar("title", 100).nullable()
    val artist = varchar("artist", 100).nullable()
}

fun insertTrack(item: YmeTrack): InsertStatement<Number> {
    return TracksTable.insert {
        it[filePath] = item.filePath
        it[title] = item.title
        it[artist] = item.artist
    }
}

class TrackEntity(id: EntityID<Int>): IntEntity(id) {
    companion object Companion : IntEntityClass<TrackEntity>(TracksTable)
    var filePath by TracksTable.filePath
    var title by TracksTable.title
    var artist by TracksTable.artist
}

fun toTrack(dao: TrackEntity) = YmeTrack(
    filePath = dao.filePath,
    title = dao.title ?: "",
    artist = dao.artist ?: ""
)

data class YmeTrack(
    val filePath: String = "",
    val title: String = UNKNOWN_TITLE,
    val artist: String = UNKNOWN_ARTIST,
    val tags: Set<YmeTag> = emptySet(),
    val duration: Int = 0,
    var album: String? = null,
    var genre: String? = null,
    var bpm: Double? = null,
    var key: String? = null,
) {
    val isNew = filePath.isEmpty()
    val isNotNew = !isNew

    fun durationDisplay(): String {
        if (duration == 0) {
            return UNKNOWN_DURATION
        }
        val minutes = duration / 60
        val seconds = duration % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    fun removeTag(tag: YmeTag): YmeTrack {
        return this.copy(tags = this.tags.filter { it.name != tag.name }.toSet())
    }

    fun addTag(newTag: YmeTag): YmeTrack {
       return this.copy(tags = this.tags.toMutableSet().apply { add(newTag) })
    }
}

data class YmeTag(val name: String = "", val active: Boolean = false)

data class TrackEvent(
    val ymeTrack: YmeTrack = YmeTrack(),
    val type: TrackEventType = TrackEventType.DEFAULT,
    val tag: YmeTag = YmeTag(),
)

enum class TrackEventType {
    DEFAULT,
    ADD_TAG,
}

object TrackFileUtil {
    fun to(file: File): YmeTrack {
        return try {
            val audioMetadata = extractAudioMetadata(file)
            audioMetadata?.let {
                YmeTrack(
                    filePath = file.absolutePath,
                    title = it.title,
                    artist = it.artist,
                    duration = it.duration,
                )
            } ?: createFallbackTrack(file)
        } catch (e: Exception) {
            println("Error processing file ${file.name}: ${e.message}")
            createFallbackTrack(file)
        }
    }

    private fun extractAudioMetadata(file: File): AudioMetadata? {
        return try {
            val audioFile: AudioFile = AudioFileIO.read(file)
            val tag = audioFile.tagOrCreateAndSetDefault
            AudioMetadata(
                title = tag.getFirst(FieldKey.TITLE).takeIf { it.isNotBlank() } ?: UNKNOWN_TITLE,
                artist = tag.getFirst(FieldKey.ARTIST).takeIf { it.isNotBlank() } ?: UNKNOWN_ARTIST,
                duration = audioFile.audioHeader.trackLength
            )
        } catch (e: Exception) {
            println("WARNING: Could not read metadata for ${file.name}: ${e.message}")
            null
        }
    }
    
    private fun createFallbackTrack(file: File): YmeTrack {
        return YmeTrack(
            filePath = file.absolutePath,
            title = UNKNOWN_TITLE,
            artist = UNKNOWN_ARTIST,
            duration = 0,
        )
    }

    private data class AudioMetadata(
        val title: String,
        val artist: String,
        val duration: Int
    )
}