package com.pranksterlab.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pranksterlab.theme.CyanAccent
import com.pranksterlab.theme.FuchsiaAccent
import com.pranksterlab.theme.GlassBackground
import com.pranksterlab.theme.LimeAccent

@Composable
fun BottomNavBar(currentRoute: String, onNavigate: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF0D0D0D).copy(alpha = 0.95f))
            .border(androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)), RoundedCornerShape(24.dp))
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NavItem(
            icon = Icons.Default.Bolt,
            label = "CORE",
            isSelected = currentRoute == "home",
            onClick = { onNavigate("home") },
            activeColor = LimeAccent
        )
        NavItem(
            icon = Icons.Default.LibraryMusic,
            label = "LIBRARY",
            isSelected = currentRoute == "library",
            onClick = { onNavigate("library") },
            activeColor = LimeAccent
        )
        NavItem(
            icon = Icons.Default.Extension,
            label = "FORGE",
            isSelected = currentRoute == "forge",
            onClick = { onNavigate("forge") },
            activeColor = LimeAccent
        )
        NavItem(
            icon = Icons.Default.Timer,
            label = "TRAPS",
            isSelected = currentRoute == "timer",
            onClick = { onNavigate("timer") },
            activeColor = LimeAccent
        )
        NavItem(
            icon = Icons.Default.RecordVoiceOver,
            label = "VOICE",
            isSelected = currentRoute == "voice_lab",
            onClick = { onNavigate("voice_lab") },
            activeColor = LimeAccent
        )
        NavItem(
            icon = Icons.Default.Settings,
            label = "SYSTEM",
            isSelected = currentRoute == "system",
            onClick = { onNavigate("system") },
            activeColor = LimeAccent
        )
    }
}

@Composable
fun NavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    activeColor: Color
) {
    val color = if (isSelected) activeColor else Color.Gray.copy(alpha = 0.6f)
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(imageVector = icon, contentDescription = label, tint = color)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = color
        )
    }
}
