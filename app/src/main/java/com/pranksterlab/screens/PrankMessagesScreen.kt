package com.pranksterlab.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pranksterlab.components.GlassPanel
import com.pranksterlab.components.HeadlineText
import com.pranksterlab.components.LabelCaps
import com.pranksterlab.theme.*
import kotlin.random.Random

data class MessageTemplate(val id: Int, val category: String, val text: String)

val CATEGORIES = listOf("FUNNY", "CREEPY LITE", "ROBOT", "AWKWARD", "PARTY", "OFFICE", "RANDOM")

val TEMPLATES = listOf(
    MessageTemplate(1, "FUNNY", "Important update: your couch has requested personal space."),
    MessageTemplate(2, "FUNNY", "A rubber chicken has been assigned to your case."),
    MessageTemplate(3, "FUNNY", "Your snack drawer has filed a complaint."),
    MessageTemplate(4, "FUNNY", "This is a friendly reminder that your left sock knows what happened."),
    MessageTemplate(5, "CREEPY LITE", "Not scary, but your hallway just made a noise in my imagination."),
    MessageTemplate(6, "CREEPY LITE", "Your closet has requested better lighting."),
    MessageTemplate(7, "CREEPY LITE", "The floorboards would like to speak with management."),
    MessageTemplate(8, "CREEPY LITE", "A tiny ghost says your Wi-Fi password is emotionally confusing."),
    MessageTemplate(9, "ROBOT", "BEEP. Your daily nonsense quota is now complete."),
    MessageTemplate(10, "ROBOT", "Robot inspection result: suspiciously human."),
    MessageTemplate(11, "ROBOT", "System scan complete. Snack levels critically low."),
    MessageTemplate(12, "ROBOT", "Alert: friendship protocol activated."),
    MessageTemplate(13, "AWKWARD", "This message was sent by mistake, but now we both have to live with it."),
    MessageTemplate(14, "AWKWARD", "Quick question: why did my phone just autocorrect your name to “chaos”?"),
    MessageTemplate(15, "AWKWARD", "Your vibe has been reviewed. Results pending."),
    MessageTemplate(16, "OFFICE", "Your imaginary meeting has been moved to never."),
    MessageTemplate(17, "OFFICE", "The printer has entered goblin mode."),
    MessageTemplate(18, "OFFICE", "Corporate has approved one tiny scream."),
    MessageTemplate(19, "PARTY", "Emergency party status: snacks required. Not a real emergency."),
    MessageTemplate(20, "PARTY", "Your dance permit has been conditionally approved."),
    MessageTemplate(21, "PARTY", "Confetti has been emotionally deployed.")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrankMessagesScreen() {
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf("FUNNY") }
    var messageText by remember { mutableStateOf("Important update: your couch has requested personal space.") }
    var phoneNumber by remember { mutableStateOf("") }

    val filteredTemplates = if (selectedCategory == "RANDOM") {
        TEMPLATES
    } else {
        TEMPLATES.filter { it.category == selectedCategory }
    }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item {
            HeadlineText("PRANK MESSAGES", color = CyanAccent)
            Text("Harmless prank texts, sent transparently", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            GlassPanel(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp), borderColor = FuchsiaAccent.copy(alpha=0.5f)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = FuchsiaAccent)
                        Spacer(modifier = Modifier.width(12.dp))
                        LabelCaps("RESPONSIBLE USE", color = FuchsiaAccent)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Messages must be sent from your real number or shared by you. Do not impersonate people, businesses, emergency services, banks, schools, employers, or official sources.",
                        color = OnBackground,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(CATEGORIES) { cat ->
                    val isSelected = selectedCategory == cat
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) CyanAccent else Color.Transparent)
                            .border(
                                1.dp,
                                if (isSelected) CyanAccent else OutlineDark,
                                RoundedCornerShape(16.dp)
                            )
                            .clickable { selectedCategory = cat }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        LabelCaps(cat, color = if (isSelected) Color.Black else OnBackground)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredTemplates) { template ->
                    GlassPanel(
                        modifier = Modifier
                            .width(200.dp)
                            .height(80.dp)
                            .clickable { messageText = template.text },
                        borderColor = Color.White.copy(alpha = 0.05f)
                    ) {
                        Text(
                            text = template.text,
                            color = OnBackground,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(12.dp),
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LabelCaps("TEMPLATE / DRAFT", color = CyanAccent)
                Row(
                    modifier = Modifier.clickable { 
                        val randomTemplate = TEMPLATES[Random.nextInt(TEMPLATES.size)]
                        messageText = randomTemplate.text
                    },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Shuffle, contentDescription = null, tint = FuchsiaAccent, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    LabelCaps("RANDOM", color = FuchsiaAccent)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = messageText,
                onValueChange = { messageText = it },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyanAccent,
                    unfocusedBorderColor = OutlineDark,
                    focusedContainerColor = GlassBackground,
                    unfocusedContainerColor = GlassBackground,
                    focusedTextColor = OnBackground,
                    unfocusedTextColor = OnBackground
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            LabelCaps("RECIPIENT (OPTIONAL)", color = CyanAccent)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                placeholder = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                 colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyanAccent,
                    unfocusedBorderColor = OutlineDark,
                    focusedContainerColor = GlassBackground,
                    unfocusedContainerColor = GlassBackground,
                    focusedTextColor = OnBackground,
                    unfocusedTextColor = OnBackground
                ),
                leadingIcon = { Icon(Icons.Default.Phone, null, tint = CyanAccent) }
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("smsto:$phoneNumber")
                        putExtra("sms_body", messageText)
                    }
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Send, null, tint = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                LabelCaps("OPEN SMS APP", color = Color.Black)
            }
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = {
                    val shareIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, "$messageText\n\n- Created with Prankster Lab")
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Share Prank"))
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, FuchsiaAccent),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = FuchsiaAccent),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Share, null, tint = FuchsiaAccent)
                Spacer(modifier = Modifier.width(8.dp))
                LabelCaps("SHARE PRANK TEXT", color = FuchsiaAccent)
            }
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
