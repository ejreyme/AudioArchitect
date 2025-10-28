package com.sealedstack.library

import com.sealedstack.config.AppConfiguration
import com.sealedstack.playlist.PlaylistExporter
import com.sealedstack.playlist.PlaylistRepository
import com.sealedstack.playlist.YmePlaylist
import com.sealedstack.track.TrackFileUtil
import com.sealedstack.track.TrackRepository
import com.sealedstack.track.YmeTag
import com.sealedstack.track.YmeTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
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
class AudioLibraryService(
    val trackRepository: TrackRepository,
    val playlistRepository: PlaylistRepository
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    val latestLibraryPlaylists: Flow<List<YmePlaylist>> = playlistRepository.latestRepoPlaylists
    val latestLibraryTracks: Flow<List<YmeTrack>> = trackRepository.latestRepoTracks

    init { loadTracksFromPath() }

    suspend fun updateTrack(track: YmeTrack, tag: YmeTag?) {
        when(tag == null) {
            true -> trackRepository.update(track)
            false -> trackRepository.update(track.addTag(tag))
        }
    }

    suspend fun newPlaylist(playlist: YmePlaylist) {
        playlistRepository.create(playlist)
    }

    suspend fun updatePlaylist(playlist: YmePlaylist, track: YmeTrack) {
        playlistRepository.update(playlist.addTrack(track))
    }

    suspend fun deletePlaylist(playlist: YmePlaylist) {
        playlistRepository.delete(playlist)
    }

    fun exportPlaylist(playlist: YmePlaylist) {
        PlaylistExporter.asM3u(playlist)
    }

    private fun loadTracksFromPath(pathname: String = AppConfiguration.LIBRARY_ROOT_PATH) {
        scope.launch {
            try {
                File(pathname).walkTopDown()
                    .filter { it.isFile }
                    .filter { it.extension == "wav" || it.extension == "mp3" || it.extension == "m4a" }
                    .map { TrackFileUtil.to(it) }
                    .forEach { track -> trackRepository.create(item = track) }
            } catch (e: Exception) {
                println("Error loading tracks from file path $pathname: ${e.message}")
            }
        }
    }
}