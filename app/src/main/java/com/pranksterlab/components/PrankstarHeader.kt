package com.pranksterlab.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pranksterlab.R
import com.pranksterlab.theme.CyanAccent
import com.pranksterlab.theme.FuchsiaAccent
import com.pranksterlab.theme.LimeAccent

/**
 * Reusable top header banner that renders one of the prankstar_sn1/sn2/sn3 art panels
 * inside a neon-bordered frame, intended to enhance (not replace) the existing
 * dark cyberpunk UI.
 *
 * - SN1 is a square logo plate (cropped as a banner by default).
 * - SN2 and SN3 are wide marquees that tile cleanly at the top of a screen.
 */
enum class PrankstarHeaderVariant(val drawableId: Int, val accent: Color) {
    SN1(R.drawable.prankstar_sn1, CyanAccent),
    SN2(R.drawable.prankstar_sn2, FuchsiaAccent),
    SN3(R.drawable.prankstar_sn3, LimeAccent),
}

@Composable
fun PrankstarHeader(
    variant: PrankstarHeaderVariant,
    modifier: Modifier = Modifier,
    height: Dp = 84.dp,
    contentScale: ContentScale = if (variant == PrankstarHeaderVariant.SN1) ContentScale.Crop else ContentScale.FillWidth,
) {
    val accent = variant.accent
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
            .height(height)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Black.copy(alpha = 0.6f))
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    listOf(accent.copy(alpha = 0.55f), Color.Transparent, accent.copy(alpha = 0.35f))
                ),
                shape = RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = variant.drawableId),
            contentDescription = "Prankstar header ${variant.name}",
            modifier = Modifier.fillMaxSize(),
            contentScale = contentScale
        )
    }
}
