package com.pranksterlab.core.model

data class SequenceStep(
    val id: String,
    val soundId: String,
    val soundName: String,
    val assetPath: String,
    val delayAfterMs: Long,
    val category: String
)

data class SoundSequencePreset(
    val id: String,
    val name: String,
    val steps: List<SequenceStep>,
    val repeatCount: Int,
    val createdAt: Long,
    val updatedAt: Long
)
