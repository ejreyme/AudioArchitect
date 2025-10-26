package com.sealedstack.player

import kotlinx.coroutines.flow.StateFlow

/**
 * Represents a service interface for playing audio tracks and managing playback controls.
 */
interface AudioPlayerService {
    val trackPositionFlow: StateFlow<Float>
    val playingFlow: StateFlow<Boolean>
    val trackPositionDelay: Long
    fun play(filePath: String)
    fun stop()
    fun pause()
    fun repeat(isRepeat: Boolean)
    fun volumeChange(value: Float)
    fun trackPositionChange(position: Float)
    fun exit()
}