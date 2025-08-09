package com.joonyor.labs.audioarchitect.player

import uk.co.caprica.vlcj.player.component.AudioPlayerComponent

interface AudioPlayerService {
    fun play(filePath: String)
    fun stop()
    fun pause()
    fun volumeChange(value: Float)
}

class YmeAudioPlayerService : AudioPlayerService {
    private var mediaPlayerComponent: AudioPlayerComponent = AudioPlayerComponent()

    init {
        mediaPlayerComponent.mediaPlayer().audio().setVolume(50)
    }

    override fun play(filePath: String) {
        print("play: $filePath")
        mediaPlayerComponent.mediaPlayer()?.media()?.play(filePath)
    }

    override fun stop() {
        print("stop")
        mediaPlayerComponent.mediaPlayer()?.controls()?.stop()
    }

    override fun pause() {
        print("pause")
        mediaPlayerComponent.mediaPlayer()?.controls()?.pause()
    }

    override fun volumeChange(value: Float) {
        val volume = (value * 100).toInt()
        try {
            mediaPlayerComponent.mediaPlayer().audio().setVolume(volume)
        } catch (e: Exception) {
            mediaPlayerComponent.mediaPlayer().audio().setVolume(50)
        }
    }
}