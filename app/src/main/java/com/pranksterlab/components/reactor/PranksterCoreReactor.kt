package com.pranksterlab.components.reactor

/**
 * PranksterCoreReactor.kt
 *
 * The visual and interactive centrepiece of Prankster Lab.
 *
 * Architecture:
 *  ┌─ PranksterCoreReactor          public API composable, receives state + callbacks
 *  │   ├─ ReactorStatusBar          top telemetry row (CORE / LOADED / SAFE pills)
 *  │   ├─ FullReactorImage          prankstar_core.png as the complete reactor face
 *  │   └─ InfoReadout               sound name / category / error strip below reactor
 *
 * All existing ReactorCorePanel.kt behaviour is preserved here.
 * ReactorCorePanel.kt is kept as a thin delegation wrapper for backwards compatibility.
 */

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pranksterlab.components.LabelCaps
import com.pranksterlab.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin

// ─────────────────────────────────────────────────────────────────────────────
// Public API
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Enhanced Core Reactor composable.
 *
 * Minimal required params match existing ReactorCorePanel usage so HomeScreen
 * needs only a one-line change.  All new params have safe defaults.
 */
@Composable
fun PranksterCoreReactor(
    // ── Required ───────────────────────────────────────────────────────────
    uiState: ReactorUiState,
    onCoreTap: () -> Unit,
    onStopAll: () -> Unit,
    onCategorySelected: (String) -> Unit,
    // ── Navigation ─────────────────────────────────────────────────────────
    onOpenStash: () -> Unit = {},
    onOpenJokes: () -> Unit = {},
    onOpenForge: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
    // ── Long press charge ──────────────────────────────────────────────────
    onCoreLongPressStart: () -> Unit = {},
    onCoreLongPressEnd: (charged: Boolean) -> Unit = {},
    onCoreChargeLevelChanged: (Float) -> Unit = {},
    // ── Extras ─────────────────────────────────────────────────────────────
    coreImageRes: Int = com.pranksterlab.R.drawable.prankstar_core,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val haptics = LocalHapticFeedback.current

    // ── Local interaction state ───────────────────────────────────────────
    var chargeLevel by remember { mutableFloatStateOf(0f) }
    val currentCategory = uiState.selectedCategory ?: uiState.currentCategory ?: "FUNNY"
    val displayUiState = if (chargeLevel > 0f && (uiState.state == ReactorState.IDLE || uiState.state == ReactorState.ARMED)) {
        uiState.copy(state = ReactorState.CHARGING, chargePercent = chargeLevel)
    } else {
        uiState
    }

    val baseCategories = listOf("FUNNY", "CREEPY", "ANIMAL", "VOICE", "FIGHTER", "CARTOON")
    val categories = if (uiState.hasCustomSounds) baseCategories + "CUSTOM" else baseCategories

    // ── Derived accent color ──────────────────────────────────────────────
    val categoryColor = getCategoryColor(currentCategory)
    val accentColor = when (displayUiState.state) {
        ReactorState.IDLE     -> categoryColor.copy(alpha = 0.55f)
        ReactorState.ARMED    -> categoryColor
        ReactorState.PLAYING  -> Color.White
        ReactorState.CHARGING -> Color(0xFFFFD400)
        ReactorState.COOLDOWN -> categoryColor.copy(alpha = 0.45f)
        ReactorState.ERROR    -> Color(0xFFFF1744)
        ReactorState.WARNING  -> Color(0xFFFF9800)
        ReactorState.GENERATING -> CyanAccent
        ReactorState.DISABLED -> Color(0xFF555555)
    }

    // ─────────────────────────────────────────────────────────────────────
    // Animation drivers — all use rememberInfiniteTransition so they never
    // allocate on every frame.  Durations are derived from state so they
    // hot-switch when state changes.
    // ─────────────────────────────────────────────────────────────────────
    val transition = rememberInfiniteTransition(label = "reactor_master")

    val errorFlash by transition.animateFloat(
        0.2f, 1f,
        infiniteRepeatable(tween(220, easing = LinearEasing), RepeatMode.Reverse),
        label = "err_flash"
    )
    // Waveform bar animation — 8 bars each with its own phase offset
    val wavePhase by transition.animateFloat(
        0f, (2f * PI).toFloat(),
        infiniteRepeatable(tween(1200, easing = LinearEasing)),
        label = "wave_phase"
    )

    val animatedCharge by animateFloatAsState(
        targetValue = chargeLevel,
        animationSpec = tween(120, easing = LinearEasing),
        label = "charge_anim"
    )

    // ─────────────────────────────────────────────────────────────────────
    // Root layout
    // ─────────────────────────────────────────────────────────────────────
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .semantics { contentDescription = "Prankster Core Reactor. Status: ${displayUiState.state.name}." },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // ── TOP STATUS BAR ────────────────────────────────────────────────
        ReactorStatusBar(ui = displayUiState, accent = accentColor)

        Spacer(Modifier.height(10.dp))

        // ── WAVEFORM HALO STRIP (shows only when PLAYING) ─────────────────
        if (displayUiState.state == ReactorState.PLAYING || displayUiState.state == ReactorState.GENERATING) {
            WaveformHaloStrip(accent = accentColor, phase = wavePhase)
            Spacer(Modifier.height(6.dp))
        } else {
            Spacer(Modifier.height(20.dp))
        }

        // ═══════════════════════════════════════════════════════════════════
        // MAIN REACTOR BOX
        // ═══════════════════════════════════════════════════════════════════
        Box(
            modifier = Modifier
                .size(330.dp)
                // ── Tap: play random or stop ───────────────────────────────
                .pointerInput(displayUiState.state) {
                    detectTapGestures(
                        onTap = {
                            when (displayUiState.state) {
                                ReactorState.PLAYING -> {
                                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onStopAll()
                                }
                                ReactorState.DISABLED -> Unit
                                else -> {
                                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onCoreTap()
                                }
                            }
                        },
                        onLongPress = {
                            if (displayUiState.state == ReactorState.IDLE || displayUiState.state == ReactorState.ARMED) {
                                scope.launch {
                                    onCoreLongPressStart()
                                    var t = 0f
                                    val maxMs = 1300f
                                    while (t < maxMs && chargeLevel < 1f) {
                                        delay(35)
                                        t += 35
                                        chargeLevel = (t / maxMs).coerceAtMost(1f)
                                        onCoreChargeLevelChanged(chargeLevel)
                                        if ((t.toInt() % 130) == 0) {
                                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        }
                                    }
                                    if (chargeLevel >= 0.98f) {
                                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                        onCoreLongPressEnd(true)
                                        chargeLevel = 0f
                                        onCoreChargeLevelChanged(0f)
                                    }
                                }
                            }
                        },
                        onPress = {
                            tryAwaitRelease()
                            if (chargeLevel > 0f && chargeLevel < 0.98f) {
                                if (chargeLevel > 0.4f) {
                                    onCoreLongPressEnd(true)
                                } else {
                                    onCoreLongPressEnd(false)
                                }
                                chargeLevel = 0f
                                onCoreChargeLevelChanged(0f)
                            }
                        }
                    )
                }
                // ── Swipe left/right to change category ───────────────────
                .pointerInput(currentCategory, categories) {
                    detectDragGestures { change, dragAmount ->
                        if (abs(dragAmount.x) > 28f) {
                            val idx = categories.indexOf(currentCategory)
                            val next = if (dragAmount.x > 0)
                                (idx - 1 + categories.size) % categories.size
                            else
                                (idx + 1) % categories.size
                            if (next != idx) {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onCategorySelected(categories[next])
                            }
                            change.consume()
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {

            // The artwork is the reactor. Keep it full-size and unmasked so the
            // image defines the whole device instead of becoming a center decal.
            Image(
                painter = painterResource(id = coreImageRes),
                contentDescription = "Prankstar Reactor",
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        alpha = when (displayUiState.state) {
                            ReactorState.ERROR    -> 0.72f + 0.22f * errorFlash
                            ReactorState.DISABLED -> 0.45f
                            else -> 1f
                        }
                    },
                contentScale = ContentScale.Fit
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .semantics { contentDescription = "Play random prank sound. ${if (displayUiState.state == ReactorState.PLAYING) "Currently playing. Tap to stop." else "Tap to deploy."}" },
                contentAlignment = Alignment.Center
            ) {
                // State affordance overlay / stop button
                if (displayUiState.state == ReactorState.PLAYING) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.55f))
                            .border(1.5.dp, accentColor.copy(alpha = 0.85f), CircleShape)
                            .semantics { contentDescription = "Stop all sounds" },
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                onStopAll()
                            },
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(Icons.Default.Stop, "Stop", tint = accentColor, modifier = Modifier.size(28.dp))
                        }
                    }
                } else if (displayUiState.state == ReactorState.CHARGING) {
                    // Charging: show percent text
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.Black.copy(alpha = 0.55f))
                            .border(1.dp, Color(0xFFFFD400).copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.FlashOn, null, tint = Color(0xFFFFD400), modifier = Modifier.size(24.dp))
                        Text(
                            text = "${(animatedCharge * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 1.sp),
                            color = Color(0xFFFFD400)
                        )
                    }
                } else {
                    // Idle / Armed / Cooldown / Error / etc.
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.Black.copy(alpha = 0.45f))
                            .border(1.dp, accentColor.copy(alpha = 0.45f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = when (displayUiState.state) {
                                ReactorState.ERROR      -> Icons.Default.ErrorOutline
                                ReactorState.WARNING    -> Icons.Default.Warning
                                ReactorState.COOLDOWN   -> Icons.Default.HourglassBottom
                                ReactorState.GENERATING -> Icons.Default.AutoMode
                                ReactorState.DISABLED   -> Icons.Default.Block
                                else                    -> Icons.Default.PowerSettingsNew
                            },
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(26.dp)
                        )
                        Text(
                            text = when (displayUiState.state) {
                                ReactorState.ERROR      -> "FAULT"
                                ReactorState.WARNING    -> "WARN"
                                ReactorState.COOLDOWN   -> "COOLING"
                                ReactorState.GENERATING -> "GEN…"
                                ReactorState.DISABLED   -> "OFFLINE"
                                ReactorState.IDLE       -> "READY"
                                else                    -> "DEPLOY"
                            },
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 1.5.sp),
                            color = accentColor
                        )
                    }
                }
            } // end core node Box

        } // end main reactor Box

        // ── CATEGORY LABEL ROW ────────────────────────────────────────────
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(0.9f),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left arrow hint
            Text("◀", color = accentColor.copy(alpha = 0.45f), style = MaterialTheme.typography.labelSmall)
            Spacer(Modifier.width(8.dp))
            Icon(
                imageVector = getCategoryIcon(currentCategory),
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(14.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = currentCategory,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                ),
                color = accentColor
            )
            if (displayUiState.isSafeMode) {
                Spacer(Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .background(LimeAccent.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                        .border(1.dp, LimeAccent.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 5.dp, vertical = 1.dp)
                ) {
                    Text("SAFE", style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontWeight = FontWeight.Black), color = LimeAccent)
                }
            }
            Spacer(Modifier.width(8.dp))
            Text("▶", color = accentColor.copy(alpha = 0.45f), style = MaterialTheme.typography.labelSmall)
        }

        Spacer(Modifier.height(10.dp))

        // ── SOUND NAME / ERROR INFO STRIP ────────────────────────────────
        Surface(
            modifier = Modifier.fillMaxWidth(0.95f),
            color = Color.Black.copy(alpha = 0.60f),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.25f))
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(8.dp).clip(CircleShape).background(accentColor))
                    Spacer(Modifier.width(8.dp))
                    LabelCaps(currentCategory, color = categoryColor)
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = displayUiState.currentSoundName ?: "READY // WAITING_COMMAND",
                        color = if (displayUiState.currentSoundName != null) Color.White else Color.White.copy(alpha = 0.35f),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (displayUiState.state == ReactorState.CHARGING) {
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { animatedCharge },
                        modifier = Modifier.fillMaxWidth().height(3.dp),
                        color = Color(0xFFFFD400),
                        trackColor = Color.White.copy(alpha = 0.08f)
                    )
                }
                if ((displayUiState.state == ReactorState.ERROR || displayUiState.state == ReactorState.WARNING) && displayUiState.lastError != null) {
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.ErrorOutline,
                            null,
                            tint = if (displayUiState.state == ReactorState.ERROR) Color(0xFFFF1744) else Color(0xFFFF9800),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "FAULT: ${displayUiState.lastError!!.uppercase()}",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            color = if (displayUiState.state == ReactorState.ERROR) Color(0xFFFF1744) else Color(0xFFFF9800),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── QUICK ACTION STRIP (Stop / Stash / Jokes / Forge) ────────────
        ReactorActionStrip(
            isPlaying = displayUiState.state == ReactorState.PLAYING || displayUiState.state == ReactorState.GENERATING,
            onStopAll = {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                onStopAll()
            },
            onOpenStash = onOpenStash,
            onOpenJokes = onOpenJokes,
            onOpenForge = onOpenForge
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Sub-composables
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ReactorStatusBar(ui: ReactorUiState, accent: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(0.95f),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ReactorPill(label = "CORE",   value = ui.state.name,                            color = accent)
        ReactorPill(label = "LOADED", value = ui.loadedSoundCount.toString().padStart(3, '0'), color = LimeAccent)
        ReactorPill(label = "SAFE",   value = ui.safeSoundCount.toString().padStart(3, '0'),   color = CyanAccent)
    }
}

@Composable
private fun ReactorPill(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier
            .background(Color.Black.copy(alpha = 0.55f), RoundedCornerShape(6.dp))
            .border(1.dp, color.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp, fontSize = 8.sp), color = color.copy(alpha = 0.7f))
        Spacer(Modifier.width(5.dp))
        Text(value, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, fontSize = 9.sp), color = color)
    }
}

