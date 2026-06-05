package com.pranksterlab.components.video

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.media3.ui.AspectRatioFrameLayout
import com.pranksterlab.R
import com.pranksterlab.core.repository.dataStore
import kotlinx.coroutines.flow.map

@Composable
fun PrankstarHeaderVideo(
    modifier: Modifier = Modifier,
    rawResId: Int = R.raw.prankstar_header,
    height: Dp = 88.dp,
) {
    val context = LocalContext.current
    val animationIntensity by remember {
        context.dataStore.data.map { preferences ->
            preferences[androidx.datastore.preferences.core.stringPreferencesKey("animation_intensity")] ?: "FULL"
        }
    }.collectAsState(initial = "FULL")
    val showVideo = animationIntensity != "MINIMAL"
    val shape = RoundedCornerShape(14.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clip(shape)
            .background(Color.Black)
            .border(
                1.dp,
                Brush.horizontalGradient(
                    listOf(Color(0xFF00E8FF), Color(0xFFFF00CC), Color(0xFFFF8800))
                ),
                shape
            ),
        contentAlignment = Alignment.Center
    ) {
        if (showVideo) {
            MutedLoopingRawVideo(
                rawResId = rawResId,
                modifier = Modifier.matchParentSize(),
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM,
            )
        } else {
            Image(
                painter = painterResource(R.drawable.prankstar_sn1),
                contentDescription = "Prankstar",
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.82f,
            )
        }
        Box(
            Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Black.copy(alpha = 0.18f), Color.Black.copy(alpha = 0.46f))
                    )
                )
        )
        Text(
            text = "PRANKSTAR CORE",
            color = Color.White,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
            modifier = Modifier.align(Alignment.BottomStart).padding(start = 14.dp, bottom = 10.dp)
        )
    }
}
