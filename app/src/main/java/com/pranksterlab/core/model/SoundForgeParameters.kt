package com.pranksterlab.core.model

data class SoundForgeParameters(
    val generatorType: SoundForgeGeneratorType,
    val durationMs: Long = 1000L,
    val pitch: Float = 1.0f,
    val pitchVariance: Float = 0.0f,
    val intensity: Float = 0.5f,
    val wobble: Float = 0.0f,
    val glitchAmount: Float = 0.0f,
    val echoAmount: Float = 0.0f,
    val reverbAmount: Float = 0.0f,
    val attackMs: Long = 10L,
    val releaseMs: Long = 100L,
    val tempo: Int = 120,
    val layers: Int = 1,
    val seed: Long = 0L,
    val volume: Float = 1.0f,
    val fxChain: Map<String, Boolean> = emptyMap(),
    val customParams: Map<String, Float> = emptyMap()
)
