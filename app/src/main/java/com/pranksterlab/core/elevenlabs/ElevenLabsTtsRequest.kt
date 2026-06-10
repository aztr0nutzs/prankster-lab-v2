package com.pranksterlab.core.elevenlabs

data class ElevenLabsVoiceSettings(
    val stability: Double = 0.55,
    val similarityBoost: Double = 0.75,
    val style: Double = 0.25,
    val useSpeakerBoost: Boolean = true
)

data class ElevenLabsTtsRequest(
    val text: String,
    val modelId: String = DEFAULT_MODEL_ID,
    val voiceSettings: ElevenLabsVoiceSettings = ElevenLabsVoiceSettings()
)
