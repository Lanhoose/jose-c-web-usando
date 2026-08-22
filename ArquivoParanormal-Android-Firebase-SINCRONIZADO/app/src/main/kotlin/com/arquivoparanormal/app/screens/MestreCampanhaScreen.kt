package com.arquivoparanormal.app.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.arquivoparanormal.app.data.Campanha
import com.arquivoparanormal.app.data.NpcCampanha
import com.arquivoparanormal.app.data.Repositorio
import com.arquivoparanormal.app.data.SessaoCampanha
import com.arquivoparanormal.app.ui.Acento
import com.arquivoparanormal.app.ui.AreaTexto
import com.arquivoparanormal.app.ui.Campo
import com.arquivoparanormal.app.ui.Painel
import com.arquivoparanormal.app.ui.Perigo
import com.arquivoparanormal.app.ui.Primaria
import com.arquivoparanormal.app.ui.TextoClaro
import com.arquivoparanormal.app.ui.TextoFraco
import com.arquivoparanormal.app.ui.Superficie

@Composable
fun MestreCampanhaScreen(
    repo: Repositorio,
    abrirAba: (String) -> Unit,
) {
    val campanha = repo.campanha.value
    val context = LocalContext.current
    var mostrarSegredo by remember { mutableStateOf(false) }
    // BUG CORRIGIDO: exportarCampanha()/importarCampanha() sempre devolveram
    // um Result<Unit>, mas os launchers abaixo jogavam esse resultado fora.
    // Se o JSON escolhido na importação estivesse corrompido, fosse de outro
    // app ou não pudesse ser lido (arquivo.exists()==false, formato inválido
    // etc.), a falha era engolida em silêncio: nada acontecia na tela e o
    // Mestre não tinha como saber que a campanha não foi restaurada. O mesmo
    // valia para uma exportação que falhasse (ex.: destino sem espaço).
    var mensagemBackup by remember { mutableStateOf<Pair<Boolean, String>?>(null) }

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        if (uri != null) {
            repo.exportarCampanha(context, uri).fold(
                onSuccess = { mensagemBackup = true to "Backup exportado com sucesso." },
                onFailure = { erro -> mensagemBackup = false to "Falha ao exportar: ${erro.message ?: "erro desconhecido"}." },
            )
        }
    }
    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            repo.importarCampanha(context, uri).fold(
                onSuccess = { mensagemBackup = true to "Campanha importada com sucesso." },
                onFailure = { erro -> mensagemBackup = false to "Falha ao importar: ${erro.message ?: "arquivo inválido"}." },
            )
        }
    }

    LazyColumn(
        Modifier.fillMaxSize().padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 14.dp),
    ) {
        item {
            Painel(titulo = "🎭 MODO MESTRE") {
                Text("Painel de controle da campanha", style = MaterialTheme.typography.headlineSmall, color = TextoClaro)
                Text("Tudo que o Mestre precisa para conduzir a sessão em um só lugar.", color = TextoFraco)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { abrirAba("Batalha") }, colors = ButtonDefaults.buttonColors(containerColor = Primaria), modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.Shield, null); Text("  Batalha")
                    }
                    OutlinedButton(onClick = { abrirAba("Bestiário") }, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.Group, null); Text("  Ameaças")
                    }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { abrirAba("Compêndio") }, modifier = Modifier.weight(1f)) {
                        Text("Compêndio")
                    }
                    OutlinedButton(onClick = { abrirAba("Batalha") }, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.Map, null); Text("  Mapa (Batalha)")
                    }
                }
            }
        }

        item {
            Painel(titulo = "Sessão atual") {
                Campo("Nome da campanha") {
                    TextField(
                        value = campanha.nome,
                        onValueChange = { repo.atualizarCampanha(campanha.copy(nome = it)) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(),
                    )
                }
                Campo("Diário / notas da sessão") {
                    AreaTexto(campanha.notas, { repo.atualizarCampanha(campanha.copy(notas = it)) }, linhas = 6)
                }
                Text("Objetivos: ${campanha.objetivos.size} · Pistas: ${campanha.pistas.size} · Locais: ${campanha.locais.size}", color = Acento)
                Campo("Objetivos") {
                    AreaTexto(campanha.objetivos.joinToString("\n"), {
                        repo.atualizarCampanha(campanha.copy(objetivos = it.lines().map(String::trim).filter(String::isNotBlank)))
                    }, linhas = 3)
                }
                Campo("Pistas") {
                    AreaTexto(campanha.pistas.joinToString("\n"), {
                        repo.atualizarCampanha(campanha.copy(pistas = it.lines().map(String::trim).filter(String::isNotBlank)))
                    }, linhas = 3)
                }
                Campo("Locais") {
                    AreaTexto(campanha.locais.joinToString("\n"), {
                        repo.atualizarCampanha(campanha.copy(locais = it.lines().map(String::trim).filter(String::isNotBlank)))
                    }, linhas = 3)
                }
            }
        }

        item {
            Painel(
                titulo = "NPCs",
                acao = {
                    IconButton(onClick = { repo.adicionarNpc() }) {
                        Icon(Icons.Default.Add, "Novo NPC", tint = Primaria)
                    }
                },
            ) {
                campanha.npcs.forEach { npc ->
                    NpcEditor(npc, repo, mostrarSegredo)
                }
                if (campanha.npcs.isEmpty()) Text("Nenhum NPC cadastrado.", color = TextoFraco)
                Button(onClick = { mostrarSegredo = !mostrarSegredo }, colors = ButtonDefaults.buttonColors(containerColor = Superficie)) {
                    Text(if (mostrarSegredo) "Ocultar segredos" else "Mostrar segredos", color = TextoClaro)
                }
            }
        }

        item {
            Painel(
                titulo = "Sessões da campanha",
                acao = {
                    IconButton(onClick = { repo.adicionarSessao() }) {
                        Icon(Icons.Default.Add, "Nova sessão", tint = Primaria)
                    }
                },
            ) {
                campanha.sessoes.sortedBy { it.numero }.forEach { sessao ->
                    SessaoEditor(sessao, repo)
                }
                if (campanha.sessoes.isEmpty()) Text("Nenhuma sessão registrada.", color = TextoFraco)
            }
        }

        item {
            Painel(titulo = "Backup / exportação") {
                Text("Exporta agentes, NPCs, ameaças customizadas, batalha, mapa, anotações e sessões em um único JSON.", color = TextoFraco)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { exportLauncher.launch("campanha_arquivo_paranormal.json") }, colors = ButtonDefaults.buttonColors(containerColor = Primaria), modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.Download, null); Text("  Exportar")
                    }
                    OutlinedButton(onClick = { importLauncher.launch(arrayOf("application/json", "text/json")) }, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.FileUpload, null); Text("  Importar")
                    }
                }
                mensagemBackup?.let { (sucesso, texto) ->
                    Text(texto, color = if (sucesso) Acento else Perigo)
                }
            }
        }
    }
}

