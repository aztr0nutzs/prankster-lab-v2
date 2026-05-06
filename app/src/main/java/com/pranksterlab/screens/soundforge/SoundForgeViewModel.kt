package com.pranksterlab.screens.soundforge

import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.pranksterlab.core.audio.generator.SoundGeneratorEngine
import com.pranksterlab.core.model.GeneratedSoundMetadata
import com.pranksterlab.core.model.GeneratedSoundResult
import com.pranksterlab.core.model.PrankSound
import com.pranksterlab.core.model.SoundForgeGeneratorType
import com.pranksterlab.core.model.SoundForgeParameters
import com.pranksterlab.core.model.SoundSourceType
import com.pranksterlab.core.repository.CustomSoundManager
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.random.Random

class SoundForgeViewModel(
    private val engine: SoundGeneratorEngine,
    private val customSoundManager: CustomSoundManager
) : ViewModel() {
    private val gson = Gson()

    val selectedType = mutableStateOf(SoundForgeGeneratorType.SCI_FI_BLIP)
    val parameters = mutableStateOf(
        SoundForgeParameters(
            generatorType = SoundForgeGeneratorType.SCI_FI_BLIP,
            customParams = defaultParamsFor(SoundForgeGeneratorType.SCI_FI_BLIP)
        )
    )

    val isGenerating = mutableStateOf(false)
    val generatedResult = mutableStateOf<GeneratedSoundResult?>(null)
    val history = mutableStateListOf<GeneratedSoundResult>()
    val savedConfirmation = mutableStateOf<String?>(null)
    val saveError = mutableStateOf<String?>(null)

    val isSeedLocked = mutableStateOf(false)
    val fxChain = mutableStateOf(
        mapOf(
            "Echo" to false,
            "Reverb" to false,
            "Wobble" to false,
            "Bitcrush" to false,
            "Distortion" to false,
            "Low-pass" to false,
            "Reverse" to false,
            "Stutter" to false
        )
    )

    val presets = listOf(
        SoundForgePreset(
            name = "Alien Console",
            type = SoundForgeGeneratorType.SCI_FI_BLIP,
            params = mapOf("Pitch" to 0.78f, "Sweep" to 0.85f, "Echo" to 0.62f, "Brightness" to 0.7f, "Reverb" to 0.48f),
            fx = mapOf("Echo" to true, "Reverb" to true),
            durationMs = 1400,
            volume = 0.9f
        ),
        SoundForgePreset(
            name = "Broken Robot",
            type = SoundForgeGeneratorType.GLITCH_BURST,
            params = mapOf("Glitch" to 0.8f, "Stutter" to 0.7f, "Chaos" to 0.7f, "Fragment Count" to 0.4f, "Bitcrush" to 0.72f, "Distortion" to 0.38f),
            fx = mapOf("Bitcrush" to true, "Stutter" to true),
            durationMs = 1200,
            volume = 0.95f
        ),
        SoundForgePreset(
            name = "Tiny Gremlin",
            type = SoundForgeGeneratorType.TOY_SQUEAK,
            params = mapOf("Pitch" to 0.85f, "Wobble" to 0.7f, "Brightness" to 0.6f, "Echo" to 0.18f),
            fx = mapOf("Wobble" to true),
            durationMs = 700,
            volume = 0.95f
        ),
        SoundForgePreset(
            name = "Basement Tap",
            type = SoundForgeGeneratorType.KNOCK_PATTERN,
            params = mapOf("Wood Tone" to 0.25f, "Knock Count" to 0.4f, "Spacing" to 0.6f, "Room Size" to 0.7f, "Reverb" to 0.66f, "Low-pass" to 0.28f),
            fx = mapOf("Reverb" to true),
            durationMs = 2200,
            volume = 1.0f
        ),
        SoundForgePreset(
            name = "Toy Squeak",
            type = SoundForgeGeneratorType.TOY_SQUEAK,
            params = mapOf("Pitch" to 0.55f, "Wobble" to 0.45f, "Brightness" to 0.4f, "Echo" to 0.12f),
            fx = emptyMap(),
            durationMs = 600,
            volume = 0.9f
        ),
        SoundForgePreset(
            name = "Arcade Fail",
            type = SoundForgeGeneratorType.ROBOT_BEEP,
            params = mapOf("Pitch" to 0.3f, "Beep Count" to 0.55f, "Spacing" to 0.4f, "Robotization" to 0.85f, "Bitcrush" to 0.58f),
            fx = mapOf("Bitcrush" to true),
            durationMs = 1300,
            volume = 0.95f
        ),
        SoundForgePreset(
            name = "Monster Closet",
            type = SoundForgeGeneratorType.MONSTER_GROWL,
            params = mapOf("Depth" to 0.85f, "Grit" to 0.75f, "Wobble" to 0.6f, "Throat Size" to 0.8f, "Distortion" to 0.5f, "Low-pass" to 0.68f),
            fx = mapOf("Distortion" to true, "Low-pass" to true),
            durationMs = 1800,
            volume = 1.0f
        ),
        SoundForgePreset(
            name = "Glitch Goblin",
            type = SoundForgeGeneratorType.GLITCH_BURST,
            params = mapOf("Glitch" to 0.95f, "Stutter" to 0.9f, "Chaos" to 0.85f, "Fragment Count" to 0.7f, "Bitcrush" to 0.85f, "Distortion" to 0.42f),
            fx = mapOf("Bitcrush" to true, "Stutter" to true, "Distortion" to true),
            durationMs = 1100,
            volume = 1.0f
        )
    )

    fun setGeneratorType(type: SoundForgeGeneratorType) {
        selectedType.value = type
        parameters.value = parameters.value.copy(
            generatorType = type,
            customParams = defaultParamsFor(type)
        )
    }

    fun applyPreset(preset: SoundForgePreset) {
        selectedType.value = preset.type
        val newFx = fxChain.value.toMutableMap()
        // reset every toggle, then apply preset toggles
        newFx.keys.forEach { newFx[it] = false }
        preset.fx.forEach { (k, v) -> newFx[k] = v }
        fxChain.value = newFx
        parameters.value = parameters.value.copy(
            generatorType = preset.type,
            customParams = defaultParamsFor(preset.type) + preset.params,
            fxChain = newFx,
            durationMs = preset.durationMs,
            volume = preset.volume
        )
    }

    fun toggleFx(fx: String) {
        val newChain = fxChain.value.toMutableMap()
        newChain[fx] = !(newChain[fx] ?: false)
        fxChain.value = newChain
        parameters.value = parameters.value.copy(fxChain = newChain)
    }

    fun updateFxAmount(fx: String, value: Float) {
        val newParams = parameters.value.customParams.toMutableMap()
        newParams[fx] = value.coerceIn(0f, 1f)
        parameters.value = parameters.value.copy(customParams = newParams)
    }

    fun updateParams(updater: (SoundForgeParameters) -> SoundForgeParameters) {
        parameters.value = updater(parameters.value)
    }

    fun updateCustomParam(key: String, value: Float) {
        val newParams = parameters.value.customParams.toMutableMap()
        newParams[key] = value
        parameters.value = parameters.value.copy(customParams = newParams)
    }

    fun randomizeSeed() {
        if (!isSeedLocked.value) {
            parameters.value = parameters.value.copy(seed = Random.Default.nextLong())
        }
    }

    fun randomizeAllParameters() {
        val rnd = Random.Default
        val current = parameters.value
        val randomized = current.customParams.mapValues { rnd.nextFloat() }
        parameters.value = current.copy(
            customParams = randomized,
            seed = if (isSeedLocked.value) current.seed else rnd.nextLong(),
            durationMs = (400L + (rnd.nextFloat() * 1800f).toLong())
        )
    }

    fun reset() {
        val type = selectedType.value
        parameters.value = SoundForgeParameters(
            generatorType = type,
            customParams = defaultParamsFor(type)
        )
        fxChain.value = fxChain.value.mapValues { false }
        savedConfirmation.value = null
        saveError.value = null
    }

    fun generate() {
        if (!isSeedLocked.value) {
            parameters.value = parameters.value.copy(seed = System.currentTimeMillis())
        }
        viewModelScope.launch {
            isGenerating.value = true
            saveError.value = null
            savedConfirmation.value = null
            try {
                val typeName = parameters.value.generatorType.displayName
                if (typeName.contains("Emergency", true) || typeName.contains("Panic", true) ||
                    typeName.contains("Siren", true) || typeName.contains("Alarm", true)) {
                    throw IllegalStateException("SAFETY_VIOLATION: Restricted content pattern.")
                }
                val result = engine.generateSound(parameters.value)
                generatedResult.value = result
                if (result.errorMessage == null) {
                    history.add(0, result)
                    if (history.size > 20) history.removeAt(history.size - 1)
                }
            } catch (e: Exception) {
                generatedResult.value = GeneratedSoundResult(
                    id = "", name = "Error", fileUri = Uri.EMPTY, durationMs = 0,
                    generatorType = parameters.value.generatorType,
                    createdAt = System.currentTimeMillis(),
                    tags = emptyList(),
                    errorMessage = e.message ?: "Unknown error"
                )
            } finally {
                isGenerating.value = false
            }
        }
    }

    fun saveGeneratedSound(name: String, category: String) {
        val result = generatedResult.value
        if (result == null || result.errorMessage != null || result.fileUri == Uri.EMPTY) {
            saveError.value = "No valid generated sound to save"
            return
        }
        if (result.tags.any { it.contains("restricted", ignoreCase = true) || it.contains("siren", ignoreCase = true) }) {
            saveError.value = "Safety guardrail blocked saving a restricted alert-like pattern"
            return
        }
        val path = result.fileUri.path ?: result.fileUri.toString()
        val file = java.io.File(path)
        if (!file.exists() || file.length() <= 0) {
            saveError.value = "Generated WAV missing on disk"
            return
        }

        val customSound = PrankSound(
            id = "forge_" + UUID.randomUUID().toString(),
            name = name.ifEmpty { result.name },
            category = category,
            assetPath = "",
            localUri = path,
            durationMs = result.durationMs,
            tags = result.tags + "forge",
            isCustom = true,
            sourceType = SoundSourceType.GENERATED,
            createdByUser = true,
            generatedMetadata = GeneratedSoundMetadata(
                generatorType = result.generatorType.name,
                parametersJson = gson.toJson(parameters.value)
            )
        )

        viewModelScope.launch {
            try {
                customSoundManager.addCustomSound(customSound)
                savedConfirmation.value = "Saved \"${customSound.name}\""
                saveError.value = null
            } catch (e: Exception) {
                saveError.value = e.message ?: "Failed to save"
            }
        }
    }

    companion object {
        fun defaultParamsFor(type: SoundForgeGeneratorType): Map<String, Float> = when (type) {
            SoundForgeGeneratorType.SCI_FI_BLIP -> mapOf(
                "Pitch" to 0.5f, "Sweep" to 0.5f, "Echo" to 0.4f, "Brightness" to 0.4f
            )
            SoundForgeGeneratorType.GLITCH_BURST -> mapOf(
                "Glitch" to 0.5f, "Stutter" to 0.4f, "Bitcrush" to 0.5f,
                "Chaos" to 0.5f, "Fragment Count" to 0.5f
            )
            SoundForgeGeneratorType.ROBOT_BEEP -> mapOf(
                "Pitch" to 0.5f, "Beep Count" to 0.4f, "Spacing" to 0.5f, "Robotization" to 0.6f
            )
            SoundForgeGeneratorType.CARTOON_POP -> mapOf(
                "Pitch" to 0.5f, "Snap" to 0.6f, "Pop Count" to 0.2f, "Brightness" to 0.5f
            )
            SoundForgeGeneratorType.TOY_SQUEAK -> mapOf(
                "Pitch" to 0.55f, "Wobble" to 0.5f, "Brightness" to 0.5f
            )
            SoundForgeGeneratorType.CREEPY_DRONE -> mapOf(
                "Depth" to 0.5f, "Wobble" to 0.4f, "Noise" to 0.3f, "Darkness" to 0.5f
            )
            SoundForgeGeneratorType.MONSTER_GROWL -> mapOf(
                "Depth" to 0.65f, "Grit" to 0.55f, "Wobble" to 0.4f, "Throat Size" to 0.5f
            )
            SoundForgeGeneratorType.KNOCK_PATTERN -> mapOf(
                "Knock Count" to 0.4f, "Spacing" to 0.5f, "Room Size" to 0.4f, "Wood Tone" to 0.5f
            )
            SoundForgeGeneratorType.FOOTSTEP_PATTERN -> mapOf(
                "Step Count" to 0.4f, "Spacing" to 0.5f, "Surface" to 0.5f, "Heaviness" to 0.5f
            )
            SoundForgeGeneratorType.CHAOS_RANDOM -> mapOf(
                "Density" to 0.7f, "Bitcrush" to 0.5f, "Chaos" to 0.6f
            )
        }
    }
}

data class SoundForgePreset(
    val name: String,
    val type: SoundForgeGeneratorType,
    val params: Map<String, Float>,
    val fx: Map<String, Boolean>,
    val durationMs: Long = 1000,
    val volume: Float = 1.0f
)
