package com.arquivoparanormal.app.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.arquivoparanormal.app.data.ConfiguracoesApp

// As propriedades usam "by mutableStateOf" (State do Compose), não "var" comum.
// Isso é essencial: um "var" simples não é observável, então telas que leem
// Fundo/Primaria/etc. não recompunham sozinhas ao trocar claro/escuro/cor —
// era preciso navegar para outra aba e voltar para o visual atualizar.
// Com State, qualquer composable que lê essas cores é automaticamente
// re-executado quando ArquivoParanormalTheme atualiza os valores abaixo.
private object CoresTemaAtual {
    var fundo by mutableStateOf(Color(0xFF12090A))
    var superficie by mutableStateOf(Color(0xFF1A1011))
    var superficieAlta by mutableStateOf(Color(0xFF241618))
    var borda by mutableStateOf(Color(0xFF3A2427))
    var primaria by mutableStateOf(Color(0xFFB23A2E))
    var acento by mutableStateOf(Color(0xFFD9A05B))
    var textoClaro by mutableStateOf(Color(0xFFEDE3DF))
    var textoFraco by mutableStateOf(Color(0xFF9C8A88))
    var perigo by mutableStateOf(Color(0xFFE05545))
}

// Mantém a API de cores usada pelas telas, mas agora os valores acompanham
// o tema escolhido. Isso evita que telas antigas fiquem presas ao modo escuro.
val Fundo: Color get() = CoresTemaAtual.fundo
val Superficie: Color get() = CoresTemaAtual.superficie
val SuperficieAlta: Color get() = CoresTemaAtual.superficieAlta
val Borda: Color get() = CoresTemaAtual.borda
val Primaria: Color get() = CoresTemaAtual.primaria
val Acento: Color get() = CoresTemaAtual.acento
val TextoClaro: Color get() = CoresTemaAtual.textoClaro
val TextoFraco: Color get() = CoresTemaAtual.textoFraco
val Perigo: Color get() = CoresTemaAtual.perigo

val CorSangue = Color(0xFFB23A2E)
val CorMorte = Color(0xFF6E6A76)
val CorConhecimento = Color(0xFFD9A05B)
val CorEnergia = Color(0xFF7B5AA6)
val CorMedo = Color(0xFF2F6D64)

fun corElemento(nome: String): Color = when (nome) {
    "Sangue" -> CorSangue
    "Morte" -> CorMorte
    "Conhecimento" -> CorConhecimento
    "Energia" -> CorEnergia
    "Medo" -> CorMedo
    else -> Borda
}

private val TipografiaBase = Typography(
    displaySmall = TextStyle(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 30.sp),
    headlineMedium = TextStyle(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 26.sp),
    headlineSmall = TextStyle(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 21.sp),
    titleMedium = TextStyle(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 17.sp),
    titleSmall = TextStyle(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 14.sp),
    bodyMedium = TextStyle(fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall = TextStyle(fontSize = 12.sp, lineHeight = 17.sp),
    labelSmall = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Medium, letterSpacing = 1.2.sp),
)

private fun Typography.escalar(fator: Float): Typography {
    val f = fator.coerceIn(0.85f, 1.25f)
    return copy(
        displaySmall = displaySmall.copy(fontSize = displaySmall.fontSize * f),
        headlineMedium = headlineMedium.copy(fontSize = headlineMedium.fontSize * f),
        headlineSmall = headlineSmall.copy(fontSize = headlineSmall.fontSize * f),
        titleMedium = titleMedium.copy(fontSize = titleMedium.fontSize * f),
        titleSmall = titleSmall.copy(fontSize = titleSmall.fontSize * f),
        bodyMedium = bodyMedium.copy(fontSize = bodyMedium.fontSize * f),
        bodySmall = bodySmall.copy(fontSize = bodySmall.fontSize * f),
        labelSmall = labelSmall.copy(fontSize = labelSmall.fontSize * f),
    )
}

@Composable
fun ArquivoParanormalTheme(
    config: ConfiguracoesApp,
    content: @Composable () -> Unit,
) {
    val sistemaEscuro = isSystemInDarkTheme()
    val escuro = when (config.tema) {
        "claro" -> false
        "sistema" -> sistemaEscuro
        else -> true
    }
    val primaria = config.corSelecionada()
    val fundo = if (escuro) Color(0xFF12090A) else Color(0xFFF7F2EF)
    val superficie = if (escuro) Color(0xFF1A1011) else Color(0xFFFFFBF8)
    val superficieAlta = if (escuro) Color(0xFF241618) else Color(0xFFEDE3DF)
    val texto = if (escuro) Color(0xFFEDE3DF) else Color(0xFF281B1C)
    val textoFraco = if (escuro) Color(0xFF9C8A88) else Color(0xFF67585A)
    val borda = if (escuro) Color(0xFF3A2427) else Color(0xFFD3C2C2)
    val acento = if (escuro) Color(0xFFD9A05B) else Color(0xFF8A5A1F)
    val perigo = if (escuro) Color(0xFFE05545) else Color(0xFFB3261E)

    CoresTemaAtual.fundo = fundo
    CoresTemaAtual.superficie = superficie
    CoresTemaAtual.superficieAlta = superficieAlta
    CoresTemaAtual.borda = borda
    CoresTemaAtual.primaria = primaria
    CoresTemaAtual.acento = acento
    CoresTemaAtual.textoClaro = texto
    CoresTemaAtual.textoFraco = textoFraco
    CoresTemaAtual.perigo = perigo

    val esquema = if (escuro) {
        darkColorScheme(
            primary = primaria,
            onPrimary = Color.White,
            secondary = acento,
            onSecondary = if (escuro) Color(0xFF241618) else Color.White,
            background = fundo,
            onBackground = texto,
            surface = superficie,
            onSurface = texto,
            surfaceVariant = superficieAlta,
            onSurfaceVariant = textoFraco,
            outline = borda,
            error = perigo,
        )
    } else {
        lightColorScheme(
            primary = primaria,
            onPrimary = Color.White,
            secondary = acento,
            onSecondary = Color.White,
            background = fundo,
            onBackground = texto,
            surface = superficie,
            onSurface = texto,
            surfaceVariant = superficieAlta,
            onSurfaceVariant = textoFraco,
            outline = borda,
            error = perigo,
        )
    }

    MaterialTheme(colorScheme = esquema, typography = TipografiaBase.escalar(config.escalaTexto), content = content)
}
