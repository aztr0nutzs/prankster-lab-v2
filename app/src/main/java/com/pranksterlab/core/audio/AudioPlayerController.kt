package com.pranksterlab.core.audio

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import com.pranksterlab.core.model.PrankSound
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

private const val TAG = "AudioPlayerController"

private val SUPPORTED_EXTENSIONS = setOf(
    "mp3", "ogg", "oga", "wav", "m4a", "aac", "mp4", "3gp", "flac", "opus", "amr"
)

sealed class PlaybackPhase {
    object Idle : PlaybackPhase()
    data class Loading(val soundId: String?) : PlaybackPhase()
    data class Playing(val soundId: String?) : PlaybackPhase()
    object Stopped : PlaybackPhase()
    data class Error(val soundId: String?, val message: String) : PlaybackPhase()
}

data class AudioPlaybackState(
    val isPlaying: Boolean = false,
    val isLoading: Boolean = false,
    val currentSoundId: String? = null,
    val currentSoundTitle: String? = null,
    val lastError: String? = null,
    val lastErrorSoundId: String? = null,
    val phase: PlaybackPhase = PlaybackPhase.Idle
)

class AudioPlayerController(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    private var masterVolume: Float = 1.0f

    private val invalidSounds = mutableSetOf<String>()
    private val _invalidSoundIds = MutableStateFlow<Set<String>>(emptySet())
    val invalidSoundIds: StateFlow<Set<String>> = _invalidSoundIds.asStateFlow()

    private val _playbackState = MutableStateFlow(AudioPlaybackState())
    val playbackState: StateFlow<AudioPlaybackState> = _playbackState.asStateFlow()

    /**
     * Convenience overload that validates and plays a PrankSound, routing to the right source.
     */
    fun playPrankSound(sound: PrankSound, isLooping: Boolean = false): Boolean {
        val isLocal = sound.isCustom || sound.localUri != null
        val path = sound.localUri ?: sound.assetPath
        return playSound(
            assetPath = path,
            isLocalUri = isLocal,
            soundId = sound.id,
            soundTitle = sound.name,
            isLooping = isLooping
        )
    }

    fun canPlayPrankSound(sound: PrankSound): Boolean {
        if (sound.id in invalidSounds) return false
        val isLocal = sound.isCustom || sound.localUri != null
        val path = sound.localUri ?: sound.assetPath
        val validation = validateSource(path, isLocal)
        if (validation != null) {
            Log.w(TAG, "Preflight rejected soundId=${sound.id} path='$path' reason=$validation")
            markInvalid(sound.id)
            return false
        }
        return true
    }

    /**
     * Plays a sound. Performs validation up front and refuses to play if the asset
     * is missing, blank, has an unsupported extension, or cannot be decoded.
     * Returns true if playback started successfully, false otherwise.
     */
    fun playSound(
        assetPath: String,
        isLocalUri: Boolean = false,
        soundId: String? = null,
        soundTitle: String? = null,
        isLooping: Boolean = false
    ): Boolean {
        stopInternal(updateState = false)

        val validation = validateSource(assetPath, isLocalUri)
        if (validation != null) {
            Log.w(TAG, "Refusing to play soundId=$soundId path='$assetPath' reason=$validation")
            soundId?.let { markInvalid(it) }
            _playbackState.value = AudioPlaybackState(
                isPlaying = false,
                isLoading = false,
                currentSoundId = null,
                currentSoundTitle = null,
                lastError = validation,
                lastErrorSoundId = soundId,
                phase = PlaybackPhase.Error(soundId, validation)
            )
            return false
        }

        _playbackState.value = AudioPlaybackState(
            isPlaying = false,
            isLoading = true,
            currentSoundId = soundId,
            currentSoundTitle = soundTitle,
            phase = PlaybackPhase.Loading(soundId)
        )

        return try {
            val player = MediaPlayer()
            if (isLocalUri) {
                val file = File(assetPath)
                if (file.exists()) {
                    java.io.FileInputStream(file).use { fis ->
                        player.setDataSource(fis.fd)
                    }
                } else {
                    player.setDataSource(assetPath)
                }
            } else {
                context.assets.openFd(assetPath).use { descriptor ->
                    player.setDataSource(
                        descriptor.fileDescriptor,
                        descriptor.startOffset,
                        descriptor.length
                    )
                }
            }

            player.setOnCompletionListener {
                if (!isLooping) {
                    Log.d(TAG, "Completion soundId=$soundId path='$assetPath'")
                    stopInternal(updateState = true)
                }
            }

            player.setOnErrorListener { _, what, extra ->
                val msg = "MediaPlayer error what=$what extra=$extra"
                Log.e(TAG, "Playback error soundId=$soundId path='$assetPath' $msg")
                soundId?.let { markInvalid(it) }
                releasePlayerSafely()
                _playbackState.value = AudioPlaybackState(
                    isPlaying = false,
                    isLoading = false,
                    lastError = msg,
                    lastErrorSoundId = soundId,
                    phase = PlaybackPhase.Error(soundId, msg)
                )
                true
            }

            player.isLooping = isLooping
            player.setVolume(masterVolume, masterVolume)
            player.prepare()
            player.start()

            mediaPlayer = player

            Log.i(TAG, "Playing soundId=$soundId path='$assetPath' looping=$isLooping")
            _playbackState.value = AudioPlaybackState(
                isPlaying = true,
                isLoading = false,
                currentSoundId = soundId,
                currentSoundTitle = soundTitle,
                lastError = null,
                phase = PlaybackPhase.Playing(soundId)
            )
            true
        } catch (e: Exception) {
            val msg = e.message ?: e.javaClass.simpleName
            Log.e(TAG, "Exception starting playback soundId=$soundId path='$assetPath' reason=$msg", e)
            soundId?.let { markInvalid(it) }
            releasePlayerSafely()
            _playbackState.value = AudioPlaybackState(
                isPlaying = false,
                isLoading = false,
                lastError = msg,
                lastErrorSoundId = soundId,
                phase = PlaybackPhase.Error(soundId, msg)
            )
            false
        }
    }

    /**
     * Returns null if source is valid, otherwise an error reason string.
     */
    private fun validateSource(path: String, isLocalUri: Boolean): String? {
        if (path.isBlank()) return "Empty asset path"

        val ext = path.substringAfterLast('.', "").lowercase()
        if (ext.isEmpty()) return "Missing file extension"
        if (ext !in SUPPORTED_EXTENSIONS) return "Unsupported format: .$ext"

        if (isLocalUri) {
            // Allow content:// URIs through (we cannot easily stat them); just block obvious empties.
            if (path.startsWith("content://") || path.startsWith("file://")) {
                return null
            }
            val file = File(path)
            if (!file.exists()) return "File not found"
            if (file.length() <= 0L) return "File is empty"
        } else {
            // Bundled asset
            try {
                context.assets.open(path).use { /* opens then closes */ }
            } catch (e: Exception) {
                return "Asset not found in bundle"
            }
        }

        return null
    }

    private fun markInvalid(soundId: String) {
        if (invalidSounds.add(soundId)) {
            _invalidSoundIds.value = invalidSounds.toSet()
        }
    }

    fun isInvalid(soundId: String): Boolean = soundId in invalidSounds

    fun clearInvalid(soundId: String) {
        if (invalidSounds.remove(soundId)) {
            _invalidSoundIds.value = invalidSounds.toSet()
        }
    }

    fun stop() {
        stopInternal(updateState = true)
    }

    private fun stopInternal(updateState: Boolean) {
        releasePlayerSafely()
        if (updateState) {
            _playbackState.value = AudioPlaybackState(
                isPlaying = false,
                isLoading = false,
                currentSoundId = null,
                currentSoundTitle = null,
                lastError = null,
                phase = PlaybackPhase.Stopped
            )
        }
    }

    private fun releasePlayerSafely() {
        val player = mediaPlayer ?: return
        try {
            if (player.isPlaying) player.stop()
        } catch (e: IllegalStateException) {
            // double-stop or stop-from-error state: safe to ignore
        } catch (e: Exception) {
            Log.w(TAG, "stop() threw ${e.message}")
        }
        try {
            player.reset()
        } catch (_: Exception) {}
        try {
            player.release()
        } catch (e: Exception) {
            Log.w(TAG, "release() threw ${e.message}")
        }
        mediaPlayer = null
    }

    /**
     * Master volume between 0.0 and 1.0. Applied to the active and future MediaPlayers.
     */
    fun setMasterVolume(value: Float) {
        masterVolume = value.coerceIn(0f, 1f)
        try {
            mediaPlayer?.setVolume(masterVolume, masterVolume)
        } catch (e: Exception) {
            Log.w(TAG, "setVolume failed ${e.message}")
        }
    }

    fun getMasterVolume(): Float = masterVolume

    @Deprecated("Use setMasterVolume", ReplaceWith("setMasterVolume(value)"))
    fun setVolume(value: Float) = setMasterVolume(value)

    fun release() {
        stop()
    }

    /**
     * Global Stop All. Stops the current MediaPlayer, clears state, releases resources.
     * Single controller backs every screen including Sound Forge preview, so this stops them all.
     */
    fun stopAll() {
        Log.i(TAG, "stopAll invoked")
        stopInternal(updateState = true)
    }

    /**
     * Helper to statically check if asset exists without playing.
     */
    fun doesAssetExist(assetPath: String): Boolean {
        return try {
            context.assets.open(assetPath).use { }
            true
        } catch (e: Exception) {
            false
        }
    }
}
