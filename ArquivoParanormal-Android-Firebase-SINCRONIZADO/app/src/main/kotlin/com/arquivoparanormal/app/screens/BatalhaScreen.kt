package com.arquivoparanormal.app.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arquivoparanormal.app.data.Combatente
import com.arquivoparanormal.app.data.Repositorio
import com.arquivoparanormal.app.ui.Campo
import com.arquivoparanormal.app.ui.Chip
import com.arquivoparanormal.app.ui.Numero
import com.arquivoparanormal.app.ui.Painel
import com.arquivoparanormal.app.ui.Perigo
import com.arquivoparanormal.app.ui.Primaria
import com.arquivoparanormal.app.ui.TextoClaro
import com.arquivoparanormal.app.ui.TextoFraco
import com.arquivoparanormal.app.ui.Texto as CampoTexto

private enum class SubabaBatalha { INICIATIVA, MESA }

@Composable
fun BatalhaScreen(repo: Repositorio) {
    var subaba by remember { mutableStateOf(SubabaBatalha.MESA) }

    androidx.compose.foundation.layout.Column(Modifier.fillMaxSize()) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Chip("Mesa tática", subaba == SubabaBatalha.MESA, { subaba = SubabaBatalha.MESA })
            Chip("Iniciativa", subaba == SubabaBatalha.INICIATIVA, { subaba = SubabaBatalha.INICIATIVA })
        }

        androidx.compose.foundation.layout.Box(Modifier.weight(1f)) {
            when (subaba) {
                SubabaBatalha.MESA -> MesaTaticaScreen(repo)
                SubabaBatalha.INICIATIVA -> IniciativaScreen(repo)
            }
        }
    }
}

@Composable
private fun IniciativaScreen(repo: Repositorio) {
    val mestre = repo.ehMestre
    val ordem = repo.combatentes.sortedByDescending { it.iniciativa }

    LazyColumn(
        Modifier.fillMaxSize().padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 14.dp),
    ) {
        item {
            Painel(
                titulo = if (mestre) "Rastreador de iniciativa" else "Rastreador de iniciativa — somente leitura",
                acao = if (mestre) ({
                    IconButton(onClick = { repo.adicionarCombatente() }) {
                        Icon(Icons.Default.Add, contentDescription = "Adicionar", tint = Primaria)
                    }
                }) else null,
            ) {
                Text(
                    "Rodada ${repo.rodada}",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextoClaro,
                )
                if (mestre) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Chip("Próxima rodada", false, { repo.proximaRodada() }, Primaria)
                        Chip("Importar agentes", false, { repo.importarAgentes() }, Primaria)
                        Chip("Limpar", false, { repo.limparBatalha() }, Perigo)
                    }
                } else {
                    Text("As alterações feitas pelo Mestre aparecem aqui em tempo real.", color = TextoFraco, style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        items(count = ordem.size, key = { ordem[it].id }) { idx ->
            val c = ordem[idx]
            val set: (Combatente) -> Unit = { repo.salvarCombatente(it) }
            Painel(
                titulo = "${idx + 1}. ${c.nome.ifBlank { "Combatente" }}",
                acao = if (mestre) ({
                    IconButton(onClick = { repo.removerCombatente(c.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Remover", tint = Perigo)
                    }
                }) else null,
            ) {
                if (mestre) {
                    Campo("Nome") { CampoTexto(c.nome) { set(c.copy(nome = it)) } }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Campo("Iniciativa", Modifier.weight(1f)) { Numero(c.iniciativa) { set(c.copy(iniciativa = it)) } }
                        Campo("PV", Modifier.weight(1f)) { Numero(c.pv) { set(c.copy(pv = it)) } }
                        Campo("PV máx.", Modifier.weight(1f)) { Numero(c.pvMax) { set(c.copy(pvMax = it)) } }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Chip("Aliado", c.aliado, { set(c.copy(aliado = true)) })
                        Chip("Inimigo", !c.aliado, { set(c.copy(aliado = false)) }, Perigo)
                    }
                } else {
                    Text("Iniciativa: ${c.iniciativa}", color = TextoClaro)
                    Text("PV: ${c.pv}/${c.pvMax}", color = TextoClaro)
                    Text(if (c.aliado) "Aliado" else "Inimigo", color = if (c.aliado) TextoClaro else Perigo)
                }
            }
        }

        if (ordem.isEmpty()) {
            item {
                Text("Nenhum combatente na cena.", color = TextoFraco,
                    style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
