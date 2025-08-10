package com.joonyor.labs.audioarchitect.data

import com.joonyor.labs.audioarchitect.home.AppConfiguration
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.jaudiotagger.audio.AudioFile
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File
import kotlin.random.Random

class YmeAudioLibraryService() : AudioLibraryService {
    private var playlistDataRepository: MutableList<YmePlaylist> = mutableListOf()
    private var trackDataRepository: MutableList<YmeTrack> = mutableListOf()

    // Use MutableStateFlow for reactive updates
    private val _playlistCollection = MutableStateFlow<List<YmePlaylist>>(emptyList())
    override val latestPlaylistCollection: Flow<List<YmePlaylist>> = _playlistCollection.asStateFlow()

    private val _trackCollection = MutableStateFlow<List<YmeTrack>>(emptyList())
    override val latestTrackCollection: Flow<List<YmeTrack>> = _trackCollection.asStateFlow()

    init {
        loadTracks()
        loadPlaylists()
        // Emit initial data
        _playlistCollection.value = playlistDataRepository.toList()
        _trackCollection.value = trackDataRepository.toList()
    }

    override fun addPlaylist(playlist: YmePlaylist) {
        println("addPlaylist")
        playlistDataRepository.add(playlist)
        // Emit updated list to trigger UI recomposition
        _playlistCollection.value = playlistDataRepository.toList()
    }

    private fun loadTracks() {
        try {
            File(AppConfiguration.LIBRARY_ROOT_PATH).walkTopDown()
                .filter { it.isFile }
                .filter { it.extension == "wav" || it.extension == "mp3" || it.extension == "m4a" }
                .forEach { trackDataRepository.add(toTrack(it)) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadPlaylists(): List<YmePlaylist> {
        return playlistDataRepository
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
    val tracks: List<YmeTrack> = emptyList()
)

data class PlaylistEvent(
    val playlist: YmePlaylist = YmePlaylist(),
    val type: PlaylistEventType = PlaylistEventType.DEFAULT,
)

enum class PlaylistEventType {
    EXPORT, CREATE, DELETE, DEFAULT
}