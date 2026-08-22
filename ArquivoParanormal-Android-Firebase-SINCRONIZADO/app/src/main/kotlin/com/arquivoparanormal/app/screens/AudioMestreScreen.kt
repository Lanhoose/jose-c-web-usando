package com.arquivoparanormal.app.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arquivoparanormal.app.data.AudioTrack
import com.arquivoparanormal.app.data.Repositorio
import com.arquivoparanormal.app.ui.*

@Composable
fun AudioMestreScreen(repo: Repositorio) {
    val estado = repo.audio.value
    val context = androidx.compose.ui.platform.LocalContext.current
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? -> uri?.let { repo.importarAudio(context, it) } }
    val padroes = estado.tracks.filter { it.sourceKey != null }
    val personalizados = estado.tracks.filter { it.sourceKey == null }
    LazyColumn(Modifier.fillMaxSize().padding(horizontal = 14.dp), verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(vertical = 14.dp)) {
        item {
            Painel(titulo = "🔊 ÁUDIO DA MESA") {
                Text("O Mestre controla o ambiente sonoro; todos os jogadores recebem a mesma reprodução em tempo real.", color = TextoFraco)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { if (estado.tocando) repo.pausarAudio() else estado.ativoId?.let(repo::reproduzirAudio) }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Primaria)) {
                        Icon(if (estado.tocando) Icons.Default.Pause else Icons.Default.PlayArrow, null); Text(if (estado.tocando) "  Pausar" else "  Continuar")
                    }
                    OutlinedButton(onClick = repo::pararAudio, modifier = Modifier.weight(1f)) { Icon(Icons.Default.Stop, null); Text("  Parar") }
                }
                Text("Volume", color = TextoClaro)
                Slider(value = estado.volume, onValueChange = { repo.ajustarAudio(volume = it) })
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Switch(checked = estado.loop, onCheckedChange = { repo.ajustarAudio(loop = it) })
                    Text("Repetir ambiente", color = TextoClaro)
                }
            }
        }
        item { Text("AMBIENTES", color = TextoFraco, style = MaterialTheme.typography.labelLarge) }
        items(padroes) { AudioCard(it, estado.ativoId == it.id && estado.tocando, { repo.reproduzirAudio(it.id) }) }
        item {
            Painel(titulo = "Meus áudios", acao = { IconButton(onClick = { picker.launch(arrayOf("audio/mpeg", "audio/ogg", "audio/wav", "audio/*")) }) { Icon(Icons.Default.Add, "Adicionar áudio", tint = Primaria) } }) {
                Text("MP3, OGG, WAV, M4A e AAC. O arquivo fica salvo somente neste aparelho. O Firebase sincroniza o nome e os comandos da mesa, sem enviar a música para a nuvem.", color = TextoFraco)
        repo.audioUploadStatus?.let { Text(it, color = if (it.startsWith("Falha")) MaterialTheme.colorScheme.error else Primaria, style = MaterialTheme.typography.bodySmall) }
                if (personalizados.isEmpty()) Text("Nenhum áudio personalizado.", color = TextoFraco)
                personalizados.forEach { AudioCard(it, estado.ativoId == it.id && estado.tocando, { repo.reproduzirAudio(it.id) }) }
            }
        }
    }
}

@Composable
private fun AudioCard(track: AudioTrack, ativo: Boolean, tocar: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = Superficie), modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(track.nome, color = TextoClaro, style = MaterialTheme.typography.titleMedium)
                Text(track.categoria, color = TextoFraco, style = MaterialTheme.typography.bodySmall)
            }
            FilledTonalButton(onClick = tocar) { Icon(Icons.Default.PlayArrow, null); Text(if (ativo) " Tocando" else " Ouvir") }
        }
    }
}
