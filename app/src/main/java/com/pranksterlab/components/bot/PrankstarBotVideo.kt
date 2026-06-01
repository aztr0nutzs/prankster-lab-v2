package com.pranksterlab.components.bot

import android.net.Uri
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.pranksterlab.R
import com.pranksterlab.core.repository.dataStore
import com.pranksterlab.theme.BackgroundDark
import com.pranksterlab.theme.CyanAccent
import com.pranksterlab.theme.FuchsiaAccent
import com.pranksterlab.theme.GlassBackground
import com.pranksterlab.theme.LimeAccent
import com.pranksterlab.theme.OrangeAccent
import kotlinx.coroutines.flow.map

@Composable
fun PrankstarBotVideo(
    mood: PrankstarBotMood,
    message: String?,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
    muted: Boolean = true,
    showMessageBubble: Boolean = true,
    onTap: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val animatedBotEnabled by remember {
        context.dataStore.data.map { preferences ->
            preferences[booleanPreferencesKey("animated_bot_enabled")] ?: true
        }
    }.collectAsState(initial = true)
    val animationIntensity by remember {
        context.dataStore.data.map { preferences ->
            preferences[stringPreferencesKey("animation_intensity")] ?: "FULL"
        }
    }.collectAsState(initial = "FULL")

    val mappedVideoResId = remember(mood, context) { mood.videoResId(context) }
    val allowVideo = animatedBotEnabled && animationIntensity != "MINIMAL" && mappedVideoResId != 0
    val allowPulse = animationIntensity == "FULL"
    val staticDrawableId = remember(context) { context.findStaticBotDrawableId() }
    val warningMood = mood in setOf(PrankstarBotMood.WARNING, PrankstarBotMood.ERROR, PrankstarBotMood.ANGRY)
    val accent = if (warningMood) OrangeAccent else CyanAccent
    val pulse by if (allowPulse) {
        rememberInfiniteTransition(label = "bot-frame-pulse").animateFloat(
            initialValue = 0.45f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(1400), RepeatMode.Reverse),
            label = "bot-frame-alpha"
        )
    } else {
        remember { mutableStateOf(0.65f) }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(if (compact) 18.dp else 22.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        BackgroundDark.copy(alpha = 0.96f),
                        GlassBackground,
                        Color(0xFF100816).copy(alpha = 0.92f)
                    )
                )
            )
            .border(1.dp, accent.copy(alpha = 0.55f), RoundedCornerShape(if (compact) 18.dp else 22.dp))
            .drawBehind {
                drawRoundRect(
                    color = accent.copy(alpha = 0.18f * pulse),
                    style = Stroke(width = if (compact) 5f else 8f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(22.dp.toPx())
                )
                drawRoundRect(
                    color = FuchsiaAccent.copy(alpha = if (warningMood) 0.08f else 0.12f * pulse),
                    style = Stroke(width = 2f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(18.dp.toPx())
                )
            }
            .then(if (onTap != null) Modifier.clickable { onTap() } else Modifier)
            .padding(if (compact) 10.dp else 12.dp)
            .semantics { contentDescription = "Prankstar Bot mood: ${mood.name.lowercase().replace('_', ' ')}" },
        verticalArrangement = Arrangement.spacedBy(if (compact) 8.dp else 10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Default.SmartToy, contentDescription = "Prankstar Bot", tint = accent, modifier = Modifier.size(if (compact) 18.dp else 20.dp))
            Text("NEO ASSISTANT", color = accent, style = MaterialTheme.typography.labelSmall)
            Spacer(Modifier.weight(1f))
            Text(mood.name, color = if (warningMood) OrangeAccent else LimeAccent, style = MaterialTheme.typography.labelSmall)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(if (compact) 1.55f else 1.7f)
                .clip(RoundedCornerShape(if (compact) 14.dp else 18.dp))
                .background(Color.Black.copy(alpha = 0.72f))
                .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(if (compact) 14.dp else 18.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (allowVideo) {
                BotPlayer(mood = mood, initialResId = mappedVideoResId, muted = muted, staticDrawableId = staticDrawableId)
            } else {
                StaticBotImage(staticDrawableId = staticDrawableId, mood = mood)
            }
            if (mood in setOf(PrankstarBotMood.PROCESSING, PrankstarBotMood.GENERATING, PrankstarBotMood.THINKING)) {
                ProcessingRing(accent = accent, pulse = pulse, compact = compact)
            }
            if (mood == PrankstarBotMood.PLAYING) {
                PlaybackBars(compact = compact)
            }
        }

        if (showMessageBubble && !message.isNullOrBlank()) {
            Text(
                text = message,
                color = Color.White,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.Black.copy(alpha = 0.38f))
                    .border(1.dp, accent.copy(alpha = 0.28f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 12.dp, vertical = 9.dp)
            )
        }
    }
}

