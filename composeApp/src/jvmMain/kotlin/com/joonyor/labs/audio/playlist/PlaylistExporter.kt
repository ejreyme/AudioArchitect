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
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.io.File;
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
            val outputFile = File(buildExportFile(outputDir, playlist.name, "m3u"))
            val playlistWriter = PlaylistWriter(outputFile.outputStream(), Format.EXT_M3U, Encoding.UTF_8)
            playlistWriter.write(playlistExport)
            return outputFile.absolutePath
        } catch (e: Exception) {
            logger.debug("Error exporting playlist ${e.message}")
        } finally {
            logger.debug("exportPlaylist: done")
        }
        return ""
    }

    fun asNML(playlist: YmePlaylist) {
        val playlistName = playlist.name
        val tracks = playlist.tracks.toMutableList()
        val outputPath = buildExportFile(outputDir, playlistName, "nml")

        val doc: Document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()
        val nml: Element = doc.createElement("NML")
        nml.setAttribute("VERSION", "19")
        doc.appendChild(nml)

        val collection: Element = doc.createElement("COLLECTION")
        nml.appendChild(collection)
        for (track in tracks) {
            val entry: Element = doc.createElement("ENTRY")
            val primaryKey: Element = doc.createElement("PRIMARYKEY")
            primaryKey.setAttribute("TYPE", "SONG")
            primaryKey.setAttribute("TITLE", track.title)
            entry.appendChild(primaryKey)
            val artist: Element = doc.createElement("ARTIST")
            artist.setTextContent(track.artist)
            entry.appendChild(artist)
            val album: Element = doc.createElement("ALBUM")
            album.setTextContent(track.album)
            entry.appendChild(album)
            val genre: Element = doc.createElement("GENRE")
            genre.setTextContent(track.genre)
            entry.appendChild(genre)
            val tempo: Element = doc.createElement("TEMPO")
            tempo.setAttribute("BPM", track.bpm.toString())
            entry.appendChild(tempo)
            val musicalKey: Element = doc.createElement("MUSICAL_KEY")
            musicalKey.setAttribute("VALUE", track.key)
            entry.appendChild(musicalKey)
            for (cue in track.cues) {
                val cueElement: Element = doc.createElement("CUE_V2")
                cueElement.setAttribute("NAME", cue.name)
                cueElement.setAttribute("START", cue.start.toString())
                if (cue.length > 0) {
                    cueElement.setAttribute("LEN", cue.length.toString())
                }
                entry.appendChild(cueElement)
            }
            collection.appendChild(entry)
        }

        val playlists: Element = doc.createElement("PLAYLISTS")
        val node: Element = doc.createElement("NODE")
        node.setAttribute("TYPE", "PLAYLIST")
        node.setAttribute("NAME", playlistName)
        val subnodes: Element = doc.createElement("SUBNODES")
        val playlist: Element = doc.createElement("PLAYLIST")
        for (track in tracks) {
            val entry: Element = doc.createElement("ENTRY")
            val primaryKey: Element = doc.createElement("PRIMARYKEY")
            primaryKey.setAttribute("TYPE", "SONG")
            primaryKey.setAttribute("TITLE", track.title)
            entry.appendChild(primaryKey)
            playlist.appendChild(entry)
        }
        subnodes.appendChild(playlist)
        node.appendChild(subnodes)
        playlists.appendChild(node)
        nml.appendChild(playlists)


        TransformerFactory.newInstance().newTransformer()
            .transform(DOMSource(doc), StreamResult(File(outputPath)))
    }

    fun asXML(playlist: YmePlaylist) {
        val tracks = playlist.tracks.toMutableList()
        val outputPath = "$LIBRARY_PLAYLIST_EXPORT_PATH/${playlist.name}.xml"

        val doc: Document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()
        val djPlaylists: Element = doc.createElement("DJ_PLAYLISTS")
        djPlaylists.setAttribute("Version", "1.0.0")
        doc.appendChild(djPlaylists)

        val collection: Element = doc.createElement("COLLECTION")
        djPlaylists.appendChild(collection)
        for (track in tracks) {
            val trackElement: Element = doc.createElement("TRACK")
            trackElement.setAttribute("TrackID", System.currentTimeMillis().toString()) // Placeholder ID
            trackElement.setAttribute("Name", track.title)
            trackElement.setAttribute("Artist", track.artist)
            trackElement.setAttribute("Album", track.album)
            trackElement.setAttribute("Genre", track.genre)
            trackElement.setAttribute("Tempo", track.bpm.toString())
            trackElement.setAttribute("Tonality", convertToCamelot(track.key))
            for (cue in track.cues) {
                val posMark: Element = doc.createElement("POSITION_MARK")
                posMark.setAttribute("Name", cue.name)
                posMark.setAttribute("Start", (cue.start / 1000).toString()) // Convert ms to seconds
                posMark.setAttribute("Num", if (cue.length > 0) "-1" else "0") // -1 for loops, 0 for cues
                trackElement.appendChild(posMark)
            }
            collection.appendChild(trackElement)
        }

        val playlists: Element = doc.createElement("PLAYLISTS")
        val node: Element = doc.createElement("NODE")
        node.setAttribute("Type", "1")
        node.setAttribute("Name", "My Playlist")
        for (track in tracks) {
            val trackRef: Element = doc.createElement("TRACK")
            trackRef.setAttribute("Key", System.currentTimeMillis().toString()) // Placeholder ID
            node.appendChild(trackRef)
        }
        playlists.appendChild(node)
        djPlaylists.appendChild(playlists)

        TransformerFactory.newInstance().newTransformer()
            .transform(DOMSource(doc), StreamResult(File(outputPath)))
    }

    private fun convertToCamelot(key: String?): String? {
        // Simplified key conversion logic (e.g., "C Major" to "8B")
        // Implement a mapping based on tools like MIXO's Key Converter
        return key // Placeholder
    }

    private fun buildExportFile(exportDir: String, name: String, extension: String): String {
        val filename = "$name.$extension"
        return Path.of(exportDir).resolve(filename).toString()
    }
}