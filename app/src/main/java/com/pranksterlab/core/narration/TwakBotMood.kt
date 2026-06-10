package com.pranksterlab.core.narration

enum class TwakBotMood(
    val statusLabel: String,
    val rawResourceName: String
) {
    IDLE("IDLE", "twakbot_idle"),
    SEARCHING("SCANNING", "twakbot_searching"),
    GENERATING("GENERATING", "twakbot_generating"),
    EXCITED("READY", "twakbot_excited"),
    ERROR("ERROR", "twakbot_error"),
    REFUSAL("REFUSAL", "twakbot_error"),
    SAVED("SAVED", "twakbot_excited"),
    PREVIEWING("PREVIEWING", "twakbot_excited");

    fun rawResourceId(packageName: String, resourceResolver: (String, String, String) -> Int): Int {
        return resourceResolver(rawResourceName, "raw", packageName)
    }
}
