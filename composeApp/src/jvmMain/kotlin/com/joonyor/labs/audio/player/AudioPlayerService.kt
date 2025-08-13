package com.joonyor.labs.audio.player

import kotlinx.coroutines.flow.Flow

/**
 * Represents a service interface for playing audio tracks and managing playback controls.
 */
interface AudioPlayerService {
    val trackPosition: Flow<Float>
    val isPlaying: Flow<Boolean>
    val trackPositionDelay: Long
    fun play(filePath: String)
    fun stop()
    fun pause()
    fun repeat(isRepeat: Boolean)
    fun volumeChange(value: Float)
    fun trackPositionChange(position: Float)
    fun exit()
}