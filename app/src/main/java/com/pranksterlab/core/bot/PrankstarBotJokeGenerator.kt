package com.pranksterlab.core.bot

import com.pranksterlab.components.bot.PrankstarBotMood

data class PrankstarGeneratedJoke(
    val text: String,
    val suggestedVoicePresetId: String,
    val suggestedMood: PrankstarBotMood
)

class PrankstarBotJokeGenerator(
    private val safety: PrankstarBotSafety = PrankstarBotSafety()
) {
    private val templates = listOf(
        JokeTemplate("funny comment", "glitch_bot", PrankstarBotMood.TYPING) { topic -> "Alert. $topic is running on potato-powered time again." },
        JokeTemplate("roast-lite", "sarcastic_butler", PrankstarBotMood.TYPING) { topic -> "Breaking news: $topic has entered the championship of dramatic delays." },
        JokeTemplate("creepy whisper", "dramatic_whisper", PrankstarBotMood.PROCESSING) { topic -> "Psst. $topic was last seen negotiating with the shadows." },
        JokeTemplate("robot announcement", "robot_elevator", PrankstarBotMood.PROCESSING) { topic -> "Prankstar notice: $topic has triggered a harmless mischief diagnostic." },
        JokeTemplate("office announcement", "mall_pa_system", PrankstarBotMood.TYPING) { topic -> "Attention crew: $topic has been moved to aisle fun. Please remain mildly confused." },
        JokeTemplate("dad joke", "awkward_dad_joke", PrankstarBotMood.HAPPY) { topic -> "I asked $topic for a punchline, but it said it needed more thyme." },
        JokeTemplate("dramatic narrator", "overly_serious_narrator", PrankstarBotMood.PROCESSING) { topic -> "In a world of tiny inconveniences, $topic chose maximum drama." }
    )

    fun generate(prompt: String): PrankstarGeneratedJoke {
        val safeTopic = safety.sanitizePrompt(prompt).ifBlank { "this tiny chaos moment" }
        val lower = prompt.lowercase()
        val template = when {
            lower.contains("creepy") || lower.contains("whisper") -> templates.first { it.mode == "creepy whisper" }
            lower.contains("robot") || lower.contains("announcement") -> templates.first { it.mode == "robot announcement" }
            lower.contains("office") || lower.contains("meeting") -> templates.first { it.mode == "office announcement" }
            lower.contains("dad") -> templates.first { it.mode == "dad joke" }
            lower.contains("dramatic") || lower.contains("narrator") -> templates.first { it.mode == "dramatic narrator" }
            lower.contains("roast") -> templates.first { it.mode == "roast-lite" }
            else -> templates[(safeTopic.length + safeTopic.count { it.isWhitespace() }) % templates.size]
        }
        return PrankstarGeneratedJoke(template.builder(safeTopic), template.presetId, template.mood)
    }

    private data class JokeTemplate(
        val mode: String,
        val presetId: String,
        val mood: PrankstarBotMood,
        val builder: (String) -> String
    )
}
