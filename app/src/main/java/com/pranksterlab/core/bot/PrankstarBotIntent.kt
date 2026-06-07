package com.pranksterlab.core.bot

sealed class PrankstarBotIntent {
    data class SearchSounds(val query: String) : PrankstarBotIntent()
    data class RecommendSounds(val vibe: String) : PrankstarBotIntent()
    data class GenerateJoke(val prompt: String) : PrankstarBotIntent()
    data class BuildPrankPlan(val prompt: String) : PrankstarBotIntent()
    data class ChooseVoice(val prompt: String) : PrankstarBotIntent()
    object PlayRandom : PrankstarBotIntent()
    object StopAll : PrankstarBotIntent()
    object OpenStash : PrankstarBotIntent()
    object OpenJokes : PrankstarBotIntent()
    object OpenForge : PrankstarBotIntent()
    object Help : PrankstarBotIntent()
    data class Unknown(val raw: String) : PrankstarBotIntent()
}
