package com.pranksterlab.screens

import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pranksterlab.core.audio.AudioPlayerController
import com.pranksterlab.core.model.PrankSound
import com.pranksterlab.core.repository.SoundRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.random.Random

data class RandomizerUiState(
    val sounds: List<PrankSound> = emptyList(),
    val isLoaded: Boolean = false,
    val isRunning: Boolean = false,
    val safeMode: Boolean = true,
    val includeGeneratedVoiceClips: Boolean = true,
    val continuousMode: Boolean = true,
    val currentSound: PrankSound? = null,
    val upcomingDelayMs: Long = 0L,
    val completedPlays: Int = 0,
    val skippedInvalid: Int = 0,
    val status: String = "RANDOMIZER IDLE"
)

class RandomizerViewModel(
    private val soundRepository: SoundRepository,
    private val audioPlayerController: AudioPlayerController
) : ViewModel() {
    private var randomizerJob: Job? = null
    private var loadJob: Job? = null
    private val maxSafeDurationMs = 15_000L

    val uiState = mutableStateOf(RandomizerUiState())
    val selectedCategories = mutableStateListOf<String>()
    val minDelaySeconds = mutableFloatStateOf(2f)
    val maxDelaySeconds = mutableFloatStateOf(8f)
    val loopCount = mutableIntStateOf(5)

    fun loadSounds() {
        if (uiState.value.isLoaded || loadJob?.isActive == true) return
        loadJob = viewModelScope.launch {
            val bundled = soundRepository.getBundledSounds()
            soundRepository.getCustomSoundsFlow().collectLatest { custom ->
                val allSounds = bundled + custom
                if (selectedCategories.isEmpty()) {
                    selectedCategories.addAll(
                        allSounds.map { it.category }
                            .distinct()
                            .sorted()
                            .take(4)
                    )
                }
                uiState.value = uiState.value.copy(
                    sounds = allSounds,
                    isLoaded = true,
                    status = if (allSounds.isEmpty()) "NO CATALOG SOUNDS FOUND" else "CATALOG READY"
                )
            }
        }
    }

    val categories: List<String>
        get() = uiState.value.sounds.map { it.category }.distinct().sorted()

    val filteredSounds: List<PrankSound>
        get() = uiState.value.sounds.filterForRandomizer()

    fun toggleCategory(category: String) {
        if (category in selectedCategories) {
            selectedCategories.remove(category)
        } else {
            selectedCategories.add(category)
        }
    }

    fun setMinDelay(value: Float) {
        val coerced = value.coerceIn(1f, 60f)
        minDelaySeconds.floatValue = coerced.coerceAtMost(maxDelaySeconds.floatValue)
    }

    fun setMaxDelay(value: Float) {
        val coerced = value.coerceIn(1f, 60f)
        maxDelaySeconds.floatValue = coerced.coerceAtLeast(minDelaySeconds.floatValue)
    }

    fun setSafeMode(enabled: Boolean) {
        uiState.value = uiState.value.copy(safeMode = enabled)
    }

    fun setContinuousMode(enabled: Boolean) {
        uiState.value = uiState.value.copy(continuousMode = enabled)
    }
    fun setIncludeGeneratedVoiceClips(enabled: Boolean) {
        uiState.value = uiState.value.copy(includeGeneratedVoiceClips = enabled)
    }

    fun setLoopCount(value: Int) {
        loopCount.intValue = value.coerceIn(1, 99)
    }

    fun startRandomizer() {
        if (randomizerJob?.isActive == true) return
        val initialCandidates = filteredSounds
        if (initialCandidates.isEmpty()) {
            uiState.value = uiState.value.copy(status = "NO VALID SOUNDS IN FILTER")
            return
        }

        randomizerJob = viewModelScope.launch {
            uiState.value = uiState.value.copy(
                isRunning = true,
                currentSound = null,
                upcomingDelayMs = 0L,
                completedPlays = 0,
                skippedInvalid = 0,
                status = "RANDOMIZER ARMED"
            )

            try {
                while (uiState.value.isRunning && (uiState.value.continuousMode || uiState.value.completedPlays < loopCount.intValue)) {
                    val sound = choosePlayableSound()
                    if (sound == null) {
                        uiState.value = uiState.value.copy(status = "VALIDATION EXHAUSTED FILTER")
                        break
                    }

                    val started = audioPlayerController.playPrankSound(sound, isLooping = false)
                    if (started) {
                        uiState.value = uiState.value.copy(
                            currentSound = sound,
                            completedPlays = uiState.value.completedPlays + 1,
                            status = "PLAYING ${sound.name.uppercase()}"
                        )
                        waitRandomDelay()
                    } else {
                        uiState.value = uiState.value.copy(
                            skippedInvalid = uiState.value.skippedInvalid + 1,
                            status = "SKIPPED INVALID: ${sound.name.uppercase()}"
                        )
                    }
                }
            } finally {
                audioPlayerController.stopAll()
                uiState.value = uiState.value.copy(
                    isRunning = false,
                    upcomingDelayMs = 0L,
                    status = if (uiState.value.status.startsWith("VALIDATION")) uiState.value.status else "RANDOMIZER STOPPED"
                )
            }
        }
    }

    fun stopRandomizer() {
        randomizerJob?.cancel()
        randomizerJob = null
        audioPlayerController.stopAll()
        uiState.value = uiState.value.copy(
            isRunning = false,
            upcomingDelayMs = 0L,
            status = "RANDOMIZER STOPPED"
        )
    }

    fun dispose() {
        stopRandomizer()
        loadJob?.cancel()
        loadJob = null
    }

    override fun onCleared() {
        dispose()
        super.onCleared()
    }

    private fun choosePlayableSound(): PrankSound? {
        val candidates = filteredSounds.shuffled()
        for (sound in candidates) {
            if (audioPlayerController.canPlayPrankSound(sound)) return sound
            uiState.value = uiState.value.copy(skippedInvalid = uiState.value.skippedInvalid + 1)
        }
        return null
    }

    private suspend fun waitRandomDelay() {
        val minMs = (minDelaySeconds.floatValue * 1000).toLong()
        val maxMs = (maxDelaySeconds.floatValue * 1000).toLong().coerceAtLeast(minMs)
        var remaining = if (maxMs == minMs) minMs else Random.nextLong(minMs, maxMs + 1)
        while (remaining > 0 && uiState.value.isRunning) {
            uiState.value = uiState.value.copy(upcomingDelayMs = remaining, status = "NEXT DEPLOY IN ${remaining / 1000}s")
            val tick = remaining.coerceAtMost(250L)
            delay(tick)
            remaining -= tick
        }
        uiState.value = uiState.value.copy(upcomingDelayMs = 0L)
    }

    private fun List<PrankSound>.filterForRandomizer(): List<PrankSound> {
        val invalidIds = audioPlayerController.invalidSoundIds.value
        return filter { sound ->
            val categoryAllowed = selectedCategories.isNotEmpty() && sound.category in selectedCategories
            val safeAllowed = !uiState.value.safeMode || sound.isSafeForRandomMode
            val durationAllowed = !uiState.value.safeMode || sound.durationMs <= 0L || sound.durationMs <= maxSafeDurationMs
            val generatedAllowed = uiState.value.includeGeneratedVoiceClips || !soundRepository.isGeneratedVoiceClip(sound)
            val playable = soundRepository.isSoundPlayable(sound)
            categoryAllowed && safeAllowed && durationAllowed && generatedAllowed && playable && sound.id !in invalidIds
        }
    }
}
