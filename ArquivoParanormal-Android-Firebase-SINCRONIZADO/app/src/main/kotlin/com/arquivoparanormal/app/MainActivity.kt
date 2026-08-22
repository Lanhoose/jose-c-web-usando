package com.arquivoparanormal.app

import android.os.Bundle
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arquivoparanormal.app.data.Autenticacao
import com.arquivoparanormal.app.data.ConfiguracoesApp
import com.arquivoparanormal.app.data.LivroPdf
import com.arquivoparanormal.app.data.FirebaseBootstrap
import com.arquivoparanormal.app.data.Repositorio
import com.arquivoparanormal.app.screens.AgentesScreen
import com.arquivoparanormal.app.screens.AudioMestreScreen
import com.arquivoparanormal.app.screens.BatalhaScreen
import com.arquivoparanormal.app.screens.BestiarioScreen
import com.arquivoparanormal.app.screens.CompendioScreen
import com.arquivoparanormal.app.screens.ConfiguracoesScreen
import com.arquivoparanormal.app.screens.CriadorPersonagemScreen
import com.arquivoparanormal.app.screens.FichaScreen
import com.arquivoparanormal.app.screens.InformacoesScreen
import com.arquivoparanormal.app.screens.LivroPdfScreen
import com.arquivoparanormal.app.screens.LoginScreen
import com.arquivoparanormal.app.screens.MestreCampanhaScreen
import com.arquivoparanormal.app.ui.ArquivoParanormalTheme
import com.arquivoparanormal.app.ui.Fundo
import com.arquivoparanormal.app.ui.Primaria
import com.arquivoparanormal.app.ui.Superficie
import com.arquivoparanormal.app.ui.SuperficieAlta
import com.arquivoparanormal.app.ui.TextoClaro
import com.arquivoparanormal.app.ui.TextoFraco
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private var repo: Repositorio? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        FirebaseBootstrap.initialize(applicationContext)
        val repositorio = Repositorio(applicationContext)
        repo = repositorio
        val auth = Autenticacao(applicationContext)
        val configuracoes = ConfiguracoesApp(applicationContext)
    setContent { ArquivoParanormalTheme(configuracoes) { App(repositorio, auth, configuracoes) } }
    }

    override fun onDestroy() {
        // Permite que gravações pendentes terminem e libera o executor da persistência.
        repo?.fechar()
        repo = null
        super.onDestroy()
    }
}

