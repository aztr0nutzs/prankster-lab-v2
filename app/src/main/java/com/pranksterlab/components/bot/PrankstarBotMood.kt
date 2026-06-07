package com.pranksterlab.components.bot

import android.content.Context
import androidx.annotation.RawRes

enum class PrankstarBotMood {
    IDLE,
    WAKEUP,
    ARMED,
    PLAYING,
    PROCESSING,
    GENERATING,
    THINKING,
    TYPING,
    LISTENING,
    HAPPY,
    SAVED,
    CELEBRATING,
    ECSTATIC,
    SURPRISED,
    WARNING,
    ERROR,
    CONFUSED,
    ANGRY,
    BORED,
    BORED_ALT,
    SEARCHING,
    SEARCHING_ALT,
    RELAXED,
    POWERUP,
    SAD,
    SHUTDOWN
}

fun PrankstarBotMood.videoResourceName(): String = when (this) {
    PrankstarBotMood.IDLE -> "prankstar_bot_relaxed"
    PrankstarBotMood.WAKEUP -> "prankstar_bot_wakeup"
    PrankstarBotMood.ARMED -> "prankstar_bot_happy"
    PrankstarBotMood.PLAYING -> "prankstar_bot_happy"
    PrankstarBotMood.PROCESSING -> "prankstar_bot_processing"
    PrankstarBotMood.GENERATING -> "prankstar_bot_processing"
    PrankstarBotMood.THINKING -> "prankstar_bot_thinking"
    PrankstarBotMood.TYPING -> "prankstar_bot_typing"
    PrankstarBotMood.LISTENING -> "prankstar_bot_typing"
    PrankstarBotMood.HAPPY -> "prankstar_bot_happy"
    PrankstarBotMood.SAVED -> "prankstar_bot_celebrate"
    PrankstarBotMood.CELEBRATING -> "prankstar_bot_celebrate"
    PrankstarBotMood.ECSTATIC -> "prankstar_bot_ecstatic"
    PrankstarBotMood.SURPRISED -> "prankstar_bot_surprised"
    PrankstarBotMood.WARNING -> "prankstar_bot_warning"
    PrankstarBotMood.ERROR -> "prankstar_bot_confused"
    PrankstarBotMood.CONFUSED -> "prankstar_bot_confused"
    PrankstarBotMood.ANGRY -> "prankstar_bot_angry"
    PrankstarBotMood.BORED -> "prankstar_bot_bored1"
    PrankstarBotMood.BORED_ALT -> "prankstar_bot_bored2"
    PrankstarBotMood.SEARCHING -> "prankstar_bot_searching"
    PrankstarBotMood.SEARCHING_ALT -> "prankstar_bot_searching2"
    PrankstarBotMood.RELAXED -> "prankstar_bot_relaxed"
    PrankstarBotMood.POWERUP -> "prankstar_bot_powerup"
    PrankstarBotMood.SAD -> "prankstar_bot_sad"
    PrankstarBotMood.SHUTDOWN -> "prankstar_bot_shutdown"
}

private fun PrankstarBotMood.fallbackVideoResourceNames(): List<String> = when (this) {
    PrankstarBotMood.SEARCHING, PrankstarBotMood.SEARCHING_ALT -> listOf("prankstar_bot_thinking", "prankstar_bot_processing")
    PrankstarBotMood.BORED, PrankstarBotMood.BORED_ALT -> listOf("prankstar_bot_relaxed", "prankstar_bot_happy")
    PrankstarBotMood.SAD -> listOf("prankstar_bot_confused")
    PrankstarBotMood.POWERUP -> listOf("prankstar_bot_wakeup")
    PrankstarBotMood.RELAXED, PrankstarBotMood.IDLE -> listOf("prankstar_bot_happy")
    else -> listOf("prankstar_bot_happy")
}

@RawRes
fun PrankstarBotMood.videoResId(context: Context): Int {
    val names = listOf(videoResourceName()) + fallbackVideoResourceNames()
    return names.asSequence()
        .map { context.resources.getIdentifier(it, "raw", context.packageName) }
        .firstOrNull { it != 0 } ?: 0
}