@Composable
private fun NpcEditor(npc: NpcCampanha, repo: Repositorio, mostrarSegredo: Boolean) {
    val save = { novo: NpcCampanha -> repo.salvarNpc(novo) }
    Painel(titulo = npc.nome.ifBlank { "Novo NPC" }, acao = {
        IconButton(onClick = { repo.removerNpc(npc.id) }) {
            Icon(Icons.Default.Delete, "Remover NPC", tint = Perigo)
        }
    }) {
        Campo("Nome") { TextField(npc.nome, { save(npc.copy(nome = it)) }, Modifier.fillMaxWidth(), colors = TextFieldDefaults.colors()) }
        Campo("Descrição") { AreaTexto(npc.descricao, { save(npc.copy(descricao = it)) }, linhas = 3) }
        Campo("Local") { TextField(npc.local, { save(npc.copy(local = it)) }, Modifier.fillMaxWidth(), colors = TextFieldDefaults.colors()) }
        if (mostrarSegredo) Campo("Segredo") { AreaTexto(npc.segredo, { save(npc.copy(segredo = it)) }, linhas = 3) }
    }
}

@Composable
private fun SessaoEditor(sessao: SessaoCampanha, repo: Repositorio) {
    val save = { novo: SessaoCampanha -> repo.salvarSessao(novo) }
    Painel(titulo = "Sessão ${sessao.numero}: ${sessao.titulo}") {
        Campo("Título") { TextField(sessao.titulo, { save(sessao.copy(titulo = it)) }, Modifier.fillMaxWidth(), colors = TextFieldDefaults.colors()) }
        Campo("Resumo") { AreaTexto(sessao.resumo, { save(sessao.copy(resumo = it)) }, linhas = 4) }
        IconButton(onClick = { repo.removerSessao(sessao.id) }) {
            Icon(Icons.Default.Delete, "Remover sessão", tint = Perigo)
        }
    }
}
