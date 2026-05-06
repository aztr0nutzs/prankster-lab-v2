package com.pranksterlab.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    background = BackgroundDark,
    surface = SurfaceDark,
    surfaceVariant = SurfaceBright,
    primary = CyanAccent,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,
    secondary = FuchsiaAccent,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = OnSecondaryContainer,
    tertiary = LimeAccent,
    error = ErrorRed,
    onBackground = OnBackground,
    onSurface = OnBackground,
    onSurfaceVariant = Color(0xFFBAC9CC),
    outline = OutlineDark
)

@Composable
fun PranksterLabTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
