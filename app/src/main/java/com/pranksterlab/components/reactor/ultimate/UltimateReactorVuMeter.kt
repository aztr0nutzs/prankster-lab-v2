package com.pranksterlab.components.reactor.ultimate

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun UltimateReactorVuMeter(
    powered: Boolean,
    color: Color,
    modifier: Modifier = Modifier,
    phaseOffset: Int = 0
) {
    val transition = rememberInfiniteTransition(label = "ultimate-vu-$phaseOffset")
    val level by transition.animateFloat(
        initialValue = if (powered) 0.18f else 0.02f,
        targetValue = if (powered) 0.92f else 0.06f,
        animationSpec = infiniteRepeatable(tween(360 + phaseOffset * 80), RepeatMode.Reverse),
        label = "vu-level"
    )

    BoxWithConstraints(
        modifier = modifier
            .width(10.dp)
            .height(52.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(Color(0xFF040C18))
            .border(1.dp, Color(0xFF1A2A3A), RoundedCornerShape(2.dp)),
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(maxHeight * level)
                .background(Brush.verticalGradient(listOf(Color(0xFFFF2200), Color(0xFFFFCC00), color)))
        )
    }
}
