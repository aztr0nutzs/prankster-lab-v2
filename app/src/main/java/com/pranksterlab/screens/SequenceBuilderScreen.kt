package com.pranksterlab.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pranksterlab.components.GlassPanel
import com.pranksterlab.components.HeadlineText
import com.pranksterlab.components.LabelCaps
import com.pranksterlab.theme.*

@Composable
fun SequenceBuilderScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
            Column {
                Text(text = "SEQUENCE BUILDER", style = MaterialTheme.typography.displayLarge, color = CyanAccent)
                Text(text = "Design the ultimate sonic trap.", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        
        SequenceItem(1, "KNOCK", "SN-092-KNK", Icons.Default.DoorFront, "1.5")
        Spacer(modifier = Modifier.height(16.dp))
        SequenceItem(2, "VOICE", "SN-441-VCE", Icons.Default.RecordVoiceOver, "0.8")
        Spacer(modifier = Modifier.height(16.dp))
        SequenceItem(3, "FART", "SN-003-FRT", Icons.Default.Air, "5.0")
        
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {},
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(2.dp, CyanAccent.copy(alpha=0.2f))
        ) {
            Icon(Icons.Default.AddCircle, null, tint = CyanAccent)
            Spacer(modifier = Modifier.width(8.dp))
            LabelCaps("APPEND SIGNAL", color = CyanAccent)
        }
    }
}

@Composable
fun SequenceItem(number: Int, title: String, id: String, icon: androidx.compose.ui.graphics.vector.ImageVector, delay: String) {
    Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(GlassBackground).border(1.dp, CyanAccent, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
            HeadlineText(text = number.toString(), color = CyanAccent)
        }
        GlassPanel(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.background(CyanAccent.copy(alpha=0.1f)).padding(8.dp).clip(RoundedCornerShape(8.dp))) {
                        Icon(icon, null, tint = FuchsiaAccent)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        LabelCaps(title, color = CyanAccent)
                        Text(id, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        LabelCaps("DELAY (S)", color = Color.Gray)
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.background(Color.Black).padding(4.dp).clip(RoundedCornerShape(8.dp))) {
                            Icon(Icons.Default.Remove, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                            Text(delay, color = CyanAccent, modifier = Modifier.padding(horizontal=8.dp))
                            Icon(Icons.Default.Add, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                        }
                    }
                    Icon(Icons.Default.DragIndicator, null, tint = Color.Gray)
                }
            }
        }
    }
}
