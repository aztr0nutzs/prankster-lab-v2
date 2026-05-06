package com.pranksterlab.components.reactor

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pranksterlab.components.LabelCaps
import com.pranksterlab.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.atan2

enum class ReactorMode {
    SAFE, PLAYING, CHARGING, COOLDOWN, ERROR
}

@Composable
fun ReactorCorePanel(
    currentSoundName: String?,
    currentCategory: String,
    isPlaying: Boolean,
    hasCustomSounds: Boolean,
    playbackError: String? = null,
    onTrigger: (category: String, intensity: Int) -> Unit,
    onStop: () -> Unit,
    onCategoryChange: (String) -> Unit
) {
    val baseCategories = listOf("FUNNY", "CREEPY", "ANIMAL", "VOICE", "FIGHTER", "CARTOON")
    val categories = if (hasCustomSounds) baseCategories + "CUSTOM" else baseCategories
    
    var mode by remember { mutableStateOf(ReactorMode.SAFE) }
    var chargeLevel by remember { mutableFloatStateOf(0f) }
    var systemError by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val haptics = LocalHapticFeedback.current

    // Animations
    val transition = rememberInfiniteTransition(label = "reactor_loops")
    
    val rotationSpeed = if (isPlaying) 2000 else 8000
    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(rotationSpeed, easing = LinearEasing)),
        label = "rotation"
    )

    val waveFrequency = if (isPlaying) 1000 else 3000
    val wavePulse by transition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(tween(waveFrequency, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "wave_pulse"
    )

    val glowAlpha by transition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(tween(1500, easing = LinearEasing), RepeatMode.Reverse),
        label = "glow"
    )

    LaunchedEffect(isPlaying, playbackError) {
        if (playbackError != null) {
            mode = ReactorMode.ERROR
            systemError = playbackError
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
            delay(3000)
            mode = ReactorMode.SAFE
            systemError = null
        } else if (!isPlaying && mode == ReactorMode.PLAYING) {
            mode = ReactorMode.COOLDOWN
            delay(1000)
            mode = ReactorMode.SAFE
        } else if (isPlaying) {
            mode = ReactorMode.PLAYING
        }
    }

    val categoryColor = getCategoryColor(currentCategory)
    val reactorColor = when (mode) {
        ReactorMode.SAFE -> categoryColor
        ReactorMode.PLAYING -> Color.White
        ReactorMode.CHARGING -> Color.Yellow
        ReactorMode.COOLDOWN -> categoryColor.copy(alpha = 0.5f)
        ReactorMode.ERROR -> Color.Red
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Status display
        LabelCaps(
            text = if (mode == ReactorMode.ERROR) "CORE_CRITICAL: ${systemError?.uppercase()}" else "CORE_STATUS: ${mode.name}",
            color = reactorColor.copy(alpha = 0.8f)
        )
        
        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .size(310.dp)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            if (mode == ReactorMode.SAFE) {
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                onTrigger(currentCategory, 1)
                            }
                        },
                        onLongPress = {
                            if (mode == ReactorMode.SAFE) {
                                scope.launch {
                                    mode = ReactorMode.CHARGING
                                    var time = 0f
                                    while (time < 1200f && mode == ReactorMode.CHARGING) {
                                        delay(40)
                                        time += 40
                                        chargeLevel = (time / 1200f).coerceAtMost(1f)
                                        if ((time.toInt() % 120) == 0) {
                                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        }
                                    }
                                    if (mode == ReactorMode.CHARGING) {
                                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                        onTrigger(currentCategory, 3)
                                        chargeLevel = 0f
                                    }
                                }
                            }
                        },
                        onPress = {
                            tryAwaitRelease()
                            if (mode == ReactorMode.CHARGING) {
                                if (chargeLevel > 0.4f) {
                                    onTrigger(currentCategory, 2)
                                } else {
                                    mode = ReactorMode.SAFE
                                }
                                chargeLevel = 0f
                            }
                        }
                    )
                }
                .pointerInput(currentCategory, categories) {
                    detectDragGestures(
                        onDrag = { change, dragAmount ->
                            val sensitivity = 30f
                            if (kotlin.math.abs(dragAmount.x) > sensitivity) {
                                val currentIndex = categories.indexOf(currentCategory)
                                val nextIndex = if (dragAmount.x > 0) {
                                    (currentIndex - 1 + categories.size) % categories.size
                                } else {
                                    (currentIndex + 1) % categories.size
                                }
                                if (nextIndex != currentIndex) {
                                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    onCategoryChange(categories[nextIndex])
                                }
                                change.consume()
                            }
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            // Waveform Halo
            Canvas(modifier = Modifier.fillMaxSize().graphicsLayer(scaleX = wavePulse, scaleY = wavePulse)) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(reactorColor.copy(alpha = glowAlpha * 0.3f), Color.Transparent),
                        center = center,
                        radius = size.minDimension / 1.2f
                    )
                )
                if (isPlaying) {
                    drawCircle(
                        color = reactorColor.copy(alpha = 0.1f),
                        radius = size.minDimension / 1.15f,
                        style = Stroke(width = 1.dp.toPx())
                    )
                }
            }

            // Outer Neon Arc Selector
            Canvas(modifier = Modifier.size(280.dp)) {
                rotate(rotation) {
                    val segments = categories.size
                    val arcGap = 8f
                    val arcSize = (360f / segments) - arcGap
                    for (i in 0 until segments) {
                        val isSelected = categories[i] == currentCategory
                        drawArc(
                            color = if (isSelected) reactorColor else reactorColor.copy(alpha = 0.15f),
                            startAngle = i * (360f / segments),
                            sweepAngle = arcSize,
                            useCenter = false,
                            style = Stroke(width = if (isSelected) 6.dp.toPx() else 2.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                }
            }

            // Category Icons Ring
            categories.forEachIndexed { index, cat ->
                val angle = (index * (360f / categories.size) - 90f) * (PI / 180f)
                val radius = 125.dp
                val isSelected = cat == currentCategory
                
                Box(
                    modifier = Modifier
                        .offset(
                            x = (radius.value * kotlin.math.cos(angle)).dp,
                            y = (radius.value * kotlin.math.sin(angle)).dp
                        )
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) reactorColor else Color.Black.copy(alpha = 0.8f))
                        .border(1.dp, if (isSelected) Color.White.copy(alpha = 0.5f) else reactorColor.copy(alpha = 0.1f), CircleShape)
                        .clickable { 
                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onCategoryChange(cat) 
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getCategoryIcon(cat),
                        contentDescription = cat,
                        tint = if (isSelected) Color.Black else reactorColor.copy(alpha = 0.5f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Inner Segmented Energy Ring
            Canvas(modifier = Modifier.size(200.dp)) {
                val segments = 32
                val gap = 2f
                val sweep = (360f / segments) - gap
                for (i in 0 until segments) {
                    val chargeThreshold = i.toFloat() / segments
                    val isActive = chargeLevel >= chargeThreshold || (isPlaying && (i % 2 == 0))
                    val color = if (isActive) reactorColor else reactorColor.copy(alpha = 0.05f)
                    drawArc(
                        color = color,
                        startAngle = i * (sweep + gap) - 90f,
                        sweepAngle = sweep,
                        useCenter = false,
                        style = Stroke(width = 6.dp.toPx())
                    )
                }
            }

            // Central Core
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .graphicsLayer(
                        scaleX = if (mode == ReactorMode.PLAYING) wavePulse else 1f,
                        scaleY = if (mode == ReactorMode.PLAYING) wavePulse else 1f
                    )
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            0.0f to if (mode == ReactorMode.PLAYING) reactorColor.copy(alpha = 0.1f) else Color(0xFF111111),
                            1.0f to Color.Black
                        )
                    )
                    .border(
                        width = if (mode == ReactorMode.PLAYING) 3.dp else 1.5.dp,
                        brush = Brush.sweepGradient(listOf(reactorColor, reactorColor.copy(alpha = 0.2f), reactorColor)),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (mode == ReactorMode.PLAYING) {
                    IconButton(onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        onStop()
                    }, modifier = Modifier.fillMaxSize()) {
                        Icon(
                            imageVector = Icons.Default.Stop,
                            contentDescription = "Stop",
                            tint = reactorColor,
                            modifier = Modifier.size(64.dp)
                        )
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = if (mode == ReactorMode.CHARGING) Icons.Default.FlashOn else Icons.Default.PowerSettingsNew,
                            contentDescription = null,
                            tint = if (mode == ReactorMode.CHARGING) Color.Yellow else reactorColor,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = if (mode == ReactorMode.CHARGING) "CHARGING" else "DEPLOY",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black, letterSpacing = 1.sp),
                            color = if (mode == ReactorMode.CHARGING) Color.Yellow else reactorColor
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Info Panel
        Surface(
            modifier = Modifier.fillMaxWidth(0.9f).height(70.dp),
            color = Color.Black.copy(alpha = 0.6f),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, reactorColor.copy(alpha = 0.1f))
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    LabelCaps(text = currentCategory, color = categoryColor)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = currentSoundName ?: "READY // WAITING_COMMAND",
                        color = if (currentSoundName != null) Color.White else Color.White.copy(alpha = 0.3f),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                if (mode == ReactorMode.CHARGING) {
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { chargeLevel },
                        modifier = Modifier.fillMaxWidth().height(2.dp),
                        color = Color.Yellow,
                        trackColor = Color.White.copy(alpha = 0.1f)
                    )
                }
            }
        }
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
    "CREEPY" -> Icons.Default.SmsFailed // Skull-like or warning
    "ANIMAL" -> Icons.Default.Pets
    "VOICE" -> Icons.Default.RecordVoiceOver
    "FIGHTER" -> Icons.Default.SportsMartialArts
    "CARTOON" -> Icons.Default.AutoFixHigh
    "CUSTOM" -> Icons.Default.FolderShared
    else -> Icons.Default.Radio
}


