package com.pranksterlab.core.voice

import java.io.File
import kotlinx.coroutines.flow.StateFlow

sealed interface VoiceEngineReadiness {
    data object Initializing : VoiceEngineReadiness
    data object Ready : VoiceEngineReadiness
    data object Unavailable : VoiceEngineReadiness
    data class Error(val message: String) : VoiceEngineReadiness
}

data class VoiceSynthesisResult(
    val outputFile: File,
    val formatLabel: String,
    val durationMs: Long?,
    val success: Boolean,
    val errorMessage: String? = null
)

interface VoiceSynthesisEngine {
    val readiness: StateFlow<VoiceEngineReadiness>
    suspend fun synthesizeToFile(settings: VoiceGeneratorSettings, outputFile: File): VoiceSynthesisResult
    fun preview(settings: VoiceGeneratorSettings)
    fun stopPreview()
    fun release()
}
