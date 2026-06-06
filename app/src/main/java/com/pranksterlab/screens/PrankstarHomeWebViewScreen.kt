package com.pranksterlab.screens

import android.annotation.SuppressLint
import android.graphics.Color as AndroidColor
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView

const val PRANKSTAR_HOME_WEBVIEW_URL = "file:///android_asset/prankstar/prankstar_new_home_bot_screen.html"

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun PrankstarHomeWebViewScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                WebView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    setBackgroundColor(AndroidColor.BLACK)
                    webChromeClient = WebChromeClient()
                    webViewClient = WebViewClient()
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.mediaPlaybackRequiresUserGesture = false
                    settings.allowFileAccess = true
                    settings.allowContentAccess = true
                    settings.loadWithOverviewMode = true
                    settings.useWideViewPort = true
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
