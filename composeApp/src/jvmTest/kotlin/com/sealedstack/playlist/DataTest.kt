package com.sealedstack.playlist

import com.sealedstack.track.YmeTag
import com.sealedstack.track.YmeTrack
import kotlin.test.Test
import kotlin.test.assertEquals

class DataTest {

    @Test
    fun testAddRemoveTag() {
        // arrange 1
        val track = YmeTrack(artist = "me")
        val tag1 = YmeTag("house")
        // act 1
        var trackAfterAdd = track.addTag(tag1)
        // assert 1
        assertEquals(1, trackAfterAdd.tags.size)
        // act 2
        val trackAfterRemove = track.removeTag(tag1)
        // assert 2
        assertEquals(0, trackAfterRemove.tags.size)
        val tag2 = YmeTag("jazz")
        // act 3
        trackAfterAdd = trackAfterRemove.addTag(tag2)
        // assert 3
        assertEquals("jazz",trackAfterAdd.tags.toList()[0].name)

    }

    @Test
    fun testPlaylistRemoveTrack() {
        val t1 = YmeTrack(artist = "me1")
        val t2 = YmeTrack(artist = "me2")
        val p1 = YmePlaylist(tracks = listOf(t1,t2))
        assertEquals(2, p1.tracks.size)
        val updatedPlaylist = p1.removeTrack(t1)
        assertEquals(1, updatedPlaylist.tracks.size)
        assertEquals("me2",updatedPlaylist.tracks[0].artist)
    }

    @Test
    fun testPlaylistAddTrack() {
        // arrange
        val t1 = YmeTrack(artist = "me1")
        val p1 = YmePlaylist(tracks = listOf(t1))
        assertEquals(1, p1.tracks.size)
        assertEquals("me1",p1.tracks[0].artist)
        // act
        val t2 = YmeTrack(artist = "me2")
        val updatedPlaylist = p1.addTrack(t2)
        assertEquals(2, updatedPlaylist.tracks.size)
        assertEquals("me2",updatedPlaylist.tracks[1].artist)
        val t3 = YmeTrack(artist = "me3")
        assertEquals(3, updatedPlaylist.addTrack(t3).tracks.size)
    }
}