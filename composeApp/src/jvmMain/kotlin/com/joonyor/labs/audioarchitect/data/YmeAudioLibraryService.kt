package com.joonyor.labs.audioarchitect.data

import com.joonyor.labs.audioarchitect.home.AppConfiguration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.jaudiotagger.audio.AudioFile
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File
import kotlin.random.Random

class YmeAudioLibraryService() : AudioLibraryService {
    val scope = CoroutineScope(Dispatchers.IO)

    private var playlistDataRepository: PlaylistDataRepository = PlaylistDataRepository()
    private var trackDataRepository: MutableList<YmeTrack> = mutableListOf()

    // Use MutableStateFlow for reactive updates (local)
    private val _playlistCollection = MutableStateFlow<List<YmePlaylist>>(emptyList())
    private val _trackCollection = MutableStateFlow<List<YmeTrack>>(emptyList())

    // Use asStateFlow to get a read-only snapshot (immutable)
    override val latestPlaylistCollection: Flow<List<YmePlaylist>> = _playlistCollection.asStateFlow()
    override val latestTrackCollection: Flow<List<YmeTrack>> = _trackCollection.asStateFlow()

    init {
        loadTracks()

        // Emit initial data
        triggerPlaylistCollectionUpdate()
        triggerTrackCollectionUpdate()
    }

    override fun addPlaylist(playlist: YmePlaylist) {
        println("addPlaylist")
        playlistDataRepository.addPlaylist(playlist)
        triggerPlaylistCollectionUpdate()
    }

    override fun updatePlaylist(
        playlist: YmePlaylist,
        track: YmeTrack
    ) {
        println("updatePlaylist")
        playlistDataRepository.updatePlaylist(playlist, track)
    }

    // Emit updated list to trigger UI recomposition
    private fun triggerPlaylistCollectionUpdate() {
        scope.launch {
            playlistDataRepository.latestPlaylistCollection.collect {
                println("triggerPlaylistCollectionUpdate: $it")
                _playlistCollection.value = it
            }
        }
    }

    // Emit updated list to trigger UI recomposition
    private fun triggerTrackCollectionUpdate() {
        _trackCollection.value = trackDataRepository.toList()
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
) {
    val isNew = filePath.isEmpty()
}

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
    EXPORT, CREATE, DELETE, DEFAULT, ADD_TRACK, REMOVE_TRACK
}