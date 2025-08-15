package com.joonyor.labs.audio.player

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.joonyor.labs.audio.loggerFor
import com.joonyor.labs.audio.playlist.YmePlaylist
import com.joonyor.labs.audio.track.YmeTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class AudioPlayerState(
    val isPlaying: MutableState<Boolean> = mutableStateOf(false),
    val trackPlaying: MutableState<YmeTrack> = mutableStateOf(YmeTrack()),
    val activeTrack: MutableState<YmeTrack> = mutableStateOf(YmeTrack()),
    val trackPosition: MutableState<Float> = mutableStateOf(0.0f)
)

/**
 * ViewModel responsible for managing audio playback functionality and state.
 *
 * @property audioPlayerService Service managing the underlying logic for audio playback.
 */
class AudioPlayerViewModel(private val audioPlayerService: AudioPlayerService) {
    private val logger = loggerFor(javaClass)
    val scope = CoroutineScope(Dispatchers.IO)
    val playerState = AudioPlayerState()
    val trackQueue = Channel<YmeTrack>()

    init {
        audioPlayerEventHandlers()
    }

    /**
     * Handles various audio player events dispatched by the user or system.
     *
     * @param event The event representing the type of interaction with the audio player, such as play, pause, stop,
     * volume change, track position change, or repeat toggle. Contains associated data depending on the event type,
     * such as the track, volume level, track position, or repeat state.
     */
    fun onAudioPlayerEvent(event: AudioPlayerEvent) {
        when (event.type) {
            AudioPlayerEventType.PLAY -> {
                onPlayClick(event.track)
            }
            AudioPlayerEventType.PAUSE -> {
                onPauseClick()
            }
            AudioPlayerEventType.STOP -> {
                onStopClick()
            }
            AudioPlayerEventType.VOLUME -> {
                onVolumeChange(event.volume)
            }
            AudioPlayerEventType.TRACK_POSITION -> {
                onTrackPositionChange(event.trackPosition)
            }
            AudioPlayerEventType.REPEAT -> {
                onRepeatClick(event.isRepeat)
            }
            AudioPlayerEventType.QUEUE -> {
                onQueueEvent(event.track)
            }
            else -> {
                logger.debug("Unknown audio player event")
            }
        }
    }

    private fun onQueueEvent(track: YmeTrack) {
        logger.debug("onQueueEvent: {}", track)
        scope.launch { trackQueue.send(track) }
    }

    private fun audioPlayerEventHandlers() {
        scope.launch {
            audioPlayerService.trackPosition.collect {
                delay(audioPlayerService.trackPositionDelay)
                logger.debug("collecting trackPosition: $it")
                playerState.trackPosition.value = it
            }
        }

        scope.launch {
            audioPlayerService.isPlaying.collect {
                logger.debug("collecting isPlaying: $it")
                playerState.isPlaying.value = it
                if (!it) {
                    playNextTrack()
                }
            }
        }
    }

    private suspend fun playNextTrack() {
        val nextTrack = trackQueue.receive()
        if (nextTrack.isNotNew) {
            onPlayClick(nextTrack)
        }
    }

    private fun onRepeatClick(isRepeat: Boolean) {
        audioPlayerService.repeat(isRepeat)
    }

    private fun onTrackPositionChange(position: Float) {
        audioPlayerService.trackPositionChange(position)
    }

    private fun onPlayClick(track: YmeTrack) {
        audioPlayerService.play(track.filePath)
        playerState.activeTrack.value = track
        playerState.trackPlaying.value = track
        playerState.isPlaying.value = true
    }

    private fun onStopClick() {
        audioPlayerService.stop()
        playerState.activeTrack.value = YmeTrack()
        playerState.trackPlaying.value = YmeTrack()
        playerState.isPlaying.value = false
    }

    private fun onPauseClick() {
        audioPlayerService.pause()
        playerState.isPlaying.value = false
    }

    private fun onVolumeChange(value: Float) {
        audioPlayerService.volumeChange(value)
    }
}


data class AudioPlayerEvent(
    val playlist: YmePlaylist = YmePlaylist(),
    val track: YmeTrack = YmeTrack(),
    val type: AudioPlayerEventType = AudioPlayerEventType.DEFAULT,
    val volume: Float = 0.0f,
    val trackPosition: Float = 0.0f,
    val isRepeat: Boolean = false,
)

enum class AudioPlayerEventType {
    DEFAULT,
    PLAY,
    STOP,
    PAUSE,
    SKIP_FORWARD,
    SKIP_BACK,
    QUEUE,
    VOLUME,
    TRACK_POSITION,
    REPEAT,
    ADD_TO_PLAYLIST,
    REMOVE_FROM_PLAYLIST
}