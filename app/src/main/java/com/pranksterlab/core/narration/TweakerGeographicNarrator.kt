package com.pranksterlab.core.narration

class TweakerGeographicNarrator {
    private data class SafeInput(val action: String, val setting: String?)

    private val blockedPatterns = listOf(
        Regex("\\b(kill|hurt|shoot|stab|poison|attack|assault|threaten|swat|bomb|weapon)\\b", RegexOption.IGNORE_CASE),
        Regex("\\b(stalk|harass|bully|dox|follow them|film them|record them|secret recording|hide camera)\\b", RegexOption.IGNORE_CASE),
        Regex("\\b(police|cop|fire department|firefighter|ambulance|ems|paramedic|911|evacuation|official alert|government|fbi|irs)\\b", RegexOption.IGNORE_CASE),
        Regex("\\b(impersonate|clone|copy|sound like|voice of|pretend to be)\\b", RegexOption.IGNORE_CASE),
        Regex("\\b(my ex|my neighbor|my coworker|my boss|my teacher|my landlord|that stranger)\\b", RegexOption.IGNORE_CASE),
        Regex("\\b\\d{3}[-.\\s]?\\d{3}[-.\\s]?\\d{4}\\b"),
        Regex("\\b[\\w.%+-]+@[\\w.-]+\\.[A-Za-z]{2,}\\b"),
        Regex("\\b\\d{2,5}\\s+[A-Za-z0-9 .'-]+\\s+(street|st|road|rd|avenue|ave|drive|dr|lane|ln|boulevard|blvd)\\b", RegexOption.IGNORE_CASE)
    )
    private val probableFullName = Regex("\\b[A-Z][a-z]{2,}\\s+[A-Z][a-z]{2,}\\b")

    fun generate(request: TweakerGeographicRequest): TweakerGeographicResult {
        val safeInput = validate(request)
            ?: return refusal(request.tone)

        val scene = safeInput.setting?.let { "in $it" } ?: "in its chosen habitat"
        val action = safeInput.action
        val seed = stableIndex("${request.tone.name}|$action|${safeInput.setting.orEmpty()}", 997)
        val opener = openers(request.tone)[seed % openers(request.tone).size]
        val observation = observations(request.tone)[seed % observations(request.tone).size]
        val closer = closers(request.tone)[seed % closers(request.tone).size]
        val soundCue = if (request.includeSoundCue) soundCue(request.tone) else null
        val narration = listOfNotNull(
            "$opener $scene, our subject begins $action.",
            observation,
            closer,
            soundCue?.let { "Suggested sound cue: $it." }
        ).joinToString(" ").take(520)

        return TweakerGeographicResult(
            title = "Tweakographic Field Report",
            narration = narration,
            tone = request.tone,
            suggestedVoicePresetId = suggestedPreset(request.tone),
            suggestedSoundQuery = soundCue
        )
    }

    private fun validate(request: TweakerGeographicRequest): SafeInput? {
        val action = clean(request.action, 120)
        val setting = request.setting?.let { clean(it, 80) }?.ifBlank { null }
        if (action.isBlank()) return null
        val combined = "$action ${setting.orEmpty()}"
        if (blockedPatterns.any { it.containsMatchIn(combined) }) return null
        if (probableFullName.containsMatchIn(request.action) || request.setting?.let { probableFullName.containsMatchIn(it) } == true) return null
        return SafeInput(action, setting)
    }

    private fun clean(value: String, maxLength: Int): String = value
        .replace(Regex("[\\r\\n\\t]+"), " ")
        .replace(Regex("\\s+"), " ")
        .trim()
        .take(maxLength)

    private fun refusal(tone: TweakerGeographicTone) = TweakerGeographicResult(
        title = "Try a safer field report",
        narration = "I can generate an original mock-documentary line about harmless fictional behavior, but not targeted real-person, emergency, impersonation, violent, or non-consensual prompts.",
        tone = tone,
        suggestedVoicePresetId = "overly_serious_narrator",
        suggestedSoundQuery = null,
        safetyNote = "Use a generic subject and a harmless action, like looking for a lighter or guarding the last snack."
    )

