package com.pranksterlab.core.voice

data class VoiceGeneratorSettings(
    val preset: VoicePreset,
    val text: String,
    val pitch: Float,
    val speechRate: Float,
    val volume: Float,
    val toneStyle: String,
    val effectAmount: Float,
    val enableEchoReverb: Boolean,
    val outputName: String
)
