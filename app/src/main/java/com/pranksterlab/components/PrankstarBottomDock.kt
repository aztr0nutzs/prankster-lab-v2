package com.pranksterlab.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
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
    DockTab("library", "STASH", "Navigate to Library", Icons.Default.LibraryMusic),
    DockTab("forge", "FORGE", "Navigate to Forge", Icons.Default.Extension),
    DockTab("voice_lab", "JOKES", "Navigate to Jokes", Icons.Default.RecordVoiceOver),
    DockTab("system", "SYS", "Navigate to System", Icons.Default.Settings),
)

@Composable
fun PrankstarBottomDock(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .height(78.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(Color(0xFF0B1017))
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    listOf(
                        Color(0xFF4A6576).copy(alpha = 0.9f),
                        Color(0xFF1E2732),
                        Color(0xFF63879C).copy(alpha = 0.85f),
                    )
                ),
                shape = RoundedCornerShape(22.dp)
            )
    ) {
        Image(
            painter = painterResource(id = R.drawable.prankstar_dock_main),
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Fit,
            alpha = 0.14f,
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color.Black.copy(alpha = 0.44f),
                            Color.Black.copy(alpha = 0.30f),
                            Color.Black.copy(alpha = 0.46f),
                        )
                    )
                )
        )

        Row(
            modifier = Modifier
                .matchParentSize()
                .padding(horizontal = 6.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            dockTabs.forEach { tab ->
                val isSelected = currentRoute == tab.route
                val glowBrush = Brush.linearGradient(listOf(Color(0xFF2DE2E6), Color(0xFFFF8B2C), Color(0xFFB9FF39)))
                val tabShape = RoundedCornerShape(18.dp)

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(66.dp)
                        .clip(tabShape)
                        .then(
                            if (isSelected) {
                                Modifier
                                    .shadow(10.dp, tabShape, clip = false)
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(
                                                Color(0xE82DE2E6),
                                                Color(0xC21A6E7E),
                                                Color(0xD912151C),
                                            )
                                        ),
                                        shape = tabShape
                                    )
                                    .border(width = 2.dp, brush = glowBrush, shape = tabShape)
                            } else {
                                Modifier
                                    .background(Color(0xCC05070A), shape = tabShape)
                                    .border(1.dp, Color.White.copy(alpha = 0.12f), tabShape)
                            }
                        )
                        .clickable { onNavigate(tab.route) }
                        .semantics {
                            contentDescription = tab.contentDescription
                            role = Role.Tab
                            selected = isSelected
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            Color.Transparent,
                                            Color.White.copy(alpha = 0.18f),
                                            Color.Transparent,
                                        )
                                    )
                                )
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = null,
                            tint = if (isSelected) Color.White else Color(0xFF8EA0A9),
                            modifier = Modifier.size(17.dp),
                        )
                        Text(
                            text = tab.label,
                            color = if (isSelected) Color.White else Color(0xFF8EA0A9),
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                            maxLines = 1,
                        )
                    }
                }
            }
        }
    }
}
