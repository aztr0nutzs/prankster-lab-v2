package com.pranksterlab.core.narration

data class TweakerGeographicRequest(
    val action: String,
    val setting: String? = null,
    val tone: TweakerGeographicTone = TweakerGeographicTone.BALANCED,
    val includeSoundCue: Boolean = false
)
