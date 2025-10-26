package com.sealedstack.player

import com.sealedstack.loggerFor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter
import uk.co.caprica.vlcj.player.component.AudioPlayerComponent
import kotlin.system.exitProcess

// https://www.capricasoftware.co.uk
/**
 * Service class for handling audio playback functionality using VLC media player.
 * Implements the `AudioPlayerService` interface to provide a consistent API
 * for controlling audio playback and tracking playback state.
 *
 * The class leverages VLC's `AudioPlayerComponent` to control media playback
 * and implements coroutine-based asynchronous operations for handling events
 * in the playback lifecycle.
 *
 * @constructor Creates a new instance of `VlcAudioPlayerService`.
 */
class VlcAudioPlayerService() : AudioPlayerService {
    private val logger = loggerFor(javaClass)
    private val vlcAudioPlayer = AudioPlayerComponent()
    private val scope = CoroutineScope(Dispatchers.IO)
    override val trackPositionDelay: Long = 0L

    private val _playingFlow = MutableStateFlow(false)
    override val playingFlow = _playingFlow.asStateFlow()
    private val _trackPositionFlow = MutableStateFlow(0.0f)
    override val trackPositionFlow = _trackPositionFlow.asStateFlow()

    init {
        vlcAudioPlayer.mediaPlayer().audio().setVolume(50)
        setupMediaPlayerEventListener()
    }

    override fun play(filePath: String) {
        println("play: $filePath")
        vlcAudioPlayer.mediaPlayer()?.media()?.play(filePath)
    }

    override fun stop() {
        println("stop")
        vlcAudioPlayer.mediaPlayer()?.controls()?.stop()
    }

    override fun pause() {
        println("pause")
        vlcAudioPlayer.mediaPlayer()?.controls()?.pause()
    }

    override fun repeat(isRepeat: Boolean) {
        println("repeat: $isRepeat")
        vlcAudioPlayer.mediaPlayer()?.controls()?.repeat = isRepeat
    }

    override fun volumeChange(value: Float) {
        println("volumeChange: $value")
        val volume = (value * 100).toInt()
        try {
            vlcAudioPlayer.mediaPlayer().audio().setVolume(volume)
        } catch (e: Exception) {
            vlcAudioPlayer.mediaPlayer().audio().setVolume(50)
            logger.error("Error setting volume: $e")
        }
    }

    override fun trackPositionChange(position: Float) {
        println("trackPositionChange: $position")
        vlcAudioPlayer.mediaPlayer().controls().setPosition(position)
    }

    override fun exit() {
        println("exit")
        vlcAudioPlayer.mediaPlayer().submit {
            vlcAudioPlayer.mediaPlayer().release()
            exitProcess(0)
        }
    }

    private fun setupMediaPlayerEventListener() {
        vlcAudioPlayer.mediaPlayer().events().addMediaPlayerEventListener(object : MediaPlayerEventAdapter() {
            override fun playing(mediaPlayer: MediaPlayer?) {
                println("player event playing")
                scope.launch {
                    _playingFlow.emit(true)
                }
            }

            // positionChanged is a percentage between 0.0 and 1.0
            override fun positionChanged(mediaPlayer: MediaPlayer?, newPosition: Float) {
                super.positionChanged(mediaPlayer, newPosition)
                println("player event positionChanged: $newPosition")

                scope.launch {
                    playingFlow.collect {
                        if (it) {
                            _trackPositionFlow.emit(newPosition)
                        }
                        delay(trackPositionDelay)
                    }
                }
            }

            override fun paused(mediaPlayer: MediaPlayer?) {
                println("player event paused")
                scope.launch { _playingFlow.emit(false) }
            }

            override fun finished(mediaPlayer: MediaPlayer?) {
                println("player event finished")
                scope.launch { _playingFlow.emit(false) }
            }

            override fun error(mediaPlayer: MediaPlayer?) {
                println("player event error")
                exitProcess(1)
            }
        })
    }
}