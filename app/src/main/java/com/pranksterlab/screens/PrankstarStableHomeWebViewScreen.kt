package com.pranksterlab.screens

import android.annotation.SuppressLint
import android.graphics.Color as AndroidColor
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import com.pranksterlab.bridge.PrankstarWebBridge
import com.pranksterlab.core.audio.AudioPlayerController
import com.pranksterlab.core.repository.SoundRepository

@SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
@Composable
fun PrankstarStableHomeWebViewScreen(
    audioPlayerController: AudioPlayerController,
    soundRepository: SoundRepository,
    onNavigate: (String) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val bridge = remember(audioPlayerController, soundRepository, onNavigate) {
        PrankstarWebBridge(audioPlayerController, soundRepository, onNavigate)
    }
    var webViewRef by remember { mutableStateOf<WebView?>(null) }

    DisposableEffect(bridge) {
        onDispose {
            webViewRef?.apply {
                stopLoading()
                loadUrl("about:blank")
                removeAllViews()
                destroy()
            }
            webViewRef = null
            bridge.release()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { viewContext ->
                WebView(viewContext).apply {
                    webViewRef = this
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    setBackgroundColor(AndroidColor.BLACK)
                    setLayerType(View.LAYER_TYPE_HARDWARE, null)
                    overScrollMode = View.OVER_SCROLL_NEVER
                    isVerticalScrollBarEnabled = false
                    isHorizontalScrollBarEnabled = false
                    isLongClickable = false
                    setOnLongClickListener { true }
                    webChromeClient = WebChromeClient()
                    webViewClient = WebViewClient()
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        mediaPlaybackRequiresUserGesture = false
                        allowFileAccess = true
                        allowContentAccess = true
                        allowFileAccessFromFileURLs = true
                        allowUniversalAccessFromFileURLs = false
                        builtInZoomControls = false
                        displayZoomControls = false
                        setSupportZoom(false)
                        loadWithOverviewMode = true
                        useWideViewPort = true
                        mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
                    }
                    addJavascriptInterface(bridge, "PrankstarBridge")
                    addJavascriptInterface(bridge, "PrankstarAndroid")
                    loadUrl(PRANKSTAR_HOME_WEBVIEW_URL)
                }
            },
            update = { webView ->
                if (webView.url != PRANKSTAR_HOME_WEBVIEW_URL) {
                    webView.loadUrl(PRANKSTAR_HOME_WEBVIEW_URL)
                }
            }
        )
    }
}
