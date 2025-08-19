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
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.io.File;
import java.text.SimpleDateFormat
import java.util.Base64
import java.util.Date
import java.util.Random
import java.util.UUID

data class CuePoint(
    var name: String?, // in milliseconds
    var start: Double, // for loops, 0 for cues
    var length: Double
)

// TODO not done
object PlaylistExporter {
    private val logger = loggerFor(javaClass)

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

    @Throws(Exception::class)
    fun exportTraktorNML(playlistName: String, tracks: MutableList<YmeTrack>, outputPath: String) {
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

    @Throws(Exception::class)
    fun exportRekordboxXML(tracks: MutableList<YmeTrack>, outputPath: String) {
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
}

private val dateFormat = SimpleDateFormat("yyyy/M/d")
private val timeFormat = SimpleDateFormat("HHmm")

fun exportPlaylist(playlist: YmePlaylist, outputPath: String) {
    val file = File(outputPath)
    val xml = generateNMLXml(playlist)
    file.writeText(xml, Charsets.UTF_8)
}

private fun generateNMLXml(playlist: YmePlaylist): String {
    val validTracks = playlist.tracks.filter { it.isNotNew }
    val currentDate = Date()
    val dollarChar = '$'

    return buildString {
        appendLine("""<?xml version="1.0" encoding="UTF-8" standalone="no" ?>""")
        appendLine("""<NML VERSION="19">""")
        appendLine("""<HEAD COMPANY="www.native-instruments.com" PROGRAM="Traktor"></HEAD>""")
        appendLine("""<COLLECTION ENTRIES="${validTracks.size}">""")

        // Generate collection entries
        validTracks.forEach { track ->
            appendLine(generateTrackEntry(track, currentDate))
        }

        appendLine("""</COLLECTION>""")
        appendLine("""<SETS ENTRIES="0"></SETS>""")
        appendLine("""<PLAYLISTS>""")
        appendLine("""<NODE TYPE="FOLDER" NAME="${dollarChar}ROOT">""")
        appendLine("""<SUBNODES COUNT="1">""")
        appendLine("""<NODE TYPE="PLAYLIST" NAME="${escapeXml(playlist.name)}">""")
        appendLine("""<PLAYLIST ENTRIES="${validTracks.size}" TYPE="LIST" UUID="${generateUUID()}">""")

        // Generate playlist entries
        validTracks.forEach { track ->
            appendLine(generatePlaylistEntry(track))
        }

        appendLine("""</PLAYLIST>""")
        appendLine("""</NODE>""")
        appendLine("""</SUBNODES>""")
        appendLine("""</NODE>""")
        appendLine("""</PLAYLISTS>""")
        appendLine("""<INDEXING></INDEXING>""")
        appendLine("""</NML>""")
    }
}

private fun generateTrackEntry(track: YmeTrack, currentDate: Date): String {
    val file = File(track.filePath)
    val directory = file.parent?.replace("/", "/:") ?: ""
    val fileName = file.name
    val volume = getVolumeName(track.filePath)

    return buildString {
        appendLine("""
                 <ENTRY MODIFIED_DATE="${dateFormat.format(currentDate)}" 
                        MODIFIED_TIME="${timeFormat.format(currentDate)}
                        AUDIO_ID="${generateAudioId()}
                        TITLE="${escapeXml(track.title)}" 
                        ARTIST="${escapeXml(track.artist)}">
                 """.trimIndent())
        appendLine("""
                 <LOCATION DIR="$directory" 
                        FILE="$fileName" 
                        VOLUME="$volume
                        VOLUMEID="$volume"></LOCATION>
                 """.trimIndent())
        appendLine("""<ALBUM OF_TRACKS="1" TRACK="1" TITLE=""></ALBUM>""")
        appendLine("""<MODIFICATION_INFO AUTHOR_TYPE="user"></MODIFICATION_INFO>""")
        appendLine("""
                 <INFO 
                    BITRATE="320000" 
                    GENRE="" 
                    LABEL="" 
                    COMMENT=""
                    COVERARTID="" 
                    KEY=""
                    PLAYCOUNT="0" 
                    PLAYTIME="${track.duration}
                    PLAYTIME_FLOAT="${track.duration}.0" 
                    RANKING="0" 
                    IMPORT_DATE="${dateFormat.format(currentDate)}" 
                    LAST_PLAYED=""
                    RELEASE_DATE="" 
                    FLAGS="0" 
                    COLOR="0"></INFO>
                 """.trimIndent())
        appendLine("""<TEMPO BPM="120.000000" BPM_QUALITY="100.000000"></TEMPO>""")
        appendLine("""<LOUDNESS PEAK_DB="-0.000000" PERCEIVED_DB="0.000000" ANALYZED_DB="0.000000"></LOUDNESS>""")
        appendLine("""<MUSICAL_KEY VALUE="0"></MUSICAL_KEY>""")
        appendLine("""</ENTRY>""")
    }
}

private fun generatePlaylistEntry(track: YmeTrack): String {
    val file = File(track.filePath)
    val volume = getVolumeName(track.filePath)
    val fullPath = "$volume${track.filePath}"

    return buildString {
        appendLine("""<ENTRY>""")
        appendLine("""<PRIMARYKEY TYPE="TRACK KEY="$fullPath"></PRIMARYKEY>""")
        appendLine("""</ENTRY>""")
    }
}

private fun generateUUID(): String {
    return UUID.randomUUID().toString().replace("-", "")
}

private fun generateAudioId(): String {
    // Generate a placeholder audio ID - in real Traktor this is a hash of the audio content
    val random = Random()
    val bytes = ByteArray(128)
    random.nextBytes(bytes)
    return Base64.getEncoder().encodeToString(bytes).replace(Regex("[+/=]"), "")
}

private fun getVolumeName(filePath: String): String {
    return when {
        filePath.startsWith("/Users/") -> "Macintosh HD"
        filePath.startsWith("C:\\") -> "C:"
        else -> "Unknown Volume"
    }
}

private fun escapeXml(text: String): String {
    return text
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&apos;")
}