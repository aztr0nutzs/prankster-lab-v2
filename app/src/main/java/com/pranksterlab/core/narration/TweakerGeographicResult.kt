package com.pranksterlab.core.narration

data class TweakerGeographicResult(
    val title: String,
    val narration: String,
    val tone: TweakerGeographicTone,
    val suggestedVoicePresetId: String,
    val suggestedSoundQuery: String?,
    val safetyNote: String? = null
) {
    val isAllowed: Boolean = safetyNote == null
}
