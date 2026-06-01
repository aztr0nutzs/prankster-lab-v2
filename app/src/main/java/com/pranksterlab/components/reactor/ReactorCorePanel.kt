package com.pranksterlab.components.reactor

/**
 * ReactorCorePanel.kt  — BACKWARDS-COMPATIBLE DELEGATION WRAPPER
 *
 * HomeScreen.kt (and any other existing call site) still calls:
 *
 *   ReactorCorePanel(
 *       currentSoundName, currentCategory, isPlaying, hasCustomSounds,
 *       playbackError, loadedSoundCount, safeSoundCount,
 *       onTrigger, onStop, onCategoryChange, coreImageRes
 *   )
 *
 * This file converts those parameters into a ReactorUiState and delegates
 * rendering entirely to PranksterCoreReactor.  All original behaviour is
 * preserved — including tap/toggle, long-press charge, category ring, stop,
 * error display, haptics — because PranksterCoreReactor implements them.
 *
 * NOTE: ReactorState / ReactorMode / ReactorUiState now live in ReactorUiState.kt.
 *       getCategoryColor / getCategoryIcon now live in PranksterCoreReactor.kt.
 */

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import kotlinx.coroutines.delay

/**
 * Legacy-compatible overload used by HomeScreen.kt.
 */
@Composable
fun ReactorCorePanel(
    currentSoundName: String?,
    currentCategory: String,
    isPlaying: Boolean,
    hasCustomSounds: Boolean,
    playbackError: String? = null,
    loadedSoundCount: Int = 0,
    safeSoundCount: Int = 0,
    onTrigger: (category: String, intensity: Int) -> Unit,
    onStop: () -> Unit,
    onCategoryChange: (String) -> Unit,
    coreImageRes: Int = com.pranksterlab.R.drawable.prankstar_core,
    // Forward-compatibility navigation hooks
    onOpenStash: () -> Unit = {},
    onOpenJokes: () -> Unit = {},
    onOpenForge: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val haptics = LocalHapticFeedback.current

    // ── Local reactor state (mirrors original ReactorCorePanel logic) ─────
    var internalState by remember { mutableStateOf(ReactorState.IDLE) }
    var chargeLevel   by remember { mutableFloatStateOf(0f) }
    var systemError   by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(isPlaying, playbackError) {
        when {
            playbackError != null -> {
                internalState = ReactorState.ERROR
                systemError   = playbackError
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                delay(2400)
                internalState = if (isPlaying) ReactorState.PLAYING else ReactorState.ARMED
                systemError   = null
            }
            isPlaying -> internalState = ReactorState.PLAYING
            !isPlaying && internalState == ReactorState.PLAYING -> {
                internalState = ReactorState.COOLDOWN
                delay(900)
                internalState = ReactorState.ARMED
            }
            !isPlaying && internalState == ReactorState.IDLE -> internalState = ReactorState.ARMED
        }
    }

    // ── Build ReactorUiState from legacy params ────────────────────────────
    val uiState = ReactorUiState(
        state            = internalState,
        currentSoundName = currentSoundName,
        selectedCategory = currentCategory,
        currentCategory  = currentCategory,
        loadedSoundCount = loadedSoundCount,
        safeSoundCount   = safeSoundCount,
        hasCustomSounds  = hasCustomSounds,
        chargePercent    = chargeLevel,
        lastError        = systemError,
        isSafeMode       = true
    )

    // ── Delegate entirely to PranksterCoreReactor ─────────────────────────
    PranksterCoreReactor(
        uiState              = uiState,
        coreImageRes         = coreImageRes,
        modifier             = modifier,
        onCoreTap            = {
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
            onTrigger(currentCategory, 1)
        },
        onCoreLongPressStart = {},
        onCoreLongPressEnd   = { charged ->
            if (charged) {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                onTrigger(currentCategory, if (chargeLevel >= 0.98f) 3 else 2)
            }
            chargeLevel = 0f
        },
        onCoreChargeLevelChanged = { chargeLevel = it },
        onStopAll            = {
            onStop()
            chargeLevel   = 0f
            internalState = ReactorState.ARMED
        },
        onCategorySelected   = { cat ->
            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onCategoryChange(cat)
        },
        onOpenStash          = onOpenStash,
        onOpenJokes          = onOpenJokes,
        onOpenForge          = onOpenForge,
    )
}
