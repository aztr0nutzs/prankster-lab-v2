package com.pranksterlab.core.model

import android.net.Uri

data class GeneratedSoundMetadata(
    val generatorType: String,
    val parametersJson: String // Serialized parameters
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
