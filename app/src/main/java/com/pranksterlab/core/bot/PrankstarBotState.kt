package com.pranksterlab.core.bot

import com.pranksterlab.components.bot.PrankstarBotMood
import com.pranksterlab.core.model.PrankSound

data class PrankstarBotState(
    val message: PrankstarBotMessage = PrankstarBotMessage("Tell me what kind of prank sound you want."),
    val mood: PrankstarBotMood = PrankstarBotMood.IDLE,
    val suggestedChips: List<String> = listOf("Find Creepy", "Funny Sound", "Make Joke", "Open Stash", "Stop All"),
    val recommendations: List<PrankSound> = emptyList(),
    val recommendationReason: String? = null,
    val generatedText: String? = null,
    val suggestedVoicePresetId: String? = null,
    val prankPlan: PrankstarPrankPlan? = null,
    val lastActions: List<PrankstarBotAction> = emptyList()
)

data class PrankstarBotResult(
    val message: String,
    val actions: List<PrankstarBotAction>,
    val mood: PrankstarBotMood,
    val suggestedChips: List<String> = PrankstarBotState().suggestedChips,
    val generatedText: String? = null,
    val suggestedVoicePresetId: String? = null,
    val prankPlan: PrankstarPrankPlan? = null
)
