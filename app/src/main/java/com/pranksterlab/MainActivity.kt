package com.pranksterlab

import android.net.Uri
import android.os.Bundle
import android.widget.VideoView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import com.pranksterlab.theme.PranksterLabTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PranksterLabTheme {
                var showBoot by remember { mutableStateOf(savedInstanceState == null) }
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
    Box(Modifier.fillMaxSize().background(Color.Black)) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                VideoView(context).apply {
                    setBackgroundColor(android.graphics.Color.BLACK)
                    setVideoURI(Uri.parse("android.resource://${context.packageName}/${R.raw.prankstar_boot}"))
                    setOnPreparedListener { player ->
                        player.isLooping = false
                        start()
                    }
                    setOnCompletionListener { onFinished() }
                    setOnErrorListener { _, _, _ ->
                        onFinished()
                        true
                    }
                }
            }
        )
    }
}
