package com.pranksterlab.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.googlefonts.Font
import com.pranksterlab.R

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val OrbitronFont = GoogleFont("Orbitron")
val RajdhaniFont = GoogleFont("Rajdhani")

val OrbitronFamily = FontFamily(
    Font(googleFont = OrbitronFont, fontProvider = provider)
)

val RajdhaniFamily = FontFamily(
    Font(googleFont = RajdhaniFont, fontProvider = provider)
)

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = OrbitronFamily,
        fontWeight = FontWeight.Black,
        fontStyle = FontStyle.Italic,
        fontSize = 40.sp,
        lineHeight = 48.sp,
        letterSpacing = 0.sp
    ),
    displayMedium = TextStyle(
        fontFamily = OrbitronFamily,
        fontWeight = FontWeight.Black,
        fontStyle = FontStyle.Italic,
        fontSize = 34.sp,
        lineHeight = 42.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = OrbitronFamily,
        fontWeight = FontWeight.Black,
        fontStyle = FontStyle.Italic,
        fontSize = 30.sp,
        lineHeight = 38.sp,
        letterSpacing = 0.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = OrbitronFamily,
        fontWeight = FontWeight.Black,
        fontStyle = FontStyle.Italic,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = OrbitronFamily,
        fontWeight = FontWeight.Black,
        fontStyle = FontStyle.Italic,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = OrbitronFamily,
        fontWeight = FontWeight.Black,
        fontStyle = FontStyle.Italic,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = OrbitronFamily,
        fontWeight = FontWeight.Black,
        fontStyle = FontStyle.Italic,
        fontSize = 22.sp,
        lineHeight = 30.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = OrbitronFamily,
        fontWeight = FontWeight.Black,
        fontStyle = FontStyle.Italic,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    ),
    titleSmall = TextStyle(
        fontFamily = OrbitronFamily,
        fontWeight = FontWeight.Black,
        fontStyle = FontStyle.Italic,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = RajdhaniFamily,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Italic,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = RajdhaniFamily,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Italic,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp
    ),
    bodySmall = TextStyle(
        fontFamily = RajdhaniFamily,
        fontWeight = FontWeight.SemiBold,
        fontStyle = FontStyle.Italic,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.sp
    ),
    labelLarge = TextStyle(
        fontFamily = OrbitronFamily,
        fontWeight = FontWeight.Black,
        fontStyle = FontStyle.Italic,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp
    ),
    labelMedium = TextStyle(
        fontFamily = OrbitronFamily,
        fontWeight = FontWeight.Black,
        fontStyle = FontStyle.Italic,
        fontSize = 12.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = OrbitronFamily,
        fontWeight = FontWeight.Black,
        fontStyle = FontStyle.Italic,
        fontSize = 10.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.sp
    )
)
