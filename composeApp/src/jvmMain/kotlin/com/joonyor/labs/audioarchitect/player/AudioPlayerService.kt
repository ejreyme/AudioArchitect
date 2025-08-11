package com.joonyor.labs.audioarchitect.player

import com.joonyor.labs.audioarchitect.data.YmeTrack
import kotlinx.coroutines.flow.Flow

interface AudioPlayerService {
    val trackPosition: Flow<Float>
    fun play(filePath: String)
    fun stop()
    fun pause()
    fun repeat(isRepeat: Boolean)
    fun volumeChange(value: Float)
    fun trackPositionChange(position: Float)
    fun exit()
}