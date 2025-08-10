package com.joonyor.labs.audioarchitect.player

interface AudioPlayerService {
    fun play(filePath: String)
    fun stop()
    fun pause()
    fun volumeChange(value: Float)
}
