package com.pranksterlab.core.bot

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class PrankstarBotVoiceDraft(
    val text: String,
    val suggestedVoicePresetId: String? = null,
    val preferBritishNarrator: Boolean = false
)

object PrankstarBotVoiceLabBridge {
    private val _pendingDraft = MutableStateFlow<PrankstarBotVoiceDraft?>(null)
    val pendingDraft: StateFlow<PrankstarBotVoiceDraft?> = _pendingDraft.asStateFlow()

    fun submit(text: String, suggestedVoicePresetId: String?, preferBritishNarrator: Boolean = false) {
        _pendingDraft.value = PrankstarBotVoiceDraft(text, suggestedVoicePresetId, preferBritishNarrator)
    }

    fun consume() {
        _pendingDraft.value = null
    }
}
