package com.pranksterlab.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pranksterlab.components.HeadlineText
import com.pranksterlab.theme.CyanAccent

@Composable
fun SettingsScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        HeadlineText("SYSTEM SETUP", color = CyanAccent)
    }
}
