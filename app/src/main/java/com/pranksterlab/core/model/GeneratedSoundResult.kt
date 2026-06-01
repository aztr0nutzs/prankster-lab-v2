package com.pranksterlab.core.model

import android.net.Uri

data class GeneratedSoundMetadata(
    val generatorType: String,
    val parametersJson: String, // Serialized parameters
    val sourceText: String? = null,
    val voicePresetId: String? = null,
    val voicePresetName: String? = null,
    val pitch: Float? = null,
    val speechRate: Float? = null,
    val volume: Float? = null,
    val toneStyle: String? = null,
    val effectStyle: String? = null,
    val createdAt: Long? = null,
    val durationMs: Long? = null
)

data class GeneratedSoundResult(
    val id: String,
    val name: String,
    val fileUri: Uri,
    val durationMs: Long,
    val generatorType: SoundForgeGeneratorType,
    val createdAt: Long,
    val tags: List<String>,
    val waveformPeaks: List<Float> = emptyList(),
    val errorMessage: String? = null
)
