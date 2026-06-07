package com.pranksterlab.core.bot

class PrankstarBotCommandParser {
    fun parse(input: String): PrankstarBotIntent {
        val raw = input.trim()
        if (raw.isBlank()) return PrankstarBotIntent.Help
        val normalized = raw.lowercase().replace(Regex("\\s+"), " ")

        if (normalized in setOf("help", "?", "what can you do")) return PrankstarBotIntent.Help
        if (containsAny(normalized, "stop all", "stop", "silence", "kill audio")) return PrankstarBotIntent.StopAll
        if (containsAny(normalized, "open stash", "stash", "library", "sound stash")) return PrankstarBotIntent.OpenStash
        if (containsAny(normalized, "open jokes", "jokes", "joke gen", "voice lab")) return PrankstarBotIntent.OpenJokes
        if (containsAny(normalized, "open forge", "forge", "sound forge")) return PrankstarBotIntent.OpenForge
        if (containsAny(normalized, "open system", "system", "settings")) return PrankstarBotIntent.OpenSystem
        if (containsAny(normalized, "random", "surprise me")) return PrankstarBotIntent.PlayRandom

        if (containsAny(normalized, "make a joke", "generate joke", "joke about", "roast", "funny comment", "make joke", "robot announcement", "create a robot announcement", "create robot announcement")) {
            return PrankstarBotIntent.GenerateJoke(cleanPrompt(normalized, raw, listOf("make a joke about", "generate joke about", "joke about", "make a joke", "generate joke", "roast", "funny comment", "create a robot announcement", "create robot announcement", "robot announcement")))
        }
        if (containsAny(normalized, "voice", "say")) {
            return PrankstarBotIntent.ChooseVoice(cleanPrompt(normalized, raw, listOf("choose voice for", "voice for", "say", "voice")))
        }
        if (containsAny(normalized, "plan", "prank plan", "create a creepy prank plan", "build prank")) {
            return PrankstarBotIntent.BuildPrankPlan(cleanPrompt(normalized, raw, listOf("create a", "build a", "make a", "prank plan", "plan", "prank")))
        }
        if (containsAny(normalized, "find", "search", "show")) {
            return PrankstarBotIntent.SearchSounds(cleanPrompt(normalized, raw, listOf("find", "search", "show", "sounds", "sound", "pranks", "prank")))
        }
        if (containsAny(normalized, "play")) {
            return PrankstarBotIntent.PlayRecommended(cleanPrompt(normalized, raw, listOf("play", "something", "sound", "sounds", "a", "an")))
        }
        if (containsAny(normalized, "recommend", "something")) {
            return PrankstarBotIntent.RecommendSounds(cleanPrompt(normalized, raw, listOf("play", "recommend", "something", "sound", "sounds")))
        }

        val vibe = listOf("creepy", "funny", "animal", "scary", "robot", "chaos", "office", "voice", "prank")
            .firstOrNull { normalized.contains(it) }
        return if (vibe != null) PrankstarBotIntent.RecommendSounds(vibe) else PrankstarBotIntent.Unknown(raw)
    }

    private fun containsAny(value: String, vararg needles: String): Boolean = needles.any { value.contains(it) }

    private fun cleanPrompt(normalized: String, raw: String, removals: List<String>): String {
        var cleaned = normalized
        removals.sortedByDescending { it.length }.forEach { cleaned = cleaned.replace(it, " ") }
        cleaned = cleaned.replace(Regex("\\b(about|for|me|please|a|an|the)\\b"), " ").replace(Regex("\\s+"), " ").trim()
        return cleaned.ifBlank { raw.trim() }
    }
}
