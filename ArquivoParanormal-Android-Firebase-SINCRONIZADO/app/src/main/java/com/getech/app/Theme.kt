package com.getech.app

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// ============================================================================
// Paleta oficial GeTech — extraída 1:1 do design system do protótipo Lovable
// (src/styles.css, tokens OKLCH convertidos para sRGB)
// ============================================================================

// ---- Light ----
private val LightBackground = Color(0xFFF7F8FA)
private val LightForeground = Color(0xFF25292E)
private val LightCard = Color(0xFFFFFFFF)
private val LightPrimary = Color(0xFF0260D9)
private val LightSecondary = Color(0xFFEBEFF4)
private val LightSecondaryFg = Color(0xFF313B4A)
private val LightMuted = Color(0xFFEFF2F5)
private val LightMutedFg = Color(0xFF646D78)
private val LightAccent = Color(0xFF5598F9)
private val LightDestructive = Color(0xFFE62B34)
private val LightSuccess = Color(0xFF1C985A)
private val LightWarning = Color(0xFFE89D00)
private val LightBorder = Color(0xFFD9DFE5)
private val LightHeader = Color(0xFF0260D9)     // header = primary no modo claro
private val LightSidebar = Color(0xFF005C9B)
private val LightPanelGradientEnd = Color(0xFF023F6F)  // gradient-panel: sidebar -> este tom (160deg)

// ---- Dark ----
private val DarkBackground = Color(0xFF222525)
private val DarkForeground = Color(0xFFF0F2F4)
private val DarkCard = Color(0xFF2C3133)
private val DarkPrimary = Color(0xFF5598F9)
private val DarkPrimaryFg = Color(0xFF0B121A)
private val DarkSecondary = Color(0xFF383E41)
private val DarkMuted = Color(0xFF32393C)
private val DarkMutedFg = Color(0xFFA7AFB7)
private val DarkAccent = Color(0xFF7AC8F5)
private val DarkDestructive = Color(0xFFF4514F)
private val DarkSuccess = Color(0xFF47B777)
private val DarkWarning = Color(0xFFF9B73F)
private val DarkBorder = Color(0x1FFFFFFF)      // borda translúcida 12%
private val DarkHeader = Color(0xFF093F6A)      // header próprio, mais escuro que o primary
private val DarkSidebar = Color(0xFF252F37)
private val DarkPanelGradientEnd = Color(0xFF1B2B36)   // gradient-panel escuro: sidebar -> este tom (160deg)

/**
 * Cores que não existem no ColorScheme padrão do Material3 mas fazem parte
 * do design system Lovable (header distinto do primary, sidebar, sucesso/aviso).
 */
data class GeTechExtraColors(
    val header: Color,
    val headerForeground: Color,
    val sidebar: Color,
    val sidebarForeground: Color,
    val success: Color,
    val successForeground: Color,
    val warning: Color,
    val warningForeground: Color,
    val cardStripe: Color,
    val panelGradientEnd: Color
)

private val LightExtra = GeTechExtraColors(
    header = LightHeader, headerForeground = Color.White,
    sidebar = LightSidebar, sidebarForeground = Color.White,
    success = LightSuccess, successForeground = Color.White,
    warning = LightWarning, warningForeground = Color(0xFF251E15),
    cardStripe = LightAccent,
    panelGradientEnd = LightPanelGradientEnd
)

private val DarkExtra = GeTechExtraColors(
    header = DarkHeader, headerForeground = Color.White,
    sidebar = DarkSidebar, sidebarForeground = Color.White,
    success = DarkSuccess, successForeground = Color(0xFF102015),
    warning = DarkWarning, warningForeground = Color(0xFF201A0C),
    cardStripe = DarkAccent,
    panelGradientEnd = DarkPanelGradientEnd
)

val LocalGeTechColors = staticCompositionLocalOf { LightExtra }

