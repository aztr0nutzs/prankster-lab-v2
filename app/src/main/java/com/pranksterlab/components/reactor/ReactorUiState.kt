package com.pranksterlab.components.reactor

/**
 * ReactorUiState.kt
 *
 * Canonical state model for the Prankster Core Reactor.
 *
 * Backwards-compatible note:
 *  - ReactorState is the primary enum (kept from original ReactorCorePanel.kt).
 *  - ReactorMode typealias preserved for any external callers that referenced the old name.
 *  - ReactorUiState is extended with all new required fields while keeping existing fields intact.
 */

enum class ReactorState {
    IDLE,
    ARMED,
    CHARGING,
    PLAYING,
    COOLDOWN,
    GENERATING,
    ERROR,
    WARNING,
    DISABLED
}

/** Backwards-compatible alias kept for existing call sites. */
@Deprecated("Use ReactorState", ReplaceWith("ReactorState"))
typealias ReactorMode = ReactorState

data class ReactorUiState(
    // ── Core state ──────────────────────────────────────────────
    val state: ReactorState = ReactorState.IDLE,

    // ── Playback telemetry ───────────────────────────────────────
    val currentSoundId: String? = null,
    val currentSoundName: String? = null,
    val currentCategory: String? = null,
    val selectedCategory: String? = null,

    // ── Catalog counts ───────────────────────────────────────────
    val loadedSoundCount: Int = 0,
    val safeSoundCount: Int = 0,
    val generatedSoundCount: Int = 0,
    val favoriteSoundCount: Int = 0,
    val hasCustomSounds: Boolean = false,

    // ── Interaction state ────────────────────────────────────────
    val chargePercent: Float = 0f,
    val playbackProgress: Float? = null,

    // ── Mode flags ───────────────────────────────────────────────
    val isSafeMode: Boolean = true,
    val isMuted: Boolean = false,

    // ── Error / trace ────────────────────────────────────────────
    val lastError: String? = null,
    val lastAction: String? = null,
    val recentSounds: List<String> = emptyList()
)
