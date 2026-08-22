package com.arquivoparanormal.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.arquivoparanormal.app.data.AMEACAS_PRONTAS
import com.arquivoparanormal.app.data.AmeacaPronta
import com.arquivoparanormal.app.data.Monstro
import com.arquivoparanormal.app.data.Repositorio
import com.arquivoparanormal.app.ui.AreaTexto
import com.arquivoparanormal.app.ui.Acento
import com.arquivoparanormal.app.ui.Borda
import com.arquivoparanormal.app.ui.Campo
import com.arquivoparanormal.app.ui.CorConhecimento
import com.arquivoparanormal.app.ui.CorEnergia
import com.arquivoparanormal.app.ui.CorMorte
import com.arquivoparanormal.app.ui.CorSangue
import com.arquivoparanormal.app.ui.Fundo
import com.arquivoparanormal.app.ui.Numero
import com.arquivoparanormal.app.ui.Painel
import com.arquivoparanormal.app.ui.Perigo
import com.arquivoparanormal.app.ui.Primaria
import com.arquivoparanormal.app.ui.Selecao
import com.arquivoparanormal.app.ui.SuperficieAlta
import com.arquivoparanormal.app.ui.TextoFraco
import com.arquivoparanormal.app.ui.Texto as CampoTexto

private fun elementoCor(elemento: String): Color = when {
    elemento.contains("Sangue", ignoreCase = true) -> CorSangue
    elemento.contains("Morte", ignoreCase = true) -> CorMorte
    elemento.contains("Conhecimento", ignoreCase = true) -> CorConhecimento
    elemento.contains("Energia", ignoreCase = true) -> CorEnergia
    else -> Borda
}

private fun limparTexto(texto: String): String =
    texto.replace(Regex("\\s+"), " ").trim()

@Composable
fun BestiarioScreen(repo: Repositorio) {
    var busca by remember { mutableStateOf("") }

    val filtradas = remember(busca) {
        AMEACAS_PRONTAS.filter {
            busca.isBlank() ||
                it.nome.contains(busca, ignoreCase = true) ||
                it.elemento.contains(busca, ignoreCase = true) ||
                it.vd.contains(busca, ignoreCase = true)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Fundo)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 18.dp, bottom = 28.dp),
    ) {
        item {
            BestiarioCabecalho(
                busca = busca,
                aoBuscar = { busca = it },
                quantidade = filtradas.size,
            )
        }

        if (filtradas.isEmpty()) {
            item {
                Painel {
                    Text(
                        "Nenhuma ameaça encontrada.",
                        style = MaterialTheme.typography.titleSmall,
                    )
                    Text(
                        "Tente pesquisar pelo nome, elemento ou VD.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextoFraco,
                    )
                }
            }
        }

        items(filtradas, key = { "catalogo-${it.nome}-${it.pagina}" }) { ameaca ->
            AmeacaCatalogoCard(
                ameaca = ameaca,
                aoAdicionar = { quantidade ->
                    repo.adicionarAmeacaABatalha(ameaca, quantidade)
                },
            )
        }

        item {
            Spacer(Modifier.height(4.dp))
            Painel(
                titulo = "Criaturas personalizadas",
                acao = {
                    IconButton(onClick = { repo.criarMonstro() }) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Nova criatura",
                            tint = Primaria,
                        )
                    }
                },
            ) {
                Text(
                    "Crie ameaças próprias para a campanha. Elas ficam disponíveis somente para o Mestre.",
                    color = TextoFraco,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }

        items(repo.monstros.toList(), key = { it.id }) { monstro ->
            MonstroPersonalizadoCard(
                monstro = monstro,
                salvar = { repo.salvar(it) },
                excluir = { repo.removerMonstro(monstro.id) },
            )
        }
    }
}

