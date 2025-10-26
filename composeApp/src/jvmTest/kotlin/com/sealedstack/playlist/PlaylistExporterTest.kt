package com.sealedstack.playlist

import com.sealedstack.track.YmeTrack
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import kotlin.test.assertEquals

class PlaylistExporterTest {
    @TempDir
    lateinit var tempDir: Path

    @BeforeEach
    fun setUp() {
    }

    @Test
    fun testAsM3u() {
        // arrange
        val playlistName = "test"
        val extension = "m3u"
        val ymeTracks = listOf(YmeTrack(), YmeTrack())
        val ymePlaylist = YmePlaylist(name = playlistName, ymeTracks = ymeTracks)
        // act
        val result = PlaylistExporter.asM3u(ymePlaylist)
        // assert
        val actual = tempDir.resolve("$playlistName.$extension").toString()
        val actualFile = actual.split("/")
        val out = actualFile[actualFile.size-1]
        assertEquals(expected = "$playlistName.$extension", actual = out)
    }

    @Test
    fun testAsNML() {
    }

    @Test
    fun testAsXML() {
    }
}