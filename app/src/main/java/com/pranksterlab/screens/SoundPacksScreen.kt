package com.pranksterlab.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pranksterlab.components.GlassPanel
import com.pranksterlab.components.HeadlineText
import com.pranksterlab.components.LabelCaps
import com.pranksterlab.theme.*

@Composable
fun SoundPacksScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        HeadlineText("FEATURED DATA PACKS", color = CyanAccent)
        Spacer(modifier = Modifier.height(24.dp))
        val packs = listOf(
            Triple("Classic Chaos", "Orange", OrangeAccent),
            Triple("Creepy House", "Fuchsia", FuchsiaAccent),
            Triple("Office Mayhem", "Cyan", CyanAccent),
            Triple("Sci-Fi Fails", "Yellow", Color.Yellow)
        )
        LazyVerticalGrid(columns = GridCells.Fixed(2), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(packs) { pack ->
                PackCard(pack.first, pack.second, pack.third)
            }
        }
    }
}

@Composable
fun PackCard(title: String, tag: String, color: Color) {
    GlassPanel {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.fillMaxWidth().height(100.dp).background(Color.Black)) {
                Box(modifier = Modifier.align(Alignment.BottomStart).padding(8.dp).background(color, RoundedCornerShape(12.dp)).padding(horizontal=8.dp, vertical=4.dp)) {
                    LabelCaps(tag, color = Color.Black)
                }
            }
            Column(modifier = Modifier.padding(12.dp)) {
                HeadlineText(title, color = color)
                Text("24 SFX", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Button(onClick={}, colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent), border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(0.3f))) {
                        LabelCaps("PREVIEW", color = color)
                    }
                }
            }
        }
    }
}
