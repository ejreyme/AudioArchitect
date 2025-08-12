package com.joonyor.labs.audio.player

import com.joonyor.labs.audio.track.YmeTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.jetbrains.skiko.DefaultConsoleLogger
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter
import uk.co.caprica.vlcj.player.component.AudioPlayerComponent
import java.util.concurrent.BlockingQueue
import kotlin.system.exitProcess

// https://www.capricasoftware.co.uk
class VlcAudioPlayerService() : AudioPlayerService {
    private val vlcAudioPlayer = AudioPlayerComponent()
    val scope = CoroutineScope(Dispatchers.IO)
    val trackPositionChannel = Channel<Float>()
    val isPlayingChannel = Channel<Boolean>()

    init {
        vlcAudioPlayer.mediaPlayer().audio().setVolume(50)
        setupMediaPlayerEventListener()
    }

    override val trackPosition: Flow<Float> = trackPositionChannel.receiveAsFlow()
    override val isPlaying: Flow<Boolean> = isPlayingChannel.receiveAsFlow()

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
        vlcAudioPlayer.mediaPlayer().controls().setPosition(position)
    }

    override fun exit() {
        vlcAudioPlayer.mediaPlayer().submit {
            vlcAudioPlayer.mediaPlayer().release()
            exitProcess(0)
        }
    }

    private fun setupMediaPlayerEventListener() {
        vlcAudioPlayer.mediaPlayer().events().addMediaPlayerEventListener(object : MediaPlayerEventAdapter() {
            override fun playing(mediaPlayer: MediaPlayer?) {
                scope.launch {
                    println("player event playing")
                    isPlayingChannel.send(true)
                }
            }

            // positionChanged is a percentage between 0.0 and 1.0
            override fun positionChanged(mediaPlayer: MediaPlayer?, newPosition: Float) {
                super.positionChanged(mediaPlayer, newPosition)
                scope.launch {
                    println("player event positionChanged: $newPosition")
                    trackPositionChannel.send(newPosition)
                }
            }

            override fun paused(mediaPlayer: MediaPlayer?) {
                println("player event paused")
                scope.launch { isPlayingChannel.send(false) }
            }

            override fun finished(mediaPlayer: MediaPlayer?) {
                println("player event finished")
                scope.launch { isPlayingChannel.send(false) }
            }

            override fun error(mediaPlayer: MediaPlayer?) {
                println("player event error")
                exitProcess(1)
            }
        })
    }
}
