package com.pranksterlab.core.voice

enum class VoiceGenerationMode {
    LOCAL_ONLY,
    DEBUG_ELEVENLABS_DIRECT,
    PRODUCTION_BACKEND;

    companion object {
        fun fromBuildConfig(value: String): VoiceGenerationMode {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: LOCAL_ONLY
        }
    }
}
