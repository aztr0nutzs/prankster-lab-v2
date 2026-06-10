package com.pranksterlab.components.twak

import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.pranksterlab.core.narration.TwakBotMood
import com.pranksterlab.core.repository.dataStore
import com.pranksterlab.theme.BackgroundDark
import com.pranksterlab.theme.CyanAccent
import com.pranksterlab.theme.FuchsiaAccent
import com.pranksterlab.theme.GlassBackground
import com.pranksterlab.theme.LimeAccent
import com.pranksterlab.theme.OrangeAccent
import kotlinx.coroutines.flow.map

@Composable
@OptIn(UnstableApi::class)
fun TwakBotVideo(
    mood: TwakBotMood,
    modifier: Modifier = Modifier,
    height: Dp = 132.dp,
    message: String? = null
) {
    val context = androidx.compose.ui.platform.LocalContext.current
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
    val videoResId = remember(context, mood) {
        mood.rawResourceId(context.packageName, context.resources::getIdentifier)
    }
    val allowVideo = animatedBotEnabled && animationIntensity != "MINIMAL" && videoResId != 0
    var playbackFailed by remember { mutableStateOf(false) }
    val accent = when (mood) {
        TwakBotMood.ERROR, TwakBotMood.REFUSAL -> OrangeAccent
        TwakBotMood.SAVED, TwakBotMood.EXCITED, TwakBotMood.PREVIEWING -> LimeAccent
        TwakBotMood.GENERATING -> FuchsiaAccent
        else -> CyanAccent
    }
    val player = remember(context) {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_ONE
            volume = 0f
            playWhenReady = true
        }
    }

    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                playbackFailed = true
            }
        }
        player.addListener(listener)
        onDispose {
            player.removeListener(listener)
            player.release()
        }
    }

    LaunchedEffect(videoResId, allowVideo) {
        playbackFailed = false
        if (allowVideo) {
            val uri = Uri.parse("android.resource://${context.packageName}/$videoResId")
            player.setMediaItem(MediaItem.fromUri(uri))
            player.prepare()
            player.playWhenReady = true
        } else {
            player.stop()
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(GlassBackground.copy(alpha = 0.82f))
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    listOf(CyanAccent.copy(alpha = 0.7f), accent.copy(alpha = 0.85f), FuchsiaAccent.copy(alpha = 0.62f))
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(10.dp)
            .semantics { contentDescription = "Twak Bot avatar ${mood.statusLabel}" },
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .clip(RoundedCornerShape(12.dp))
                .background(BackgroundDark.copy(alpha = 0.92f))
                .border(1.dp, accent.copy(alpha = 0.58f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (allowVideo && !playbackFailed) {
                AndroidView(
                    factory = { viewContext ->
                        PlayerView(viewContext).apply {
                            useController = false
                            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                            setShutterBackgroundColor(android.graphics.Color.TRANSPARENT)
                            this.player = player
                        }
                    },
                    update = { it.player = player },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Filled.SmartToy, contentDescription = null, tint = accent)
                    Text("TWAK BOT", color = accent, style = MaterialTheme.typography.labelLarge)
                    Text(
                        if (videoResId == 0) "ASSET PR PENDING" else "STATIC SAFE MODE",
                        color = Color.LightGray,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
        Text("TWAK BOT // ${mood.statusLabel}", color = accent, style = MaterialTheme.typography.labelLarge)
        message?.takeIf { it.isNotBlank() }?.let {
            Text(it, color = Color.LightGray, style = MaterialTheme.typography.bodySmall)
        }
    }
}
