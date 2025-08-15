package com.joonyor.labs.audio.playlist

import com.iheartradio.m3u8.Encoding
import com.iheartradio.m3u8.Format
import com.iheartradio.m3u8.PlaylistWriter
import com.iheartradio.m3u8.data.MediaPlaylist
import com.iheartradio.m3u8.data.Playlist
import com.iheartradio.m3u8.data.TrackData
import com.iheartradio.m3u8.data.TrackInfo
import com.joonyor.labs.audio.config.AppConfiguration.LIBRARY_PLAYLIST_EXPORT_PATH
import com.joonyor.labs.audio.loggerFor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class PlaylistService {
    private val logger = loggerFor(javaClass)
    private val scope = CoroutineScope(Dispatchers.IO)

    fun exportPlaylist(
        playlist: YmePlaylist,
        type: PlaylistExportType = PlaylistExportType.M3U
    ) {
        scope.launch {
            logger.debug("exportPlaylist: {}", playlist.name)
            when (type) {
                PlaylistExportType.M3U -> exportPlaylistAsM3u(playlist)
                PlaylistExportType.TRAKTOR -> exportAsTraktorNML(playlist)
                PlaylistExportType.REKORDBOX -> exportAsRekordboxXML(playlist)
            }
        }
    }

    fun exportAsTraktorNML(playlist: YmePlaylist) {
        println("exportAsTraktorNML:")
    }

    fun exportAsRekordboxXML(playlist: YmePlaylist) {
        println("exportAsRekordboxXML:")
    }

    fun exportPlaylistAsM3u(playlist: YmePlaylist) {
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
            logger.debug("Error exporting playlist ${e.message}")
        } finally {
            logger.debug("exportPlaylist: done")
        }
    }
}