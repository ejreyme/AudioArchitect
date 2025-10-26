package com.sealedstack.playlist

import com.iheartradio.m3u8.Encoding
import com.iheartradio.m3u8.Format
import com.iheartradio.m3u8.PlaylistWriter
import com.iheartradio.m3u8.data.MediaPlaylist
import com.iheartradio.m3u8.data.Playlist
import com.iheartradio.m3u8.data.TrackData
import com.iheartradio.m3u8.data.TrackInfo
import com.sealedstack.config.AppConfiguration.LIBRARY_PLAYLIST_EXPORT_PATH
import com.sealedstack.track.YmeTrack
import java.io.File
import java.nio.file.Path

object PlaylistExporter {
    fun asM3u(ymePlaylist: YmePlaylist): String {
        return try {
            val trackDataList = buildTrackDataList(ymePlaylist.ymeTracks)
            val playlistExport = buildM3uPlaylist(trackDataList)
            writePlaylistToFile(playlistExport, ymePlaylist.name, "m3u")
        } catch (e: Exception) {
            println("Error exporting playlist: ${e.message}")
            ""
        } finally {
            println("exportPlaylist: done")
        }
    }

    private fun buildTrackDataList(ymeTracks: List<YmeTrack>): List<TrackData> {
        return ymeTracks.stream()
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
        val outputFile = File(buildExportFile(LIBRARY_PLAYLIST_EXPORT_PATH, playlistName, extension))
        val playlistWriter = PlaylistWriter(outputFile.outputStream(), Format.EXT_M3U, Encoding.UTF_8)
        playlistWriter.write(playlistExport)
        return outputFile.absolutePath
    }

    private fun buildExportFile(exportDir: String, name: String, extension: String): String {
        val filename = "$name.$extension"
        return Path.of(exportDir).resolve(filename).toString()
    }
}