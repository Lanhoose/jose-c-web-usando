
package com.getech.app.ui

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Tokens derivados dos CSS do site GeTech.
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

val LightBg = Color(0xFFF0F6FF)
val LightCard = Color(0xFFFFFFFF)
val LightText = Color(0xFF1E293B)
val LightSecondary = Color(0xFF475569)
val LightHeader = Color(0xFF1E3A6E)

@Composable
fun GeTechTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val scheme = if (darkTheme) {
        darkColorScheme(
            primary = Cyan, onPrimary = Color(0xFF001116),
            secondary = Blue, background = Bg, onBackground = TextPrimary,
            surface = Card, onSurface = TextPrimary,
            surfaceVariant = Card2, onSurfaceVariant = TextSecondary,
            outline = Border, error = Red
        )
    } else {
        lightColorScheme(
            primary = LightHeader, onPrimary = Color.White,
            secondary = Blue, background = LightBg, onBackground = LightText,
            surface = LightCard, onSurface = LightText,
            surfaceVariant = Color(0xFFE7EEF8), onSurfaceVariant = LightSecondary,
            outline = Color(0xFFCBD5E1), error = Red
        )
    }

    MaterialTheme(
        colorScheme = scheme,
        typography = Typography(
            headlineLarge = Typography().headlineLarge.copy(fontWeight = FontWeight.ExtraBold, fontSize = 30.sp),
            headlineMedium = Typography().headlineMedium.copy(fontWeight = FontWeight.Bold),
            titleLarge = Typography().titleLarge.copy(fontWeight = FontWeight.Bold),
            bodyMedium = Typography().bodyMedium.copy(lineHeight = 21.sp)
        ),
        content = content
    )
}