@Composable
private fun BotPlayer(mood: PrankstarBotMood, initialResId: Int, muted: Boolean, staticDrawableId: Int) {
    val context = LocalContext.current
    var targetResId by remember(mood, initialResId) { mutableIntStateOf(initialResId) }
    var showStaticFallback by remember(mood) { mutableStateOf(false) }
    val currentTargetResId by rememberUpdatedState(targetResId)

    val player = remember {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_ALL
            playWhenReady = true
            volume = if (muted) 0f else 1f
        }
    }

    LaunchedEffect(muted, player) {
        player.volume = if (muted) 0f else 1f
    }

    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                val happyResId = context.happyBotVideoResId()
                if (happyResId != 0 && currentTargetResId != happyResId) {
                    targetResId = happyResId
                } else {
                    showStaticFallback = true
                }
            }
        }
        player.addListener(listener)
        onDispose {
            player.removeListener(listener)
            player.release()
        }
    }

    LaunchedEffect(player, targetResId) {
        if (targetResId == 0) {
            showStaticFallback = true
            return@LaunchedEffect
        }
        showStaticFallback = false
        val uri = Uri.parse("android.resource://${context.packageName}/$targetResId")
        player.setMediaItem(MediaItem.fromUri(uri))
        player.prepare()
        player.playWhenReady = true
        player.play()
    }

    if (showStaticFallback) {
        StaticBotImage(staticDrawableId = staticDrawableId, mood = mood)
    } else {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { viewContext ->
                PlayerView(viewContext).apply {
                    useController = false
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                    setShowBuffering(PlayerView.SHOW_BUFFERING_NEVER)
                    this.player = player
                    setShutterBackgroundColor(android.graphics.Color.BLACK)
                }
            },
            update = { it.player = player }
        )
    }
}

@Composable
private fun StaticBotImage(staticDrawableId: Int, mood: PrankstarBotMood) {
    Image(
        painter = painterResource(staticDrawableId),
        contentDescription = "Prankstar Bot mood: ${mood.name.lowercase().replace('_', ' ')}",
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    )
}

@Composable
private fun BoxScope.PlaybackBars(compact: Boolean) {
    val transition = rememberInfiniteTransition(label = "bot-playback-bars")
    Row(
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        repeat(5) { index ->
            val height by transition.animateFloat(
                initialValue = 8f + index * 2,
                targetValue = if (compact) 20f else 30f,
                animationSpec = infiniteRepeatable(tween(420 + index * 70), RepeatMode.Reverse),
                label = "bot-bar-$index"
            )
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(height.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (index % 2 == 0) CyanAccent else LimeAccent)
            )
        }
    }
}


@Composable
private fun ProcessingRing(accent: Color, pulse: Float, compact: Boolean) {
    Box(
        modifier = Modifier
            .size(if (compact) 52.dp else 68.dp)
            .border((2 + pulse).dp, accent.copy(alpha = 0.45f + pulse * 0.2f), CircleShape)
            .border(1.dp, FuchsiaAccent.copy(alpha = 0.38f), CircleShape)
    )
}

private fun android.content.Context.happyBotVideoResId(): Int {
    return resources.getIdentifier("prankstar_bot_happy", "raw", packageName)
}

private fun android.content.Context.findStaticBotDrawableId(): Int {
    val namedBot = resources.getIdentifier("prankstar_bot", "drawable", packageName)
    return when {
        namedBot != 0 -> namedBot
        else -> R.drawable.prankstar_sn1
    }
}
