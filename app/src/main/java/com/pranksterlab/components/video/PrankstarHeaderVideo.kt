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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pranksterlab.R

@Composable
fun PrankstarHeaderVideo(
    modifier: Modifier = Modifier,
    height: Dp = 88.dp,
) {
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
        Image(
            painter = painterResource(R.drawable.prankstar_header),
            contentDescription = "Prankstar",
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Fit,
            alpha = 0.96f,
        )
    }
}
