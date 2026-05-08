package com.pranksterlab.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pranksterlab.R
import com.pranksterlab.theme.CyanAccent
import com.pranksterlab.theme.FuchsiaAccent
import com.pranksterlab.theme.LimeAccent

private fun accentForImage(@DrawableRes imageRes: Int): Color = when (imageRes) {
    R.drawable.prankstar_sn1 -> CyanAccent
    R.drawable.prankstar_sn2 -> FuchsiaAccent
    R.drawable.prankstar_sn3 -> LimeAccent
    else -> CyanAccent
}

/**
 * Reusable, premium-looking top header used by every major screen in the app.
 *
 * Renders the supplied drawable at the top of the screen, layers a dark gradient
 * over it for legibility, and overlays the screen title, subtitle, an optional
 * status pill, and optional trailing controls. The header is wrapped in a neon
 * frame compatible with the existing dark cyberpunk theme so it enhances rather
 * than replaces the screen layout below it.
 */
@Composable
fun PrankstarHeader(
    title: String,
    subtitle: String,
    @DrawableRes imageRes: Int,
    modifier: Modifier = Modifier,
    statusLabel: String? = null,
    trailingContent: (@Composable RowScope.() -> Unit)? = null,
) {
    val accent = accentForImage(imageRes)
    val shape = RoundedCornerShape(bottomStart = 18.dp, bottomEnd = 18.dp, topStart = 12.dp, topEnd = 12.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 132.dp, max = 156.dp)
            .clip(shape)
            .background(Color.Black)
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    listOf(
                        accent.copy(alpha = 0.65f),
                        Color.Transparent,
                        accent.copy(alpha = 0.45f),
                    )
                ),
                shape = shape,
            )
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.20f),
                            Color.Black.copy(alpha = 0.55f),
                            Color.Black.copy(alpha = 0.85f),
                        )
                    )
                )
        )

        ScanlineTrim()

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(2.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(Color.Transparent, accent.copy(alpha = 0.85f), Color.Transparent)
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.End,
            ) {
                if (statusLabel != null) {
                    StatusPill(label = statusLabel, accent = accent)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Text(
                        text = title.uppercase(),
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.5.sp,
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = subtitle,
                        color = accent.copy(alpha = 0.88f),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                if (trailingContent != null) {
                    Spacer(modifier = Modifier.width(12.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        content = trailingContent,
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusPill(label: String, accent: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Color.Black.copy(alpha = 0.55f))
            .border(1.dp, accent.copy(alpha = 0.7f), RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Text(
            text = label.uppercase(),
            color = accent,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
            ),
            maxLines = 1,
        )
    }
}

@Composable
private fun ScanlineTrim() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val spacing = 4.dp.toPx()
        var y = 0f
        while (y < size.height) {
            drawLine(
                color = Color.Black.copy(alpha = 0.10f),
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1.dp.toPx(),
            )
            y += spacing
        }
    }
}
