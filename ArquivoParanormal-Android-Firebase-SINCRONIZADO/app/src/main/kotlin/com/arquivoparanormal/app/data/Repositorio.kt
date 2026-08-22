package com.arquivoparanormal.app.data

import com.arquivoparanormal.app.BuildConfig
import android.content.Context
import android.net.Uri
import java.io.InputStream
import java.io.File
import android.util.Base64
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.contentOrNull
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.random.Random

private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

private const val MINIATURA_LADO_MAXIMO = 128

/**
 * Gera a mesma miniatura Base64 usada no perfil do jogador. Mantida aqui
 * também para que o Repositorio possa responder às solicitações de foto
 * recebidas do Mestre sem depender de uma função privada de Autenticacao.kt.
 */
private fun gerarMiniaturaBase64(caminhoArquivo: String): String? {
    val original = BitmapFactory.decodeFile(caminhoArquivo) ?: return null
    val maior = maxOf(original.width, original.height)
    if (maior <= 0) {
        original.recycle()
        return null
    }
    val escala = MINIATURA_LADO_MAXIMO.toFloat() / maior
    val miniatura = if (escala < 1f) {
        Bitmap.createScaledBitmap(
            original,
            (original.width * escala).toInt().coerceAtLeast(1),
            (original.height * escala).toInt().coerceAtLeast(1),
            true,
        )
    } else {
        original
    }
    val saida = ByteArrayOutputStream()
    val ok = miniatura.compress(Bitmap.CompressFormat.JPEG, 60, saida)
    if (miniatura !== original) miniatura.recycle()
    original.recycle()
    if (!ok) return null
    return "data:image/jpeg;base64," + Base64.encodeToString(saida.toByteArray(), Base64.NO_WRAP)
}


@Serializable
private data class TokenMesaWebPayload(
    val id: String,
    val nome: String,
    val x: Int,
    val y: Int,
    val cor: String,
    val tipo: String,
    val pvAtual: Int,
    val pvMax: Int,
    val imagem: String? = null,
)

@Serializable
private data class MesaWebPayload(
    val cols: Int,
    val rows: Int,
    val tokens: List<TokenMesaWebPayload>,
    val ocultos: List<String>,
    val ambiente: String,
    val intensidade: Int,
    val rodada: Int,
    val mapa: String? = null,
    val mapaAjuste: String = "conter",
)

/**
 * Repositório local. As alterações de UI continuam síncronas na memória,
 * mas a serialização JSON e a gravação no SharedPreferences acontecem fora
 * da thread principal e são agrupadas por alguns milissegundos. Isso evita
 * travamentos ao digitar rapidamente em campos da ficha.
 */
class Repositorio(context: Context) {

    private val appContext = context.applicationContext
    private val prefs = context.getSharedPreferences("arquivo_paranormal", Context.MODE_PRIVATE)
    private val persistExecutor: ScheduledExecutorService =
        Executors.newSingleThreadScheduledExecutor { runnable ->
            Thread(runnable, "ArquivoParanormal-Persistencia").apply { isDaemon = true }
        }

    private var personagensSave: ScheduledFuture<*>? = null
    private var monstrosSave: ScheduledFuture<*>? = null
    private var batalhaSave: ScheduledFuture<*>? = null
    private var mesaSave: ScheduledFuture<*>? = null
    private var mesaCloudSave: ScheduledFuture<*>? = null
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private var personagensListener: ListenerRegistration? = null
    private var monstrosListener: ListenerRegistration? = null
    private var batalhaListener: ListenerRegistration? = null
    private var mesaListener: ListenerRegistration? = null
    private var campanhaListener: ListenerRegistration? = null
    private var audioListener: ListenerRegistration? = null
    private var audioTracksListener: ListenerRegistration? = null
    private val audioUploadStatusState = mutableStateOf<String?>(null)
    private var mestreAtual = false
    private val mestreState = mutableStateOf(false)
    private val sincronizacaoAtivaState = mutableStateOf(false)
    private val ultimaSincronizacaoState = mutableStateOf<Long?>(null)
    // Cache para não re-ler e re-encodar um mapa grande a cada movimento de token.
    private var mapaCachePath: String? = null
    private var mapaCacheDataUrl: String? = null
    private var mapaRemotoDataUrl: String? = null
    private val personagensPendentes = mutableMapOf<String, Personagem>()
    private var ultimaMesaLocalAtualizacao = 0L
    private var ultimaBatalhaLocalAtualizacao = 0L
    private var ultimaCampanhaLocalAtualizacao = 0L
    private val personagensExclusaoPendentes = mutableSetOf<String>()

    val ehMestre: Boolean get() = mestreState.value
    val sincronizacaoAtiva: Boolean get() = sincronizacaoAtivaState.value
    val ultimaSincronizacao: Long? get() = ultimaSincronizacaoState.value

    val personagens: SnapshotStateList<Personagem> = mutableStateListOf()
    val monstros: SnapshotStateList<Monstro> = mutableStateListOf()
    var batalha = mutableStateOf(Batalha())
        private set
    var mesa = mutableStateOf(MesaBatalha())
        private set
    var campanha = mutableStateOf(Campanha())
        private set
    var audio = mutableStateOf(AudioMesaState(tracks = audiosPadrao()))
        private set
    val audioUploadStatus: String? get() = audioUploadStatusState.value

    fun iniciarSincronizacao() {
        personagensListener?.remove()
        batalhaListener?.remove()
        mesaListener?.remove()
        val user = auth.currentUser ?: return
        sincronizacaoAtivaState.value = false
        db.collection("usuarios").document(user.uid).get()
            .addOnFailureListener {
                // BUG CORRIGIDO: antes só havia addOnSuccessListener aqui. Se essa
                // leitura falhasse (ex.: app abrindo sem internet no primeiro
                // login, uma falha passageira de rede, ou o documento do perfil
                // ainda não ter propagado logo após criarConta()), a sincronização
                // inteira travava para sempre: nenhum listener do Firestore era
                // criado, sincronizacaoAtiva ficava "false" e nada mais era
                // tentado automaticamente — o app parecia vazio/travado sem
                // nenhuma mensagem de erro. Agora, se a leitura do perfil falhar,
                // seguimos como "jogador" (mesmo fallback usado em
                // Autenticacao.papelAtual()) e iniciamos a sincronização mesmo
                // assim; os listeners do Firestore continuam tentando reconectar
                // sozinhos, e uma nova chamada a iniciarSincronizacao() (ex.: o
                // botão "Sincronizar agora") corrige o papel assim que a leitura
                // do perfil funcionar.
                iniciarSincronizacaoComPapel(user, "jogador")
            }
            .addOnSuccessListener { perfil ->
            val role = perfil.getString("role") ?: "jogador"
            iniciarSincronizacaoComPapel(user, role)
        }
    }

    private fun iniciarSincronizacaoComPapel(user: com.google.firebase.auth.FirebaseUser, role: String) {
        mestreAtual = role == "mestre"
        mestreState.value = mestreAtual
        sincronizacaoAtivaState.value = true
        ultimaSincronizacaoState.value = System.currentTimeMillis()
        if (mestreAtual) {
            iniciarSincronizacaoMonstros()
        } else {
            monstrosListener?.remove()
            monstrosListener = null
            monstros.clear()
        }
        iniciarSincronizacaoBatalha()
        iniciarSincronizacaoMesa()
        iniciarSincronizacaoCampanha()
        iniciarSincronizacaoAudio()
        iniciarSincronizacaoAudioTracks()
        val query = if (role == "mestre") {
            db.collection("fichas")
        } else {
            db.collection("fichas").whereEqualTo("ownerUid", user.uid)
        }
        personagensListener = query.addSnapshotListener { snapshot, error ->
            if (error != null || snapshot == null) return@addSnapshotListener
            val remotos = snapshot.documents.mapNotNull { doc ->
                val payload = doc.getString("payload") ?: return@mapNotNull null
                val decodificado = runCatching { json.decodeFromString<Personagem>(payload) }.getOrNull()
                    ?: return@mapNotNull null
                // Migração suave: fichas gravadas antes desta correção não
                // têm ownerUid dentro do payload. Se o payload não trouxer
                // o dono, herdamos do campo de nível superior do próprio
                // documento (que continua correto até a primeira gravação
                // feita pelo app antigo) para já preservá-lo daqui em diante.
                if (decodificado.ownerUid.isBlank()) {
                    val ownerUidTopo = doc.getString("ownerUid")
                    if (!ownerUidTopo.isNullOrBlank()) {
                        decodificado.copy(
                            ownerUid = ownerUidTopo,
                            ownerEmail = decodificado.ownerEmail.ifBlank { doc.getString("ownerEmail") ?: "" },
                        )
                    } else decodificado
                } else decodificado
            }
            if (!mestreAtual) {
                atenderSolicitacoesDeFoto(user, remotos)
            } else {
                // O Mestre não precisa solicitar a foto. Quando uma ficha
                // chega sem miniatura, buscamos automaticamente a miniatura
                // pública do perfil do proprietário e atualizamos somente o
                // campo da foto, preservando os demais dados da ficha.
                sincronizarFotosDosJogadores(remotos)
            }
            mesclarPersonagens(remotos)
            personagensPendentes.values.forEach { pendente ->
                val i = personagens.indexOfFirst { it.id == pendente.id }
                if (i >= 0) personagens[i] = pendente else personagens.add(pendente)
            }
            gravarPersonagensLocal(personagens.toList())
        }
    }

