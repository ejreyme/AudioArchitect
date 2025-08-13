package com.joonyor.labs.audio.player

import com.joonyor.labs.audio.loggerFor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
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
    val scope = CoroutineScope(Dispatchers.IO)
    val trackPositionChannel = Channel<Float>()
    val isPlayingChannel = Channel<Boolean>()

    init {
        vlcAudioPlayer.mediaPlayer().audio().setVolume(50)
        setupMediaPlayerEventListener()
    }

    override val trackPositionDelay: Long = 100
    override val trackPosition: Flow<Float> = trackPositionChannel.receiveAsFlow()
    override val isPlaying: Flow<Boolean> = isPlayingChannel.receiveAsFlow()

    override fun play(filePath: String) {
        logger.debug("play: $filePath")
        vlcAudioPlayer.mediaPlayer()?.media()?.play(filePath)
    }

    override fun stop() {
        logger.debug("stop")
        vlcAudioPlayer.mediaPlayer()?.controls()?.stop()
    }

    override fun pause() {
        logger.debug("pause")
        vlcAudioPlayer.mediaPlayer()?.controls()?.pause()
    }

    override fun repeat(isRepeat: Boolean) {
        logger.debug("repeat: $isRepeat")
        vlcAudioPlayer.mediaPlayer()?.controls()?.repeat = isRepeat
    }

    override fun volumeChange(value: Float) {
        logger.debug("volumeChange: $value")
        val volume = (value * 100).toInt()
        try {
            vlcAudioPlayer.mediaPlayer().audio().setVolume(volume)
        } catch (e: Exception) {
            vlcAudioPlayer.mediaPlayer().audio().setVolume(50)
            logger.error("Error setting volume: $e")
        }
    }

    override fun trackPositionChange(position: Float) {
        logger.debug("trackPositionChange: $position")
        vlcAudioPlayer.mediaPlayer().controls().setPosition(position)
    }

    override fun exit() {
        logger.debug("exit")
        vlcAudioPlayer.mediaPlayer().submit {
            vlcAudioPlayer.mediaPlayer().release()
            exitProcess(0)
        }
    }

    private fun setupMediaPlayerEventListener() {
        vlcAudioPlayer.mediaPlayer().events().addMediaPlayerEventListener(object : MediaPlayerEventAdapter() {
            override fun playing(mediaPlayer: MediaPlayer?) {
                logger.debug("player event playing")
                scope.launch { isPlayingChannel.send(true) }
            }

            // positionChanged is a percentage between 0.0 and 1.0
            override fun positionChanged(mediaPlayer: MediaPlayer?, newPosition: Float) {
                super.positionChanged(mediaPlayer, newPosition)
                logger.debug("player event positionChanged: $newPosition")
                scope.launch {
                    delay(trackPositionDelay)
                    if (isPlayingChannel.tryReceive().getOrNull() == false) trackPositionChannel.cancel()
                    trackPositionChannel.send(newPosition)
                }
            }

            override fun paused(mediaPlayer: MediaPlayer?) {
                logger.debug("player event paused")
                scope.launch { isPlayingChannel.send(false) }
            }

            override fun finished(mediaPlayer: MediaPlayer?) {
                logger.debug("player event finished")
                scope.launch { isPlayingChannel.send(false) }
            }

            override fun error(mediaPlayer: MediaPlayer?) {
                logger.debug("player event error")
                exitProcess(1)
            }
        })
    }
}
