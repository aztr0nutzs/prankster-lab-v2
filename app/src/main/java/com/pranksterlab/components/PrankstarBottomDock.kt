package com.pranksterlab.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.matchParentSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pranksterlab.R

data class DockTab(
    val route: String,
    val label: String,
    val contentDescription: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
)

private val dockTabs = listOf(
    DockTab("home", "CORE", "Navigate to Core", Icons.Default.Bolt),
    DockTab("library", "LIBRARY", "Navigate to Library", Icons.Default.LibraryMusic),
    DockTab("forge", "FORGE", "Navigate to Forge", Icons.Default.Extension),
    DockTab("voice_lab", "JOKES", "Navigate to Jokes", Icons.Default.RecordVoiceOver),
    DockTab("system", "SYSTEM", "Navigate to System", Icons.Default.Settings),
)

@Composable
fun PrankstarBottomDock(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .height(92.dp)
            .clip(RoundedCornerShape(26.dp))
    ) {
        Image(
            painter = painterResource(id = R.drawable.prankstar_dock_main),
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.FillBounds,
        )

        Row(
            modifier = Modifier
                .matchParentSize()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            dockTabs.forEach { tab ->
                val isSelected = currentRoute == tab.route
                val glowBrush = Brush.horizontalGradient(listOf(Color(0xFF2DE2E6), Color(0xFFFF8B2C)))

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(18.dp))
                        .then(
                            if (isSelected) {
                                Modifier
                                    .border(width = 2.dp, brush = glowBrush, shape = RoundedCornerShape(18.dp))
                                    .background(Color(0xA02DE2E6), shape = RoundedCornerShape(18.dp))
                            } else {
                                Modifier.background(Color(0x33000000), shape = RoundedCornerShape(18.dp))
                            }
                        )
                        .clickable { onNavigate(tab.route) }
                        .semantics { contentDescription = tab.contentDescription },
                    contentAlignment = Alignment.Center,
                ) {
                    Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = null,
                            tint = if (isSelected) Color(0xFFEBFCFF) else Color(0xFF95A7B0),
                            modifier = Modifier.size(16.dp),
                        )
                        Text(
                            text = tab.label,
                            color = if (isSelected) Color(0xFFEBFCFF) else Color(0xFF95A7B0),
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            modifier = Modifier.padding(start = 4.dp),
                        )
                    }
                }
            }
        }
    }
}
