package com.joonyor.labs.audio.playlist

import com.joonyor.labs.audio.track.YmeTrack
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path

class PlaylistExporterTest {
    private lateinit var exporter: PlaylistExporter

    @TempDir
    lateinit var tempDir: Path

    @BeforeEach
    fun setUp() {
        exporter = PlaylistExporter(tempDir.toString())
    }

    @Test
    fun testAsM3u() {
        // arrange
        val playlistName = "test"
        val extension = "m3u"
        val tracks = listOf(YmeTrack(), YmeTrack())
        val playlist = YmePlaylist(name = playlistName, tracks = tracks)
        // act
        val result = exporter.asM3u(playlist)
        // assert
        val expectedPath = tempDir.resolve("$playlistName.$extension").toString()
        assertEquals(expectedPath, result)
    }

    @Test
    fun testAsNML() {
    }

    @Test
    fun testAsXML() {
    }
}