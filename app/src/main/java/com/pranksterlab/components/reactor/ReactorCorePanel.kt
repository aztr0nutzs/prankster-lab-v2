package com.pranksterlab.components.reactor

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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pranksterlab.components.LabelCaps
import com.pranksterlab.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

enum class ReactorState {
    IDLE,
    ARMED,
    CHARGING,
    PLAYING,
    COOLDOWN,
    ERROR
}

data class ReactorUiState(
    val state: ReactorState,
    val currentSoundName: String?,
    val currentCategory: String?,
    val loadedSoundCount: Int,
    val safeSoundCount: Int,
    val chargePercent: Float,
    val lastError: String?
)

// Backwards-compatible alias kept for existing call sites that still reference ReactorMode.
@Deprecated("Use ReactorState", ReplaceWith("ReactorState"))
typealias ReactorMode = ReactorState

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
    coreImageRes: Int = com.pranksterlab.R.drawable.prankstar_core
) {
    val baseCategories = listOf("FUNNY", "CREEPY", "ANIMAL", "VOICE", "FIGHTER", "CARTOON")
    val categories = if (hasCustomSounds) baseCategories + "CUSTOM" else baseCategories

    var state by remember { mutableStateOf(ReactorState.IDLE) }
    var chargeLevel by remember { mutableFloatStateOf(0f) }
    var systemError by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val haptics = LocalHapticFeedback.current

    LaunchedEffect(isPlaying, playbackError) {
        when {
            playbackError != null -> {
                state = ReactorState.ERROR
                systemError = playbackError
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                delay(2400)
                state = if (isPlaying) ReactorState.PLAYING else ReactorState.ARMED
                systemError = null
            }
            isPlaying -> state = ReactorState.PLAYING
            !isPlaying && state == ReactorState.PLAYING -> {
                state = ReactorState.COOLDOWN
                delay(900)
                state = ReactorState.ARMED
            }
            !isPlaying && state == ReactorState.IDLE -> state = ReactorState.ARMED
        }
    }

    val ui = ReactorUiState(
        state = state,
        currentSoundName = currentSoundName,
        currentCategory = currentCategory,
        loadedSoundCount = loadedSoundCount,
        safeSoundCount = safeSoundCount,
        chargePercent = chargeLevel,
        lastError = systemError
    )

    val categoryColor = getCategoryColor(currentCategory)
    val accentColor = when (state) {
        ReactorState.IDLE -> categoryColor.copy(alpha = 0.55f)
        ReactorState.ARMED -> categoryColor
        ReactorState.PLAYING -> Color.White
        ReactorState.CHARGING -> Color(0xFFFFD400)
        ReactorState.COOLDOWN -> categoryColor.copy(alpha = 0.45f)
        ReactorState.ERROR -> Color(0xFFFF1744)
    }

    // Animation drivers
    val transition = rememberInfiniteTransition(label = "reactor")

    val rotationDuration = when (state) {
        ReactorState.PLAYING -> 1800
        ReactorState.CHARGING -> 1100
        ReactorState.ERROR -> 700
        ReactorState.ARMED -> 6000
        else -> 9000
    }
    val outerRotation by transition.animateFloat(
        0f, 360f,
        infiniteRepeatable(tween(rotationDuration, easing = LinearEasing)),
        label = "outer_rot"
    )
    val innerRotation by transition.animateFloat(
        360f, 0f,
        infiniteRepeatable(tween((rotationDuration * 1.4f).toInt(), easing = LinearEasing)),
        label = "inner_rot"
    )

    val pulseDuration = when (state) {
        ReactorState.PLAYING -> 700
        ReactorState.CHARGING -> 480
        ReactorState.ERROR -> 320
        else -> 1700
    }
    val corePulse by transition.animateFloat(
        0.94f, 1.10f,
        infiniteRepeatable(tween(pulseDuration, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "core_pulse"
    )

    val haloPulse by transition.animateFloat(
        0.92f, 1.18f,
        infiniteRepeatable(tween((pulseDuration * 1.6f).toInt(), easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "halo"
    )

    val glowAlpha by transition.animateFloat(
        if (state == ReactorState.IDLE) 0.12f else 0.28f,
        if (state == ReactorState.IDLE) 0.35f else 0.7f,
        infiniteRepeatable(tween(1300, easing = LinearEasing), RepeatMode.Reverse),
        label = "glow"
    )

    val sweepAngle by transition.animateFloat(
        0f, 360f,
        infiniteRepeatable(tween(2200, easing = LinearEasing)),
        label = "sweep"
    )

    val errorFlash by transition.animateFloat(
        0.2f, 1f,
        infiniteRepeatable(tween(220, easing = LinearEasing), RepeatMode.Reverse),
        label = "err_flash"
    )

    val animatedCharge by animateFloatAsState(
        targetValue = chargeLevel,
        animationSpec = tween(120, easing = LinearEasing),
        label = "charge_anim"
    )

    Column(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ReactorStatusBar(ui = ui, accent = accentColor)

        Spacer(Modifier.height(14.dp))

        Box(
            modifier = Modifier
                .size(330.dp)
                .pointerInput(state) {
                    detectTapGestures(
                        onTap = {
                            when (state) {
                                ReactorState.PLAYING -> {
                                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onStop()
                                }
                                ReactorState.IDLE, ReactorState.ARMED, ReactorState.COOLDOWN -> {
                                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onTrigger(currentCategory, 1)
                                }
                                else -> Unit
                            }
                        },
                        onLongPress = {
                            if (state == ReactorState.IDLE || state == ReactorState.ARMED) {
                                scope.launch {
                                    state = ReactorState.CHARGING
                                    var t = 0f
                                    while (t < 1300f && state == ReactorState.CHARGING) {
                                        delay(35)
                                        t += 35
                                        chargeLevel = (t / 1300f).coerceAtMost(1f)
                                        if ((t.toInt() % 130) == 0) {
                                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        }
                                    }
                                    if (state == ReactorState.CHARGING) {
                                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                        onTrigger(currentCategory, 3)
                                        chargeLevel = 0f
                                    }
                                }
                            }
                        },
                        onPress = {
                            tryAwaitRelease()
                            if (state == ReactorState.CHARGING) {
                                if (chargeLevel > 0.4f) {
                                    onTrigger(currentCategory, 2)
                                } else {
                                    state = ReactorState.ARMED
                                }
                                chargeLevel = 0f
                            }
                        }
                    )
                }
                .pointerInput(currentCategory, categories) {
                    detectDragGestures(
                        onDrag = { change, dragAmount ->
                            if (kotlin.math.abs(dragAmount.x) > 28f) {
                                val idx = categories.indexOf(currentCategory)
                                val next = if (dragAmount.x > 0)
                                    (idx - 1 + categories.size) % categories.size
                                else
                                    (idx + 1) % categories.size
                                if (next != idx) {
                                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    onCategoryChange(categories[next])
                                }
                                change.consume()
                            }
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            // Layer 1 — corner HUD brackets
            HudBrackets(accent = accentColor, alpha = glowAlpha)

            // Layer 2 — waveform halo
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { scaleX = haloPulse; scaleY = haloPulse }
            ) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(accentColor.copy(alpha = glowAlpha * 0.45f), Color.Transparent),
                        center = center,
                        radius = size.minDimension / 1.05f
                    )
                )
                if (state == ReactorState.PLAYING) {
                    for (ring in 0 until 3) {
                        drawCircle(
                            color = accentColor.copy(alpha = (0.22f - ring * 0.06f).coerceAtLeast(0f)),
                            radius = size.minDimension / 2.1f + ring * 16.dp.toPx(),
                            style = Stroke(width = 1.dp.toPx())
                        )
                    }
                }
            }

            // Layer 3 — outer tick ring (rotates)
            Canvas(modifier = Modifier.size(310.dp)) {
                rotate(outerRotation) {
                    val ticks = 72
                    val r = size.minDimension / 2f
                    for (i in 0 until ticks) {
                        val angle = i * (360f / ticks)
                        val rad = angle * (PI / 180f).toFloat()
                        val major = i % 6 == 0
                        val len = if (major) 14.dp.toPx() else 6.dp.toPx()
                        val start = Offset(
                            center.x + (r - len) * cos(rad),
                            center.y + (r - len) * sin(rad)
                        )
                        val end = Offset(
                            center.x + r * cos(rad),
                            center.y + r * sin(rad)
                        )
                        drawLine(
                            color = if (major) accentColor.copy(alpha = 0.85f) else accentColor.copy(alpha = 0.25f),
                            start = start,
                            end = end,
                            strokeWidth = if (major) 1.6.dp.toPx() else 1.dp.toPx()
                        )
                    }
                }
            }

            // Layer 4 — sweep beam (radar-like)
            Canvas(modifier = Modifier.size(300.dp)) {
                val brush = Brush.sweepGradient(
                    0.0f to Color.Transparent,
                    0.85f to Color.Transparent,
                    0.97f to accentColor.copy(alpha = if (state == ReactorState.ERROR) errorFlash * 0.7f else 0.55f),
                    1.0f to Color.Transparent,
                    center = center
                )
                rotate(sweepAngle) {
                    drawCircle(brush = brush, radius = size.minDimension / 2f)
                }
            }

            // Layer 5 — outer category arc selector (rotates)
            Canvas(modifier = Modifier.size(280.dp)) {
                rotate(outerRotation * 0.4f) {
                    val segments = categories.size
                    val gap = 10f
                    val arcSize = (360f / segments) - gap
                    for (i in 0 until segments) {
                        val isSelected = categories[i] == currentCategory
                        drawArc(
                            color = if (isSelected) accentColor else accentColor.copy(alpha = 0.18f),
                            startAngle = i * (360f / segments),
                            sweepAngle = arcSize,
                            useCenter = false,
                            style = Stroke(
                                width = if (isSelected) 7.dp.toPx() else 2.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                        )
                    }
                }
            }

            // Layer 6 — category icon ring (static positions, individually clickable)
            categories.forEachIndexed { index, cat ->
                val angle = (index * (360f / categories.size) - 90f) * (PI / 180f).toFloat()
                val radius = 132.dp
                val isSelected = cat == currentCategory
                Box(
                    modifier = Modifier
                        .offset(
                            x = (radius.value * cos(angle)).dp,
                            y = (radius.value * sin(angle)).dp
                        )
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) accentColor else Color.Black.copy(alpha = 0.85f))
                        .border(
                            1.dp,
                            if (isSelected) Color.White.copy(alpha = 0.7f) else accentColor.copy(alpha = 0.25f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = {
                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onCategoryChange(cat)
                        },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = getCategoryIcon(cat),
                            contentDescription = cat,
                            tint = if (isSelected) Color.Black else accentColor.copy(alpha = 0.7f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Layer 7 — inner segmented energy ring (counter-rotates, charge-driven)
            Canvas(modifier = Modifier.size(210.dp)) {
                rotate(innerRotation) {
                    val segments = 40
                    val gap = 2.2f
                    val sweep = (360f / segments) - gap
                    for (i in 0 until segments) {
                        val threshold = i.toFloat() / segments
                        val active = animatedCharge >= threshold ||
                                (state == ReactorState.PLAYING && (i % 2 == 0)) ||
                                (state == ReactorState.ARMED && i % 8 == 0)
                        drawArc(
                            color = if (active) accentColor else accentColor.copy(alpha = 0.05f),
                            startAngle = i * (sweep + gap) - 90f,
                            sweepAngle = sweep,
                            useCenter = false,
                            style = Stroke(width = 7.dp.toPx())
                        )
                    }
                }
            }

            // Layer 8 — central core (prankstar_core image is the centerpiece)
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .graphicsLayer {
                        // Outer chassis breathes/scales with state. Inner image
                        // does NOT compound this — it has its own subtle breath.
                        val s = when (state) {
                            ReactorState.PLAYING, ReactorState.CHARGING -> corePulse
                            ReactorState.IDLE, ReactorState.ARMED -> 0.98f + (corePulse - 0.94f) * 0.25f
                            else -> 1f
                        }
                        scaleX = s
                        scaleY = s
                    }
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            0.0f to when (state) {
                                ReactorState.PLAYING -> accentColor.copy(alpha = 0.10f)
                                ReactorState.CHARGING -> Color(0xFFFFD400).copy(alpha = 0.12f)
                                ReactorState.ERROR -> Color(0xFFFF1744).copy(alpha = 0.18f * errorFlash)
                                else -> Color(0xFF0A0D10)
                            },
                            0.7f to Color(0xFF030506),
                            1.0f to Color.Black
                        )
                    )
                    .border(
                        width = if (state == ReactorState.PLAYING) 3.dp else 1.5.dp,
                        brush = Brush.sweepGradient(
                            listOf(
                                accentColor,
                                accentColor.copy(alpha = 0.15f),
                                accentColor,
                                accentColor.copy(alpha = 0.15f),
                                accentColor
                            )
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                // 8a — Faint HUD crosshair sits BEHIND the artwork as a backplate.
                Canvas(modifier = Modifier.fillMaxSize().padding(18.dp)) {
                    val a = accentColor.copy(alpha = 0.10f)
                    drawLine(a, Offset(0f, center.y), Offset(size.width, center.y), 0.8.dp.toPx())
                    drawLine(a, Offset(center.x, 0f), Offset(center.x, size.height), 0.8.dp.toPx())
                    drawCircle(a, radius = size.minDimension / 4f, style = Stroke(0.6.dp.toPx()))
                }

                // 8b — prankstar_core artwork is the visual centerpiece.
                // Clipped circular, full clarity, only a tiny breath/charge scale.
                val imageScale = when (state) {
                    ReactorState.CHARGING -> 1.0f + animatedCharge * 0.06f
                    ReactorState.PLAYING -> 0.99f + (corePulse - 0.94f) * 0.35f
                    ReactorState.IDLE, ReactorState.ARMED -> 0.985f + (corePulse - 0.94f) * 0.25f
                    else -> 1f
                }
                Image(
                    painter = painterResource(id = coreImageRes),
                    contentDescription = "Prankstar Reactor Core",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(6.dp)
                        .clip(CircleShape)
                        .graphicsLayer {
                            scaleX = imageScale
                            scaleY = imageScale
                            alpha = when (state) {
                                ReactorState.ERROR -> 0.55f + 0.35f * errorFlash
                                ReactorState.COOLDOWN -> 0.85f
                                else -> 1f
                            }
                        },
                    contentScale = ContentScale.Crop
                )

                // 8c — Animated pulse overlay sits ON TOP of the image.
                // Subtle radial wash that intensifies during PLAYING / CHARGING / ERROR.
                Canvas(modifier = Modifier.fillMaxSize().padding(6.dp)) {
                    val washAlpha = when (state) {
                        ReactorState.PLAYING -> 0.18f + (haloPulse - 0.92f) * 0.6f
                        ReactorState.CHARGING -> 0.20f + animatedCharge * 0.25f
                        ReactorState.ERROR -> 0.30f * errorFlash
                        ReactorState.COOLDOWN -> 0.10f
                        else -> 0.06f + (corePulse - 0.94f) * 0.4f
                    }.coerceIn(0f, 0.6f)
                    val washColor = when (state) {
                        ReactorState.CHARGING -> Color(0xFFFFD400)
                        ReactorState.ERROR -> Color(0xFFFF1744)
                        else -> accentColor
                    }
                    // Soft inner ring highlight blends image into reactor.
                    drawCircle(
                        brush = Brush.radialGradient(
                            0.0f to Color.Transparent,
                            0.55f to Color.Transparent,
                            0.85f to washColor.copy(alpha = washAlpha * 0.55f),
                            1.0f to washColor.copy(alpha = washAlpha)
                        ),
                        radius = size.minDimension / 2f
                    )
                    // Center bloom on activity for "live" feel.
                    if (state == ReactorState.PLAYING || state == ReactorState.CHARGING) {
                        drawCircle(
                            brush = Brush.radialGradient(
                                0.0f to washColor.copy(alpha = washAlpha * 0.45f),
                                0.5f to Color.Transparent
                            ),
                            radius = size.minDimension / 2.2f
                        )
                    }
                }

                // 8d — Interaction highlight + state affordance / stop button.
                if (state == ReactorState.PLAYING) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.55f))
                            .border(1.5.dp, accentColor.copy(alpha = 0.85f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                onStop()
                            },
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stop,
                                contentDescription = "Stop",
                                tint = accentColor,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.Black.copy(alpha = 0.45f))
                            .border(
                                1.dp,
                                accentColor.copy(alpha = 0.45f),
                                RoundedCornerShape(10.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = when (state) {
                                ReactorState.CHARGING -> Icons.Default.FlashOn
                                ReactorState.ERROR -> Icons.Default.ErrorOutline
                                ReactorState.COOLDOWN -> Icons.Default.HourglassBottom
                                else -> Icons.Default.PowerSettingsNew
                            },
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(26.dp)
                        )
                        Text(
                            text = when (state) {
                                ReactorState.CHARGING -> "CHARGING"
                                ReactorState.ERROR -> "FAULT"
                                ReactorState.COOLDOWN -> "COOLDOWN"
                                ReactorState.IDLE -> "READY"
                                else -> "DEPLOY"
                            },
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.5.sp
                            ),
                            color = accentColor
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(18.dp))

        // Info readout
        Surface(
            modifier = Modifier.fillMaxWidth(0.95f),
            color = Color.Black.copy(alpha = 0.6f),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.25f))
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(accentColor)
                    )
                    Spacer(Modifier.width(8.dp))
                    LabelCaps(currentCategory, color = categoryColor)
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = currentSoundName ?: "READY // WAITING_COMMAND",
                        color = if (currentSoundName != null) Color.White else Color.White.copy(alpha = 0.35f),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (state == ReactorState.CHARGING) {
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { animatedCharge },
                        modifier = Modifier.fillMaxWidth().height(3.dp),
                        color = Color(0xFFFFD400),
                        trackColor = Color.White.copy(alpha = 0.08f)
                    )
                }
                if (state == ReactorState.ERROR && systemError != null) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "FAULT: ${systemError!!.uppercase()}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFFF1744)
                    )
                }
            }
        }
    }
}

