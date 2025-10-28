package com.sealedstack.player

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.sealedstack.playlist.YmePlaylist
import com.sealedstack.track.YmeTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class AudioPlayerState(
    val isPlaying: MutableState<Boolean> = mutableStateOf(false),
    val trackPlaying: MutableState<YmeTrack> = mutableStateOf(YmeTrack()),
    val activeTrack: MutableState<YmeTrack> = mutableStateOf(YmeTrack()),
    val trackPosition: MutableState<Float> = mutableStateOf(0.0f),
    val mediaState: MutableState<MediaPlayerState> = mutableStateOf(MediaPlayerState.Stopped)
)

/**
 * ViewModel responsible for managing audio playback functionality and state.
 *
 * @property audioPlayerService Service managing the underlying logic for audio playback.
 */
class AudioPlayerViewModel(private val audioPlayerService: AudioPlayerService) {
    val scope = CoroutineScope(Dispatchers.IO)
    val playerState = AudioPlayerState()
    val trackQueue = Channel<YmeTrack>()

    init { audioPlayerEventHandlers() }

    /**
     * Handles various audio player events dispatched by the user or system.
     *
     * @param event The event representing the type of interaction with the audio player, such as play, pause, stop,
     * volume change, track position change, or repeat toggle. Contains associated data depending on the event type,
     * such as the track, volume level, track position, or repeat state.
     */
    fun onAudioPlayerEvent(event: AudioPlayerEvent) {
        println(event)
        when (event.type) {
            AudioPlayerEventType.PLAY -> onPlayClick(event.track)
            AudioPlayerEventType.PAUSE -> onPauseClick(event)
            AudioPlayerEventType.STOP -> onStopClick()
            AudioPlayerEventType.RESUME -> onResumeClick(event)
            AudioPlayerEventType.VOLUME -> onVolumeChange(event.volume)
            AudioPlayerEventType.POSITION -> onTrackPositionChange(event.trackPosition)
            AudioPlayerEventType.REPEAT -> onRepeatClick(event.isRepeat)
            AudioPlayerEventType.QUEUE -> onQueueEvent(event.track)
            else -> println("Unknown audio player event")
        }
    }

    private fun onResumeClick(event: AudioPlayerEvent) {
        scope.launch {
            audioPlayerService.pause()
            playerState.mediaState.value = MediaPlayerState.Playing
        }
    }

    private fun onQueueEvent(track: YmeTrack) {
        scope.launch { trackQueue.send(track) }
    }

    private fun audioPlayerEventHandlers() {
        scope.launch {
            audioPlayerService.trackPositionFlow.collect {
                delay(audioPlayerService.trackPositionDelay)
                playerState.trackPosition.value = it
            }
        }

        scope.launch {
            audioPlayerService.playingFlow.collect {
                playerState.isPlaying.value = it
                if (!it) { playNextTrack() }
            }
        }
    }

    private suspend fun playNextTrack() {
        val nextTrack = trackQueue.receive()
        if (nextTrack.isNotNew) { onPlayClick(nextTrack) }
    }

    private fun onRepeatClick(isRepeat: Boolean) {
        audioPlayerService.repeat(isRepeat)
    }

    private fun onTrackPositionChange(position: Float) {
        audioPlayerService.trackPositionChange(position)
    }

    private fun onPlayClick(track: YmeTrack) {
        scope.launch {
            audioPlayerService.play(track.filePath)
            playerState.activeTrack.value = track
            playerState.trackPlaying.value = track
            playerState.isPlaying.value = true
            playerState.mediaState.value = MediaPlayerState.Playing
        }
    }

    private fun onPauseClick(event: AudioPlayerEvent) {
        audioPlayerService.pause()
        playerState.isPlaying.value = false
        playerState.mediaState.value = MediaPlayerState.Paused
    }

    private fun onStopClick() {
        audioPlayerService.stop()
        playerState.activeTrack.value = YmeTrack()
        playerState.trackPlaying.value = YmeTrack()
        playerState.isPlaying.value = false
        playerState.mediaState.value = MediaPlayerState.Stopped
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

sealed class MediaPlayerState(
    val label: String = "stopped",
    val availableActions: Set<AudioPlayerEventType> = setOf(AudioPlayerEventType.PLAY)
) {
    object Paused: MediaPlayerState(
        "paused",
        setOf(
            AudioPlayerEventType.RESUME
        )
    )
    object Playing: MediaPlayerState(
        "playing",
        setOf(
            AudioPlayerEventType.PAUSE,
            AudioPlayerEventType.STOP
        )
    )
    object Stopped: MediaPlayerState(
        "stopped",
        setOf(AudioPlayerEventType.PLAY)
    )
}

fun buttonActionState(
    selectedTrack: YmeTrack,
    trackPlaying: YmeTrack,
    mediaPlayerState: MediaPlayerState,
): AudioPlayerEvent {

    return when(trackPlaying.isNotNew && (trackPlaying == selectedTrack)) {
        true -> { // only apply to when trackPlaying with data is selectedTrack
            when(mediaPlayerState) {
                MediaPlayerState.Paused -> AudioPlayerEvent(track = selectedTrack, type = AudioPlayerEventType.RESUME)
                MediaPlayerState.Playing -> AudioPlayerEvent(track = selectedTrack, type = AudioPlayerEventType.PAUSE)
                MediaPlayerState.Stopped -> AudioPlayerEvent(track = selectedTrack, type = AudioPlayerEventType.PLAY)
            }
        }
        false -> AudioPlayerEvent(track = selectedTrack, type = AudioPlayerEventType.PLAY)
    }
}

fun buttonTextState(
    trackRow: YmeTrack,
    trackPlaying: YmeTrack,
    mediaPlayerState: MediaPlayerState
): String {
    val text = when(trackPlaying == trackRow) {
        true -> {
            when(mediaPlayerState) {
                MediaPlayerState.Paused -> "Resume"
                MediaPlayerState.Stopped -> "Play"
                MediaPlayerState.Playing -> "Pause"
            }
        }
        false -> "Play"
    }
    return text
}

enum class AudioPlayerEventType {
    DEFAULT,
    PLAY,
    STOP,
    PAUSE,
    RESUME,
    POSITION,
    QUEUE,
    VOLUME,
    REPEAT,
}


