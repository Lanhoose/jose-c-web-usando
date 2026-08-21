package com.arquivoparanormal.app.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.arquivoparanormal.app.data.Autenticacao
import com.arquivoparanormal.app.data.ConfiguracoesApp
import com.arquivoparanormal.app.data.Repositorio
import com.arquivoparanormal.app.ui.Painel
import com.arquivoparanormal.app.ui.TextoClaro
import com.arquivoparanormal.app.ui.TextoFraco
import java.text.DateFormat
import java.util.Date

@Composable
fun ConfiguracoesScreen(
    auth: Autenticacao,
    repo: Repositorio,
    config: ConfiguracoesApp,
    aoSair: () -> Unit,
) {
    // Incrementado após salvar nome/foto com sucesso, para forçar a releitura
    // de auth.usuarioAtualUser/auth.fotoLocalUri: o FirebaseUser não é um
    // estado observável do Compose, então sem isso a tela não recompunha ao
    // concluir a alteração.
    var versaoPerfil by remember { mutableStateOf(0) }
    val user = remember(versaoPerfil) { auth.usuarioAtualUser }
    val fotoLocal = remember(versaoPerfil, user?.uid) { auth.fotoLocalUri }
    var nome by remember(user?.uid) { mutableStateOf(user?.displayName.orEmpty()) }
    var senhaDialog by remember { mutableStateOf(false) }
    var statusFoto by remember { mutableStateOf<String?>(null) }
    var enviandoFoto by remember { mutableStateOf(false) }
    var statusNome by remember { mutableStateOf<String?>(null) }
    val fotoPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        if (uri != null) {
            enviandoFoto = true
            statusFoto = null
            auth.atualizarFoto(uri) { erro ->
                enviandoFoto = false
                statusFoto = erro
                if (erro == null) versaoPerfil++
            }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 14.dp),
    ) {
        item {
            Painel(titulo = "👤 CONTA") {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (fotoLocal != null) {
                                AsyncImage(
                                    model = fotoLocal,
                                    contentDescription = "Foto de perfil",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.size(52.dp).clip(CircleShape),
                                )
                            } else {
                                Icon(Icons.Default.AccountCircle, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(52.dp))
                            }
                            Spacer(Modifier.size(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text("Minha conta", color = TextoClaro, style = MaterialTheme.typography.titleMedium)
                                Text(user?.email.orEmpty(), color = TextoFraco)
                                if (enviandoFoto) Text("Enviando foto…", color = TextoFraco)
                                if (statusFoto != null) Text(statusFoto!!, color = Color(0xFFEF5350))
                            }
                            IconButton(onClick = { fotoPicker.launch(arrayOf("image/*")) }, enabled = !enviandoFoto) {
                                Icon(Icons.Default.Image, "Alterar foto", tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                        OutlinedTextField(value = nome, onValueChange = { nome = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Nome de usuário") }, singleLine = true)
                        Button(
                            onClick = {
                                statusNome = null
                                auth.atualizarNome(nome.trim()) { erro ->
                                    statusNome = erro
                                    if (erro == null) versaoPerfil++
                                }
                            },
                            enabled = nome.trim().isNotBlank(),
                        ) { Text("Salvar nome") }
                        if (statusNome != null) Text(statusNome!!, color = Color(0xFFEF5350))
                        Text("E-mail", color = TextoFraco)
                        Text(user?.email.orEmpty(), color = TextoClaro)
                        OutlinedButton(onClick = { senhaDialog = true }, modifier = Modifier.fillMaxWidth()) { Icon(Icons.Default.Key, null); Text("  Alterar senha") }
                        OutlinedButton(onClick = { auth.sair(); repo.pararSincronizacao(); aoSair() }, modifier = Modifier.fillMaxWidth()) { Icon(Icons.Default.Logout, null); Text("  Sair da conta") }
                    }
                }
            }
        }
        item {
            Painel(titulo = "☁️ CONTA FIREBASE") {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(if (repo.sincronizacaoAtiva) Icons.Default.CloudDone else Icons.Default.CloudSync, null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.size(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Status da sincronização", color = TextoClaro)
                        Text(if (repo.sincronizacaoAtiva) "Conectado ao Firebase" else "Desconectado", color = TextoFraco)
                    }
                }
                Text("Última sincronização: ${repo.ultimaSincronizacao?.let { DateFormat.getDateTimeInstance().format(Date(it)) } ?: "Ainda não registrada"}", color = TextoFraco)
                Button(onClick = { repo.iniciarSincronizacao() }, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.CloudSync, null); Text("  Sincronizar agora")
                }
            }
        }
        item {
            Painel(titulo = "🎨 APARÊNCIA") {
                Text("Tema", color = TextoClaro)
                listOf("escuro" to "🌑 Escuro", "claro" to "☀️ Claro", "sistema" to "🌓 Seguir sistema").forEach { (key, label) ->
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = config.tema == key, onClick = { config.atualizarTema(key) })
                        Text(label, color = TextoClaro)
                    }
                }
                Text("Cor principal", color = TextoClaro)
                listOf("vermelho" to "🔴 Vermelho", "roxo" to "🟣 Roxo", "azul" to "🔵 Azul", "verde" to "🟢 Verde", "laranja" to "🟠 Laranja").forEach { (key, label) ->
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = config.corPrincipal == key, onClick = { config.atualizarCorPrincipal(key) })
                        Text(label, color = TextoClaro)
                    }
                }
                Divider()
                Text("Interface", color = TextoClaro)
                Text("Tamanho dos textos: ${"%.0f".format(config.escalaTexto * 100)}%", color = TextoFraco)
                Slider(value = config.escalaTexto, onValueChange = config::atualizarEscalaTexto, valueRange = 0.85f..1.25f)
                Text("Tamanho dos ícones: ${"%.0f".format(config.escalaIcones * 100)}%", color = TextoFraco)
                Slider(value = config.escalaIcones, onValueChange = config::atualizarEscalaIcones, valueRange = 0.85f..1.25f)
                ToggleRow("Animações", config.animacoes, config::atualizarAnimacoes)
                ToggleRow("Efeitos visuais", config.efeitos, config::atualizarEfeitos)
                ToggleRow("Contraste aumentado", config.altoContraste, config::atualizarAltoContraste)
            }
        }
    }

    if (senhaDialog) {
        AlertDialog(
            onDismissRequest = { senhaDialog = false },
            title = { Text("Alterar senha") },
            text = { Text("Enviaremos um e-mail para ${user?.email.orEmpty()} com o link seguro para definir uma nova senha.") },
            confirmButton = { TextButton(onClick = { auth.enviarRedefinicaoSenha { senhaDialog = false } }) { Text("Enviar") } },
            dismissButton = { TextButton(onClick = { senhaDialog = false }) { Text("Cancelar") } },
        )
    }
}

@Composable
private fun ToggleRow(titulo: String, valor: Boolean, aoMudar: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(titulo, modifier = Modifier.weight(1f), color = TextoClaro)
        Switch(checked = valor, onCheckedChange = aoMudar)
    }
}
