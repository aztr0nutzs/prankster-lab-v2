package com.pranksterlab.core.voice

import com.pranksterlab.core.model.GeneratedSoundMetadata
import com.pranksterlab.core.elevenlabs.ELEVENLABS_SOURCE
import com.pranksterlab.core.elevenlabs.TWEAKER_GEOGRAPHIC_FEATURE
import com.pranksterlab.core.elevenlabs.TWEAKER_GEOGRAPHIC_VOICE_ID
import com.pranksterlab.core.model.PrankSound
import com.pranksterlab.core.model.SoundSourceType
import com.pranksterlab.core.repository.SoundRepository
import org.json.JSONObject
import java.io.File

class GeneratedVoiceRepository(private val soundRepository: SoundRepository) {
    suspend fun saveGeneratedVoice(file: File, settings: VoiceGeneratorSettings, durationMs: Long?): PrankSound {
        require(file.exists() && file.length() > 0L) { "Generated voice file is missing or empty." }

        val createdAt = System.currentTimeMillis()
        val id = "voice_${file.nameWithoutExtension.removePrefix("voice_").take(16)}"
        val parametersJson = JSONObject()
            .put("voicePresetId", settings.preset.id)
            .put("voicePresetName", settings.preset.displayName)
            .put("pitch", settings.pitch)
            .put("speechRate", settings.speechRate)
            .put("volume", settings.volume)
            .put("toneStyle", settings.toneStyle)
            .put("effectStyle", settings.preset.effectStyle)
            .put("effectAmount", settings.effectAmount)
            .put("echoReverb", settings.enableEchoReverb)
            .toString()

        val sound = PrankSound(
            id = id,
            name = settings.outputName.ifBlank { "Voice Clip" },
            category = "VOICE_GENERATED",
            packId = "voice_lab",
            assetPath = file.absolutePath,
            durationMs = durationMs ?: 0L,
            tags = listOf("generated", "voice", "joke", "custom"),
            isCustom = true,
            localUri = file.absolutePath,
            sourceType = SoundSourceType.GENERATED,
            createdByUser = true,
            createdAt = createdAt,
            description = settings.text.take(80),
            prankStyle = settings.toneStyle,
            previewLabel = settings.preset.displayName,
            isSafeForRandomMode = settings.preset.isSafeForRandomMode,
            intensityLevel = settings.preset.intensityLevel,
            generatedMetadata = GeneratedSoundMetadata(
                generatorType = "VOICE_LAB",
                parametersJson = parametersJson,
                sourceText = settings.text,
                voicePresetId = settings.preset.id,
                voicePresetName = settings.preset.displayName,
                pitch = settings.pitch,
                speechRate = settings.speechRate,
                volume = settings.volume,
                toneStyle = settings.toneStyle,
                effectStyle = settings.preset.effectStyle,
                createdAt = createdAt,
                durationMs = durationMs
            )
        )
        soundRepository.saveCustomSound(sound)
        return sound
    }

    suspend fun saveTweakerGeographicElevenLabsVoice(
        file: File,
        title: String,
        narrationText: String,
        durationMs: Long?
    ): PrankSound {
        require(file.exists() && file.length() > 0L) { "Generated ElevenLabs narration file is missing or empty." }

        val createdAt = System.currentTimeMillis()
        val id = "tweaker_geo_${file.nameWithoutExtension.removePrefix("tweaker_geo_").take(24)}"
        val safeTitle = title.ifBlank { "Tweaker Geographic Narration" }
        val parametersJson = JSONObject()
            .put("title", safeTitle)
            .put("source", ELEVENLABS_SOURCE)
            .put("voiceId", TWEAKER_GEOGRAPHIC_VOICE_ID)
            .put("format", "mp3")
            .put("feature", TWEAKER_GEOGRAPHIC_FEATURE)
            .put("createdAt", createdAt)
            .toString()

        val sound = PrankSound(
            id = id,
            name = if (safeTitle.startsWith("Tweaker Geographic", ignoreCase = true)) safeTitle else "Tweaker Geographic: $safeTitle",
            category = "VOICE_GENERATED",
            packId = "voice_lab",
            assetPath = file.absolutePath,
            durationMs = durationMs ?: 0L,
            tags = listOf("generated", "voice", "elevenlabs", "tweaker_geographic", "custom"),
            isCustom = true,
            localUri = file.absolutePath,
            sourceType = SoundSourceType.GENERATED,
            createdByUser = true,
            createdAt = createdAt,
            description = narrationText.take(120),
            prankStyle = "mock-documentary",
            previewLabel = "Tweaker Geographic British Narrator",
            isSafeForRandomMode = true,
            intensityLevel = 2,
            generatedMetadata = GeneratedSoundMetadata(
                generatorType = "VOICE_LAB",
                parametersJson = parametersJson,
                sourceText = narrationText,
                voicePresetId = TWEAKER_GEOGRAPHIC_VOICE_ID,
                voicePresetName = "Tweaker Geographic British Narrator",
                toneStyle = "mock-documentary",
                effectStyle = "elevenlabs",
                source = ELEVENLABS_SOURCE,
                voiceId = TWEAKER_GEOGRAPHIC_VOICE_ID,
                format = "mp3",
                feature = TWEAKER_GEOGRAPHIC_FEATURE,
                createdAt = createdAt,
                durationMs = durationMs
            )
        )
        soundRepository.saveCustomSound(sound)
        return sound
    }

}
