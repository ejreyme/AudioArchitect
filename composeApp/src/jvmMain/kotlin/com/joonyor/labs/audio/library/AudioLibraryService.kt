package com.joonyor.labs.audio.library

import com.joonyor.labs.audio.config.AppConfiguration
import com.joonyor.labs.audio.config.AppConfiguration.LIBRARY_PLAYLIST_EXPORT_PATH
import com.joonyor.labs.audio.loggerFor
import com.joonyor.labs.audio.playlist.PlaylistDataRepository
import com.joonyor.labs.audio.playlist.PlaylistExportType
import com.joonyor.labs.audio.playlist.PlaylistExporter
import com.joonyor.labs.audio.playlist.YmePlaylist
import com.joonyor.labs.audio.track.TrackDataRepository
import com.joonyor.labs.audio.track.YmeTag
import com.joonyor.labs.audio.track.YmeTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.jaudiotagger.audio.AudioFile
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File

/**
 * Service class responsible for managing audio library operations, handling playlists and tracks.
 * It interacts with the playlist and track repositories to perform tasks such as creation,
 * retrieval, updating, deletion, and search of audio data. Additionally, it provides functionality
 * for exporting playlists and initiating library processing tasks.
 *
 * This class leverages a reactive approach to track updates, ensuring that changes to playlists
 * or tracks trigger appropriate updates and recompositions. It also uses coroutine scopes
 * for background operations like exporting playlists and loading tracks from a specified path.
 *
 * Main functionalities include:
 * - Creating, updating, and deleting playlists.
 * - Updating tracks with associated metadata or tags.
 * - Searching tracks based on a query.
 * - Exporting playlists to a file in specified formats.
 * - Loading and initializing the library from a given root path.
 *
 * The class exposes `Flow` objects for both playlist and track collections to support reactive UI updates
 * based on the latest data repositories' states.
 */
class AudioLibraryService {
    private val logger = loggerFor(javaClass)
    private val scope = CoroutineScope(Dispatchers.IO)
    private var playlistDataRepository: PlaylistDataRepository = PlaylistDataRepository()
    private var trackDataRepository: TrackDataRepository = TrackDataRepository()
    // read-only
    val latestPlaylistCollection: Flow<List<YmePlaylist>> = playlistDataRepository.latestPlaylistCollection
    val latestTrackCollection: Flow<List<YmeTrack>> = trackDataRepository.latestTrackCollection

    init {
        trackDataRepository.dataSource.value = loadTracksFromPath()
        triggerPlaylistCollectionUpdate()
        triggerTrackCollectionUpdate()
    }

    fun updateTrack(track: YmeTrack, tag: YmeTag) {
        logger.debug("updateTrack")
        trackDataRepository.updateTrack(track, tag)
        triggerTrackCollectionUpdate()
    }

    fun createPlaylist(playlist: YmePlaylist) {
        logger.debug("addPlaylist")
        playlistDataRepository.createPlaylist(playlist)
        triggerPlaylistCollectionUpdate()
    }

    fun updatePlaylist(playlist: YmePlaylist, track: YmeTrack) {
        logger.debug("updatePlaylist")
        playlistDataRepository.updatePlaylist(playlist, track)
        triggerPlaylistCollectionUpdate()
    }

    fun deletePlaylist(playlist: YmePlaylist) {
        playlistDataRepository.deletePlaylist(playlist)
        triggerPlaylistCollectionUpdate()
    }

    fun exportPlaylist(playlist: YmePlaylist, type: PlaylistExportType =  PlaylistExportType.M3U) {
        scope.launch {
            logger.debug("exportPlaylist: {}", playlist.name)
            when (type) {
                PlaylistExportType.M3U -> {
                    PlaylistExporter.exportPlaylistAsM3u(playlist)
                }
                PlaylistExportType.TRAKTOR -> {
                    PlaylistExporter.exportTraktorNML(
                        playlistName = playlist.name,
                        tracks = playlist.tracks.toMutableList(),
                        outputPath = "$LIBRARY_PLAYLIST_EXPORT_PATH/${playlist.name}.nml")
                }
                PlaylistExportType.REKORDBOX -> {
                    PlaylistExporter.exportRekordboxXML(
                        tracks = playlist.tracks.toMutableList(),
                        outputPath = "$LIBRARY_PLAYLIST_EXPORT_PATH/${playlist.name}.xml")
                }
            }
        }
    }

    fun searchTracks(query: String): List<YmeTrack> {
        return trackDataRepository.search(query)
    }

    // Emit updated list to trigger UI recomposition
    private fun triggerPlaylistCollectionUpdate() {
        scope.launch {
            playlistDataRepository.latestPlaylistCollection.collect {
                logger.debug("triggerPlaylistCollectionUpdate: size ${it.size}")
                playlistDataRepository.dataSource.value = it
            }
        }
    }

    // Emit updated list to trigger UI recomposition
    private fun triggerTrackCollectionUpdate() {
        scope.launch {
            trackDataRepository.latestTrackCollection.collect {
                logger.debug("triggerTrackCollectionUpdate: size=${it.size}")
                trackDataRepository.dataSource.value = it
            }
        }
    }

    private fun loadTracksFromPath(pathname: String = AppConfiguration.LIBRARY_ROOT_PATH): List<YmeTrack> {
        try {
            return File(pathname).walkTopDown()
                .filter { it.isFile }
                .filter { it.extension == "wav" || it.extension == "mp3" || it.extension == "m4a" }
                .map { toTrack(it) }
                .toList()
        } catch (e: Exception) {
            logger.debug("Error loading tracks from file path $pathname: ${e.message}")
            return emptyList()
        }
    }

    private fun toTrack(file: File): YmeTrack {
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
            logger.debug("Error processing file ${file.name}: ${e.message}")
            createFallbackTrack(file)
        }
    }

    private fun extractAudioMetadata(file: File): AudioMetadata? {
        return try {
            val audioFile: AudioFile = AudioFileIO.read(file)
            val tag = audioFile.tagOrCreateAndSetDefault
            AudioMetadata(
                title = tag.getFirst(FieldKey.TITLE).takeIf { it.isNotBlank() } ?: file.nameWithoutExtension,
                artist = tag.getFirst(FieldKey.ARTIST).takeIf { it.isNotBlank() } ?: "Unknown Artist",
                duration = audioFile.audioHeader.trackLength
            )
        } catch (e: Exception) {
            logger.debug("WARNING: Could not read metadata for ${file.name}: ${e.message}")
            null
        }
    }

    private fun createFallbackTrack(file: File): YmeTrack {
        return YmeTrack(
            filePath = file.absolutePath,
            title = file.nameWithoutExtension,
            artist = "Unknown Artist",
            duration = 0,
        )
    }

    private data class AudioMetadata(
        val title: String,
        val artist: String,
        val duration: Int
    )
}