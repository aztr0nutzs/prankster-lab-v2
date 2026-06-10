package com.pranksterlab.components.twak

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.pranksterlab.theme.BackgroundDark
import com.pranksterlab.theme.CyanAccent
import com.pranksterlab.theme.FuchsiaAccent
import com.pranksterlab.theme.LimeAccent

@Composable
fun TwakAttackHeader(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val headerResId = remember(context) {
        context.resources.getIdentifier("twak_attack_header", "drawable", context.packageName)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(104.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(BackgroundDark.copy(alpha = 0.72f))
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    listOf(
                        CyanAccent.copy(alpha = 0.75f),
                        FuchsiaAccent.copy(alpha = 0.7f),
                        Color(0xFFB7FF2A).copy(alpha = 0.65f)
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(6.dp)
            .semantics { contentDescription = "Twak-Attacks narrator header" },
        contentAlignment = Alignment.Center
    ) {
        if (headerResId != 0) {
            Image(
                painter = painterResource(headerResId),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("TWAK-ATTACKS", color = LimeAccent, style = MaterialTheme.typography.titleMedium)
                Text("Tweaker Geographic narrator", color = CyanAccent, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
