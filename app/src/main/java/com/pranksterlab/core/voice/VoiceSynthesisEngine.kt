package com.pranksterlab.core.voice

import java.io.File

data class VoiceSynthesisResult(val outputFile: File, val formatLabel: String, val durationMs: Long?)

interface VoiceSynthesisEngine {
    suspend fun synthesizeToFile(settings: VoiceGeneratorSettings, outputFile: File): VoiceSynthesisResult
    fun preview(settings: VoiceGeneratorSettings)
    fun stopPreview()
    fun release()
}
