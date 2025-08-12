package com.joonyor.labs.audio.library

import com.joonyor.labs.audio.config.AppConfiguration
import com.joonyor.labs.audio.playlist.PlaylistDataRepository
import com.joonyor.labs.audio.playlist.YmePlaylist
import com.joonyor.labs.audio.track.TrackDataRepository
import com.joonyor.labs.audio.track.YmeTrack
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

class AudioLibraryService() {
    val scope = CoroutineScope(Dispatchers.IO)

    private var playlistDataRepository: PlaylistDataRepository = PlaylistDataRepository()
    private var trackDataRepository: TrackDataRepository = TrackDataRepository()

    // write-only
    private val _playlistCollection = MutableStateFlow<List<YmePlaylist>>(emptyList())
    private val _trackCollection = MutableStateFlow<List<YmeTrack>>(emptyList())

    // read-only
    val latestPlaylistCollection: Flow<List<YmePlaylist>> = _playlistCollection.asStateFlow()
    val latestTrackCollection: Flow<List<YmeTrack>> = _trackCollection.asStateFlow()

    init {
        loadTracks()
        loadPlayLists()
        triggerPlaylistCollectionUpdate()
        triggerTrackCollectionUpdate()
    }

    fun addPlaylist(playlist: YmePlaylist) {
        println("addPlaylist")
        playlistDataRepository.addPlaylist(playlist)
        triggerPlaylistCollectionUpdate()
    }

    fun updatePlaylist(playlist: YmePlaylist, track: YmeTrack) {
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
        scope.launch {
            trackDataRepository.latestPlaylistCollection.collect {
                println("triggerTrackCollectionUpdate: $it")
                _trackCollection.value = it
            }
        }
    }

    private fun loadTracks() {
        try {
            File(AppConfiguration.LIBRARY_ROOT_PATH).walkTopDown().filter { it.isFile }
                .filter { it.extension == "wav" || it.extension == "mp3" || it.extension == "m4a" }
                .forEach { trackDataRepository.addTrack(toTrack(it)) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadPlayLists() {
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





