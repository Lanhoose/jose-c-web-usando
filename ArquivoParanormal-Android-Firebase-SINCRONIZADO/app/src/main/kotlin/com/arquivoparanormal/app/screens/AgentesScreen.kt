package com.arquivoparanormal.app.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.arquivoparanormal.app.data.Personagem
import com.arquivoparanormal.app.data.Repositorio
import com.arquivoparanormal.app.ui.Borda
import com.arquivoparanormal.app.ui.IconeCircular
import com.arquivoparanormal.app.ui.Painel
import com.arquivoparanormal.app.ui.Perigo
import com.arquivoparanormal.app.ui.Primaria
import com.arquivoparanormal.app.ui.RotuloOP
import com.arquivoparanormal.app.ui.Superficie
import com.arquivoparanormal.app.ui.TextoClaro
import com.arquivoparanormal.app.ui.TextoFraco

@Composable
fun AgentesScreen(
    repo: Repositorio,
    aoAbrirFicha: (String) -> Unit,
    aoAbrirBestiario: () -> Unit,
    aoCriarAgente: () -> Unit,
    mostrarBestiario: Boolean,
) {
    val lista = repo.personagens.sortedByDescending { it.atualizadoEm }

    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 14.dp),
    ) {
        item {
            Card(
                shape = RoundedCornerShape(4.dp),
                colors = CardDefaults.cardColors(containerColor = Superficie),
                border = BorderStroke(1.dp, Borda),
            ) {
                Column(Modifier.padding(18.dp)) {
                    RotuloOP("Ordem Paranormal · Sobrevivendo ao Horror")
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "O medo é real. Sua ficha também precisa ser.",
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextoClaro,
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "Atributos, perícias, origens e classes do livro base já integrados. " +
                            "Controle PV, PE e Sanidade, marque estados, escolha sua afinidade " +
                            "com o Outro Lado e administre cada espaço do inventário.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextoFraco,
                    )
                    Spacer(Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = aoCriarAgente,
                            colors = ButtonDefaults.buttonColors(containerColor = Primaria),
                            shape = RoundedCornerShape(3.dp),
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(Modifier.height(0.dp))
                            Text("  Novo agente")
                        }
                        if (mostrarBestiario) {
                            OutlinedButton(
                                onClick = aoAbrirBestiario,
                                shape = RoundedCornerShape(3.dp),
                                border = BorderStroke(1.dp, Borda),
                            ) {
                                Text("Bestiário", color = TextoClaro)
                            }
                        }
                    }
                }
            }
        }

        item {
            Painel(titulo = "Agentes registrados") {
                if (lista.isEmpty()) {
                    Text(
                        "Nenhum agente no arquivo. Crie o primeiro antes que o Outro Lado crie por você.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextoFraco,
                    )
                }
            }
        }

        items(lista, key = { it.id }) { p ->
            CartaoAgente(p, { aoAbrirFicha(p.id) }, { repo.removerPersonagem(p.id) }, repo)
        }
    }
}

@Composable
private fun CartaoAgente(p: Personagem, aoAbrir: () -> Unit, aoRemover: () -> Unit, repo: Repositorio) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = aoAbrir),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = Superficie),
        border = BorderStroke(1.dp, Borda),
    ) {
        Row(
            Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            val fotoLocalExiste = !p.fotoArquivo.isNullOrBlank() && java.io.File(p.fotoArquivo!!).exists()
            when {
                fotoLocalExiste -> com.arquivoparanormal.app.ui.Retrato(p.fotoArquivo!!, tamanho = 54.dp)
                !p.fotoAgenteThumb.isNullOrBlank() -> com.arquivoparanormal.app.ui.RetratoDataUrl(p.fotoAgenteThumb, tamanho = 54.dp)
                !p.fotoJogadorThumb.isNullOrBlank() -> com.arquivoparanormal.app.ui.RetratoDataUrl(p.fotoJogadorThumb, tamanho = 54.dp)
                else -> IconeCircular { Icon(Icons.Default.Person, contentDescription = null, tint = TextoFraco) }
            }
            Column(Modifier.weight(1f)) {
                Text(
                    p.nome,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextoClaro,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    buildString {
                        append(p.classe)
                        if (p.trilha.isNotBlank()) append(" · ${p.trilha}")
                        append(" · NEX ${p.nex}%")
                        if (p.origem.isNotBlank()) append(" · ${p.origem}")
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = TextoFraco,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (repo.ehMestre && p.ownerUid.isNotBlank()) {
                    OutlinedButton(
                        onClick = { repo.solicitarFotoDoJogador(p.id) },
                        modifier = Modifier.padding(top = 4.dp),
                        enabled = p.fotoSolicitadaEm <= 0L,
                    ) {
                        Text(
                            if (p.fotoSolicitadaEm > 0L) "Solicitação enviada" else "Carregar foto do jogador",
                            color = TextoClaro,
                        )
                    }
                    if (p.fotoSolicitadaEm > 0L) {
                        Text("Aguardando o aparelho do jogador enviar a foto do perfil…", color = TextoFraco, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
            IconButton(onClick = aoRemover) {
                Icon(Icons.Default.Delete, contentDescription = "Excluir ${p.nome}", tint = Perigo)
            }
        }
    }
}
