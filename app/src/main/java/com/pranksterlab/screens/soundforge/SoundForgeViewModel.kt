package com.pranksterlab.screens.soundforge

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pranksterlab.core.audio.generator.SoundGeneratorEngine
import com.pranksterlab.core.model.GeneratedSoundResult
import com.pranksterlab.core.model.SoundForgeGeneratorType
import com.pranksterlab.core.model.SoundForgeParameters
import com.pranksterlab.core.repository.CustomSoundManager
import com.pranksterlab.core.model.PrankSound
import com.pranksterlab.core.model.SoundSourceType
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

class SoundForgeViewModel(
    private val engine: SoundGeneratorEngine,
    private val customSoundManager: CustomSoundManager
) : ViewModel() {

    val selectedType = mutableStateOf(SoundForgeGeneratorType.SCI_FI_BLIP)
    val parameters = mutableStateOf(SoundForgeParameters(generatorType = SoundForgeGeneratorType.SCI_FI_BLIP))
    
    val isGenerating = mutableStateOf(false)
    val generatedResult = mutableStateOf<GeneratedSoundResult?>(null)
    val history = mutableStateListOf<GeneratedSoundResult>()
    
    val isSeedLocked = mutableStateOf(false)
    val fxChain = mutableStateOf<Map<String, Boolean>>(mapOf(
        "Bitcrush" to false,
        "Echo" to false,
        "Wobble" to false,
        "Distortion" to false,
        "Low-pass" to false,
        "Reverse" to false
    ))

    val presets = listOf(
        SoundForgePreset("Alien Console", SoundForgeGeneratorType.SCI_FI_BLIP, mapOf("Pitch" to 0.8f, "Sweep" to 0.9f), mapOf("Echo" to true)),
        SoundForgePreset("Broken Robot", SoundForgeGeneratorType.GLITCH_BURST, mapOf("Chaos" to 0.7f, "Fragment Count" to 0.4f), mapOf("Bitcrush" to true)),
        SoundForgePreset("Basement Tap", SoundForgeGeneratorType.KNOCK_PATTERN, mapOf("Wood Tone" to 0.2f, "Knock Count" to 0.4f), emptyMap()),
        SoundForgePreset("Toy Squeak", SoundForgeGeneratorType.TOY_SQUEAK, emptyMap(), emptyMap()),
        SoundForgePreset("Monster Closet", SoundForgeGeneratorType.MONSTER_GROWL, emptyMap(), mapOf("Distortion" to true))
    )

    fun setGeneratorType(type: SoundForgeGeneratorType) {
        selectedType.value = type
        parameters.value = parameters.value.copy(
            generatorType = type,
            customParams = getDefaultParamsForType(type)
        )
    }

    fun applyPreset(preset: SoundForgePreset) {
        selectedType.value = preset.type
        fxChain.value = fxChain.value.toMutableMap().also { it.putAll(preset.fx) }
        parameters.value = parameters.value.copy(
            generatorType = preset.type,
            customParams = preset.params,
            fxChain = fxChain.value
        )
    }

    private fun getDefaultParamsForType(type: SoundForgeGeneratorType): Map<String, Float> = when (type) {
        SoundForgeGeneratorType.SCI_FI_BLIP -> mapOf("Pitch" to 0.5f, "Sweep" to 0.5f)
        SoundForgeGeneratorType.GLITCH_BURST -> mapOf("Fragment Count" to 0.5f, "Chaos" to 0.5f)
        SoundForgeGeneratorType.CREEPY_DRONE -> mapOf("Depth" to 0.5f, "Darkness" to 0.5f)
        SoundForgeGeneratorType.KNOCK_PATTERN -> mapOf("Knock Count" to 0.4f, "Spacing" to 0.5f, "Wood Tone" to 0.5f)
        else -> emptyMap()
    }

    fun toggleFx(fx: String) {
        val newChain = fxChain.value.toMutableMap()
        newChain[fx] = !(newChain[fx] ?: false)
        fxChain.value = newChain
        parameters.value = parameters.value.copy(fxChain = newChain)
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
            parameters.value = parameters.value.copy(seed = java.util.Random().nextLong())
        }
    }

    fun generate() {
        if (!isSeedLocked.value) {
            parameters.value = parameters.value.copy(seed = System.currentTimeMillis())
        }
        
        viewModelScope.launch {
            isGenerating.value = true
            try {
                // Safety check for names (labels)
                if (parameters.value.generatorType.displayName.contains("Emergency", true) ||
                    parameters.value.generatorType.displayName.contains("Panic", true)) {
                    throw Exception("SAFETY_VIOLATION: Restricted content pattern.")
                }

                val result = engine.generateSound(parameters.value)
                generatedResult.value = result
                if (result.errorMessage == null) {
                    history.add(0, result)
                    if (history.size > 20) history.removeAt(history.size - 1)
                }
            } catch (e: Exception) {
                generatedResult.value = GeneratedSoundResult(
                    id = "",
                    name = "Error",
                    fileUri = "",
                    durationMs = 0,
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
        val result = generatedResult.value ?: return
        if (result.errorMessage != null) return

        val customSound = PrankSound(
            id = "forge_" + UUID.randomUUID().toString(),
            name = name.ifEmpty { result.name },
            category = category,
            assetPath = "", 
            localUri = result.fileUri,
            durationMs = result.durationMs,
            tags = result.tags + "forge",
            isCustom = true,
            sourceType = SoundSourceType.GENERATED,
            createdByUser = true
        )
        
        viewModelScope.launch {
            customSoundManager.addCustomSound(customSound)
        }
    }
}

data class SoundForgePreset(
    val name: String,
    val type: SoundForgeGeneratorType,
    val params: Map<String, Float>,
    val fx: Map<String, Boolean>
)
