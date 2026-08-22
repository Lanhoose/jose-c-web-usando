package com.arquivoparanormal.app.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arquivoparanormal.app.data.CLASSES
import com.arquivoparanormal.app.data.CONDICOES
import com.arquivoparanormal.app.data.CONTEUDOS_ADICIONAIS
import com.arquivoparanormal.app.data.ORIGENS_COMPLETAS
import com.arquivoparanormal.app.data.PERICIAS
import com.arquivoparanormal.app.data.TIPOS_CONTEUDO
import com.arquivoparanormal.app.data.LivroPdf
import com.arquivoparanormal.app.data.ARMAS_LIVRO
import com.arquivoparanormal.app.data.RITUAIS_LIVRO
import com.arquivoparanormal.app.data.MUNICOES_LIVRO
import com.arquivoparanormal.app.data.CONTEUDO_COMPLETO_PDFS
import com.arquivoparanormal.app.data.ITENS_ARQUIVOS_SECRETOS
import com.arquivoparanormal.app.data.AMEACAS_PRONTAS
import com.arquivoparanormal.app.ui.Acento
import com.arquivoparanormal.app.ui.Painel
import com.arquivoparanormal.app.ui.Primaria
import com.arquivoparanormal.app.ui.TextoClaro
import com.arquivoparanormal.app.ui.TextoFraco
import com.arquivoparanormal.app.ui.corElemento

private data class PesquisaCompendio(
    val nome: String,
    val tipo: String,
    val detalhe: String,
    val livro: LivroPdf?,
    val pagina: Int,
)

