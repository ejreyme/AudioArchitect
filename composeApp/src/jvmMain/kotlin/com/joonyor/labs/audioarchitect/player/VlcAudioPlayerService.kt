package com.joonyor.labs.audioarchitect.player

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter
import uk.co.caprica.vlcj.player.component.AudioPlayerComponent
import kotlin.system.exitProcess

// https://www.capricasoftware.co.uk
class VlcAudioPlayerService() : AudioPlayerService {
    private val vlcAudioPlayer = AudioPlayerComponent()
    val trackPositionChannel = Channel<Float>()
    val scope = CoroutineScope(Dispatchers.IO)

    override val trackPosition: Flow<Float> = trackPositionChannel.receiveAsFlow()

    init {
        vlcAudioPlayer.mediaPlayer().audio().setVolume(50)
        setupMediaPlayerEventListener()
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


    override fun repeat(isRepeat: Boolean) {
        vlcAudioPlayer.mediaPlayer()?.controls()?.repeat = isRepeat
    }

    override fun volumeChange(value: Float) {
        val volume = (value * 100).toInt()
        try {
            vlcAudioPlayer.mediaPlayer().audio().setVolume(volume)
        } catch (e: Exception) {
            vlcAudioPlayer.mediaPlayer().audio().setVolume(50)
            e.printStackTrace()
        }
    }

    override fun trackPositionChange(position: Float) {
        println("trackPositionChange: $position")
        TODO("Add position control in future")
//        vlcAudioPlayer.mediaPlayer().controls().setPosition(position)
    }

    override fun exit() {
        vlcAudioPlayer.mediaPlayer().submit {
            vlcAudioPlayer.mediaPlayer().release()
            exitProcess(0)
        }
    }


    private fun setupMediaPlayerEventListener() {
        vlcAudioPlayer.mediaPlayer().events().addMediaPlayerEventListener(object : MediaPlayerEventAdapter() {

            // percentage between 0.0 and 1.0
            override fun positionChanged(mediaPlayer: MediaPlayer?, newPosition: Float) {
                super.positionChanged(mediaPlayer, newPosition)
                scope.launch {
                    println("player event positionChanged: $newPosition")
                    trackPositionChannel.send(newPosition)
                }
            }

            override fun finished(mediaPlayer: MediaPlayer?) {
                println("player event finished")
//                exitProcess(0)
            }

            override fun error(mediaPlayer: MediaPlayer?) {
                println("player event error")
                exitProcess(1)
            }
        })
    }
}
