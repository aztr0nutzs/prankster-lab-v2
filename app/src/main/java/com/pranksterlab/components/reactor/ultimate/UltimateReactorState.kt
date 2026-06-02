package com.pranksterlab.components.reactor.ultimate

enum class UltimateReactorTab {
    CORE,
    MODE,
    SENSOR,
    LOG
}

enum class UltimatePrankMode {
    CLASSIC,
    STEALTH,
    MEGA,
    CHAOS
}

enum class UltimatePrankType {
    SPLASH,
    SOUND,
    SMOKE,
    ZAP
}

data class UltimateReactorState(
    val powered: Boolean = true,
    val activeTab: UltimateReactorTab = UltimateReactorTab.CORE,
    val prankCount: Int = 47,
    val chargeLevel: Float = 0.0f,
    val intensity: Int = 72,
    val syncFrequency: Int = 88,
    val chaos: Int = 72,
    val sneak: Int = 45,
    val boom: Int = 88,
    val mode: UltimatePrankMode = UltimatePrankMode.CLASSIC,
    val prankType: UltimatePrankType = UltimatePrankType.SPLASH,
    val tempCelsius: Int = 847,
    val batteryPercent: Int = 72,
    val audioModEnabled: Boolean = true,
    val holoProjectorEnabled: Boolean = true,
    val mischiefAiEnabled: Boolean = true,
    val nerdModeEnabled: Boolean = true,
    val remoteArmEnabled: Boolean = false,
    val currentSoundName: String? = null,
    val currentSoundId: String? = null,
    val lastLogMessage: String = "ALL SYSTEMS GO",
    val alertMessage: String? = null,
    val isPlaying: Boolean = false,
    val isOverloaded: Boolean = false
)

fun UltimatePrankMode.accentColor(): androidx.compose.ui.graphics.Color = when (this) {
    UltimatePrankMode.CLASSIC -> androidx.compose.ui.graphics.Color(0xFF00E8FF)
    UltimatePrankMode.STEALTH -> androidx.compose.ui.graphics.Color(0xFF00AAFF)
    UltimatePrankMode.MEGA -> androidx.compose.ui.graphics.Color(0xFFFF6600)
    UltimatePrankMode.CHAOS -> androidx.compose.ui.graphics.Color(0xFFFF2200)
}
