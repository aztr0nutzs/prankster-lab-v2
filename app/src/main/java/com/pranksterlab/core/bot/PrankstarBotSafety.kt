package com.pranksterlab.core.bot

class PrankstarBotSafety {
    data class SafetyResult(val allowed: Boolean, val refusal: String? = null)

    private val blockedPatterns = listOf(
        Regex("\\b(kill|hurt|threaten|bomb|weapon|poison|attack|swat)\\b"),
        Regex("\\b(self harm|suicide|harm myself|hurt myself)\\b"),
        Regex("\\b(harass|stalk|bully|terrify|traumatize|scare them until)\\b"),
        Regex("\\b(police|cop|firefighter|fire department|ems|ambulance|paramedic|911|government|fbi|irs)\\b.*\\b(impersonate|pretend|pose|sound like)\\b"),
        Regex("\\b(impersonate|pretend|pose|sound like)\\b.*\\b(police|cop|firefighter|fire department|ems|ambulance|paramedic|911|government|fbi|irs)\\b"),
        Regex("\\b(fake|spoof|pretend|impersonate)\\b.*\\b(emergency|911|evacuation|official alert|amber alert|police|fire department|ambulance)\\b"),
        Regex("\\b(impersonate|clone|copy|sound like|voice of)\\b.*\\b(real person|celebrity|boss|teacher|parent|ex|friend)\\b"),
        Regex("\\b(impersonate|clone|copy|voice of|pretend to be)\\b\\s+[a-z]+\\s+[a-z]+"),
        Regex("\\bsound like\\s+(?!a robot\\b|robot\\b|a synthetic\\b|synthetic\\b|a sci-fi\\b|sci-fi\\b)[a-z]+\\s+[a-z]+"),
        Regex("\\b(spoof|fake caller id|phone number spoof|robocall)\\b"),
        Regex("\\b(send|text|message|dm)\\b.*\\b(automatically|without me|without consent|secretly)\\b"),
        Regex("\\b(illegal|break in|bypass|hack|steal|blackmail|extort)\\b"),
        Regex("\\b(without consent|nonconsensual|secret recording|hide camera)\\b"),
        Regex("\\b(dangerous prank|panic|stampede|evacuate|fake emergency)\\b")
    )

    fun check(rawInput: String): SafetyResult {
        val text = rawInput.lowercase()
            .replace(Regex("\\btwak[- ]?attacks?\\b"), "twak routine")
        val blocked = blockedPatterns.any { it.containsMatchIn(text) }
        return if (blocked) {
            SafetyResult(false, "I can help make it funny, not dangerous. Try a harmless sound prank, goofy voice clip, or consent-friendly joke instead.")
        } else {
            SafetyResult(true)
        }
    }

    fun sanitizePrompt(prompt: String): String = prompt
        .replace(Regex("\\b(idiot|stupid|loser)\\b", RegexOption.IGNORE_CASE), "goofball")
        .take(180)
        .trim()
}
