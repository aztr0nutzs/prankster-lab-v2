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
    SHUTDOWN
}

fun PrankstarBotMood.videoResourceName(): String = when (this) {
    PrankstarBotMood.IDLE -> "prankstar_bot_happy"
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
    PrankstarBotMood.SHUTDOWN -> "prankstar_bot_shutdown"
}

@RawRes
fun PrankstarBotMood.videoResId(context: Context): Int {
    return context.resources.getIdentifier(videoResourceName(), "raw", context.packageName)
}