private val LightColors = lightColorScheme(
    primary = LightPrimary,
    onPrimary = Color.White,
    secondary = LightSecondary,
    onSecondary = LightSecondaryFg,
    tertiary = LightAccent,
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = LightForeground,
    surface = LightCard,
    onSurface = LightForeground,
    surfaceVariant = LightMuted,
    onSurfaceVariant = LightMutedFg,
    outline = LightBorder,
    error = LightDestructive,
    onError = Color.White
)

private val DarkColors = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkPrimaryFg,
    secondary = DarkSecondary,
    onSecondary = DarkForeground,
    tertiary = DarkAccent,
    onTertiary = DarkPrimaryFg,
    background = DarkBackground,
    onBackground = DarkForeground,
    surface = DarkCard,
    onSurface = DarkForeground,
    surfaceVariant = DarkMuted,
    onSurfaceVariant = DarkMutedFg,
    outline = DarkBorder,
    error = DarkDestructive,
    onError = Color.White
)

// Raio de 0.5rem (8dp) usado em todos os cards/botões do protótipo Lovable
private val GeTechShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(6.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(12.dp),
    extraLarge = RoundedCornerShape(16.dp)
)

// ============================================================================
// Alto contraste — aproxima o filtro CSS "contrast(1.45) saturate(1.35)" do
// Lovable (classe .alto-contraste) usando tons extremos e cores mais saturadas.
// ============================================================================
private val LightColorsContraste = lightColorScheme(
    primary = Color(0xFF0047A8),
    onPrimary = Color.White,
    secondary = LightSecondary,
    onSecondary = Color.Black,
    tertiary = Color(0xFF1B6FDC),
    onTertiary = Color.White,
    background = Color.White,
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black,
    surfaceVariant = Color(0xFFE4E9EE),
    onSurfaceVariant = Color.Black,
    outline = Color(0xFF000000),
    error = Color(0xFFC40015),
    onError = Color.White
)

private val DarkColorsContraste = darkColorScheme(
    primary = Color(0xFF8FC1FF),
    onPrimary = Color.Black,
    secondary = DarkSecondary,
    onSecondary = Color.White,
    tertiary = Color(0xFFA7D8FF),
    onTertiary = Color.Black,
    background = Color.Black,
    onBackground = Color.White,
    surface = Color(0xFF121212),
    onSurface = Color.White,
    surfaceVariant = Color(0xFF2A2A2A),
    onSurfaceVariant = Color.White,
    outline = Color(0xFFFFFFFF),
    error = Color(0xFFFF8A80),
    onError = Color.Black
)

@Composable
fun GeTechTheme(dark: Boolean, altoContraste: Boolean = false, content: @Composable () -> Unit) {
    val extra = if (dark) DarkExtra else LightExtra
    val colors = when {
        dark && altoContraste -> DarkColorsContraste
        dark -> DarkColors
        altoContraste -> LightColorsContraste
        else -> LightColors
    }
    androidx.compose.runtime.CompositionLocalProvider(LocalGeTechColors provides extra) {
        MaterialTheme(
            colorScheme = colors,
            typography = Typography(),
            shapes = GeTechShapes,
            content = content
        )
    }
}

/** Acesso rápido às cores extras (header, sidebar, sucesso, aviso, listra do card). */
val MaterialTheme.geTechColors: GeTechExtraColors
    @Composable get() = LocalGeTechColors.current

// ============================================================================
// Controles globais de UI (tema e acessibilidade), equivalentes ao
// AppHeader.tsx (toggle sol/lua) e Acessibilidade.tsx do protótipo Lovable.
// Expostos via CompositionLocal para que qualquer tela (AppBar, botão
// flutuante) possa lê-los sem precisar receber parâmetros extras.
// ============================================================================
data class AppUiControls(
    val dark: Boolean,
    val onToggleDark: () -> Unit,
    val fontScale: Float,
    val onFontScaleChange: (Float) -> Unit,
    val altoContraste: Boolean,
    val onToggleContraste: () -> Unit,
    val onResetAcessibilidade: () -> Unit
)

val LocalAppUiControls = staticCompositionLocalOf<AppUiControls> {
    error("AppUiControls não fornecido — envolva o app com CompositionLocalProvider(LocalAppUiControls provides ...)")
}
