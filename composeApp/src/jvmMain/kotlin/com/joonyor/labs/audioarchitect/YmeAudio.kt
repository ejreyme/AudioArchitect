package com.joonyor.labs.audioarchitect

import org.jaudiotagger.audio.AudioFile
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import uk.co.caprica.vlcj.player.component.AudioPlayerComponent
import java.io.File
import kotlin.random.Random

interface AudioPlayerService {
    fun play(filePath: String)
    fun stop()
    fun pause()
    fun volumeChange(value: Float)
}

class YmeAudioPlayerService : AudioPlayerService {
    private var mediaPlayerComponent: AudioPlayerComponent = AudioPlayerComponent()

    override fun play(filePath: String) {
        print("play: $filePath")
        mediaPlayerComponent.mediaPlayer()?.media()?.play(filePath)
    }

    override fun stop() {
        print("stop")
        mediaPlayerComponent.mediaPlayer()?.controls()?.stop()
    }

    override fun pause() {
        print("pause")
        mediaPlayerComponent.mediaPlayer()?.controls()?.pause()
    }

    override fun volumeChange(value: Float) {
        mediaPlayerComponent.mediaPlayer().audio().setVolume(value.toInt())
    }
}

interface LibraryManager {
    fun loadLibrary(): List<YmeTrack>
    fun loadPlaylists(): List<YmePlaylist>
}

class YmeLibraryManager : LibraryManager {
    override fun loadLibrary(): List<YmeTrack> {
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
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return YmeTrack(filePath = file.absolutePath)
    }
}

data class YmeTrack(
    val filePath: String = "",
    val title: String = "Unknown title",
    val artist: String = "Unknown artist",
)

data class YmePlaylist(
    var id: Int = 0,
    val name: String = "New playlist-" + Random.nextInt(1000),
    val items: List<YmeTrack> = emptyList()
)