@Composable
private fun WaveformHaloStrip(accent: Color, phase: Float) {
    val barCount = 14
    Row(
        modifier = Modifier.fillMaxWidth(0.7f).height(28.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
    ) {
        for (i in 0 until barCount) {
            val barPhase = phase + (i * 0.45f)
            val heightFraction = ((sin(barPhase.toDouble()) + 1.0) / 2.0).toFloat()
            val barHeightDp = 6f + heightFraction * 22f
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(barHeightDp.dp)
                    .padding(horizontal = 1.dp)
                    .clip(RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
                    .background(
                        when (i % 4) {
                            0    -> accent
                            1    -> accent.copy(alpha = 0.7f)
                            2    -> FuchsiaAccent.copy(alpha = 0.6f)
                            else -> accent.copy(alpha = 0.5f)
                        }
                    )
            )
        }
    }
}

@Composable
private fun ReactorActionStrip(
    isPlaying: Boolean,
    onStopAll: () -> Unit,
    onOpenStash: () -> Unit,
    onOpenJokes: () -> Unit,
    onOpenForge: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(0.95f),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Stop All — always prominent
        ReactorActionButton(
            icon  = Icons.Default.Dangerous,
            label = "STOP ALL",
            color = if (isPlaying) Color(0xFFFF1744) else Color(0xFFFF1744).copy(alpha = 0.55f),
            pulsing = isPlaying,
            modifier = Modifier.weight(1.4f),
            onClick = onStopAll
        )
        ReactorActionButton(
            icon  = Icons.Default.LibraryMusic,
            label = "STASH",
            color = CyanAccent,
            modifier = Modifier.weight(1f),
            onClick = onOpenStash
        )
        ReactorActionButton(
            icon  = Icons.Default.RecordVoiceOver,
            label = "JOKES",
            color = FuchsiaAccent,
            modifier = Modifier.weight(1f),
            onClick = onOpenJokes
        )
        ReactorActionButton(
            icon  = Icons.Default.PrecisionManufacturing,
            label = "FORGE",
            color = OrangeAccent,
            modifier = Modifier.weight(1f),
            onClick = onOpenForge
        )
    }
}

