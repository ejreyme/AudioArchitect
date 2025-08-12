package com.joonyor.labs.audio.player

import com.joonyor.labs.audio.track.YmeTrack
import kotlinx.coroutines.flow.Flow

interface AudioPlayerService {
    val trackPosition: Flow<Float>
    val isPlaying: Flow<Boolean>
    fun play(filePath: String)
    fun stop()
    fun pause()
    fun repeat(isRepeat: Boolean)
    fun volumeChange(value: Float)
    fun trackPositionChange(position: Float)
    fun exit()
}