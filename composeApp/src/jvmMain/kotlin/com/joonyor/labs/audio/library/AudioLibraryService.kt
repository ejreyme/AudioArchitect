package com.joonyor.labs.audio.library

import com.iheartradio.m3u8.Encoding
import com.iheartradio.m3u8.Format
import com.iheartradio.m3u8.PlaylistWriter
import com.iheartradio.m3u8.data.MediaPlaylist
import com.iheartradio.m3u8.data.Playlist
import com.iheartradio.m3u8.data.TrackData
import com.iheartradio.m3u8.data.TrackInfo
import com.joonyor.labs.audio.config.AppConfiguration
import com.joonyor.labs.audio.config.AppConfiguration.LIBRARY_PLAYLIST_EXPORT_PATH
import com.joonyor.labs.audio.playlist.PlaylistDataRepository
import com.joonyor.labs.audio.playlist.YmePlaylist
import com.joonyor.labs.audio.track.TrackDataRepository
import com.joonyor.labs.audio.track.YmeTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.jaudiotagger.audio.AudioFile
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File

class AudioLibraryService() {
    private var playlistDataRepository: PlaylistDataRepository = PlaylistDataRepository()
    private var trackDataRepository: TrackDataRepository = TrackDataRepository()

    // read-only
    val latestPlaylistCollection: Flow<List<YmePlaylist>> = playlistDataRepository.latestPlaylistCollection
    val latestTrackCollection: Flow<List<YmeTrack>> = trackDataRepository.latestTrackCollection

    val scope = CoroutineScope(Dispatchers.IO)

    init {
        initLibrary()
        triggerPlaylistCollectionUpdate()
        triggerTrackCollectionUpdate()
    }

    fun createPlaylist(playlist: YmePlaylist) {
        println("addPlaylist")
        playlistDataRepository.createPlaylist(playlist)
        triggerPlaylistCollectionUpdate()
    }

    fun updatePlaylist(playlist: YmePlaylist, track: YmeTrack) {
        println("updatePlaylist")
        playlistDataRepository.updatePlaylist(playlist, track)
        triggerPlaylistCollectionUpdate()
    }

    fun deletePlaylist(playlist: YmePlaylist) {
        playlistDataRepository.deletePlaylist(playlist)
        triggerPlaylistCollectionUpdate()
    }

    fun searchTracks(query: String): List<YmeTrack> {
        return trackDataRepository.search(query)
    }

    fun exportPlaylist(playlist: YmePlaylist) {
        scope.launch {
            println("exportPlaylist: $playlist")
            try {
                // build TrackData list
                val tracks = playlist.tracks.stream()
                    .filter { it.isNotNew }
                    .map {
                        TrackData.Builder()
                            .withTrackInfo(TrackInfo(it.duration.toFloat(), it.title))
                            .withUri(it.filePath)
                            .build()
                    }.toList()

                // build media playlist
                val mediaPlaylist = MediaPlaylist.Builder()
                    .withTracks(tracks)
                    .build()

                // build playlist
                val playlistExport = Playlist.Builder()
                    .withCompatibilityVersion(1)
                    .withMediaPlaylist(mediaPlaylist)
                    .build()

                // writer playlist to file
                val outputFile = File("$LIBRARY_PLAYLIST_EXPORT_PATH/${playlist.name}.m3u")
                val playlistWriter = PlaylistWriter(outputFile.outputStream(), Format.EXT_M3U, Encoding.UTF_8)
                playlistWriter.write(playlistExport)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                println("exportPlaylist: done")
            }
        }
    }

    // Emit updated list to trigger UI recomposition
    private fun triggerPlaylistCollectionUpdate() {
        scope.launch {
            playlistDataRepository.latestPlaylistCollection.collect {
                println("triggerPlaylistCollectionUpdate: $it")
                playlistDataRepository.dataSource.value = it
            }
        }
    }

    // Emit updated list to trigger UI recomposition
    private fun triggerTrackCollectionUpdate() {
        scope.launch {
            trackDataRepository.latestTrackCollection.collect {
                println("triggerTrackCollectionUpdate: $it")
                trackDataRepository.dataSource.value = it
            }
        }
    }

    private fun initLibrary() {
        trackDataRepository.dataSource.value = loadTracksFromPath()
    }

    private fun loadTracksFromPath(pathname: String = AppConfiguration.LIBRARY_ROOT_PATH): List<YmeTrack> {
        try {
            return File(pathname).walkTopDown()
                .filter { it.isFile }
                .filter { it.extension == "wav" || it.extension == "mp3" || it.extension == "m4a" }
                .map { toTrack(it) }
                .toList()
        } catch (e: Exception) {
            e.printStackTrace()
        }
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
                duration = audioFile.audioHeader.trackLength,
                durationDisplay = toDurationDisplay(audioFile.audioHeader.trackLength)
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return YmeTrack(filePath = file.absolutePath)
    }

    private fun toDurationDisplay(duration: Int): String {
        val minutes = duration / 60
        val seconds = duration % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}