    /**
     * Atualiza [personagens] com os dados vindos do Firestore SEM nunca deixar
     * a lista vazia no meio do caminho. O antigo `personagens.clear()` seguido
     * de `personagens.addAll(...)` disparava DUAS mutações separadas na
     * SnapshotStateList; como o listener do Firestore roda fora de um bloco de
     * snapshot do Compose, a UI podia recompor logo após o `clear()` e antes do
     * `addAll()`, encontrando a lista vazia. Como a FichaScreen busca a ficha
     * com `repo.personagem(id)` (que é `personagens.firstOrNull { it.id == id }`),
     * esse instante vazio fazia a tela mostrar "Ficha não encontrada" — ou seja,
     * a ficha "sumia". Isso ficava muito mais visível quando o mestre entrava
     * em uma ficha, porque a consulta do mestre não tem filtro (`fichas`
     * inteira, sem `whereEqualTo`) e por isso o listener dele recebe TODA
     * gravação feita por qualquer jogador — inclusive as gravações repetidas
     * que acontecem a cada campo editado por um jogador criando a ficha,
     * disparando o recompute vazio repetidas vezes bem na hora em que o mestre
     * está olhando aquela ficha específica.
     *
     * A correção faz tudo dentro de um único snapshot mutável do Compose
     * (`Snapshot.withMutableSnapshot`), então a UI só recompõe uma vez, já com
     * o resultado final — nunca com a lista vazia — e sem descartar a
     * identidade dos itens que não mudaram.
     */
    private fun mesclarPersonagens(remotos: List<Personagem>) {
        androidx.compose.runtime.snapshots.Snapshot.withMutableSnapshot {
            val idsRemotos = remotos.map { it.id }.toSet()
            personagens.removeAll { it.id !in idsRemotos && it.id !in personagensPendentes && it.id !in personagensExclusaoPendentes }
            remotos.forEach { novo ->
                if (novo.id in personagensPendentes || novo.id in personagensExclusaoPendentes) return@forEach
                val i = personagens.indexOfFirst { it.id == novo.id }
                if (i >= 0) personagens[i] = novo else personagens.add(novo)
            }
        }
    }