private enum class Aba(val titulo: String) {
    Agentes("Agentes"),
    Batalha("Batalha"),
    Bestiario("Bestiário"),
    Compendio("Compêndio"),
    Informacoes("Informações"),
    Mestre("Modo Mestre"),
    Mais("+ Mais"),
    Configuracoes("Configurações"),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun App(repo: Repositorio, auth: Autenticacao, config: ConfiguracoesApp) {
    var logado by remember { mutableStateOf(auth.logado) }
    var aba by remember { mutableStateOf(Aba.Agentes) }
    var fichaId by remember { mutableStateOf<String?>(null) }
    var criacaoId by remember { mutableStateOf<String?>(null) }
    var paginaPdf by remember { mutableStateOf<Int?>(null) }
    var livroPdf by remember { mutableStateOf(LivroPdf.REGRAS) }

    if (!logado) {
        // repo.iniciarSincronizacao() NÃO é chamado aqui: o LaunchedEffect(logado)
        // logo abaixo já cuida disso assim que `logado` vira true. Chamar nos
        // dois lugares fazia a sincronização (todos os listeners do Firestore)
        // ser recriada duas vezes seguidas seguidas a cada login.
        LoginScreen(auth) { logado = true }
        return
    }

    LaunchedEffect(logado) {
        if (logado) repo.iniciarSincronizacao()
    }

    AudioAmbientalController(repo)

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val mestre = repo.ehMestre
    val emDetalhe = fichaId != null || criacaoId != null || paginaPdf != null

    // Redireciona para Agentes se o usuário perder o papel de mestre estando
    // numa aba exclusiva de mestre. Isso é um efeito colateral e precisa
    // rodar fora do corpo do `when` abaixo, que roda durante a composição.
    LaunchedEffect(aba, mestre) {
        if (!mestre && (aba == Aba.Bestiario || aba == Aba.Mais)) {
            aba = Aba.Agentes
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = !emDetalhe,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Superficie,
                modifier = Modifier.fillMaxHeight().width(310.dp),
            ) {
                DrawerConteudo(
                    abaSelecionada = aba,
                    repoMestreAtual = mestre,
                    aoSelecionar = { novaAba ->
                        fichaId = null
                        criacaoId = null
                        paginaPdf = null
                        aba = novaAba
                        scope.launch { drawerState.close() }
                    },
                    aoSair = {
                        scope.launch {
                            drawerState.close()
                            auth.sair()
                            repo.pararSincronizacao()
                            logado = false
                        }
                    },
                    aoFechar = { scope.launch { drawerState.close() } },
                )
            }
        },
    ) {
        Scaffold(
            containerColor = Fundo,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            when {
                                criacaoId != null -> "Novo agente"
                                fichaId != null -> "Ficha do agente"
                                paginaPdf != null -> "${livroPdf.titulo} — pág. ${paginaPdf}"
                                else -> aba.titulo
                            },
                            color = TextoClaro,
                        )
                    },
                    navigationIcon = {
                        if (emDetalhe) {
                            IconButton(onClick = {
                                fichaId = null
                                criacaoId = null
                                paginaPdf = null
                            }) {
                                Icon(
                                    Icons.Default.ArrowBack,
                                    contentDescription = "Voltar",
                                    tint = TextoClaro,
                                )
                            }
                        } else {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(
                                    Icons.Default.Menu,
                                    contentDescription = "Abrir menu lateral",
                                    tint = TextoClaro,
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Superficie),
                )
            },
        ) { pad ->
            val m = Modifier.padding(pad)
            val id = fichaId
            val criacao = criacaoId
            val pagina = paginaPdf
            if (criacao != null) {
                androidx.compose.foundation.layout.Box(m) {
                    CriadorPersonagemScreen(
                        repo = repo,
                        id = criacao,
                        aoConcluir = { fichaId = criacaoId; criacaoId = null },
                        aoCancelar = { repo.removerPersonagem(criacao); criacaoId = null },
                    )
                }
            } else if (id != null) {
                androidx.compose.foundation.layout.Box(m) { FichaScreen(repo, id, isMestre = mestre) }
            } else if (pagina != null) {
                androidx.compose.foundation.layout.Box(m) { LivroPdfScreen(livroPdf, pagina) }
            } else {
                androidx.compose.foundation.layout.Box(m) {
                    when (aba) {
                        Aba.Agentes -> AgentesScreen(repo, { fichaId = it }, { if (mestre) aba = Aba.Bestiario }, { criacaoId = repo.criarPersonagem().id }, mostrarBestiario = mestre)
                        Aba.Batalha -> BatalhaScreen(repo)
                        Aba.Bestiario -> if (mestre) BestiarioScreen(repo)
                        Aba.Compendio -> CompendioScreen { livro, pagina ->
                            livroPdf = livro
                            paginaPdf = pagina
                        }
                        Aba.Informacoes -> InformacoesScreen(onPaginaClick = { livro, pagina ->
                            livroPdf = livro
                            paginaPdf = pagina
                        })
                        Aba.Mais -> if (mestre) AudioMestreScreen(repo)
                        Aba.Configuracoes -> ConfiguracoesScreen(auth = auth, repo = repo, config = config, aoSair = { logado = false; aba = Aba.Agentes })
                        Aba.Mestre -> MestreCampanhaScreen(repo) { destino ->
                            aba = when (destino) {
                                "Batalha" -> Aba.Batalha
                                "Bestiário" -> Aba.Bestiario
                                "Compêndio" -> Aba.Compendio
                                else -> Aba.Batalha
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AudioAmbientalController(repo: Repositorio) {
    val estado = repo.audio.value
    val context = androidx.compose.ui.platform.LocalContext.current

    // O player só é criado/destruído quando a faixa ativa muda de verdade.
    // Pausar, ajustar volume ou repetição NÃO recriam o MediaPlayer: eles
    // agem sobre a mesma instância, preservando a posição de reprodução.
    val playerState = remember { mutableStateOf<android.media.MediaPlayer?>(null) }
    val preparedState = remember { mutableStateOf(false) }

    DisposableEffect(estado.ativoId) {
        val track = estado.tracks.firstOrNull { it.id == estado.ativoId }
        preparedState.value = false
        var novoPlayer: android.media.MediaPlayer? = null
        if (track != null) {
            runCatching {
                if (track.sourceKey != null) {
                    val resId = when (track.sourceKey) {
                        "floresta" -> com.arquivoparanormal.app.R.raw.ambiente_floresta
                        "chuva" -> com.arquivoparanormal.app.R.raw.ambiente_chuva
                        "casa" -> com.arquivoparanormal.app.R.raw.ambiente_casa
                        "perseguicao" -> com.arquivoparanormal.app.R.raw.ambiente_perseguicao
                        "combate" -> com.arquivoparanormal.app.R.raw.ambiente_combate
                        "terror" -> com.arquivoparanormal.app.R.raw.ambiente_terror
                        else -> 0
                    }
                    novoPlayer = android.media.MediaPlayer.create(context, resId)?.apply {
                        isLooping = estado.loop
                        setVolume(estado.volume, estado.volume)
                        setOnErrorListener { mp, what, extra ->
                            android.util.Log.e("AudioAmbiental", "Erro no áudio interno: what=$what extra=$extra")
                            runCatching { mp.reset() }
                            preparedState.value = false
                            true
                        }
                    }
                    // MediaPlayer.create já entrega o player preparado.
                    if (novoPlayer != null) preparedState.value = true
                } else {
                    val caminhoLocal = track.localPath
                    val urlExterna = track.url
                    when {
                        !caminhoLocal.isNullOrBlank() && java.io.File(caminhoLocal).exists() -> {
                            novoPlayer = android.media.MediaPlayer().apply {
                                setDataSource(caminhoLocal)
                                isLooping = estado.loop
                                setVolume(estado.volume, estado.volume)
                                setOnPreparedListener { preparedState.value = true }
                                setOnErrorListener { mp, what, extra ->
                                    android.util.Log.e("AudioAmbiental", "Erro no áudio local: what=$what extra=$extra")
                                    preparedState.value = false
                                    runCatching { mp.reset() }
                                    true
                                }
                                prepareAsync()
                            }
                        }
                        !urlExterna.isNullOrBlank() -> {
                            novoPlayer = android.media.MediaPlayer().apply {
                                setDataSource(context, Uri.parse(urlExterna))
                                isLooping = estado.loop
                                setVolume(estado.volume, estado.volume)
                                setOnPreparedListener { preparedState.value = true }
                                setOnErrorListener { mp, what, extra ->
                                    android.util.Log.e("AudioAmbiental", "Erro no áudio remoto: what=$what extra=$extra")
                                    preparedState.value = false
                                    runCatching { mp.reset() }
                                    true
                                }
                                prepareAsync()
                            }
                        }
                    }
                }
            }
        }
        playerState.value = novoPlayer
        onDispose {
            preparedState.value = false
            runCatching { novoPlayer?.stop() }
            runCatching { novoPlayer?.release() }
            playerState.value = null
        }
    }

    // Liga/pausa (sem recriar) assim que o player estiver pronto ou o
    // Mestre apertar Pausar/Continuar. Continuar retoma de onde parou.
    LaunchedEffect(estado.tocando, preparedState.value, playerState.value) {
        val player = playerState.value ?: return@LaunchedEffect
        if (!preparedState.value) return@LaunchedEffect
        runCatching {
            if (estado.tocando) {
                if (!player.isPlaying) player.start()
            } else {
                if (player.isPlaying) player.pause()
            }
        }
    }

    // Volume e repetição são aplicados ao vivo, na mesma instância.
    LaunchedEffect(estado.volume, estado.loop, preparedState.value, playerState.value) {
        val player = playerState.value ?: return@LaunchedEffect
        if (!preparedState.value) return@LaunchedEffect
        runCatching {
            player.setVolume(estado.volume, estado.volume)
            player.isLooping = estado.loop
        }
    }
}

@Composable
private fun DrawerConteudo(
    abaSelecionada: Aba,
    repoMestreAtual: Boolean,
    aoSelecionar: (Aba) -> Unit,
    aoSair: () -> Unit,
    aoFechar: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(vertical = 10.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Default.Book, contentDescription = null, tint = Primaria)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("Arquivo Paranormal", color = TextoClaro)
                Text("Navegação", color = TextoFraco)
            }
            IconButton(onClick = aoFechar) {
                Icon(Icons.Default.Close, contentDescription = "Fechar menu", tint = TextoFraco)
            }
        }

        Divider(color = SuperficieAlta)

        Text(
            "SEÇÕES",
            color = TextoFraco,
            modifier = Modifier.padding(horizontal = 22.dp, vertical = 14.dp),
        )

        val itens = buildList {
            add(Aba.Agentes to Icons.Default.Group)
            add(Aba.Batalha to Icons.Default.Shield)
            if (repoMestreAtual) add(Aba.Bestiario to Icons.Default.Pets)
            add(Aba.Compendio to Icons.Default.MenuBook)
            add(Aba.Informacoes to Icons.Default.Info)
            if (repoMestreAtual) add(Aba.Mestre to Icons.Default.AdminPanelSettings)
            if (repoMestreAtual) add(Aba.Mais to Icons.Default.Add)
            add(Aba.Configuracoes to Icons.Default.Settings)
        }

        itens.forEach { (item, icone) ->
            NavigationDrawerItem(
                label = { Text(item.titulo) },
                selected = abaSelecionada == item,
                onClick = { aoSelecionar(item) },
                icon = { Icon(icone, contentDescription = null) },
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
            )
        }

        Spacer(Modifier.weight(1f))
        Divider(color = SuperficieAlta)

        NavigationDrawerItem(
            label = { Text("Sair") },
            selected = false,
            onClick = aoSair,
            icon = { Icon(Icons.Default.Logout, contentDescription = null) },
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
        )
    }
}