@Composable
private fun ReactorActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
    pulsing: Boolean = false,
    onClick: () -> Unit,
) {
    val transition = rememberInfiniteTransition(label = "action_btn")
    val pulse by transition.animateFloat(
        0.55f, 1f,
        infiniteRepeatable(tween(600, easing = LinearEasing), RepeatMode.Reverse),
        label = "btn_p"
    )
    val borderAlpha = if (pulsing) pulse else 0.45f

    Box(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Black.copy(alpha = 0.55f))
            .border(1.5.dp, color.copy(alpha = borderAlpha), RoundedCornerShape(12.dp))
            .semantics { contentDescription = label }
            .then(Modifier.wrapContentWidth(Alignment.CenterHorizontally)),
        contentAlignment = Alignment.Center
    ) {
        // Make the whole box tappable
        Surface(
            onClick = onClick,
            modifier = Modifier.fillMaxSize(),
            color = Color.Transparent,
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize().padding(4.dp)
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
                Spacer(Modifier.height(3.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontWeight = FontWeight.Black, letterSpacing = 0.8.sp),
                    color = color.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Clip
                )
            }
        }
    }
}

// Corner HUD bracket overlay (unchanged from original)
@Composable
private fun HudBrackets(accent: Color, alpha: Float) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val len = 28.dp.toPx()
        val w = 2.dp.toPx()
        val color = accent.copy(alpha = alpha)
        drawLine(color, Offset(0f, 0f), Offset(len, 0f), w)
        drawLine(color, Offset(0f, 0f), Offset(0f, len), w)
        drawLine(color, Offset(size.width - len, 0f), Offset(size.width, 0f), w)
        drawLine(color, Offset(size.width, 0f), Offset(size.width, len), w)
        drawLine(color, Offset(0f, size.height - len), Offset(0f, size.height), w)
        drawLine(color, Offset(0f, size.height), Offset(len, size.height), w)
        drawLine(color, Offset(size.width - len, size.height), Offset(size.width, size.height), w)
        drawLine(color, Offset(size.width, size.height - len), Offset(size.width, size.height), w)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Helpers (re-exported so ReactorCorePanel.kt can use them without duplication)
// ─────────────────────────────────────────────────────────────────────────────

fun getCategoryColor(category: String): Color = when (category.uppercase()) {
    "FUNNY", "CARTOON" -> PrimaryContainer
    "CREEPY"           -> FuchsiaAccent
    "ANIMAL"           -> LimeAccent
    "VOICE", "CUSTOM"  -> CyanAccent
    "FIGHTER"          -> OrangeAccent
    else               -> CyanAccent
}

fun getCategoryIcon(category: String) = when (category.uppercase()) {
    "FUNNY"   -> Icons.Default.SentimentVerySatisfied
    "CREEPY"  -> Icons.Default.SmsFailed
    "ANIMAL"  -> Icons.Default.Pets
    "VOICE"   -> Icons.Default.RecordVoiceOver
    "FIGHTER" -> Icons.Default.SportsMartialArts
    "CARTOON" -> Icons.Default.AutoFixHigh
    "CUSTOM"  -> Icons.Default.FolderShared
    else      -> Icons.Default.Radio
}
