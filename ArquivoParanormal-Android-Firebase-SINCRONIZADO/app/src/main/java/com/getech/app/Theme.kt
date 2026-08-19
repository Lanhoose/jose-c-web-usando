package com.getech.app

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Blue = Color(0xFF4A69E8)
private val Accent = Color(0xFF7397FF)
private val LightBg = Color(0xFFF7F8FA)
private val DarkBg = Color(0xFF252A2E)

private val LightColors = lightColorScheme(
    primary = Blue,
    secondary = Accent,
    background = LightBg,
    surface = Color.White,
    onPrimary = Color.White,
    onBackground = Color(0xFF30343B),
    onSurface = Color(0xFF30343B),
    error = Color(0xFFD94841)
)

private val DarkColors = darkColorScheme(
    primary = Accent,
    secondary = Color(0xFFA8BCFF),
    background = DarkBg,
    surface = Color(0xFF30363B),
    onPrimary = Color(0xFF17203A),
    onBackground = Color(0xFFF2F4F8),
    onSurface = Color(0xFFF2F4F8)
)

@Composable
fun GeTechTheme(dark: Boolean, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (dark) DarkColors else LightColors,
        typography = Typography(),
        shapes = Shapes(),
        content = content
    )
}
