package com.joonyor.labs.audioarchitect.player

import uk.co.caprica.vlcj.player.component.AudioPlayerComponent

// https://www.capricasoftware.co.uk
class VlcAudioPlayerService : AudioPlayerService {
    private var vlcAudioPlayer: AudioPlayerComponent = AudioPlayerComponent()

    init {
        vlcAudioPlayer.mediaPlayer().audio().setVolume(50)
//        vlcAudioPlayer.timeChanged()
    }

    override fun play(filePath: String) {
        print("play: $filePath")
        vlcAudioPlayer.mediaPlayer()?.media()?.play(filePath)
    }

    override fun stop() {
        print("stop")
        vlcAudioPlayer.mediaPlayer()?.controls()?.stop()
    }

    override fun pause() {
        print("pause")
        vlcAudioPlayer.mediaPlayer()?.controls()?.pause()
    }

    override fun volumeChange(value: Float) {
        val volume = (value * 100).toInt()
        try {
            vlcAudioPlayer.mediaPlayer().audio().setVolume(volume)
        } catch (e: Exception) {
            vlcAudioPlayer.mediaPlayer().audio().setVolume(50)
        }
    }

    // TODO events listener VLC MediaPlayerEventListener
}