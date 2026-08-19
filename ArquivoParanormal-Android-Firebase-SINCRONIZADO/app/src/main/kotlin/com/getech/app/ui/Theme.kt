package com.getech.app.ui

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Bg = Color(0xFF090D16)
val Bg2 = Color(0xFF0F172A)
val Card = Color(0xFF111827)
val Card2 = Color(0xFF172033)
val TextPrimary = Color(0xFFF8FAFC)
val TextSecondary = Color(0xFF94A3B8)
val Cyan = Color(0xFF00F2FE)
val Blue = Color(0xFF007BFF)
val Green = Color(0xFF10B981)
val Red = Color(0xFFEF4444)
val Border = Color(0x2638A3D1)

@Composable
fun GeTechTheme(content: @Composable () -> Unit) {
    val scheme = darkColorScheme(
        primary = Cyan,
        onPrimary = Color(0xFF001116),
        secondary = Blue,
        background = Bg,
        onBackground = TextPrimary,
        surface = Card,
        onSurface = TextPrimary,
        surfaceVariant = Card2,
        onSurfaceVariant = TextSecondary,
        outline = Border,
        error = Red
    )
    MaterialTheme(
        colorScheme = scheme,
        typography = Typography(
            headlineLarge = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold, fontSize = 30.sp),
            headlineMedium = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            titleLarge = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            bodyMedium = MaterialTheme.typography.bodyMedium.copy(lineHeight = 21.sp)
        ),
        content = content
    )
}
