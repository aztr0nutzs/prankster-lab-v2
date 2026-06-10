package com.pranksterlab.components.bot

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pranksterlab.core.bot.PrankstarBotState
import com.pranksterlab.core.model.PrankSound
import com.pranksterlab.theme.BackgroundDark
import com.pranksterlab.theme.CyanAccent
import com.pranksterlab.theme.FuchsiaAccent
import com.pranksterlab.theme.GlassBackground
import com.pranksterlab.theme.LimeAccent
import com.pranksterlab.theme.OrangeAccent

@Composable
fun PrankstarBotPanel(
    state: PrankstarBotState,
    onSubmit: (String) -> Unit,
    onPlaySound: (PrankSound) -> Unit,
    onStopAll: () -> Unit,
    onFavoriteSound: ((PrankSound) -> Unit)? = null,
    onOpenStash: () -> Unit,
    onSendToVoiceLab: (String, String?) -> Unit,
    onInputChanged: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var input by remember { mutableStateOf("") }
    val clipboard = LocalClipboardManager.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, CyanAccent.copy(alpha = 0.55f), RoundedCornerShape(22.dp))
            .background(
                Brush.linearGradient(
                    listOf(BackgroundDark.copy(alpha = 0.98f), GlassBackground, Color(0xFF14071D).copy(alpha = 0.96f))
                ),
                RoundedCornerShape(22.dp)
            )
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
            PrankstarBotVideo(
                mood = state.mood,
                message = null,
                compact = true,
                showMessageBubble = false,
                modifier = Modifier.weight(0.38f).semantics { contentDescription = "Prankstar Bot Agent mood ${state.mood}" }
            )
            Column(modifier = Modifier.weight(0.62f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("PRANKSTAR BOT AGENT", color = CyanAccent, style = MaterialTheme.typography.labelLarge)
                Text(state.message.text, color = Color.White, style = MaterialTheme.typography.bodyMedium)
                Text(
                    "For harmless comedy only. Do not use for threats, harassment, impersonation, emergency hoaxes, or illegal activity.",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(state.suggestedChips) { chip ->
                BotChip(chip) {
                    when (chip.lowercase()) {
                        "stop all" -> onStopAll()
                        "open stash" -> onOpenStash()
                        "send to voice lab" -> state.generatedText?.let { onSendToVoiceLab(it, state.suggestedVoicePresetId) }
                        else -> onSubmit(chip)
                    }
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = input,
                onValueChange = {
                    input = it.take(220)
                    onInputChanged(input)
                },
                modifier = Modifier.weight(1f).semantics { contentDescription = "Ask Prankstar Bot Agent" },
                placeholder = { Text("find creepy sounds") },
                label = { Text("Ask the bot") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FuchsiaAccent,
                    unfocusedBorderColor = CyanAccent.copy(alpha = 0.45f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = LimeAccent
                )
            )
            Button(
                onClick = {
                    if (input.isNotBlank()) {
                        onSubmit(input)
                        input = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = FuchsiaAccent),
                modifier = Modifier.semantics { contentDescription = "Send request to Prankstar Bot Agent" }
            ) {
                Icon(Icons.Default.Send, contentDescription = null, tint = Color.Black)
            }
        }

        if (state.recommendations.isNotEmpty()) {
            Text(state.recommendationReason ?: "Recommended stash sounds", color = LimeAccent, style = MaterialTheme.typography.labelMedium)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                state.recommendations.forEach { sound ->
                    RecommendationCard(
                        sound = sound,
                        onPlay = { onPlaySound(sound) },
                        onFavorite = onFavoriteSound?.let { { it(sound) } },
                        onOpenStash = onOpenStash
                    )
                }
            }
        }

        state.generatedText?.let { generated ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1220).copy(alpha = 0.92f)),
                border = BorderStroke(1.dp, FuchsiaAccent.copy(alpha = 0.45f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Generated line", color = FuchsiaAccent, style = MaterialTheme.typography.labelMedium)
                    Text(generated, color = Color.White)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { onSendToVoiceLab(generated, state.suggestedVoicePresetId) }, colors = ButtonDefaults.buttonColors(containerColor = CyanAccent)) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.Black)
                            Text("Voice Lab", color = Color.Black, modifier = Modifier.padding(start = 6.dp))
                        }
                        IconButton(onClick = { clipboard.setText(AnnotatedString(generated)) }) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy generated prank line", tint = LimeAccent)
                        }
                    }
                }
            }
        }

        state.prankPlan?.let { plan ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF101827).copy(alpha = 0.92f)),
                border = BorderStroke(1.dp, OrangeAccent.copy(alpha = 0.45f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(plan.title, color = OrangeAccent, style = MaterialTheme.typography.titleSmall)
                    plan.steps.forEachIndexed { index, step ->
                        Text("${index + 1}. ${step.label}", color = Color.White, style = MaterialTheme.typography.bodySmall)
                    }
                    Text(plan.safetyNote, color = Color(0xFFFCA5A5), style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
private fun BotChip(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .border(1.dp, CyanAccent.copy(alpha = 0.55f), RoundedCornerShape(999.dp))
            .background(Color(0xFF07111F).copy(alpha = 0.88f), RoundedCornerShape(999.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .semantics { contentDescription = "Bot quick action $label" }
    ) {
        Text(label, color = CyanAccent, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun RecommendationCard(
    sound: PrankSound,
    onPlay: () -> Unit,
    onFavorite: (() -> Unit)?,
    onOpenStash: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF07111F).copy(alpha = 0.9f)),
        border = BorderStroke(1.dp, CyanAccent.copy(alpha = 0.35f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(sound.name, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(2.dp))
                Text("${sound.category} • ${sound.tags.take(3).joinToString()}", color = Color.Gray, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            IconButton(onClick = onPlay, modifier = Modifier.size(44.dp)) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Play ${sound.name}", tint = LimeAccent)
            }
            if (onFavorite != null) {
                IconButton(onClick = onFavorite, modifier = Modifier.size(44.dp)) {
                    Icon(Icons.Default.Favorite, contentDescription = "Favorite ${sound.name}", tint = FuchsiaAccent)
                }
            }
            IconButton(onClick = onOpenStash, modifier = Modifier.size(44.dp)) {
                Icon(Icons.Default.LibraryMusic, contentDescription = "Open ${sound.name} in Sound Stash", tint = CyanAccent)
            }
        }
    }
}

@Composable
fun PrankstarBotStopButton(onStopAll: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onStopAll,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent)
    ) {
        Icon(Icons.Default.Stop, contentDescription = null, tint = Color.Black)
        Text("Stop All", color = Color.Black, modifier = Modifier.padding(start = 6.dp))
    }
}