    private fun openers(tone: TweakerGeographicTone) = when (tone) {
        TweakerGeographicTone.MILD -> listOf("Observe quietly.", "In a calm corner of the ecosystem.", "With minimal disturbance.")
        TweakerGeographicTone.BALANCED -> listOf("Observe the specimen.", "Here we witness a delicate ritual.", "Across the domestic plains.")
        TweakerGeographicTone.CHAOTIC -> listOf("Chaos enters the habitat.", "The ritual accelerates without warning.", "The local energy spikes immediately.")
        TweakerGeographicTone.SUSPENSEFUL -> listOf("Something stirs in the low light.", "A hush falls over the habitat.", "The scene becomes unusually tense.")
        TweakerGeographicTone.DRAMATIC -> listOf("History pauses for this moment.", "Against impossible odds.", "In the grand theater of ordinary life.")
    }

    private fun observations(tone: TweakerGeographicTone) = when (tone) {
        TweakerGeographicTone.MILD -> listOf(
            "Each small decision appears harmless, though deeply important to the subject.",
            "The movement is careful, curious, and only slightly unreasonable.",
            "Nearby objects remain safe, but clearly judged."
        )
        TweakerGeographicTone.BALANCED -> listOf(
            "The subject scans the terrain with the focus of a detective who misplaced the evidence.",
            "A pause. A glance. A tiny negotiation with reality begins.",
            "Experts agree this behavior is common in habitats containing snacks, chargers, or missing keys."
        )
        TweakerGeographicTone.CHAOTIC -> listOf(
            "Momentum builds. Confidence departs. Several invisible buttons have clearly been pressed.",
            "The subject changes tactics twice before forming a first tactic.",
            "Every surface is now a suspect and the mission refuses to stay small."
        )
        TweakerGeographicTone.SUSPENSEFUL -> listOf(
            "No one moves. The subject senses that the answer is close, or at least under something.",
            "A faint clue appears, then immediately becomes less helpful.",
            "The silence grows heavier as the search pattern becomes personal."
        )
        TweakerGeographicTone.DRAMATIC -> listOf(
            "The stakes are technically low, yet emotionally listed as catastrophic.",
            "Generations may never know why this mattered, only that it did.",
            "The subject stands at the edge of triumph, confusion, and a suspiciously empty pocket."
        )
    }

    private fun closers(tone: TweakerGeographicTone) = when (tone) {
        TweakerGeographicTone.MILD -> listOf(
            "For now, the habitat remains peaceful.",
            "The journey continues at a respectful indoor volume.",
            "Nature, once again, chooses mild inconvenience."
        )
        TweakerGeographicTone.BALANCED -> listOf(
            "Whether success arrives is unclear, but the commitment is undeniable.",
            "The habitat adapts. The subject absolutely does not.",
            "And so the cycle continues, graceful in theory and messy in practice."
        )
        TweakerGeographicTone.CHAOTIC -> listOf(
            "The habitat has lost control of the meeting.",
            "No conclusions are reached, but several drawers have been emotionally opened.",
            "Scientists recommend backing away and labeling this a vibe."
        )
        TweakerGeographicTone.SUSPENSEFUL -> listOf(
            "The answer waits somewhere nearby, deeply committed to being annoying.",
            "For now, the mystery survives another lap around the room.",
            "The subject disappears into the next clue with unnecessary seriousness."
        )
        TweakerGeographicTone.DRAMATIC -> listOf(
            "This is not survival. This is legend with lint on it.",
            "The universe watches, politely confused.",
            "Thus ends a chapter no one asked for and everyone will remember."
        )
    }

    private fun suggestedPreset(tone: TweakerGeographicTone) = when (tone) {
        TweakerGeographicTone.MILD -> "tiny_documentary_voice"
        TweakerGeographicTone.BALANCED -> "overly_serious_narrator"
        TweakerGeographicTone.CHAOTIC -> "confused_narrator"
        TweakerGeographicTone.SUSPENSEFUL -> "dramatic_whisper"
        TweakerGeographicTone.DRAMATIC -> "movie_trailer_guy"
    }

    private fun soundCue(tone: TweakerGeographicTone) = when (tone) {
        TweakerGeographicTone.MILD -> "soft ambience"
        TweakerGeographicTone.BALANCED -> "documentary"
        TweakerGeographicTone.CHAOTIC -> "chaos"
        TweakerGeographicTone.SUSPENSEFUL -> "creepy"
        TweakerGeographicTone.DRAMATIC -> "dramatic"
    }

    private fun stableIndex(value: String, modulo: Int): Int = value.fold(0) { acc, c ->
        ((acc * 31) + c.code) and Int.MAX_VALUE
    } % modulo
}
