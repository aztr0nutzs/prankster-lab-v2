package com.pranksterlab.core.audio

import android.content.Context
import android.media.MediaPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.IOException

data class AudioPlaybackState(
    val isPlaying: Boolean = false,
    val currentSoundId: String? = null,
    val currentSoundTitle: String? = null,
    val lastError: String? = null
)

class AudioPlayerController(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    
    private val _playbackState = MutableStateFlow(AudioPlaybackState())
    val playbackState: StateFlow<AudioPlaybackState> = _playbackState.asStateFlow()

    /**
     * Plays a sound from the assets folder.
     * Returns true if playback started successfully, false if asset is missing or cannot be played.
     */
    fun playSound(assetPath: String, isLocalUri: Boolean = false, soundId: String? = null, soundTitle: String? = null, isLooping: Boolean = false): Boolean {
        stop()
        
        try {
            mediaPlayer = MediaPlayer().apply {
                if (isLocalUri) {
                    val file = java.io.File(assetPath)
                    if (file.exists()) {
                        val fis = java.io.FileInputStream(file)
                        setDataSource(fis.fd)
                        fis.close()
                    } else {
                        setDataSource(assetPath)
                    }
                } else {
                    val descriptor = context.assets.openFd(assetPath)
                    setDataSource(
                        descriptor.fileDescriptor,
                        descriptor.startOffset,
                        descriptor.length
                    )
                    descriptor.close()
                }
                
                setOnCompletionListener {
                    if (!isLooping) {
                        stop()
                    }
                }
                
                setOnErrorListener { _, what, extra ->
                    _playbackState.value = AudioPlaybackState(
                        isPlaying = false,
                        lastError = "Playback error: $what, $extra"
                    )
                    mediaPlayer?.release()
                    mediaPlayer = null
                    true // error handled
                }

                this.isLooping = isLooping
                prepare()
                start()
            }
            
            _playbackState.value = AudioPlaybackState(
                isPlaying = true,
                currentSoundId = soundId,
                currentSoundTitle = soundTitle,
                lastError = null
            )
            return true
            
        } catch (e: Exception) {
            e.printStackTrace()
            _playbackState.value = AudioPlaybackState(
                isPlaying = false,
                lastError = e.message ?: "Failed to play sound"
            )
            mediaPlayer?.release()
            mediaPlayer = null
            return false
        }
    }

    fun stop() {
        try {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.stop()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            mediaPlayer?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mediaPlayer = null
        _playbackState.value = AudioPlaybackState(isPlaying = false)
    }
    
    fun setVolume(value: Float) {
        try {
            mediaPlayer?.setVolume(value, value)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun release() {
        stop()
    }
    
    fun stopAll() {
        stop()
    }

    /**
     * Helper to statically check if asset exists without playing
     */
    fun doesAssetExist(assetPath: String): Boolean {
        return try {
            val stream = context.assets.open(assetPath)
            stream.close()
            true
        } catch (e: Exception) {
            false
        }
    }
}
