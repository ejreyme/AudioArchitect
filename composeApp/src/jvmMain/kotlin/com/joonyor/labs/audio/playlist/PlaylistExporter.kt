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
import com.joonyor.labs.audio.track.YmeTrack
import java.io.File
import java.nio.file.Path

data class CuePoint(
    var name: String?, // in milliseconds
    var start: Double, // for loops, 0 for cues
    var length: Double
)

// TODO not done
class PlaylistExporter(val outputDir: String = LIBRARY_PLAYLIST_EXPORT_PATH) {
    private val logger = loggerFor(javaClass)

    fun asM3u(playlist: YmePlaylist): String {
        return try {
            val trackDataList = buildTrackDataList(playlist.tracks)
            val playlistExport = buildM3uPlaylist(trackDataList)
            writePlaylistToFile(playlistExport, playlist.name, "m3u")
        } catch (e: Exception) {
            logger.debug("Error exporting playlist: ${e.message}")
            ""
        } finally {
            logger.debug("exportPlaylist: done")
        }
    }

    private fun buildTrackDataList(tracks: List<YmeTrack>): List<TrackData> {
        return tracks.stream()
            .filter { it.isNotNew }
            .map { track ->
                TrackData.Builder()
                    .withTrackInfo(TrackInfo(track.duration.toFloat(), track.title))
                    .withUri(track.filePath)
                    .build()
            }
            .toList()
    }

    private fun buildM3uPlaylist(trackDataList: List<TrackData>): Playlist {
        val mediaPlaylist = MediaPlaylist.Builder()
            .withTracks(trackDataList)
            .build()

        return Playlist.Builder()
            .withCompatibilityVersion(1)
            .withMediaPlaylist(mediaPlaylist)
            .build()
    }

    private fun writePlaylistToFile(playlistExport: Playlist, playlistName: String, extension: String): String {
        val outputFile = File(buildExportFile(outputDir, playlistName, extension))
        val playlistWriter = PlaylistWriter(outputFile.outputStream(), Format.EXT_M3U, Encoding.UTF_8)
        playlistWriter.write(playlistExport)
        return outputFile.absolutePath
    }

    private fun buildExportFile(exportDir: String, name: String, extension: String): String {
        val filename = "$name.$extension"
        return Path.of(exportDir).resolve(filename).toString()
    }
}