package com.arquivoparanormal.app.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun RotuloOP(texto: String, modifier: Modifier = Modifier, cor: Color = TextoFraco) {
    Text(
        texto.uppercase(),
        modifier = modifier,
        style = MaterialTheme.typography.labelSmall,
        color = cor,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
fun Painel(
    titulo: String? = null,
    modifier: Modifier = Modifier,
    acao: (@Composable () -> Unit)? = null,
    conteudo: @Composable ColumnScopeAlias.() -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = Superficie),
        border = BorderStroke(1.dp, Borda),
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            if (titulo != null || acao != null) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        titulo.orEmpty(),
                        style = MaterialTheme.typography.titleMedium,
                        color = TextoClaro,
                        modifier = Modifier.weight(1f),
                    )
                    acao?.invoke()
                }
            }
            conteudo(ColumnScopeAlias)
        }
    }
}

/** Marcador simples para permitir conteúdo em coluna dentro do [Painel]. */
object ColumnScopeAlias

@Composable
fun Campo(
    label: String,
    modifier: Modifier = Modifier,
    conteudo: @Composable () -> Unit,
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        RotuloOP(label)
        conteudo()
    }
}

@Composable
private fun coresCampo() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Primaria,
    unfocusedBorderColor = Borda,
    focusedContainerColor = Fundo,
    unfocusedContainerColor = Fundo,
    focusedTextColor = TextoClaro,
    unfocusedTextColor = TextoClaro,
    cursorColor = Primaria,
)

@Composable
fun Texto(
    valor: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    senha: Boolean = false,
    aoMudar: (String) -> Unit,
) {
    OutlinedTextField(
        value = valor,
        onValueChange = aoMudar,
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        placeholder = { Text(placeholder, color = TextoFraco) },
        shape = RoundedCornerShape(3.dp),
        colors = coresCampo(),
        visualTransformation = if (senha) androidx.compose.ui.text.input.PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        textStyle = MaterialTheme.typography.bodyMedium,
    )
}

@Composable
fun AreaTexto(
    valor: String,
    aoMudar: (String) -> Unit,
    modifier: Modifier = Modifier,
    linhas: Int = 4,
) {
    OutlinedTextField(
        value = valor,
        onValueChange = aoMudar,
        modifier = modifier.fillMaxWidth(),
        minLines = linhas,
        shape = RoundedCornerShape(3.dp),
        colors = coresCampo(),
        textStyle = MaterialTheme.typography.bodyMedium,
    )
}

@Composable
fun Numero(
    valor: Int,
    modifier: Modifier = Modifier,
    aoMudar: (Int) -> Unit,
) {
    var texto by remember(valor) { mutableStateOf(valor.toString()) }
    OutlinedTextField(
        value = texto,
        onValueChange = { novo ->
            // BUG CORRIGIDO: o filtro antigo (`it.isDigit() || it == '-'`) aceitava
            // um "-" em qualquer posição do texto, então digitar algo como "1-2"
            // deixava o campo mostrando "1-2" (um número inválido) enquanto
            // `toIntOrNull()` silenciosamente virava 0 e era isso que ia para
            // `aoMudar` — o valor exibido na tela e o valor realmente salvo na
            // ficha ficavam diferentes, sem nenhum aviso para quem digitou.
            // Agora só um "-" é aceito, e apenas como primeiro caractere.
            val limpo = novo.filter { it.isDigit() || it == '-' }
            val negativo = limpo.startsWith('-')
            val digitos = limpo.filter { it.isDigit() }
            texto = if (negativo) "-$digitos" else digitos
            aoMudar(texto.toIntOrNull() ?: 0)
        },
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
        shape = RoundedCornerShape(3.dp),
        colors = coresCampo(),
        textStyle = MaterialTheme.typography.bodyMedium,
    )
}

@Composable
fun Selecao(
    valor: String,
    opcoes: List<String>,
    aoMudar: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Selecione",
) {
    var aberto by remember { mutableStateOf(false) }
    Box(modifier.fillMaxWidth()) {
        Row(
            Modifier
                .fillMaxWidth()
                .border(1.dp, Borda, RoundedCornerShape(3.dp))
                .background(Fundo, RoundedCornerShape(3.dp))
                .clickable { aberto = true }
                .padding(horizontal = 12.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                valor.ifBlank { placeholder },
                color = if (valor.isBlank()) TextoFraco else TextoClaro,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = TextoFraco)
        }
        DropdownMenu(expanded = aberto, onDismissRequest = { aberto = false }) {
            if (placeholder.isNotBlank()) {
                DropdownMenuItem(text = { Text(placeholder) }, onClick = { aoMudar(""); aberto = false })
            }
            opcoes.forEach { opcao ->
                DropdownMenuItem(text = { Text(opcao) }, onClick = { aoMudar(opcao); aberto = false })
            }
        }
    }
}

@Composable
fun BarraRecurso(
    label: String,
    atual: Int,
    max: Int,
    cor: Color,
    aoMudarAtual: (Int) -> Unit,
    aoMudarMax: (Int) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            RotuloOP(label)
            Text(
                "$atual / $max",
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.Monospace,
                color = cor,
                fontWeight = FontWeight.Bold,
            )
        }
        LinearProgressIndicator(
            progress = { if (max <= 0) 0f else (atual.toFloat() / max).coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = cor,
            trackColor = SuperficieAlta,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Numero(atual, Modifier.weight(1f), aoMudarAtual)
            Numero(max, Modifier.weight(1f), aoMudarMax)
        }
    }
}

@Composable
fun Chip(
    texto: String,
    ativo: Boolean,
    aoClicar: () -> Unit,
    corAtiva: Color = Primaria,
    enabled: Boolean = true,
) {
    Box(
        Modifier
            .border(1.dp, if (!enabled) Borda.copy(alpha = 0.45f) else if (ativo) corAtiva else Borda, RoundedCornerShape(3.dp))
            .background(if (!enabled) Color.Transparent else if (ativo) corAtiva.copy(alpha = 0.18f) else Color.Transparent, RoundedCornerShape(3.dp))
            .clickable(enabled = enabled, onClick = aoClicar)
            .padding(horizontal = 10.dp, vertical = 6.dp),
    ) {
        Text(
            texto,
            style = MaterialTheme.typography.bodySmall,
            color = if (!enabled) TextoFraco.copy(alpha = 0.38f) else if (ativo) TextoClaro else TextoFraco,
        )
    }
}

@Composable
fun IconeCircular(icone: @Composable () -> Unit) {
    Box(
        Modifier
            .size(44.dp)
            .border(1.dp, Borda, RoundedCornerShape(3.dp))
            .background(SuperficieAlta, RoundedCornerShape(3.dp)),
        contentAlignment = Alignment.Center,
    ) { icone() }
}
