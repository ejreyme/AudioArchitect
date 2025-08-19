package com.joonyor.labs.audio.track

import com.joonyor.labs.audio.playlist.CuePoint

data class YmeTrack(
    val filePath: String = "",
    val title: String = "Unknown title",
    val artist: String = "Unknown artist",
    val tags: Set<YmeTag> = emptySet(),
    val duration: Int = 0,
    var album: String? = null,
    var genre: String? = null,
    var bpm: Double? = null,
    var key: String? = null,
    var cues: MutableList<CuePoint> = mutableListOf()
) {
    val isNew = filePath.isEmpty()
    val isNotNew = !isNew
    fun durationDisplay(): String {
        if (duration == 0) {
            return "Unknown duration"
        }
        val minutes = duration / 60
        val seconds = duration % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}

data class YmeTag(val name: String = "", val active: Boolean = false)

data class TrackEvent(
    val track: YmeTrack = YmeTrack(),
    val type: TrackEventType = TrackEventType.DEFAULT,
    val tag: YmeTag = YmeTag(),
)

enum class TrackEventType {
    DEFAULT,
    UPDATE,
    ADD_TAG,
    REMOVE_TAG,
}