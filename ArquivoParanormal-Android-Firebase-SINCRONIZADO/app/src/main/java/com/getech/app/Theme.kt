package com.getech.app

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Paleta oficial GeTech (fonte: design system do protótipo Lovable)
// azul industrial #0664D7 · azul alerta #3D92FF · cinza claro #F4F4F4
// fundo escuro #232626 · card escuro #005CA1 · listra card #7AC7FF
private val IndustrialBlue = Color(0xFF0664D7)
private val AlertBlue = Color(0xFF3D92FF)
private val LightGray = Color(0xFFF4F4F4)
private val DarkBg = Color(0xFF232626)
private val DarkCard = Color(0xFF005CA1)
private val CardStripe = Color(0xFF7AC7FF)

private val LightColors = lightColorScheme(
    primary = IndustrialBlue,
    onPrimary = Color.White,
    secondary = AlertBlue,
    onSecondary = Color.White,
    background = LightGray,
    onBackground = Color(0xFF232626),
    surface = Color.White,
    onSurface = Color(0xFF232626),
    error = Color(0xFFD94841)
)

private val DarkColors = darkColorScheme(
    primary = AlertBlue,
    onPrimary = Color(0xFF0A1A2E),
    secondary = CardStripe,
    onSecondary = Color(0xFF0A1A2E),
    background = DarkBg,
    onBackground = Color(0xFFF4F4F4),
    surface = DarkCard,
    onSurface = Color.White
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