@Composable
private fun BestiarioCabecalho(
    busca: String,
    aoBuscar: (String) -> Unit,
    quantidade: Int,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SuperficieAlta),
        border = androidx.compose.foundation.BorderStroke(1.dp, Borda),
    ) {
        Column(
            Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        "BESTIÁRIO",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Acento,
                    )
                    Text(
                        "Catálogo de ameaças",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextoFraco,
                    )
                }
                Box(
                    modifier = Modifier
                        .border(1.dp, Primaria, RoundedCornerShape(8.dp))
                        .background(Primaria.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                ) {
                    Text(
                        "$quantidade ameaças",
                        style = MaterialTheme.typography.labelSmall,
                        color = Acento,
                    )
                }
            }

            OutlinedTextField(
                value = busca,
                onValueChange = aoBuscar,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = TextoFraco)
                },
                placeholder = {
                    Text("Pesquisar por nome, elemento ou VD", color = TextoFraco)
                },
                shape = RoundedCornerShape(9.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primaria,
                    unfocusedBorderColor = Borda,
                    focusedContainerColor = Fundo,
                    unfocusedContainerColor = Fundo,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    cursorColor = Primaria,
                ),
            )
        }
    }
}

@Composable
private fun AmeacaCatalogoCard(
    ameaca: AmeacaPronta,
    aoAdicionar: (Int) -> Unit,
) {
    var expandido by remember(ameaca.nome, ameaca.pagina) { mutableStateOf(false) }
    var quantidade by remember(ameaca.nome, ameaca.pagina) { mutableStateOf(1) }
    val cor = elementoCor(ameaca.elemento)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = SuperficieAlta),
        border = androidx.compose.foundation.BorderStroke(1.dp, Borda),
    ) {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    Modifier
                        .size(8.dp)
                        .background(cor, RoundedCornerShape(50)),
                )
                Spacer(Modifier.size(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        ameaca.nome,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        ameaca.fonte,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextoFraco,
                    )
                }
                IconButton(onClick = { expandido = !expandido }) {
                    Icon(
                        if (expandido) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (expandido) "Recolher" else "Ver detalhes",
                        tint = Acento,
                    )
                }
            }

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Estatistica("VD", ameaca.vd, Modifier.weight(1f))
                Estatistica("PV", ameaca.pv.toString(), Modifier.weight(1f))
                Estatistica("DEF", ameaca.defesa.toString(), Modifier.weight(1f))
                Estatistica("MOV", "${ameaca.deslocamento}m", Modifier.weight(1f))
            }

            if (ameaca.elemento.isNotBlank()) {
                Text(
                    ameaca.elemento,
                    style = MaterialTheme.typography.labelSmall,
                    color = cor,
                    fontWeight = FontWeight.Bold,
                )
            }

            if (expandido) {
                HorizontalDivider(color = Borda)

                Text(
                    "ATRIBUTOS",
                    style = MaterialTheme.typography.labelSmall,
                    color = Acento,
                )
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    listOf("FOR", "AGI", "INT", "PRE", "VIG").forEach { chave ->
                        val valor = ameaca.atributos[chave.lowercase()] ?: 0
                        Atributo(chave, valor)
                    }
                }

                if (ameaca.ataques.isNotBlank()) {
                    DetalheTexto("ATAQUES", ameaca.ataques)
                }

                if (ameaca.detalhes.isNotBlank()) {
                    DetalheTexto("INFORMAÇÕES", limparTexto(ameaca.detalhes))
                }

                if (ameaca.pagina > 0) {
                    Text(
                        "Fonte: ${ameaca.fonte} • Página ${ameaca.pagina}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextoFraco,
                    )
                }
            }

            HorizontalDivider(color = Borda)

            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Campo("Qtd.", Modifier.weight(0.8f)) {
                    Numero(
                        quantidade,
                        Modifier.fillMaxWidth(),
                    ) { quantidade = it.coerceIn(1, 20) }
                }
                Button(
                    onClick = { aoAdicionar(quantidade) },
                    modifier = Modifier
                        .weight(2.2f)
                        .height(54.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primaria),
                ) {
                    Text("Adicionar à batalha", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun Estatistica(
    titulo: String,
    valor: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier
            .background(Fundo, RoundedCornerShape(7.dp))
            .border(1.dp, Borda, RoundedCornerShape(7.dp))
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(titulo, style = MaterialTheme.typography.labelSmall, color = TextoFraco)
        Text(valor, style = MaterialTheme.typography.titleSmall, color = Acento)
    }
}

@Composable
private fun Atributo(titulo: String, valor: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(titulo, style = MaterialTheme.typography.labelSmall, color = TextoFraco)
        Text(valor.toString(), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun DetalheTexto(titulo: String, texto: String) {
    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Text(titulo, style = MaterialTheme.typography.labelSmall, color = Acento)
        Box(
            Modifier
                .fillMaxWidth()
                .background(Fundo, RoundedCornerShape(7.dp))
                .border(1.dp, Borda, RoundedCornerShape(7.dp))
                .padding(11.dp),
        ) {
            Text(
                limparTexto(texto),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = MaterialTheme.typography.bodySmall.lineHeight,
            )
        }
    }
}

@Composable
private fun MonstroPersonalizadoCard(
    monstro: Monstro,
    salvar: (Monstro) -> Unit,
    excluir: () -> Unit,
) {
    var expandido by remember(monstro.id) { mutableStateOf(true) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = SuperficieAlta),
        border = androidx.compose.foundation.BorderStroke(1.dp, Borda),
    ) {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(11.dp),
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        monstro.nome.ifBlank { "Criatura sem nome" },
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        "Personalizada • ${monstro.tipo}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextoFraco,
                    )
                }
                IconButton(onClick = { expandido = !expandido }) {
                    Icon(
                        if (expandido) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = Acento,
                    )
                }
                IconButton(onClick = excluir) {
                    Icon(Icons.Default.Delete, contentDescription = "Excluir criatura", tint = Perigo)
                }
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Estatistica("VD", monstro.vd, Modifier.weight(1f))
                Estatistica("PV", monstro.pv.toString(), Modifier.weight(1f))
                Estatistica("DEF", monstro.defesa.toString(), Modifier.weight(1f))
                Estatistica("MOV", "${monstro.deslocamento}m", Modifier.weight(1f))
            }

            if (expandido) {
                Campo("Nome") { CampoTexto(monstro.nome) { salvar(monstro.copy(nome = it)) } }

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Campo("VD", Modifier.weight(1f)) {
                        CampoTexto(monstro.vd) { salvar(monstro.copy(vd = it)) }
                    }
                    Campo("Tipo", Modifier.weight(1.5f)) {
                        CampoTexto(monstro.tipo) { salvar(monstro.copy(tipo = it)) }
                    }
                }

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Campo("PV", Modifier.weight(1f)) { Numero(monstro.pv) { salvar(monstro.copy(pv = it)) } }
                    Campo("Defesa", Modifier.weight(1f)) { Numero(monstro.defesa) { salvar(monstro.copy(defesa = it)) } }
                    Campo("Iniciativa", Modifier.weight(1f)) { Numero(monstro.iniciativa) { salvar(monstro.copy(iniciativa = it)) } }
                    Campo("Mov.", Modifier.weight(1f)) { Numero(monstro.deslocamento) { salvar(monstro.copy(deslocamento = it)) } }
                }

                Selecao(
                    valor = monstro.elemento,
                    opcoes = listOf("Sangue", "Morte", "Conhecimento", "Energia", "Medo"),
                    aoMudar = { salvar(monstro.copy(elemento = it)) },
                    placeholder = "Elemento",
                )

                Campo("Ataques") {
                    AreaTexto(monstro.ataques, { salvar(monstro.copy(ataques = it)) }, linhas = 4)
                }
                Campo("Habilidades") {
                    AreaTexto(monstro.habilidades, { salvar(monstro.copy(habilidades = it)) }, linhas = 4)
                }
                Campo("Descrição") {
                    AreaTexto(monstro.descricao, { salvar(monstro.copy(descricao = it)) }, linhas = 4)
                }
                Campo("Anotações") {
                    AreaTexto(monstro.notas, { salvar(monstro.copy(notas = it)) }, linhas = 3)
                }
            }
        }
    }
}
