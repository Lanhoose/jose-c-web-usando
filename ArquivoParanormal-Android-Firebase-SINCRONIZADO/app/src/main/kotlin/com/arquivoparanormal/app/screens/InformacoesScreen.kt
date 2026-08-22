package com.arquivoparanormal.app.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arquivoparanormal.app.data.AVISOS_IMPORTANTES
import com.arquivoparanormal.app.data.CAPITULOS_INFO
import com.arquivoparanormal.app.data.CapituloInfo
import com.arquivoparanormal.app.data.ItemSumario
import com.arquivoparanormal.app.data.LivroPdf
import com.arquivoparanormal.app.data.SUMARIOS_LIVROS
import com.arquivoparanormal.app.ui.Acento
import com.arquivoparanormal.app.ui.Painel
import com.arquivoparanormal.app.ui.Primaria
import com.arquivoparanormal.app.ui.TextoClaro
import com.arquivoparanormal.app.ui.TextoFraco

/**
 * Aba "Informações": reúne avisos importantes sobre o app e um índice de referência
 * com o sumário do Livro de Regras, organizado por capítulo e seção.
 */
@Composable
fun InformacoesScreen(onPaginaClick: (LivroPdf, Int) -> Unit) {
    LazyColumn(
        Modifier.fillMaxSize().padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 14.dp),
    ) {
        item {
            Text(
                "Informações importantes",
                style = MaterialTheme.typography.headlineSmall,
                color = TextoClaro,
            )
        }
        items(AVISOS_IMPORTANTES) { aviso ->
            Painel(titulo = aviso.titulo) {
                Text(aviso.texto, style = MaterialTheme.typography.bodySmall, color = TextoFraco)
            }
        }

        item {
            Text(
                "Sumário do livro de regras",
                style = MaterialTheme.typography.headlineSmall,
                color = TextoClaro,
                modifier = Modifier.padding(top = 6.dp),
            )
        }
        items(CAPITULOS_INFO) { capitulo ->
            CapituloExpansivel(capitulo) { pagina -> onPaginaClick(LivroPdf.REGRAS, pagina) }
        }

        item {
            Text(
                "Outros livros",
                style = MaterialTheme.typography.headlineSmall,
                color = TextoClaro,
                modifier = Modifier.padding(top = 10.dp),
            )
        }
        items(SUMARIOS_LIVROS) { livro ->
            LivroSumarioExpansivel(livro, onPaginaClick)
        }
    }
}

@Composable
private fun CapituloExpansivel(
    capitulo: CapituloInfo,
    onPaginaClick: (Int) -> Unit,
) {
    var aberto by remember { mutableStateOf(false) }

    Painel(
        acao = {
            Icon(
                if (aberto) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = if (aberto) "Recolher" else "Expandir",
                tint = TextoFraco,
            )
        },
        conteudo = {
            Column(
                Modifier.fillMaxWidth().clickable { aberto = !aberto },
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(capitulo.titulo, style = MaterialTheme.typography.titleSmall, color = Acento, modifier = Modifier.weight(1f))
                    Text(
                        "pág. ${capitulo.pagina}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Primaria,
                        modifier = Modifier.clickable { onPaginaClick(capitulo.pagina) }.padding(4.dp),
                    )
                }
                Text(capitulo.descricao, style = MaterialTheme.typography.bodySmall, color = TextoFraco)
            }

            if (aberto) {
                Column(Modifier.padding(top = 6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    capitulo.secoes.forEach { secao ->
                        ItemSumarioLinha(secao.nome, secao.pagina, secao.nivel, onPaginaClick)
                    }
                }
            }
        },
    )
}

@Composable
private fun LivroSumarioExpansivel(
    livro: com.arquivoparanormal.app.data.LivroSumario,
    onPaginaClick: (LivroPdf, Int) -> Unit,
) {
    var aberto by remember(livro.id) { mutableStateOf(false) }

    Painel(
        acao = {
            Icon(
                if (aberto) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = if (aberto) "Recolher" else "Expandir",
                tint = TextoFraco,
            )
        },
        conteudo = {
            Column(Modifier.fillMaxWidth().clickable { aberto = !aberto }) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(livro.titulo, style = MaterialTheme.typography.titleMedium, color = Acento, modifier = Modifier.weight(1f))
                    Text("${livro.secoes.firstOrNull()?.pagina ?: 1}", style = MaterialTheme.typography.labelSmall, color = Primaria)
                }
                Text(livro.descricao, style = MaterialTheme.typography.bodySmall, color = TextoFraco, modifier = Modifier.padding(top = 4.dp))
            }

            if (aberto) {
                Column(Modifier.padding(top = 8.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                    livro.secoes.forEach { secao ->
                        Row(
                            Modifier.fillMaxWidth().clickable { onPaginaClick(livro.id, secao.pagina) }.padding(vertical = 5.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(secao.titulo, style = MaterialTheme.typography.bodySmall, color = TextoClaro, modifier = Modifier.weight(1f))
                            Text("pág. ${secao.pagina}", style = MaterialTheme.typography.bodySmall, color = Primaria)
                        }
                        secao.secoes.forEach { item ->
                            ItemSumarioLinha(
                                item.nome,
                                item.pagina,
                                item.nivel,
                                { pagina -> onPaginaClick(livro.id, pagina) },
                            )
                        }
                    }
                }
            }
        },
    )
}

@Composable
private fun ItemSumarioLinha(
    nome: String,
    pagina: Int,
    nivel: Int,
    onPaginaClick: (Int) -> Unit,
) {
    Row(
        Modifier.fillMaxWidth().clickable { onPaginaClick(pagina) }.padding(start = (nivel * 14).dp, top = 5.dp, bottom = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(nome, style = MaterialTheme.typography.bodySmall, color = TextoClaro, modifier = Modifier.weight(1f))
        Text("pág. $pagina", style = MaterialTheme.typography.bodySmall, color = Primaria)
    }
}
