package com.joonyor.labs.audioarchitect

import org.jaudiotagger.audio.AudioFile
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File
import kotlin.random.Random


interface AudioLibraryService {
    fun loadTracks(): List<YmeTrack>
    fun loadPlaylists(): List<YmePlaylist>
}

class YmeAudioLibraryService : AudioLibraryService {
    override fun loadTracks(): List<YmeTrack> {
        val tracks: MutableList<YmeTrack> = mutableListOf()
        try {
            File(AppConfiguration.LIBRARY_ROOT_PATH).walkTopDown()
                .filter { it.isFile }
                .filter { it.extension == "wav" || it.extension == "mp3" || it.extension == "m4a" }
                .forEach { tracks.add(toTrack(it)) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return tracks
    }

    override fun loadPlaylists(): List<YmePlaylist> {
        return emptyList()
    }

    private fun toTrack(file: File): YmeTrack {
        try {
            val audioFile: AudioFile = AudioFileIO.read(file)
            val tag = audioFile.tagOrCreateAndSetDefault
            return YmeTrack(
                filePath = file.absolutePath,
                title = tag.getFirst(FieldKey.TITLE),
                artist = tag.getFirst(FieldKey.ARTIST),
                duration = toDuration(audioFile.audioHeader.trackLength)
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return YmeTrack(filePath = file.absolutePath)
    }

    private fun toDuration(duration: Int): String {
        val minutes = duration / 60
        val seconds = duration % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}

data class YmeTrack(
    val filePath: String = "",
    val title: String = "Unknown title",
    val artist: String = "Unknown artist",
    val duration: String = "00:00"
)

data class YmePlaylist(
    var id: Int = 0,
    val name: String = "New playlist-" + Random.nextInt(1000),
    val items: List<YmeTrack> = emptyList()
)

data class PlaylistEvent(
    val playlist: YmePlaylist = YmePlaylist(),
    val type: PlaylistEventType = PlaylistEventType.DEFAULT,
)

enum class PlaylistEventType {
    EXPORT, CREATE, DELETE, DEFAULT
}