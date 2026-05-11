package com.pranksterlab.core.voice

import com.pranksterlab.core.model.PrankSound
import com.pranksterlab.core.model.GeneratedSoundMetadata
import com.pranksterlab.core.model.SoundSourceType
import com.pranksterlab.core.repository.SoundRepository
import java.io.File
import java.util.UUID

class GeneratedVoiceRepository(private val soundRepository: SoundRepository) {
    suspend fun saveGeneratedVoice(file: File, settings: VoiceGeneratorSettings, durationMs: Long?): PrankSound {
        val id = "voice_${UUID.randomUUID().toString().take(8)}"
        val sound = PrankSound(
            id = id, name = settings.outputName.ifBlank { "Voice Clip" }, category = "VOICE_GENERATED", packId = "voice_lab",
            assetPath = file.absolutePath, durationMs = durationMs ?: 0L, tags = listOf("generated","voice","joke","custom"),
            isCustom = true, localUri = file.absolutePath, sourceType = SoundSourceType.GENERATED, createdByUser = true,
            description = settings.text.take(80), prankStyle = settings.toneStyle, previewLabel = settings.preset.displayName,
            isSafeForRandomMode = settings.preset.isSafeForRandomMode, intensityLevel = settings.preset.intensityLevel,
            generatedMetadata = GeneratedSoundMetadata(
                generatorType = "VOICE_LAB",
                parametersJson = """{"voicePresetId":"${settings.preset.id}","toneStyle":"${settings.toneStyle}","effectStyle":"${settings.preset.effectStyle}","effectAmount":${settings.effectAmount}}""",
                sourceText = settings.text,
                voicePresetId = settings.preset.id,
                voicePresetName = settings.preset.displayName,
                pitch = settings.pitch,
                speechRate = settings.speechRate,
                volume = settings.volume,
                toneStyle = settings.toneStyle,
                effectStyle = settings.preset.effectStyle
            )
        )
        soundRepository.saveCustomSound(sound)
        return sound
    }
}
