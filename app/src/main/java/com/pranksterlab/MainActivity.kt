package com.pranksterlab

import android.graphics.Matrix
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.Surface
import android.view.TextureView
import android.view.ViewGroup
import android.view.Window
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.viewinterop.AndroidView
import com.pranksterlab.theme.PranksterLabTheme
import kotlinx.coroutines.delay
import androidx.compose.foundation.gestures.detectTapGestures
import kotlin.math.min

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setBackgroundDrawable(android.graphics.drawable.ColorDrawable(android.graphics.Color.BLACK))
        window.statusBarColor = android.graphics.Color.BLACK
        window.navigationBarColor = android.graphics.Color.BLACK
        setContent {
            PranksterLabTheme {
                var showBoot by remember { mutableStateOf(true) }
                if (showBoot) {
                    PrankstarBootSequence(onFinished = { showBoot = false })
                } else {
                    PranksterApp()
                }
            }
        }
    }
}

@Composable
private fun PrankstarBootSequence(onFinished: () -> Unit) {
    var finished by remember { mutableStateOf(false) }
    var playbackStarted by remember { mutableStateOf(false) }
    var completionFallbackMs by remember { mutableStateOf<Long?>(null) }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    fun finishOnce(reason: String) {
        if (!finished) {
            finished = true
            Log.i(PRANKSTAR_BOOT_TAG, "Boot sequence finished: $reason")
            onFinished()
        }
    }

    LaunchedEffect(Unit) {
        delay(PRANKSTAR_BOOT_STARTUP_TIMEOUT_MS)
        if (!playbackStarted) {
            finishOnce("startup timeout fallback")
        }
    }

    LaunchedEffect(completionFallbackMs) {
        val fallbackDelay = completionFallbackMs ?: return@LaunchedEffect
        delay(fallbackDelay)
        if (playbackStarted) {
            finishOnce("duration fallback")
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                detectTapGestures { finishOnce("tap skip") }
            }
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                FrameLayout(context).apply {
                    setBackgroundColor(android.graphics.Color.BLACK)
                    val videoSurface = TextureView(context).apply {
                        layoutParams = FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }
                    addView(videoSurface)

                    videoSurface.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                        override fun onSurfaceTextureAvailable(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
                            val surface = Surface(surfaceTexture)
                            val player = MediaPlayer()
                            mediaPlayer = player

                            try {
                                val asset = context.assets.openFd(PRANKSTAR_BOOT_ASSET_PATH)
                                player.setDataSource(asset.fileDescriptor, asset.startOffset, asset.length)
                                asset.close()
                                player.setSurface(surface)
                                player.isLooping = false
                                player.setOnPreparedListener { preparedPlayer ->
                                    applyVideoContain(videoSurface, width, height, preparedPlayer.videoWidth, preparedPlayer.videoHeight)
                                    playbackStarted = true
                                    completionFallbackMs = if (preparedPlayer.duration > 0) {
                                        preparedPlayer.duration.toLong() + PRANKSTAR_BOOT_COMPLETION_GRACE_MS
                                    } else {
                                        PRANKSTAR_BOOT_UNKNOWN_DURATION_TIMEOUT_MS
                                    }
                                    Log.i(
                                        PRANKSTAR_BOOT_TAG,
                                        "Boot video prepared from $PRANKSTAR_BOOT_ASSET_PATH durationMs=${preparedPlayer.duration}"
                                    )
                                    preparedPlayer.start()
                                }
                                player.setOnCompletionListener { finishOnce("video completed") }
                                player.setOnErrorListener { _, what, extra ->
                                    Log.e(PRANKSTAR_BOOT_TAG, "Boot video error what=$what extra=$extra")
                                    completionFallbackMs = PRANKSTAR_BOOT_FAILURE_TIMEOUT_MS
                                    true
                                }
                                player.prepareAsync()
                            } catch (error: Exception) {
                                Log.e(PRANKSTAR_BOOT_TAG, "Unable to load boot video asset: $PRANKSTAR_BOOT_ASSET_PATH", error)
                                surface.release()
                                player.release()
                                mediaPlayer = null
                            }
                        }

                        override fun onSurfaceTextureSizeChanged(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
                            mediaPlayer?.let { player ->
                                applyVideoContain(videoSurface, width, height, player.videoWidth, player.videoHeight)
                            }
                        }

                        override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture): Boolean {
                            mediaPlayer?.release()
                            mediaPlayer = null
                            return true
                        }

                        override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture) = Unit
                    }
                }
            }
        )
    }
}

private fun applyVideoContain(textureView: TextureView, viewWidth: Int, viewHeight: Int, videoWidth: Int, videoHeight: Int) {
    if (viewWidth <= 0 || viewHeight <= 0 || videoWidth <= 0 || videoHeight <= 0) return

    val scale = min(viewWidth.toFloat() / videoWidth.toFloat(), viewHeight.toFloat() / videoHeight.toFloat())
    val scaledWidth = videoWidth * scale
    val scaledHeight = videoHeight * scale
    val matrix = Matrix().apply {
        setScale(scaledWidth / viewWidth, scaledHeight / viewHeight, viewWidth / 2f, viewHeight / 2f)
    }
    textureView.setTransform(matrix)
}

private const val PRANKSTAR_BOOT_TAG = "PrankstarBoot"
private const val PRANKSTAR_BOOT_ASSET_PATH = "prankstar/assets/prankstar_boot2.mp4"
private const val PRANKSTAR_BOOT_STARTUP_TIMEOUT_MS = 8_000L
private const val PRANKSTAR_BOOT_UNKNOWN_DURATION_TIMEOUT_MS = 8_000L
private const val PRANKSTAR_BOOT_FAILURE_TIMEOUT_MS = 6_000L
private const val PRANKSTAR_BOOT_COMPLETION_GRACE_MS = 1_500L