@Composable
fun CompendioScreen(onReferenciaClick: (LivroPdf, Int) -> Unit) {
    var filtro by remember { mutableStateOf("Todos") }
    var busca by remember { mutableStateOf("") }
    val referencias = if (filtro == "Todos") CONTEUDOS_ADICIONAIS else CONTEUDOS_ADICIONAIS.filter { it.tipo == filtro }
    val termo = busca.trim().lowercase()
    val resultados = buildList {
        CONTEUDO_COMPLETO_PDFS.filter { termo.isNotBlank() && (it.nome.lowercase().contains(termo) || it.tipo.lowercase().contains(termo)) }
            .forEach { add(PesquisaCompendio(it.nome, it.tipo, "${it.livro.titulo} · pág. ${it.pagina}", it.livro, it.pagina)) }
        CLASSES.filter { termo.isNotBlank() && (it.nome.lowercase().contains(termo) || it.trilhas.any { trilha -> trilha.lowercase().contains(termo) }) }
            .forEach { add(PesquisaCompendio(it.nome, "Classe", it.trilhas.joinToString(" · "), null, 0)) }
        PERICIAS.filter { termo.isNotBlank() && it.nome.lowercase().contains(termo) }
            .forEach { add(PesquisaCompendio(it.nome, "Perícia", it.attr.uppercase(), null, 0)) }
        CONDICOES.filter { termo.isNotBlank() && (it.nome.lowercase().contains(termo) || it.desc.lowercase().contains(termo)) }
            .forEach { add(PesquisaCompendio(it.nome, "Condição", it.desc, null, 0)) }
        com.arquivoparanormal.app.data.ELEMENTOS.filter { termo.isNotBlank() && (it.nome.lowercase().contains(termo) || it.desc.lowercase().contains(termo)) }
            .forEach { add(PesquisaCompendio(it.nome, "Elemento", it.desc, null, 0)) }
        CONTEUDOS_ADICIONAIS.filter { termo.isNotBlank() && (it.nome.lowercase().contains(termo) || it.tipo.lowercase().contains(termo) || it.detalhe.lowercase().contains(termo)) }
            .forEach { add(PesquisaCompendio(it.nome, it.tipo, "${it.livro.titulo} · pág. ${it.pagina}", it.livro, it.pagina)) }
        ARMAS_LIVRO.filter { termo.isNotBlank() && (it.nome.lowercase().contains(termo) || it.grupo.lowercase().contains(termo) || it.tipoDano.lowercase().contains(termo)) }
            .forEach { add(PesquisaCompendio(it.nome, "Arma", "${it.dano} · ${it.critico} · ${it.alcance}", LivroPdf.REGRAS, 0)) }
        RITUAIS_LIVRO.filter { termo.isNotBlank() && (it.nome.lowercase().contains(termo) || it.elemento.lowercase().contains(termo)) }
            .forEach { add(PesquisaCompendio(it.nome, "Ritual", "${it.circulo} · ${it.elemento}", LivroPdf.REGRAS, 0)) }
        ITENS_ARQUIVOS_SECRETOS.filter { termo.isNotBlank() && it.nome.lowercase().contains(termo) }
            .forEach { add(PesquisaCompendio(it.nome, "Item", "${it.tipo} · pág. ${it.pagina}", it.livro, it.pagina)) }
        AMEACAS_PRONTAS.filter { termo.isNotBlank() && (it.nome.lowercase().contains(termo) || it.elemento.lowercase().contains(termo)) }
            .forEach { add(PesquisaCompendio(it.nome, "Ameaça", "VD ${it.vd} · Defesa ${it.defesa} · PV ${it.pv}", LivroPdf.REGRAS, it.pagina)) }
        ORIGENS_COMPLETAS.filter { termo.isNotBlank() && it.nome.lowercase().contains(termo) }
            .forEach { add(PesquisaCompendio(it.nome, "Origem", "Perícias: ${it.pericias}", null, 0)) }
    }.distinctBy { "${it.tipo}|${it.nome}|${it.pagina}" }

    LazyColumn(
        Modifier.fillMaxSize().padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 14.dp),
    ) {
        item {
            Painel(titulo = "🔍 Buscar no Compêndio") {
                OutlinedTextField(
                    value = busca,
                    onValueChange = { busca = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Ex.: vampirismo, zumbi, fuzil, origem...") },
                    singleLine = true,
                )
                if (termo.isNotBlank()) {
                    Text("${resultados.size} resultado(s)", color = Acento, modifier = Modifier.padding(top = 8.dp))
                    resultados.take(40).forEach { r ->
                        Row(
                            Modifier.fillMaxWidth().clickable { if (r.livro != null && r.pagina > 0) onReferenciaClick(r.livro, r.pagina) }.padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            androidx.compose.foundation.layout.Column(Modifier.weight(1f)) {
                                Text(r.nome, color = TextoClaro, style = MaterialTheme.typography.titleSmall)
                                Text("${r.tipo} · ${r.detalhe}", color = TextoFraco, style = MaterialTheme.typography.bodySmall)
                            }
                            if (r.livro != null && r.pagina > 0) Text("Abrir", color = Primaria)
                        }
                    }
                }
            }
        }
        item {
            Painel(titulo = "Elementos do Outro Lado") {
                com.arquivoparanormal.app.data.ELEMENTOS.forEach { e ->
                    Text(e.nome, style = MaterialTheme.typography.titleSmall, color = corElemento(e.nome))
                    Text(e.desc, style = MaterialTheme.typography.bodySmall, color = TextoFraco)
                }
            }
        }
        item {
            Painel(titulo = "Classes e trilhas") {
                CLASSES.forEach { c ->
                    Text(c.nome, style = MaterialTheme.typography.titleSmall, color = TextoClaro)
                    Text(c.trilhas.joinToString(" · "), style = MaterialTheme.typography.bodySmall, color = TextoFraco)
                }
            }
        }
        item {
            Painel(titulo = "Origens") {
                ORIGENS_COMPLETAS.forEach { o ->
                    Text(o.nome, style = MaterialTheme.typography.titleSmall, color = TextoClaro)
                    Text("Perícias: ${o.pericias}", style = MaterialTheme.typography.bodySmall, color = Acento)
                    Text("Poder: ${o.poder}", style = MaterialTheme.typography.bodySmall, color = TextoFraco)
                }
            }
        }
        item {
            Painel(titulo = "Perícias") {
                PERICIAS.forEach { p ->
                    Text("${p.nome} (${p.attr.uppercase()})", style = MaterialTheme.typography.bodySmall, color = TextoClaro)
                }
            }
        }
        item {
            Painel(titulo = "Condições") {
                CONDICOES.forEach { c ->
                    Text(c.nome, style = MaterialTheme.typography.titleSmall, color = TextoClaro)
                    Text(c.desc, style = MaterialTheme.typography.bodySmall, color = TextoFraco)
                }
            }
        }

        item {
            Painel(titulo = "Equipamentos — Livro de Regras") {
                Text("Armas: ${ARMAS_LIVRO.size}", style = MaterialTheme.typography.titleSmall, color = TextoClaro)
                ARMAS_LIVRO.forEach { arma ->
                    Text(
                        "${arma.nome} · ${arma.dano} · ${arma.critico} · ${arma.alcance}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextoFraco,
                    )
                }
                Text("Munições: ${MUNICOES_LIVRO.size}", style = MaterialTheme.typography.titleSmall, color = TextoClaro, modifier = Modifier.padding(top = 8.dp))
                MUNICOES_LIVRO.forEach { municao ->
                    Text(municao.nome, style = MaterialTheme.typography.bodySmall, color = TextoFraco)
                }
            }
        }
        item {
            Painel(titulo = "Rituais — Livro de Regras") {
                Text("${RITUAIS_LIVRO.size} entradas do catálogo básico", style = MaterialTheme.typography.titleSmall, color = TextoClaro)
                RITUAIS_LIVRO.forEach { ritual ->
                    Text(
                        "${ritual.nome} · ${ritual.circulo} · ${ritual.elemento}",
                        style = MaterialTheme.typography.bodySmall,
                        color = corElemento(ritual.elemento),
                    )
                }
            }
        }
        item {
            Painel(titulo = "Catálogo completo dos PDFs") {
                Text(
                    "${CONTEUDO_COMPLETO_PDFS.size} entradas adicionais indexadas por livro e página.",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextoClaro,
                )
                CONTEUDO_COMPLETO_PDFS
                    .groupBy { it.tipo }
                    .forEach { (tipo, entradas) ->
                        Text("$tipo: ${entradas.size}", style = MaterialTheme.typography.labelLarge, color = Acento, modifier = Modifier.padding(top = 6.dp))
                        entradas.forEach { entrada ->
                            Text(
                                "${entrada.nome} · ${entrada.livro.titulo} · pág. ${entrada.pagina}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextoFraco,
                            )
                        }
                    }
            }
        }
        item {
            Text(
                "Conteúdo adicional",
                style = MaterialTheme.typography.headlineSmall,
                color = TextoClaro,
            )
            Text(
                "Rituais, itens, origens e trilhas dos livros extras. Toque em qualquer entrada para abrir exatamente a página correspondente no PDF.",
                style = MaterialTheme.typography.bodySmall,
                color = TextoFraco,
                modifier = Modifier.padding(top = 4.dp, bottom = 10.dp),
            )
            LazyFiltro(
                selecionado = filtro,
                opcoes = TIPOS_CONTEUDO,
                onSelecionar = { filtro = it },
            )
        }

        items(referencias, key = { "${it.livro}-${it.nome}-${it.pagina}" }) { referencia ->
            Painel(
                titulo = referencia.nome,
                acao = {
                    Text("pág. ${referencia.pagina}", color = Primaria, style = MaterialTheme.typography.labelMedium)
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                androidx.compose.foundation.layout.Column(
                    Modifier.fillMaxWidth().clickable { onReferenciaClick(referencia.livro, referencia.pagina) },
                ) {
                    Text(
                        "${referencia.tipo} • ${referencia.livro.titulo}${if (referencia.detalhe.isNotBlank()) " • ${referencia.detalhe}" else ""}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextoFraco,
                    )
                }
            }
        }
    }
}

@Composable
private fun LazyFiltro(
    selecionado: String,
    opcoes: List<String>,
    onSelecionar: (String) -> Unit,
) {
    androidx.compose.foundation.lazy.LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        items(opcoes) { opcao ->
            FilterChip(
                selected = selecionado == opcao,
                onClick = { onSelecionar(opcao) },
                label = { Text(opcao) },
            )
        }
    }
}
