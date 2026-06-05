package com.pranksterlab.components.video

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.pranksterlab.R
import com.pranksterlab.core.repository.dataStore
import kotlinx.coroutines.flow.map

@Composable
fun PrankstarVideoBackground(
    modifier: Modifier = Modifier,
    rawResId: Int = R.raw.prankstar_bg,
) {
    val context = LocalContext.current
    val animationIntensity by remember {
        context.dataStore.data.map { preferences ->
            preferences[androidx.datastore.preferences.core.stringPreferencesKey("animation_intensity")] ?: "FULL"
        }
    }.collectAsState(initial = "FULL")
    val showVideo = animationIntensity != "MINIMAL"

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    listOf(Color(0xFF08203A), Color(0xFF050812), Color.Black)
                )
            )
    ) {
        if (showVideo) {
            MutedLoopingRawVideo(
                rawResId = rawResId,
                modifier = Modifier.fillMaxSize(),
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM,
            )
        }
    }
}

@Composable
internal fun MutedLoopingRawVideo(
    rawResId: Int,
    modifier: Modifier = Modifier,
    resizeMode: Int = AspectRatioFrameLayout.RESIZE_MODE_ZOOM,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var failed by remember(rawResId) { mutableStateOf(false) }

    val player = remember(rawResId) {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_ALL
            playWhenReady = true
            volume = 0f
        }
    }

    DisposableEffect(player, lifecycleOwner) {
        val listener = object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                failed = true
            }
        }
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> if (!failed) player.play()
                Lifecycle.Event.ON_PAUSE -> player.pause()
                else -> Unit
            }
        }
        player.addListener(listener)
        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
            player.removeListener(listener)
            player.release()
        }
    }

    LaunchedEffect(player, rawResId) {
        failed = false
        player.setMediaItem(MediaItem.fromUri(Uri.parse("android.resource://${context.packageName}/$rawResId")))
        player.prepare()
        player.playWhenReady = true
        player.volume = 0f
        player.play()
    }

    if (!failed) {
        val exoPlayer = player
        AndroidView(
            modifier = modifier,
            factory = { viewContext ->
                PlayerView(viewContext).apply {
                    useController = false
                    this.resizeMode = resizeMode
                    setShowBuffering(PlayerView.SHOW_BUFFERING_NEVER)
                    setShutterBackgroundColor(android.graphics.Color.TRANSPARENT)
                    setPlayer(exoPlayer)
                }
            },
            update = { it.setPlayer(exoPlayer) }
        )
    }
}
