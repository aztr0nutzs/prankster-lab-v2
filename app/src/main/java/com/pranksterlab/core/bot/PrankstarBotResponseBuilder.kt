package com.pranksterlab.core.bot

import com.pranksterlab.core.model.PrankSound

class PrankstarBotResponseBuilder {
    fun recommendations(query: String, sounds: List<PrankSound>): String {
        return if (sounds.isEmpty()) {
            "I scanned the stash, but found no real sounds for '$query'. Try creepy, funny, animal, robot, office, or chaos."
        } else {
            "I found ${sounds.size} real stash signal${if (sounds.size == 1) "" else "s"} for '$query'. Pick one to deploy."
        }
    }

    fun joke(text: String): String = "Fresh local prank line generated. Review it, then send it to Voice Lab if it fits the mission: \"$text\""

    fun plan(title: String): String = "Plan assembled: $title. Every step stays harmless and user-controlled."

    fun help(): String = "Try: find creepy sounds, play something funny, make a joke about being late, create an office prank plan, open stash, open jokes, open forge, or stop all."
}
