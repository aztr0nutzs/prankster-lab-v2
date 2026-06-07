package com.pranksterlab.core.bot

import com.pranksterlab.components.bot.PrankstarBotMood
import com.pranksterlab.core.model.PrankSound
import com.pranksterlab.core.voice.VoicePresetLibrary

class PrankstarBotController(
    private val commandParser: PrankstarBotCommandParser = PrankstarBotCommandParser(),
    private val safety: PrankstarBotSafety = PrankstarBotSafety(),
    private val soundRecommender: PrankstarBotSoundRecommender = PrankstarBotSoundRecommender(),
    private val jokeGenerator: PrankstarBotJokeGenerator = PrankstarBotJokeGenerator(safety),
    private val responseBuilder: PrankstarBotResponseBuilder = PrankstarBotResponseBuilder()
) {
    fun handle(input: String, sounds: List<PrankSound>): PrankstarBotResult {
        val safetyResult = safety.check(input)
        if (!safetyResult.allowed) {
            val refusal = safetyResult.refusal ?: "I can keep it mischievous, not harmful. Try a safe sound or joke request."
            return PrankstarBotResult(
                message = refusal,
                actions = listOf(PrankstarBotAction.Refuse(refusal), PrankstarBotAction.ShowMessage(refusal)),
                mood = PrankstarBotMood.WARNING,
                suggestedChips = listOf("Find Funny", "Make Joke", "Open Stash", "Help")
            )
        }

        return when (val intent = commandParser.parse(input)) {
            is PrankstarBotIntent.SearchSounds -> recommend(intent.query, sounds, PrankstarBotMood.THINKING)
            is PrankstarBotIntent.RecommendSounds -> recommend(intent.vibe, sounds, PrankstarBotMood.PLAYING)
            is PrankstarBotIntent.GenerateJoke -> generateJoke(intent.prompt)
            is PrankstarBotIntent.BuildPrankPlan -> buildPlan(intent.prompt, sounds)
            is PrankstarBotIntent.ChooseVoice -> chooseVoice(intent.prompt)
            PrankstarBotIntent.PlayRandom -> playRandom(sounds)
            PrankstarBotIntent.StopAll -> PrankstarBotResult(
                message = "All prank audio stopped. Reactor is safe and quiet.",
                actions = listOf(PrankstarBotAction.StopAllSounds),
                mood = PrankstarBotMood.HAPPY,
                suggestedChips = listOf("Find Creepy", "Funny Sound", "Make Joke", "Open Stash")
            )
            PrankstarBotIntent.OpenStash -> navigate("library", "Opening Sound Stash. I’ll keep the neon warm.")
            PrankstarBotIntent.OpenJokes -> navigate("voice_lab", "Opening Voice Lab / Joke Gen. Bring the line, you tap Generate.")
            PrankstarBotIntent.OpenForge -> navigate("forge", "Opening Sound Forge for handcrafted chaos.")
            PrankstarBotIntent.Help -> PrankstarBotResult(
                message = responseBuilder.help(),
                actions = listOf(PrankstarBotAction.ShowMessage(responseBuilder.help())),
                mood = PrankstarBotMood.HAPPY
            )
            is PrankstarBotIntent.Unknown -> PrankstarBotResult(
                message = "I didn’t catch that signal yet. Try 'find creepy sounds' or 'make a joke about being late'.",
                actions = listOf(PrankstarBotAction.ShowMessage("Unknown request: ${intent.raw}")),
                mood = PrankstarBotMood.CONFUSED,
                suggestedChips = listOf("Find Creepy", "Funny Sound", "Make Joke", "Help")
            )
        }
    }

    private fun recommend(query: String, sounds: List<PrankSound>, mood: PrankstarBotMood): PrankstarBotResult {
        val recommended = soundRecommender.recommend(query, sounds.filter { it.assetPath.isNotBlank() || it.localUri != null })
        val message = responseBuilder.recommendations(query, recommended)
        val actions = mutableListOf<PrankstarBotAction>(PrankstarBotAction.ShowMessage(message))
        if (recommended.isNotEmpty()) {
            actions += PrankstarBotAction.ShowSoundRecommendations(recommended, "Matched name, category, tags, pack, and vibe keywords for '$query'.")
        }
        return PrankstarBotResult(
            message = message,
            actions = actions,
            mood = if (recommended.isEmpty()) PrankstarBotMood.CONFUSED else mood,
            suggestedChips = listOf("Play Random", "Make Joke", "Open Stash", "Stop All")
        )
    }

    private fun generateJoke(prompt: String): PrankstarBotResult {
        val generated = jokeGenerator.generate(prompt)
        return PrankstarBotResult(
            message = responseBuilder.joke(generated.text),
            actions = listOf(
                PrankstarBotAction.ShowMessage(generated.text),
                PrankstarBotAction.FillVoiceLabText(generated.text, generated.suggestedVoicePresetId)
            ),
            mood = generated.suggestedMood,
            suggestedChips = listOf("Send to Voice Lab", "Robot Voice", "Open Jokes", "Find Funny"),
            generatedText = generated.text,
            suggestedVoicePresetId = generated.suggestedVoicePresetId
        )
    }

    private fun chooseVoice(prompt: String): PrankstarBotResult {
        val lower = prompt.lowercase()
        val preset = VoicePresetLibrary.presets.firstOrNull { preset ->
            lower.contains(preset.id.lowercase()) || lower.contains(preset.displayName.lowercase()) || lower.contains(preset.category.name.lowercase())
        } ?: when {
            lower.contains("creepy") || lower.contains("whisper") -> VoicePresetLibrary.presets.firstOrNull { it.id == "dramatic_whisper" }
            lower.contains("robot") || lower.contains("sci") -> VoicePresetLibrary.presets.firstOrNull { it.id == "glitch_bot" || it.id == "robot_elevator" }
            lower.contains("office") -> VoicePresetLibrary.presets.firstOrNull { it.id == "mall_pa_system" }
            else -> VoicePresetLibrary.presets.firstOrNull { it.id == "glitch_bot" } ?: VoicePresetLibrary.presets.firstOrNull()
        }
        val text = prompt.ifBlank { "Diagnostic complete. Mischief engine online." }
        val presetId = preset?.id
        return PrankstarBotResult(
            message = "Voice pick ready${preset?.let { ": ${it.displayName}" } ?: ""}. I can place the text in Voice Lab; you stay in control of Generate.",
            actions = listOf(PrankstarBotAction.FillVoiceLabText(text, presetId), PrankstarBotAction.Navigate("voice_lab")),
            mood = PrankstarBotMood.HAPPY,
            generatedText = text,
            suggestedVoicePresetId = presetId
        )
    }

    private fun buildPlan(prompt: String, sounds: List<PrankSound>): PrankstarBotResult {
        val vibe = prompt.ifBlank { "funny" }
        val picks = soundRecommender.recommend(vibe, sounds, limit = 3)
        val joke = jokeGenerator.generate(vibe)
        val steps = buildList {
            add(PrankstarPrankStep("Confirm everyone is okay with a harmless audio gag.", "SAFETY_CHECK"))
            picks.firstOrNull()?.let { add(PrankstarPrankStep("Preview ${it.name} at low volume.", "PLAY_SOUND", soundId = it.id, delaySeconds = 0)) }
            add(PrankstarPrankStep("Use this optional Voice Lab line.", "VOICE_TEXT", voiceText = joke.text, delaySeconds = 3))
            picks.drop(1).firstOrNull()?.let { add(PrankstarPrankStep("If the vibe is still fun, play ${it.name} once.", "PLAY_SOUND", soundId = it.id, delaySeconds = 5)) }
        }
        val plan = PrankstarPrankPlan(
            title = "Harmless ${vibe.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }} Prank Plan",
            steps = steps,
            safetyNote = "Keep volume reasonable, do not target strangers, stop immediately if someone asks, and never pretend to be emergency or government services."
        )
        val message = responseBuilder.plan(plan.title)
        return PrankstarBotResult(
            message = message,
            actions = listOf(PrankstarBotAction.ShowPrankPlan(plan), PrankstarBotAction.ShowMessage(message)),
            mood = PrankstarBotMood.CELEBRATING,
            suggestedChips = listOf("Find Sounds", "Make Joke", "Open Stash", "Stop All"),
            prankPlan = plan
        )
    }

    private fun playRandom(sounds: List<PrankSound>): PrankstarBotResult {
        val sound = sounds.filter { it.isSafeForRandomMode }.randomOrNull()
        return if (sound == null) {
            PrankstarBotResult(
                message = "No safe random sound is loaded yet. Open Stash or try a search.",
                actions = listOf(PrankstarBotAction.ShowMessage("No safe random sound available.")),
                mood = PrankstarBotMood.CONFUSED
            )
        } else {
            PrankstarBotResult(
                message = "Deploying one safe random stash sound: ${sound.name}.",
                actions = listOf(PrankstarBotAction.PlaySound(sound), PrankstarBotAction.ShowSoundRecommendations(listOf(sound), "Safe random pick")),
                mood = PrankstarBotMood.PLAYING,
                suggestedChips = listOf("Stop All", "Find Funny", "Make Joke", "Open Stash")
            )
        }
    }

    private fun navigate(route: String, message: String): PrankstarBotResult = PrankstarBotResult(
        message = message,
        actions = listOf(PrankstarBotAction.Navigate(route), PrankstarBotAction.ShowMessage(message)),
        mood = PrankstarBotMood.HAPPY
    )
}
