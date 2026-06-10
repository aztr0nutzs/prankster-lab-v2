package com.pranksterlab.bridge

import android.util.Log
import android.webkit.JavascriptInterface
import com.pranksterlab.core.audio.AudioPlayerController
import com.pranksterlab.core.model.PrankSound
import com.pranksterlab.core.repository.SoundRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

private const val TAG = "PrankstarWebBridge"
private const val SAFE_MODE_MAX_DURATION_MS = 5000L

class PrankstarWebBridge(
    private val audioPlayerController: AudioPlayerController,
    private val soundRepository: SoundRepository,
    private val onNavigate: (String) -> Unit,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val random = Random(System.currentTimeMillis())
    private var currentReactorMode: String = "core"

    @JavascriptInterface
    fun deployRandom() {
        scope.launch {
            val sound = selectRandomCatalogSound(preferSafe = true)
            if (sound == null) {
                Log.w(TAG, "deployRandom: no playable catalog sound found")
                return@launch
            }
            val started = audioPlayerController.playPrankSound(sound)
            Log.i(TAG, "deployRandom soundId=${sound.id} started=$started mode=$currentReactorMode")
        }
    }

    @JavascriptInterface
    fun stopAll() {
        scope.launch {
            audioPlayerController.stopAll()
            Log.i(TAG, "stopAll invoked")
        }
    }

    @JavascriptInterface
    fun openStash() = navigate("library")

    @JavascriptInterface
    fun openJokes() = navigate("voice_lab")

    @JavascriptInterface
    fun openForge() = navigate("forge")

    @JavascriptInterface
    fun openSystem() = navigate("system")

    @JavascriptInterface
    fun openTimer() = navigate("timer")

    @JavascriptInterface
    fun openRandomizer() = navigate("randomizer")

    @JavascriptInterface
    fun openPacks() = navigate("lab")

    @JavascriptInterface
    fun openMessages() = navigate("messages")

    @JavascriptInterface
    fun deployChain(count: Int) {
        val cappedCount = count.coerceIn(1, 3)
        scope.launch {
            repeat(cappedCount) { index ->
                val sound = selectRandomCatalogSound(preferSafe = true)
                if (sound == null) {
                    Log.w(TAG, "deployChain: no playable catalog sound found")
                    return@launch
                }
                val started = audioPlayerController.playPrankSound(sound)
                Log.i(TAG, "deployChain index=$index soundId=${sound.id} started=$started mode=$currentReactorMode")
                if (index < cappedCount - 1) delay(650L)
            }
        }
    }

    @JavascriptInterface
    fun playReactorCategory(category: String) {
        playSoundByCategory(category)
    }

    @JavascriptInterface
    fun setReactorMode(mode: String) {
        currentReactorMode = mode.ifBlank { "core" }
        Log.d(TAG, "setReactorMode=$currentReactorMode")
    }

    @JavascriptInterface
    fun setSafeMode(enabled: Boolean) {
        scope.launch {
            soundRepository.setSafeRandomModeDefault(enabled)
            Log.i(TAG, "setSafeMode enabled=$enabled mode=$currentReactorMode")
        }
    }

    @JavascriptInterface
    fun setMasterVolume(value: Double) {
        val capped = value.toFloat().coerceIn(0f, 0.9f)
        scope.launch {
            audioPlayerController.setMasterVolume(capped)
            soundRepository.setMasterVolume(capped)
            Log.i(TAG, "setMasterVolume value=$capped mode=$currentReactorMode")
        }
    }

    @JavascriptInterface
    fun logEvent(event: String) {
        Log.i(TAG, event)
    }

    @JavascriptInterface
    fun playRandomSound() = deployRandom()

    @JavascriptInterface
    fun playSoundByCategory(category: String) {
        scope.launch {
            val sound = selectByCategory(category)
            if (sound == null) {
                Log.w(TAG, "playSoundByCategory: no sound for category=$category")
                return@launch
            }
            val started = audioPlayerController.playPrankSound(sound)
            Log.i(TAG, "playSoundByCategory category=$category soundId=${sound.id} started=$started")
        }
    }

    @JavascriptInterface
    fun playRandomJoke() {
        playSoundByCategory("funny")
    }

    @JavascriptInterface
    fun playJokeByType(type: String) {
        playSoundByCategory(type)
    }

    @JavascriptInterface
    fun stopPlayback() = stopAll()

    fun release() {
        scope.cancel()
    }

    private fun navigate(route: String) {
        scope.launch {
            Log.i(TAG, "navigate=$route")
            onNavigate(route)
        }
    }

    private suspend fun selectRandomCatalogSound(preferSafe: Boolean): PrankSound? {
        return withContext(Dispatchers.IO) {
            val bundled = soundRepository.getBundledSounds()
                .filter { soundRepository.isCatalogSoundPlayable(it) }
            if (bundled.isEmpty()) {
                return@withContext null
            }

            val safeMode = soundRepository.getSafeRandomModeDefaultFlow().first()
            val candidates = when {
                preferSafe && safeMode -> bundled.filter { it.isSafeForRandomMode && isWithinSafeDuration(it) }
                else -> bundled
            }.ifEmpty { bundled }

            candidates.randomOrNull(random)
        }
    }

    private suspend fun selectByCategory(category: String): PrankSound? {
        return withContext(Dispatchers.IO) {
            val normalized = mapCategory(category)
            val bundled = soundRepository.getBundledSounds()
                .filter { soundRepository.isCatalogSoundPlayable(it) }

            val safeMode = soundRepository.getSafeRandomModeDefaultFlow().first()
            val candidates = bundled.filter { sound ->
                sound.category.equals(normalized, ignoreCase = true) ||
                    sound.tags.any { it.equals(category, ignoreCase = true) } ||
                    sound.packId?.contains(category, ignoreCase = true) == true ||
                    sound.name.contains(category, ignoreCase = true)
            }.let { matches ->
                if (safeMode) matches.filter { it.isSafeForRandomMode && isWithinSafeDuration(it) }
                else matches
            }

            candidates.randomOrNull(random) ?: selectRandomCatalogSound(preferSafe = true)
        }
    }

    private fun mapCategory(category: String): String {
        return when (category.trim().lowercase()) {
            "effects", "scan", "power", "forge", "zap", "audio" -> "AMBIENCE"
            "stash", "deploy", "release", "funny", "dance", "joke" -> "FUNNY"
            "voice", "disguise" -> "VOICE"
            "animal" -> "ANIMAL"
            "creepy", "sneak", "trap" -> "CREEPY"
            "cartoon", "classic", "mega" -> "CARTOON"
            else -> category.uppercase()
        }
    }

    private fun isWithinSafeDuration(sound: PrankSound): Boolean {
        return sound.durationMs <= 0L || sound.durationMs <= SAFE_MODE_MAX_DURATION_MS
    }
}
