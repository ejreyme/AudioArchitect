package com.joonyor.labs.audio.track

import com.joonyor.labs.audio.config.AppConfiguration
import com.joonyor.labs.audio.loggerFor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.jaudiotagger.audio.AudioFile
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File

class TrackService {
    private val logger = loggerFor(javaClass)

    fun loadTracksFromPath(pathname: String = AppConfiguration.LIBRARY_ROOT_PATH): List<YmeTrack> {
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