@Composable
private fun ReactorStatusBar(ui: ReactorUiState, accent: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(0.95f),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatusPill(label = "CORE", value = ui.state.name, color = accent)
        StatusPill(
            label = "LOADED",
            value = ui.loadedSoundCount.toString().padStart(3, '0'),
            color = LimeAccent
        )
        StatusPill(
            label = "SAFE",
            value = ui.safeSoundCount.toString().padStart(3, '0'),
            color = CyanAccent
        )
    }
}

@Composable
private fun StatusPill(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier
            .background(Color.Black.copy(alpha = 0.55f), RoundedCornerShape(6.dp))
            .border(1.dp, color.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
            color = color.copy(alpha = 0.7f)
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black),
            color = color
        )
    }
}

@Composable
private fun HudBrackets(accent: Color, alpha: Float) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val len = 28.dp.toPx()
        val w = 2.dp.toPx()
        val color = accent.copy(alpha = alpha)
        // top-left
        drawLine(color, Offset(0f, 0f), Offset(len, 0f), w)
        drawLine(color, Offset(0f, 0f), Offset(0f, len), w)
        // top-right
        drawLine(color, Offset(size.width - len, 0f), Offset(size.width, 0f), w)
        drawLine(color, Offset(size.width, 0f), Offset(size.width, len), w)
        // bottom-left
        drawLine(color, Offset(0f, size.height - len), Offset(0f, size.height), w)
        drawLine(color, Offset(0f, size.height), Offset(len, size.height), w)
        // bottom-right
        drawLine(color, Offset(size.width - len, size.height), Offset(size.width, size.height), w)
        drawLine(color, Offset(size.width, size.height - len), Offset(size.width, size.height), w)
    }
}

fun getCategoryColor(category: String) = when (category.uppercase()) {
    "FUNNY", "CARTOON" -> PrimaryContainer
    "CREEPY" -> FuchsiaAccent
    "ANIMAL" -> LimeAccent
    "VOICE", "CUSTOM" -> CyanAccent
    "FIGHTER" -> OrangeAccent
    else -> CyanAccent
}

fun getCategoryIcon(category: String) = when (category.uppercase()) {
    "FUNNY" -> Icons.Default.SentimentVerySatisfied
    "CREEPY" -> Icons.Default.SmsFailed
    "ANIMAL" -> Icons.Default.Pets
    "VOICE" -> Icons.Default.RecordVoiceOver
    "FIGHTER" -> Icons.Default.SportsMartialArts
    "CARTOON" -> Icons.Default.AutoFixHigh
    "CUSTOM" -> Icons.Default.FolderShared
    else -> Icons.Default.Radio
}
