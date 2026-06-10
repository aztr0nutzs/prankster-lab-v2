package com.pranksterlab.core.bot

import com.pranksterlab.core.model.PrankSound

class PrankstarBotSoundRecommender {
    private val vibeMap = mapOf(
        "creepy" to listOf("creepy", "horror", "ghost", "whisper", "scary", "spooky", "haunt"),
        "scary" to listOf("creepy", "horror", "ghost", "whisper", "scary", "spooky", "haunt"),
        "funny" to listOf("funny", "cartoon", "laugh", "goofy", "fart", "silly", "comedy"),
        "animal" to listOf("animal", "dog", "cat", "bird", "monkey", "squirrel"),
        "voice" to listOf("voice", "robot", "speech", "talking", "announcement"),
        "chaos" to listOf("explosion", "alarm", "siren", "glitch", "zap", "chaos"),
        "office" to listOf("office", "phone", "bell", "announcement", "meeting"),
        "robot" to listOf("robot", "sci-fi", "scifi", "glitch", "machine", "bot"),
        "prank" to listOf("prank", "funny", "cartoon", "voice", "misc", "surprise")
    )

    fun recommend(query: String, sounds: List<PrankSound>, limit: Int = 6): List<PrankSound> {
        val tokens = expandTokens(query)
        if (tokens.isEmpty()) return sounds.filter { it.isSafeForRandomMode }.take(limit)
        return sounds.asSequence()
            .map { it to score(it, tokens) }
            .filter { (_, score) -> score > 0 }
            .sortedWith(compareByDescending<Pair<PrankSound, Int>> { it.second }.thenBy { it.first.name })
            .map { it.first }
            .distinctBy { it.id }
            .take(limit.coerceIn(3, 6))
            .toList()
    }

    private fun expandTokens(query: String): Set<String> {
        val base = query.lowercase().split(Regex("[^a-z0-9-]+"))
            .filter { it.length > 1 }
            .toMutableSet()
        vibeMap.forEach { (vibe, mapped) ->
            if (base.contains(vibe) || query.lowercase().contains(vibe)) base.addAll(mapped)
        }
        return base
    }

    private fun score(sound: PrankSound, tokens: Set<String>): Int {
        val fields = listOf(
            sound.name to 5,
            sound.category to 4,
            sound.packId.orEmpty() to 3,
            sound.description.orEmpty() to 2,
            sound.previewLabel.orEmpty() to 2,
            sound.prankStyle.orEmpty() to 2,
            sound.tags.joinToString(" ") to 4
        )
        return fields.sumOf { (value, weight) ->
            val text = value.lowercase()
            tokens.sumOf { token -> if (text.contains(token)) weight else 0 }
        } + if (sound.isSafeForRandomMode) 1 else 0
    }
}
