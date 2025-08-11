package com.joonyor.labs.audioarchitect.player

import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter
import uk.co.caprica.vlcj.player.component.AudioPlayerComponent
import java.lang.System.exit
import kotlin.system.exitProcess


// https://www.capricasoftware.co.uk
class VlcAudioPlayerService : AudioPlayerService {
    private val vlcAudioPlayer = AudioPlayerComponent()
    init {
        vlcAudioPlayer.mediaPlayer().events().addMediaPlayerEventListener(object : MediaPlayerEventAdapter() {
            override fun positionChanged(mediaPlayer: MediaPlayer?, newPosition: Float) {
                super.positionChanged(mediaPlayer, newPosition)
                println("positionChanged: $newPosition")
            }
            override fun finished(mediaPlayer: MediaPlayer?) {
                exitProcess(0)
            }

            override fun error(mediaPlayer: MediaPlayer?) {
                exitProcess(1)
            }
        })
        vlcAudioPlayer.mediaPlayer().audio().setVolume(50)
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

    fun exit() {
        vlcAudioPlayer.mediaPlayer().submit {
            vlcAudioPlayer.mediaPlayer().release()
            exitProcess(0)
        }
    }

    // TODO events listener VLC MediaPlayerEventListener
}

class MyEventHandler : MediaPlayerEventAdapter() {

}