    private fun iniciarSincronizacaoMonstros() {
        monstrosListener?.remove()
        if (auth.currentUser == null || !mestreAtual) {
            monstros.clear()
            monstrosListener = null
            return
        }
        val locais = monstros.toList()
        // Garante que os monstros locais só sejam "semeados" de volta no
        // Firestore uma única vez por sessão de sincronização, mesmo que o
        // listener dispare mais de um snapshot vazio antes das gravações
        // se refletirem (ex.: rede lenta). Sem essa trava, cada snapshot
        // vazio disparava uma nova rodada de gravações redundantes.
        var semeadoInicial = false
        monstrosListener = db.collection("monstros")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                val remotos = snapshot.documents.mapNotNull { document ->
                    val payload = document.getString("payload") ?: return@mapNotNull null
                    runCatching { json.decodeFromString<Monstro>(payload) }.getOrNull()?.copy(id = document.id)
                }
                if (remotos.isEmpty() && mestreAtual && locais.isNotEmpty() && !semeadoInicial) {
                    semeadoInicial = true
                    locais.forEach { salvarNuvemMonstro(it) }
                }
                if (remotos.isNotEmpty()) {
                    monstros.clear()
                    monstros.addAll(remotos.sortedByDescending { it.atualizadoEm })
                    gravarMonstros(monstros.toList())
                }
            }
    }

    private fun iniciarSincronizacaoBatalha() {
        batalhaListener?.remove()
        if (auth.currentUser == null) return
        batalhaListener = db.collection("batalhas").document("principal")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null || !snapshot.exists()) return@addSnapshotListener
                val payload = snapshot.getString("payload") ?: return@addSnapshotListener
                val remotoUpdatedAt = snapshot.getLong("battleUpdatedAt") ?: 0L
                if (remotoUpdatedAt > 0L && remotoUpdatedAt < ultimaBatalhaLocalAtualizacao) return@addSnapshotListener
                val remoto = runCatching { json.decodeFromString<Batalha>(payload) }.getOrNull() ?: return@addSnapshotListener
                val corrigido = corrigirIniciativasZeradas(remoto)
                batalha.value = corrigido
                gravarBatalhaLocal(corrigido)
                if (mestreAtual && corrigido != remoto) gravarBatalha(corrigido)
            }
    }

    /**
     * Recebe a grade tática publicada pelo site no mesmo documento
     * `batalhas/principal`. O site usa `tacticalPayload`, com tokens contendo
     * `imagem` (data URL), `cor`, `x`, `y`, `tipo`, PV etc.
     */
    private fun iniciarSincronizacaoMesa() {
        mesaListener?.remove()
        if (auth.currentUser == null) return

        mesaListener = db.collection("batalhas").document("principal")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null || !snapshot.exists()) return@addSnapshotListener
                val raw = snapshot.getString("tacticalPayload") ?: return@addSnapshotListener
                val remotoUpdatedAt = snapshot.getLong("tacticalUpdatedAt") ?: 0L
                // Um snapshot atrasado não pode desfazer um movimento local
                // recém-publicado. O Firestore continua sendo a fonte remota,
                // mas somente versões não anteriores à última gravação local
                // podem substituir a Mesa neste aparelho.
                if (remotoUpdatedAt > 0L && remotoUpdatedAt < ultimaMesaLocalAtualizacao) return@addSnapshotListener

                val remoto = runCatching {
                    val obj = json.parseToJsonElement(raw).jsonObject
                    val cols = obj["cols"]?.jsonPrimitive?.intOrNull ?: 14
                    val rows = obj["rows"]?.jsonPrimitive?.intOrNull ?: 10
                    val tokens = obj["tokens"]?.jsonArray?.mapNotNull { el ->
                        runCatching {
                            val t = el.jsonObject
                            TokenMesa(
                                id = t["id"]?.jsonPrimitive?.contentOrNull ?: novoId(),
                                nome = t["nome"]?.jsonPrimitive?.contentOrNull ?: "Token",
                                x = t["x"]?.jsonPrimitive?.intOrNull ?: 0,
                                y = t["y"]?.jsonPrimitive?.intOrNull ?: 0,
                                corNome = corTokenDoSite(t["cor"]?.jsonPrimitive?.contentOrNull),
                                tipo = t["tipo"]?.jsonPrimitive?.contentOrNull ?: "agente",
                                pvAtual = t["pvAtual"]?.jsonPrimitive?.intOrNull ?: 10,
                                pvMax = t["pvMax"]?.jsonPrimitive?.intOrNull ?: 10,
                                imagem = t["imagem"]?.jsonPrimitive?.contentOrNull,
                            )
                        }.getOrNull()
                    } ?: emptyList()

                    val mapaDataUrl = obj["mapa"]?.jsonPrimitive?.contentOrNull
                    val mapaAjuste = obj["mapaAjuste"]?.jsonPrimitive?.contentOrNull
                        ?.takeIf { it == "cobrir" || it == "conter" }
                        ?: "conter"

                    // O site guarda o mapa como data:image/... no tacticalPayload.
                    // Copiamos os bytes para um arquivo local sem redimensionar,
                    // preservando toda a imagem recebida pelo Android.
                    val caminhoMapa = if (!mapaDataUrl.isNullOrBlank()) {
                        if (mapaDataUrl != mapaRemotoDataUrl || mesa.value.mapaArquivo.isNullOrBlank()) {
                            val caminho = MapaImportador.salvarDataUrl(appContext, mapaDataUrl)
                            if (caminho != null) mapaRemotoDataUrl = mapaDataUrl
                            caminho ?: mesa.value.mapaArquivo
                        } else {
                            mesa.value.mapaArquivo
                        }
                    } else {
                        mapaRemotoDataUrl = null
                        null
                    }

                    MesaBatalha(
                        cols = cols.coerceIn(1, 40),
                        rows = rows.coerceIn(1, 40),
                        tokens = tokens,
                        ocultos = obj["ocultos"]?.jsonArray?.mapNotNull {
                            it.jsonPrimitive.contentOrNull
                        } ?: emptyList(),
                        ambiente = obj["ambiente"]?.jsonPrimitive?.contentOrNull ?: "Névoa densa",
                        intensidade = (obj["intensidade"]?.jsonPrimitive?.intOrNull ?: 55).coerceIn(0, 100),
                        rodada = (obj["rodada"]?.jsonPrimitive?.intOrNull ?: 1).coerceAtLeast(1),
                        mapaArquivo = caminhoMapa,
                        mapaNome = if (!mapaDataUrl.isNullOrBlank()) "Mapa sincronizado do site" else null,
                        mapaAjuste = mapaAjuste,
                    )
                }.getOrNull() ?: return@addSnapshotListener

                mesa.value = remoto
                gravarMesaLocal(remoto)
            }
    }

    private fun corTokenDoSite(cor: String?): String = when {
        cor.isNullOrBlank() -> "Agente"
        cor.contains("sangue", ignoreCase = true) -> "Sangue"
        cor.contains("morte", ignoreCase = true) -> "Morte"
        cor.contains("conhecimento", ignoreCase = true) -> "Conhecimento"
        cor.contains("energia", ignoreCase = true) -> "Energia"
        cor.contains("medo", ignoreCase = true) -> "Medo"
        cor.contains("accent", ignoreCase = true) || cor.contains("pe", ignoreCase = true) -> "Agente"
        else -> "Agente"
    }

    /**
     * BUG CORRIGIDO: diferente de Batalha e Mesa tática, esta sincronização não
     * comparava o horário da gravação remota com o da última gravação local
     * antes de sobrescrever `campanha.value`. Como o Mestre grava a Campanha a
     * CADA caractere digitado nas Notas/NPCs/Sessões (ver `atualizarCampanha`,
     * chamada em todo `onValueChange`), cada tecla disparava uma escrita no
     * Firestore; se a rede estivesse lenta e mais de uma escrita ficasse "em
     * voo" ao mesmo tempo, elas podiam chegar de volta pelo listener fora de
     * ordem. A gravação mais antiga chegando por último sobrescrevia o texto
     * mais novo que o Mestre acabara de digitar, apagando/revertendo
     * caracteres no meio da digitação.
     *
     * A correção segue o mesmo padrão já usado em Batalha (`battleUpdatedAt`)
     * e Mesa tática (`tacticalUpdatedAt`): a gravação carimba
     * `campaignUpdatedAt` e o listener descarta qualquer snapshot remoto mais
     * antigo que a última gravação local, para nunca "andar para trás".
     */
    private fun iniciarSincronizacaoCampanha() {
        campanhaListener?.remove()
        if (auth.currentUser == null) return
        campanhaListener = db.collection("campanhas").document("principal")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null || !snapshot.exists()) return@addSnapshotListener
                val payload = snapshot.getString("payload") ?: return@addSnapshotListener
                val remotoUpdatedAt = snapshot.getLong("campaignUpdatedAt") ?: 0L
                if (remotoUpdatedAt > 0L && remotoUpdatedAt < ultimaCampanhaLocalAtualizacao) return@addSnapshotListener
                val remoto = runCatching { json.decodeFromString<Campanha>(payload) }.getOrNull() ?: return@addSnapshotListener
                campanha.value = remoto
                gravarCampanhaLocal(remoto)
            }
    }

    private fun iniciarSincronizacaoAudio() {
        audioListener?.remove()
        if (auth.currentUser == null) return
        audioListener = db.collection("batalhas").document("principal")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null || !snapshot.exists()) return@addSnapshotListener
                val payload = snapshot.getString("audioPayload") ?: return@addSnapshotListener
                val remoto = runCatching { json.decodeFromString<AudioMesaState>(payload) }.getOrNull() ?: return@addSnapshotListener
                // No Mestre, preservamos os arquivos locais importados.
                // No jogador, os metadados sincronizados continuam sem caminho local.
                val locais = if (mestreAtual) {
                    audio.value.tracks.filter { it.sourceKey == null && !it.localPath.isNullOrBlank() }
                } else {
                    emptyList()
                }
                val trilha = (audiosPadrao() + locais + remoto.tracks).distinctBy { it.id }
                audio.value = remoto.copy(tracks = trilha)
                prefs.edit().putString("audio_mesa", json.encodeToString(audio.value)).apply()
            }
    }

    private fun iniciarSincronizacaoAudioTracks() {
        audioTracksListener?.remove()
        if (auth.currentUser == null) return
        audioTracksListener = db.collection("batalhas").document("principal").collection("audioTracks")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                val extras = snapshot.documents.mapNotNull { doc ->
                    AudioTrack(
                        id = doc.getString("id") ?: doc.id,
                        nome = doc.getString("nome") ?: "Áudio personalizado",
                        categoria = doc.getString("categoria") ?: "Personalizado",
                        arquivoNome = doc.getString("arquivoNome"),
                        // Arquivos personalizados são locais ao aparelho do Mestre.
                        // Nunca tentamos reproduzir um caminho privado recebido do Firestore.
                        localPath = null,
                        url = doc.getString("url")?.takeIf { it.isNotBlank() },
                    )
                }
                if (extras.isNotEmpty()) {
                    val locais = audio.value.tracks.filter { it.localPath != null }
                    val novo = audio.value.copy(
                        tracks = (audiosPadrao() + locais + extras).distinctBy { it.id }
                    )
                    audio.value = novo
                    prefs.edit().putString("audio_mesa", json.encodeToString(novo)).apply()
                }
            }
    }

    private fun gravarAudioNuvem(snapshot: AudioMesaState) {
        if (!mestreAtual) return
        val user = auth.currentUser ?: return

        // O Firestore recebe somente o estado de reprodução e os metadados.
        // O caminho local do arquivo nunca sai do aparelho do Mestre.
        val remoto = snapshot.copy(
            tracks = snapshot.tracks.map { track ->
                if (track.sourceKey == null) {
                    track.copy(localPath = null)
                } else {
                    track
                }
            }
        )

        db.collection("batalhas").document("principal")
            .set(
                mapOf(
                    "audioPayload" to json.encodeToString(remoto),
                    "updatedAt" to System.currentTimeMillis(),
                    "updatedBy" to user.uid,
                    "audioMode" to "local-master",
                ),
                SetOptions.merge()
            )
    }

    fun reproduzirAudio(trackId: String) {
        if (!mestreAtual) return
        val novo = audio.value.copy(ativoId = trackId, tocando = true)
        audio.value = novo; prefs.edit().putString("audio_mesa", json.encodeToString(novo)).apply(); gravarAudioNuvem(novo)
    }

    fun pausarAudio() {
        if (!mestreAtual) return
        val novo = audio.value.copy(tocando = false)
        audio.value = novo; gravarAudioNuvem(novo)
    }

    fun pararAudio() {
        if (!mestreAtual) return
        val novo = audio.value.copy(ativoId = null, tocando = false)
        audio.value = novo; gravarAudioNuvem(novo)
    }

    fun ajustarAudio(volume: Float? = null, loop: Boolean? = null) {
        if (!mestreAtual) return
        val novo = audio.value.copy(volume = (volume ?: audio.value.volume).coerceIn(0f, 1f), loop = loop ?: audio.value.loop)
        audio.value = novo; gravarAudioNuvem(novo)
    }

    fun importarAudio(context: Context, uri: Uri) {
        if (!mestreAtual) {
            audioUploadStatusState.value = "Apenas o Mestre pode adicionar áudios."
            return
        }

        val resolver = context.contentResolver
        val nome = queryNomeArquivo(context, uri) ?: "Audio_personalizado.mp3"
        val ext = nome.substringAfterLast('.', "mp3").lowercase()
        if (ext !in setOf("mp3", "ogg", "wav", "m4a", "aac")) {
            audioUploadStatusState.value = "Formato não suportado: .$ext"
            return
        }

        val tipoMime = resolver.getType(uri)?.takeIf { it.startsWith("audio/") } ?: when (ext) {
            "mp3" -> "audio/mpeg"
            "ogg" -> "audio/ogg"
            "wav" -> "audio/wav"
            "m4a" -> "audio/mp4"
            "aac" -> "audio/aac"
            else -> "audio/mpeg"
        }

        // Sem Firebase Storage: copia o arquivo para o armazenamento privado do
        // aparelho. O Firestore sincroniza apenas nome + comando de reprodução.
        audioUploadStatusState.value = "Copiando áudio para este aparelho…"
        val audioDir = File(appContext.filesDir, "audios_mestre").apply { mkdirs() }
        val id = novoId()
        val nomeSeguro = nome
            .replace(Regex("[^A-Za-z0-9._-]"), "_")
            .trim('_')
            .take(80)
            .ifBlank { "audio.mp3" }
        val destino = File(audioDir, "$id-$nomeSeguro")

        runCatching {
            resolver.openInputStream(uri)?.use { input ->
                destino.outputStream().use { output -> input.copyTo(output) }
            } ?: error("Não foi possível abrir o arquivo selecionado.")
        }.onFailure { erro ->
            destino.delete()
            audioUploadStatusState.value = "Falha ao importar: ${erro.message ?: "arquivo indisponível"}"
            return
        }

        val track = AudioTrack(
            id = id,
            nome = nome.substringBeforeLast('.').take(60),
            categoria = "Personalizado",
            localPath = destino.absolutePath,
            arquivoNome = nome.take(150),
        )
        val novo = audio.value.copy(tracks = (audio.value.tracks + track).distinctBy { it.id })
        audio.value = novo
        prefs.edit().putString("audio_mesa", json.encodeToString(novo)).apply()

        // Índice leve no Firestore. Nenhum byte do áudio é enviado para a nuvem.
        db.collection("batalhas")
            .document("principal")
            .collection("audioTracks")
            .document(id)
            .set(
                mapOf(
                    "id" to id,
                    "nome" to track.nome,
                    "categoria" to track.categoria,
                    "arquivoNome" to nome.take(150),
                    "contentType" to tipoMime,
                    "modo" to "local-master",
                    "criadoEm" to System.currentTimeMillis(),
                    "criadoPor" to (auth.currentUser?.uid ?: ""),
                )
            )
            .addOnSuccessListener {
                gravarAudioNuvem(novo)
                audioUploadStatusState.value = "Áudio adicionado: ${track.nome}"
            }
            .addOnFailureListener { erro ->
                // O arquivo local continua disponível mesmo se a sincronização
                // do índice falhar. O Mestre pode tentar sincronizar novamente.
                audioUploadStatusState.value =
                    "Áudio salvo neste aparelho, mas falhou a sincronização: ${erro.message ?: "erro do Firestore"}"
            }
    }

    private fun queryNomeArquivo(context: Context, uri: Uri): String? {
        return context.contentResolver.query(uri, arrayOf(android.provider.OpenableColumns.DISPLAY_NAME), null, null, null)?.use { c ->
            if (c.moveToFirst()) c.getString(0) else null
        }
    }

    fun sincronizarAgora() = iniciarSincronizacao()

    fun pararSincronizacao() {
        personagensListener?.remove()
        personagensListener = null
        monstrosListener?.remove()
        monstrosListener = null
        batalhaListener?.remove()
        batalhaListener = null
        mesaListener?.remove()
        mesaListener = null
        campanhaListener?.remove()
        campanhaListener = null
        audioListener?.remove()
        audioListener = null
        audioTracksListener?.remove()
        audioTracksListener = null
        mesaCloudSave?.cancel(false)
        mesaCloudSave = null
        mestreAtual = false
        mestreState.value = false
        sincronizacaoAtivaState.value = false
    }

    /**
     * BUG CORRIGIDO: esta função gravava sempre "ownerUid" = uid da sessão
     * atual. Como o Mestre enxerga TODAS as fichas (consulta sem filtro em
     * iniciarSincronizacao) e a FichaScreen não bloqueia edição por dono,
     * qualquer ação do Mestre que chamasse repo.salvar() numa ficha de
     * jogador (ex.: ajustar PV em combate) sobrescrevia o "ownerUid" no
     * Firestore com o uid do MESTRE. Isso fazia a ficha "sumir" da consulta
     * do próprio jogador (`whereEqualTo("ownerUid", user.uid)`) e, pela regra
     * do Firestore, bloqueava a edição/remoção dela pelo dono de verdade.
     *
     * Agora o dono gravado é sempre `p.ownerUid` (preenchido uma única vez,
     * na criação, em salvar()) - nunca o uid de quem está salvando no
     * momento. `user.uid` só é usado como fallback para fichas antigas que
     * ainda não tiverem o campo preenchido.
     */
    private fun salvarNuvem(p: Personagem) {
        val user = auth.currentUser ?: return
        val ref = db.collection("fichas").document(p.id)
        val donoUid = p.ownerUid.ifBlank { user.uid }
        val donoEmail = p.ownerEmail.ifBlank { user.email ?: "" }
        val comDono = p.copy(ownerUid = donoUid, ownerEmail = donoEmail)

        val dadosBase = mapOf(
            "ownerUid" to donoUid,
            "ownerEmail" to donoEmail,
            "payload" to json.encodeToString(comDono),
            "updatedAt" to System.currentTimeMillis(),
            "updatedBy" to user.uid,
        )

        ref.set(dadosBase, SetOptions.merge())
            .addOnSuccessListener {
                if (personagensPendentes[p.id]?.atualizadoEm == p.atualizadoEm) {
                    personagensPendentes.remove(p.id)
                    persistirPendentes()
                }
                android.util.Log.d("Repositorio", "Ficha ${p.id} enviada para o Firebase.")
            }
            .addOnFailureListener { erro ->
                android.util.Log.e(
                    "Repositorio",
                    "Erro ao enviar ficha ${p.id}: ${erro.message}",
                    erro
                )
            }
    }

    private fun salvarNuvemMonstro(m: Monstro) {
        if (!mestreAtual) return
        val user = auth.currentUser ?: return
        db.collection("monstros").document(m.id).set(
            mapOf(
                "payload" to json.encodeToString(m),
                "updatedAt" to System.currentTimeMillis(),
                "updatedBy" to user.uid,
                "updatedByEmail" to user.email,
            ),
            SetOptions.merge(),
        )
    }

    private fun removerNuvemMonstro(id: String) {
        if (!mestreAtual) return
        db.collection("monstros").document(id).delete()
    }

    private fun removerNuvem(id: String) {
        db.collection("fichas").document(id).delete()
            .addOnSuccessListener {
                personagensExclusaoPendentes.remove(id)
                persistirPendentes()
            }
            .addOnFailureListener { erro ->
                android.util.Log.e("Repositorio", "Erro ao excluir ficha $id: ${erro.message}", erro)
            }
    }

    init {
        personagens.addAll(ler("personagens") ?: emptyList())
        carregarPendentes()
        personagensPendentes.values.forEach { pendente ->
            if (pendente.id in personagensExclusaoPendentes) return@forEach
            val i = personagens.indexOfFirst { it.id == pendente.id }
            if (i >= 0) personagens[i] = pendente else personagens.add(pendente)
        }
        personagens.removeAll { it.id in personagensExclusaoPendentes }
        monstros.addAll(ler<Monstro>("monstros") ?: emptyList())
        prefs.getString("batalha", null)?.let {
            runCatching { batalha.value = json.decodeFromString<Batalha>(it) }
        }
        // Migração de batalhas antigas: versões anteriores criavam/importavam
        // combatentes com iniciativa = 0. Corrigimos isso uma única vez no
        // carregamento, tentando recuperar o bônus a partir da ficha/monstro;
        // quando não há origem identificável, geramos uma iniciativa válida.
        migrarIniciativasZeradas()
        prefs.getString("mesa_batalha", null)?.let {
            runCatching { mesa.value = json.decodeFromString<MesaBatalha>(it) }
        }
        prefs.getString("campanha", null)?.let {
            runCatching { campanha.value = json.decodeFromString<Campanha>(it) }
        }
        prefs.getString("audio_mesa", null)?.let {
            runCatching { audio.value = json.decodeFromString<AudioMesaState>(it).let { a -> a.copy(tracks = (audiosPadrao() + a.tracks).distinctBy { t -> t.id }) } }
        }
    }

    private inline fun <reified T> ler(chave: String): List<T>? =
        prefs.getString(chave, null)?.let { raw ->
            runCatching { json.decodeFromString<List<T>>(raw) }.getOrNull()
        }

    private fun persistirPendentes() {
        prefs.edit()
            .putString("personagens_pendentes", json.encodeToString(personagensPendentes.values.toList()))
            .putString("personagens_exclusoes_pendentes", json.encodeToString(personagensExclusaoPendentes.toList()))
            .apply()
    }

    private fun carregarPendentes() {
        prefs.getString("personagens_pendentes", null)?.let { raw ->
            runCatching { json.decodeFromString<List<Personagem>>(raw).forEach { personagensPendentes[it.id] = it } }
        }
        prefs.getString("personagens_exclusoes_pendentes", null)?.let { raw ->
            runCatching { json.decodeFromString<List<String>>(raw).forEach { personagensExclusaoPendentes += it } }
        }
    }

    private fun agendarGravacao(
        anterior: ScheduledFuture<*>?,
        tarefa: () -> Unit,
    ): ScheduledFuture<*> {
        anterior?.cancel(false)
        return persistExecutor.schedule(
            { runCatching { tarefa() } },
            120,
            TimeUnit.MILLISECONDS,
        )
    }

    private fun gravarPersonagensLocal(snapshot: List<Personagem>) {
        personagensSave = agendarGravacao(personagensSave) {
            val raw = json.encodeToString(snapshot)
            prefs.edit().putString("personagens", raw).apply()
        }
    }

    private fun gravarPersonagens(snapshot: List<Personagem>) {
        gravarPersonagensLocal(snapshot)
    }

    private fun gravarMonstros(snapshot: List<Monstro>) {
        monstrosSave = agendarGravacao(monstrosSave) {
            val raw = json.encodeToString(snapshot)
            prefs.edit().putString("monstros", raw).apply()
        }
    }

    private fun gravarCampanhaLocal(snapshot: Campanha) {
        val raw = json.encodeToString(snapshot)
        prefs.edit().putString("campanha", raw).apply()
    }

    private fun gravarCampanha(snapshot: Campanha) {
        gravarCampanhaLocal(snapshot)
        if (!mestreAtual) return
        val user = auth.currentUser ?: return
        val revisaoCampanha = System.currentTimeMillis()
        ultimaCampanhaLocalAtualizacao = revisaoCampanha
        db.collection("campanhas").document("principal")
            .set(
                mapOf(
                    "payload" to json.encodeToString(snapshot),
                    "campaignUpdatedAt" to revisaoCampanha,
                    "updatedAt" to revisaoCampanha,
                    "updatedBy" to user.uid,
                ),
                SetOptions.merge(),
            )
            .addOnFailureListener { erro ->
                android.util.Log.e("Repositorio", "Erro ao sincronizar campanha: ${erro.message}", erro)
            }
    }

    private fun gravarBatalhaLocal(snapshot: Batalha) {
        batalhaSave = agendarGravacao(batalhaSave) {
            val raw = json.encodeToString(snapshot)
            prefs.edit().putString("batalha", raw).apply()
        }
    }

    private fun gravarBatalha(snapshot: Batalha) {
        gravarBatalhaLocal(snapshot)
        if (!mestreAtual) return
        if (ultimaBatalhaLocalAtualizacao <= 0L) ultimaBatalhaLocalAtualizacao = System.currentTimeMillis()
        val user = auth.currentUser ?: return
        db.collection("batalhas").document("principal")
            .set(
                mapOf(
                    "salaId" to snapshot.salaId,
                    "payload" to json.encodeToString(snapshot),
                    "combatentesJson" to json.encodeToString(snapshot.combatentes),
                    "battleUpdatedAt" to ultimaBatalhaLocalAtualizacao,
                    "updatedAt" to System.currentTimeMillis(),
                    "updatedBy" to user.uid,
                    "updatedByEmail" to user.email,
                ),
                SetOptions.merge(),
            )
            .addOnFailureListener { erro ->
                android.util.Log.e("Repositorio", "Erro ao sincronizar batalha: ${erro.message}", erro)
            }
    }

    private fun gravarMesaLocal(snapshot: MesaBatalha) {
        mesaSave = agendarGravacao(mesaSave) {
            val raw = json.encodeToString(snapshot)
            prefs.edit().putString("mesa_batalha", raw).apply()
        }
    }

    /**
     * Publica a grade no formato usado pelo site:
     * batalhas/principal.tacticalPayload
     */
    private fun gravarMesa(snapshot: MesaBatalha) {
        gravarMesaLocal(snapshot)
        if (!mestreAtual) return
        val user = auth.currentUser ?: return
        val revisaoMesa = System.currentTimeMillis()
        ultimaMesaLocalAtualizacao = revisaoMesa
        mesaCloudSave = agendarGravacao(mesaCloudSave) {
            runCatching {
                val mapaDataUrl = snapshot.mapaArquivo?.let { caminho ->
                    if (caminho == mapaCachePath && !mapaCacheDataUrl.isNullOrBlank()) mapaCacheDataUrl
                    else codificarMapaSeguro(File(caminho))?.also { mapaCachePath = caminho; mapaCacheDataUrl = it }
                }
                if (snapshot.mapaArquivo != null && mapaDataUrl == null) {
                    android.util.Log.e("Repositorio", "Mapa grande demais para sincronizar com segurança; preservando o último estado remoto.")
                    return@runCatching
                }
                val tactical = MesaWebPayload(
                    cols = snapshot.cols, rows = snapshot.rows,
                    tokens = snapshot.tokens.map { token -> TokenMesaWebPayload(
                        id = token.id, nome = token.nome, x = token.x, y = token.y,
                        cor = corTokenParaSite(token.corNome), tipo = token.tipo,
                        pvAtual = token.pvAtual, pvMax = token.pvMax, imagem = token.imagem
                    ) },
                    ocultos = snapshot.ocultos, ambiente = snapshot.ambiente,
                    intensidade = snapshot.intensidade, rodada = snapshot.rodada,
                    mapa = mapaDataUrl, mapaAjuste = snapshot.mapaAjuste
                )
                db.collection("batalhas").document("principal").set(
                    mapOf(
                        "salaId" to snapshotToSalaId(snapshot),
                        "tacticalPayload" to json.encodeToString(tactical),
                        "tacticalUpdatedAt" to revisaoMesa,
                        "updatedBy" to user.uid,
                        "updatedByEmail" to user.email
                    ), SetOptions.merge()
                ).addOnFailureListener { erro ->
                    android.util.Log.e("Repositorio", "Erro ao sincronizar mesa tática: ${erro.message}", erro)
                }
            }.onFailure { erro ->
                android.util.Log.e("Repositorio", "Erro preparando mapa da Mesa: ${erro.message}", erro)
            }
        }
    }

    private fun codificarMapaSeguro(arquivo: File): String? {
        if (!arquivo.exists() || arquivo.length() == 0L) return null
        val limites = android.graphics.BitmapFactory.Options().apply { inJustDecodeBounds = true }
        android.graphics.BitmapFactory.decodeFile(arquivo.absolutePath, limites)
        if (limites.outWidth <= 0 || limites.outHeight <= 0) return null
        var amostra = 1
        while (limites.outWidth / amostra > 1600 || limites.outHeight / amostra > 1600) amostra *= 2
        val op = android.graphics.BitmapFactory.Options().apply {
            inSampleSize = amostra
            inPreferredConfig = android.graphics.Bitmap.Config.RGB_565
        }
        val bitmap = android.graphics.BitmapFactory.decodeFile(arquivo.absolutePath, op) ?: return null
        return try {
            var qualidade = 82
            var bytes = ByteArray(0)
            do {
                val out = java.io.ByteArrayOutputStream()
                bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, qualidade, out)
                bytes = out.toByteArray()
                qualidade -= 8
            } while (bytes.size > 700 * 1024 && qualidade >= 42)
            if (bytes.size > 700 * 1024) null
            else "data:image/jpeg;base64," + Base64.encodeToString(bytes, Base64.NO_WRAP)
        } finally { bitmap.recycle() }
    }

    private fun snapshotToSalaId(snapshot: MesaBatalha): String = "principal"

    private fun corTokenParaSite(cor: String): String = when (cor) {
        "Sangue" -> "var(--el-sangue)"
        "Morte" -> "var(--el-morte)"
        "Conhecimento" -> "var(--el-conhecimento)"
        "Energia" -> "var(--el-energia)"
        "Medo" -> "var(--el-medo)"
        else -> "var(--stat-pe)"
    }

    // ---------- Personagens ----------

    fun salvar(p: Personagem) {
        // Preenche o dono (ownerUid/ownerEmail) só na primeira vez que a ficha
        // é salva (quando ainda está em branco). Em salvamentos seguintes -
        // inclusive quando é o Mestre quem está salvando, por exemplo ao
        // ajustar o PV de um jogador durante o combate - o dono original é
        // preservado. Ver o comentário em salvarNuvem() para o motivo.
        val user = auth.currentUser
        val comDono = if (p.ownerUid.isBlank() && user != null) {
            p.copy(ownerUid = user.uid, ownerEmail = user.email ?: "")
        } else {
            p
        }
        val atualizado = comDono.copy(atualizadoEm = System.currentTimeMillis())
        personagensExclusaoPendentes.remove(atualizado.id)
        val i = personagens.indexOfFirst { it.id == p.id }
        if (i >= 0) personagens[i] = atualizado else personagens.add(atualizado)
        personagensPendentes[atualizado.id] = atualizado
        persistirPendentes()
        gravarPersonagens(personagens.toList())
        salvarNuvem(atualizado)
    }

    fun criarPersonagem(): Personagem = Personagem().also { salvar(it) }

    /**
     * O Mestre não escolhe uma foto manualmente para o jogador. Esta ação
     * apenas marca a ficha com uma solicitação. O aparelho do jogador, ao
     * receber o snapshot, responde usando a foto que já está salva no perfil.
     */
    fun solicitarFotoDoJogador(fichaId: String, aoResultado: (String?) -> Unit = {}) {
        if (!mestreAtual) return aoResultado("Apenas o Mestre pode solicitar a foto do jogador.")
        val ficha = personagem(fichaId) ?: return aoResultado("Ficha não encontrada.")
        if (ficha.ownerUid.isBlank()) return aoResultado("Esta ficha não informa o dono original.")
        val solicitacao = ficha.copy(fotoSolicitadaEm = System.currentTimeMillis())
        salvar(solicitacao)
        aoResultado(null)
    }

    /** Sincroniza automaticamente as fotos dos proprietários para o Mestre.
     * A atualização remota é feita em transação: lemos a versão mais recente
     * da ficha e alteramos somente fotoJogadorThumb dentro do payload. Assim,
     * uma foto chegando ao mesmo tempo que uma edição de PV/perícias não
     * sobrescreve os demais dados da ficha. */
    private fun sincronizarFotosDosJogadores(remotos: List<Personagem>) {
        val pendentes = remotos.filter { it.ownerUid.isNotBlank() && it.fotoJogadorThumb.isNullOrBlank() }
        pendentes.groupBy { it.ownerUid }.forEach { (uid, fichas) ->
            db.collection("usuarios").document(uid).get()
                .addOnSuccessListener { perfil ->
                    val thumb = perfil.getString("photoThumb").orEmpty()
                    if (thumb.isBlank()) return@addOnSuccessListener
                    fichas.forEach { ficha -> atualizarSomenteFotoDaFicha(ficha.id, thumb) }
                }
        }
    }

    private fun atualizarSomenteFotoDaFicha(fichaId: String, thumb: String) {
        val ref = db.collection("fichas").document(fichaId)
        db.runTransaction { tx ->
            val snap = tx.get(ref)
            val payload = snap.getString("payload") ?: return@runTransaction null
            val atual = runCatching { json.decodeFromString<Personagem>(payload) }.getOrNull()
                ?: return@runTransaction null
            if (atual.fotoJogadorThumb == thumb) return@runTransaction null
            val atualizado = atual.copy(fotoJogadorThumb = thumb, fotoSolicitadaEm = 0L)
            tx.set(
                ref,
                mapOf(
                    "payload" to json.encodeToString(atualizado),
                    "updatedAt" to System.currentTimeMillis(),
                ),
                SetOptions.merge(),
            )
            atualizado
        }.addOnSuccessListener { atualizado ->
            if (atualizado != null) {
                androidx.compose.runtime.snapshots.Snapshot.withMutableSnapshot {
                    val i = personagens.indexOfFirst { it.id == fichaId }
                    if (i >= 0) personagens[i] = atualizado
                }
            }
        }
    }

    /**
     * Atende solicitações vindas do Mestre sem abrir o seletor de arquivos.
     * Primeiro usa o arquivo de perfil local; se ele não existir, usa a
     * miniatura já sincronizada no documento do próprio usuário.
     */
    private fun atenderSolicitacoesDeFoto(user: com.google.firebase.auth.FirebaseUser, remotos: List<Personagem>) {
        val solicitadas = remotos.filter { it.ownerUid == user.uid && it.fotoSolicitadaEm > 0L }
        if (solicitadas.isEmpty()) return

        val caminhoLocal = prefs.getString("foto_${user.uid}", null)
        val thumbLocal = caminhoLocal?.takeIf { File(it).exists() }?.let {
            runCatching { gerarMiniaturaBase64(it) }.getOrNull()
        }

        fun responder(thumb: String?) {
            if (thumb.isNullOrBlank()) return
            solicitadas.forEach { ficha ->
                val atual = personagens.firstOrNull { it.id == ficha.id } ?: ficha
                // Só limpa a solicitação depois de realmente obter uma imagem.
                salvar(
                    atual.copy(
                        fotoJogadorThumb = thumb,
                        fotoSolicitadaEm = 0L,
                    ),
                )
            }
        }

        if (!thumbLocal.isNullOrBlank()) {
            responder(thumbLocal)
            return
        }

        db.collection("usuarios").document(user.uid).get()
            .addOnSuccessListener { perfil ->
                responder(perfil.getString("photoThumb"))
            }
    }

    fun removerPersonagem(id: String) {
        personagens.firstOrNull { it.id == id }?.let { ImagemImportador.remover(it.fotoArquivo) }
        personagens.removeAll { it.id == id }
        personagensPendentes.remove(id)
        personagensExclusaoPendentes.add(id)
        persistirPendentes()
        gravarPersonagens(personagens.toList())
        removerNuvem(id)
    }

    fun personagem(id: String?): Personagem? = personagens.firstOrNull { it.id == id }

    // ---------- Bestiário ----------

    fun salvar(m: Monstro) {
        if (!mestreAtual) return
        val atualizado = m.copy(atualizadoEm = System.currentTimeMillis())
        val i = monstros.indexOfFirst { it.id == m.id }
        if (i >= 0) monstros[i] = atualizado else monstros.add(atualizado)
        gravarMonstros(monstros.toList())
        salvarNuvemMonstro(atualizado)
    }

    fun criarMonstro(): Monstro = Monstro().also { salvar(it) }

    fun removerMonstro(id: String) {
        if (!mestreAtual) return
        monstros.firstOrNull { it.id == id }?.let { ImagemImportador.remover(it.fotoArquivo) }
        monstros.removeAll { it.id == id }
        gravarMonstros(monstros.toList())
        removerNuvemMonstro(id)
    }

    // ---------- Batalha ----------

    private fun rolarIniciativaBase(bonus: Int): Int = Random.nextInt(1, 21) + bonus

    private fun corrigirIniciativasZeradas(batalhaAtual: Batalha): Batalha {
        if (batalhaAtual.combatentes.none { it.iniciativa == 0 }) return batalhaAtual
        var alterou = false
        val corrigidos = batalhaAtual.combatentes.map { c ->
            if (c.iniciativa != 0) return@map c

            // Primeiro tenta localizar uma ficha de agente pelo nome.
            val personagem = personagens.firstOrNull { it.nome.equals(c.nome, ignoreCase = true) }
            if (personagem != null) {
                alterou = true
                c.copy(iniciativa = rolarIniciativaBase(iniciativaDoPersonagem(personagem)))
            } else {
                // Depois tenta localizar um monstro cadastrado pelo nome.
                val monstro = monstros.firstOrNull { it.nome.equals(c.nome, ignoreCase = true) }
                if (monstro != null) {
                    alterou = true
                    c.copy(iniciativa = rolarIniciativaBase(monstro.iniciativa.coerceAtLeast(monstro.atributos["agi"] ?: 0)))
                } else {
                    // Combatente criado manualmente/ameaça pronta antiga: não
                    // deixamos o rastreador permanecer zerado. O Mestre pode
                    // editar o resultado depois.
                    alterou = true
                    c.copy(iniciativa = rolarIniciativaBase(if (c.aliado) 1 else 0))
                }
            }
        }
        return if (alterou) batalhaAtual.copy(combatentes = corrigidos) else batalhaAtual
    }

    private fun migrarIniciativasZeradas() {
        val corrigida = corrigirIniciativasZeradas(batalha.value)
        if (corrigida != batalha.value) {
            batalha.value = corrigida
            gravarBatalhaLocal(corrigida)
        }
    }

    /**
     * Rola novamente a iniciativa de todos os combatentes. O resultado é
     * persistido e sincronizado com a sala, permitindo que o Mestre refaça a
     * ordem quando desejar.
     */
    fun rolarIniciativas() {
        if (!mestreAtual) return
        val nova = batalha.value.combatentes.map { c ->
            val personagem = personagens.firstOrNull { it.nome.equals(c.nome, ignoreCase = true) }
            val monstro = monstros.firstOrNull { it.nome.equals(c.nome, ignoreCase = true) }
            val bonus = when {
                personagem != null -> iniciativaDoPersonagem(personagem)
                monstro != null -> monstro.iniciativa.takeIf { it != 0 } ?: (monstro.atributos["agi"] ?: 0)
                else -> if (c.aliado) 1 else 0
            }
            c.copy(iniciativa = rolarIniciativaBase(bonus))
        }
        atualizarBatalha(batalha.value.copy(combatentes = nova))
    }

    fun atualizarBatalha(nova: Batalha) {
        batalha.value = nova.copy(salaId = "principal")
        ultimaBatalhaLocalAtualizacao = System.currentTimeMillis()
        gravarBatalha(batalha.value)
    }

    val combatentes: List<Combatente> get() = batalha.value.combatentes
    val rodada: Int get() = batalha.value.rodada

    fun adicionarCombatente() {
        if (!mestreAtual) return
        atualizarBatalha(batalha.value.copy(combatentes = combatentes + Combatente(
            nome = "Combatente",
            iniciativa = rolarIniciativaBase(1),
        )))
    }

    /**
     * Salva o combatente e, se existir um token na Mesa tática com o mesmo id
     * (criado junto, por exemplo via "Adicionar ameaça"), propaga PV e nome
     * para o token também — assim editar o PV na Iniciativa e na Mesa tática
     * nunca fica dessincronizado.
     */
    /**
     * Salva o combatente e, se existir um token na Mesa tática com o mesmo id
     * (criado junto, por exemplo via "Adicionar ameaça"), propaga PV, nome e
     * lado (aliado/inimigo) para o token também — assim editar qualquer um dos
     * dois nunca deixa o outro desatualizado.
     */
    fun salvarCombatente(c: Combatente) {
        if (!mestreAtual) return
        atualizarBatalha(
            batalha.value.copy(combatentes = combatentes.map { if (it.id == c.id) c else it })
        )
        if (mesa.value.tokens.any { it.id == c.id }) {
            atualizarMesa(
                mesa.value.copy(
                    tokens = mesa.value.tokens.map {
                        if (it.id == c.id) {
                            it.copy(
                                nome = c.nome,
                                pvAtual = c.pv,
                                pvMax = c.pvMax,
                                tipo = if (c.aliado) "agente" else "ameaca",
                            )
                        } else it
                    },
                )
            )
        }
    }

    fun removerCombatente(id: String) {
        if (!mestreAtual) return
        atualizarBatalha(
            batalha.value.copy(combatentes = combatentes.filterNot { it.id == id })
        )
        if (mesa.value.tokens.any { it.id == id }) {
            removerTokenMesa(id)
        }
    }

    /**
     * Avança a rodada da batalha. Atualiza tanto o rastreador de Iniciativa
     * (Batalha.rodada) quanto a Mesa tática (MesaBatalha.rodada) juntos, para
     * que as duas abas nunca fiquem mostrando números de rodada diferentes.
     */
    fun proximaRodada() {
        if (!mestreAtual) return
        val novaRodada = batalha.value.rodada + 1
        atualizarBatalha(batalha.value.copy(rodada = novaRodada))
        atualizarMesa(mesa.value.copy(rodada = novaRodada))
    }

    fun limparBatalha() {
        if (!mestreAtual) return
        atualizarBatalha(Batalha())
        atualizarMesa(mesa.value.copy(rodada = 1))
    }

    /** Encontra a próxima célula livre da grade da mesa tática, varrendo linha a linha. */
    private fun proximaPosicaoLivre(ocupados: Set<Pair<Int, Int>>): Pair<Int, Int> {
        val cols = mesa.value.cols.coerceAtLeast(1)
        val rows = mesa.value.rows.coerceAtLeast(1)
        for (y in 0 until rows) {
            for (x in 0 until cols) {
                if ((x to y) !in ocupados) return x to y
            }
        }
        return 0 to 0
    }

    /** Adiciona uma ou mais cópias de uma ameaça pronta ao rastreador de iniciativa
     * E como token na Mesa tática, já posicionado em uma célula livre da grade.
     * A iniciativa da ameaça fica como 0 para que o mestre possa rolar o dado
     * e preencher o resultado; PV, nome e lado já entram automaticamente.
     */
    fun adicionarAmeacaABatalha(ameaca: AmeacaPronta, quantidade: Int) {
        if (!mestreAtual) return
        val qtd = quantidade.coerceIn(1, 20)
        val ocupados = mesa.value.tokens.map { it.x to it.y }.toMutableSet()
        val novosCombatentes = mutableListOf<Combatente>()
        val novosTokens = mutableListOf<TokenMesa>()
        for (numero in 1..qtd) {
            val id = novoId()
            val nome = "${ameaca.nome} #$numero"
            val (x, y) = proximaPosicaoLivre(ocupados)
            ocupados += x to y
            novosCombatentes += Combatente(
                id = id,
                nome = nome,
                iniciativa = rolarIniciativaBase(ameaca.atributos["agi"] ?: 0),
                pv = ameaca.pv,
                pvMax = ameaca.pv,
                aliado = false,
            )
            novosTokens += TokenMesa(
                id = id,
                nome = nome,
                x = x,
                y = y,
                corNome = "Sangue",
                tipo = "ameaca",
                pvAtual = ameaca.pv,
                pvMax = ameaca.pv,
            )
        }
        atualizarBatalha(batalha.value.copy(combatentes = combatentes + novosCombatentes))
        atualizarMesa(mesa.value.copy(tokens = mesa.value.tokens + novosTokens))
    }

    /** Importa os agentes (personagens) para a Iniciativa, sem duplicar quem já está na lista.
     * A iniciativa de cada um já entra calculada a partir da ficha (AGI + perícia Iniciativa),
     * em vez de zerada — o mestre só ajusta se rolar dado com alguma variação. */
    fun importarAgentes() {
        if (!mestreAtual) return
        val jaImportados = combatentes.map { it.nome }.toSet()
        val candidatos = personagens.filterNot { it.nome in jaImportados }
        if (candidatos.isEmpty()) return

        val novosCombatentes = candidatos.map { personagem ->
            Combatente(
                nome = personagem.nome,
                iniciativa = iniciativaDoPersonagem(personagem),
                pv = personagem.pvAtual,
                pvMax = personagem.pvMax,
                aliado = true,
            )
        }
        val ocupados = mesa.value.tokens.map { it.x to it.y }.toMutableSet()
        val novosTokens = candidatos.map { personagem ->
            val (x, y) = proximaPosicaoLivre(ocupados)
            ocupados += x to y
            TokenMesa(
                id = novosCombatentes.first { it.nome == personagem.nome }.id,
                nome = personagem.nome,
                x = x,
                y = y,
                corNome = "Agente",
                tipo = "agente",
                pvAtual = personagem.pvAtual,
                pvMax = personagem.pvMax,
                imagem = personagem.fotoJogadorThumb ?: personagem.fotoAgenteThumb,
            )
        }
        atualizarBatalha(batalha.value.copy(combatentes = combatentes + novosCombatentes))
        atualizarMesa(mesa.value.copy(tokens = mesa.value.tokens + novosTokens))
    }

    // ---------- Mesa tática ----------

    fun atualizarMesa(nova: MesaBatalha) {
        mesa.value = nova
        gravarMesa(nova)
    }

    /** Encontra a próxima célula livre da grade, varrendo linha a linha. */
    fun proximaCelulaLivreMesa(): Pair<Int, Int> =
        proximaPosicaoLivre(mesa.value.tokens.map { it.x to it.y }.toSet())

    fun adicionarTokenMesa(token: TokenMesa) =
        atualizarMesa(mesa.value.copy(tokens = mesa.value.tokens + token))

    /**
     * Cria um token na Mesa tática e já registra o combatente correspondente
     * no rastreador de Iniciativa (mesmo id), para que apareçam ligados desde
     * o início e o PV se mantenha sincronizado nas duas telas. `iniciativa`
     * deve vir do valor real da ficha (agente) ou do bestiário (ameaça).
     */
    fun adicionarTokenComCombatente(token: TokenMesa, aliado: Boolean, iniciativa: Int = 0) {
        if (!mestreAtual) return
        val iniciativaFinal = if (iniciativa == 0) rolarIniciativaBase(if (aliado) 1 else 0) else iniciativa
        adicionarTokenMesa(token)
        atualizarBatalha(
            batalha.value.copy(
                combatentes = combatentes + Combatente(
                    id = token.id,
                    nome = token.nome,
                    iniciativa = iniciativaFinal,
                    pv = token.pvAtual,
                    pvMax = token.pvMax,
                    aliado = aliado,
                ),
            ),
        )
    }

    /**
     * Atualiza o token e, se houver um combatente com o mesmo id na
     * Iniciativa, propaga PV, nome e lado (aliado/inimigo) para ele também.
     */
    fun atualizarTokenMesa(id: String, patch: (TokenMesa) -> TokenMesa) {
        atualizarMesa(mesa.value.copy(tokens = mesa.value.tokens.map { if (it.id == id) patch(it) else it }))
        val token = mesa.value.tokens.firstOrNull { it.id == id } ?: return
        if (combatentes.any { it.id == id }) {
            atualizarBatalha(
                batalha.value.copy(
                    combatentes = combatentes.map {
                        if (it.id == id) {
                            it.copy(
                                nome = token.nome,
                                pv = token.pvAtual,
                                pvMax = token.pvMax,
                                aliado = token.tipo != "ameaca",
                            )
                        } else it
                    },
                ),
            )
        }
    }

    /** Remove o token e, se houver um combatente ligado a ele (mesmo id), remove-o também da Iniciativa. */
    fun removerTokenMesa(id: String) {
        mesa.value.tokens.firstOrNull { it.id == id }?.let { ImagemImportador.remover(it.imagemArquivo) }
        atualizarMesa(mesa.value.copy(tokens = mesa.value.tokens.filterNot { it.id == id }))
        if (combatentes.any { it.id == id }) {
            atualizarBatalha(batalha.value.copy(combatentes = combatentes.filterNot { it.id == id }))
        }
    }

    fun definirMapa(caminho: String?, nome: String?) =
        atualizarMesa(
            mesa.value.copy(
                mapaArquivo = caminho,
                mapaNome = nome,
                mapaOpacidade = 100,
                mapaAjuste = "conter",
            ),
        )

    fun limparMesa(context: Context) {
        MapaImportador.removerMapas(context)
        atualizarMesa(MesaBatalha())
    }

    // ---------- Campanha / Mestre ----------
    fun atualizarCampanha(nova: Campanha) {
        val atualizada = nova.copy(id = "principal", atualizadoEm = System.currentTimeMillis())
        campanha.value = atualizada
        gravarCampanha(atualizada)
    }

    fun adicionarNpc() {
        if (!mestreAtual) return
        atualizarCampanha(campanha.value.copy(npcs = campanha.value.npcs + NpcCampanha()))
    }

    fun salvarNpc(npc: NpcCampanha) {
        if (!mestreAtual) return
        atualizarCampanha(campanha.value.copy(npcs = campanha.value.npcs.map { if (it.id == npc.id) npc.copy(atualizadoEm = System.currentTimeMillis()) else it }))
    }

    fun removerNpc(id: String) {
        if (!mestreAtual) return
        atualizarCampanha(campanha.value.copy(npcs = campanha.value.npcs.filterNot { it.id == id }))
    }

    fun adicionarSessao() {
        if (!mestreAtual) return
        val numero = (campanha.value.sessoes.maxOfOrNull { it.numero } ?: 0) + 1
        atualizarCampanha(campanha.value.copy(sessoes = campanha.value.sessoes + SessaoCampanha(numero = numero, titulo = "Sessão ${numero.toString().padStart(2, '0')}")))
    }

    fun salvarSessao(sessao: SessaoCampanha) {
        if (!mestreAtual) return
        atualizarCampanha(campanha.value.copy(sessoes = campanha.value.sessoes.map { if (it.id == sessao.id) sessao.copy(atualizadoEm = System.currentTimeMillis()) else it }))
    }

    fun removerSessao(id: String) {
        if (!mestreAtual) return
        atualizarCampanha(campanha.value.copy(sessoes = campanha.value.sessoes.filterNot { it.id == id }))
    }

    fun exportarCampanha(context: Context, uri: Uri): Result<Unit> = runCatching {
        val mapa = mesa.value.mapaArquivo?.let { caminho ->
            val arquivo = File(caminho)
            if (arquivo.exists()) Base64.encodeToString(arquivo.readBytes(), Base64.NO_WRAP) else null
        }
        val backup = CampanhaBackup(
            campanha = campanha.value,
            personagens = personagens.toList(),
            monstros = monstros.toList(),
            batalha = batalha.value,
            mesa = mesa.value,
            mapaBase64 = mapa,
            mapaNome = mesa.value.mapaNome,
        )
        context.contentResolver.openOutputStream(uri)?.use { it.write(json.encodeToString(backup).toByteArray(Charsets.UTF_8)) }
            ?: error("Não foi possível abrir o arquivo de destino.")
    }

    fun importarCampanha(context: Context, uri: Uri): Result<Unit> = runCatching {
        check(mestreAtual) { "Somente o Mestre pode importar uma campanha." }
        val raw = context.contentResolver.openInputStream(uri)?.use { it.readBytes().toString(Charsets.UTF_8) }
            ?: error("Não foi possível ler o arquivo.")
        val backup = json.decodeFromString<CampanhaBackup>(raw)
        personagens.clear(); personagens.addAll(backup.personagens)
        monstros.clear(); monstros.addAll(backup.monstros)
        batalha.value = corrigirIniciativasZeradas(backup.batalha)
        mesa.value = backup.mesa.copy(mapaArquivo = null)
        campanha.value = backup.campanha.copy(id = "principal", atualizadoEm = System.currentTimeMillis())
        gravarPersonagensLocal(personagens.toList())
        gravarMonstros(monstros.toList())
        gravarBatalha(batalha.value)
        gravarCampanha(campanha.value)
        backup.mapaBase64?.let { base64 ->
            val pasta = File(context.filesDir, "mapas").apply { mkdirs() }
            pasta.listFiles()?.forEach { it.delete() }
            val destino = File(pasta, "mapa_importado_${System.currentTimeMillis()}.png")
            destino.writeBytes(Base64.decode(base64, Base64.DEFAULT))
            mesa.value = mesa.value.copy(mapaArquivo = destino.absolutePath, mapaNome = backup.mapaNome)
            gravarMesa(mesa.value)
        }
        personagens.forEach { salvarNuvem(it) }
    }

    fun fechar() {
        personagensListener?.remove()
        personagensListener = null
        monstrosListener?.remove()
        monstrosListener = null
        batalhaListener?.remove()
        batalhaListener = null
        mesaListener?.remove()
        mesaListener = null
        campanhaListener?.remove()
        campanhaListener = null
        audioListener?.remove()
        audioListener = null
        audioTracksListener?.remove()
        audioTracksListener = null
        persistExecutor.shutdown()
    }
}
