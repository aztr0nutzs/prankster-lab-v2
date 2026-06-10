package com.pranksterlab.core.bot

import com.pranksterlab.core.model.PrankSound

sealed class PrankstarBotAction {
    data class ShowMessage(val text: String) : PrankstarBotAction()
    data class ShowSoundRecommendations(val sounds: List<PrankSound>, val reason: String) : PrankstarBotAction()
    data class PlaySound(val sound: PrankSound) : PrankstarBotAction()
    object StopAllSounds : PrankstarBotAction()
    data class Navigate(val route: String) : PrankstarBotAction()
    data class FillVoiceLabText(val text: String, val suggestedVoicePresetId: String? = null, val preferBritishNarrator: Boolean = false) : PrankstarBotAction()
    data class ShowPrankPlan(val plan: PrankstarPrankPlan) : PrankstarBotAction()
    data class Refuse(val reason: String) : PrankstarBotAction()
}

data class PrankstarPrankPlan(
    val title: String,
    val steps: List<PrankstarPrankStep>,
    val safetyNote: String
)

data class PrankstarPrankStep(
    val label: String,
    val actionType: String,
    val soundId: String? = null,
    val delaySeconds: Int? = null,
    val voiceText: String? = null
)
