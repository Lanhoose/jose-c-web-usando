package com.arquivoparanormal.app.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.arquivoparanormal.app.data.AMBIENTES_MESA
import com.arquivoparanormal.app.data.iniciativaDoPersonagem
import com.arquivoparanormal.app.data.MapaImportador
import com.arquivoparanormal.app.data.Repositorio
import com.arquivoparanormal.app.data.TokenMesa
import com.arquivoparanormal.app.ui.Acento
import com.arquivoparanormal.app.ui.Borda
import com.arquivoparanormal.app.ui.Campo
import com.arquivoparanormal.app.ui.Chip
import com.arquivoparanormal.app.ui.CorConhecimento
import com.arquivoparanormal.app.ui.CorEnergia
import com.arquivoparanormal.app.ui.CorMedo
import com.arquivoparanormal.app.ui.CorMorte
import com.arquivoparanormal.app.ui.CorSangue
import com.arquivoparanormal.app.ui.Fundo
import com.arquivoparanormal.app.ui.Retrato
import com.arquivoparanormal.app.ui.SeletorImagem
import com.arquivoparanormal.app.ui.Superficie
import com.arquivoparanormal.app.ui.lembrarImagemLocal
import com.arquivoparanormal.app.ui.Painel
import com.arquivoparanormal.app.ui.Perigo
import com.arquivoparanormal.app.ui.Primaria
import com.arquivoparanormal.app.ui.RotuloOP
import com.arquivoparanormal.app.ui.Selecao
import com.arquivoparanormal.app.ui.TextoClaro
import com.arquivoparanormal.app.ui.TextoFraco
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.floor
import kotlin.math.roundToInt

private val CELULA = 44.dp

private enum class Pincel { MOVER, OCULTAR, REVELAR }

private fun corDoToken(nome: String): Color = when (nome) {
    "Sangue" -> CorSangue
    "Morte" -> CorMorte
    "Conhecimento" -> CorConhecimento
    "Energia" -> CorEnergia
    "Medo" -> CorMedo
    else -> Primaria
}

private fun corDoElemento(elemento: String): String = when (elemento) {
    "Sangue", "Morte", "Conhecimento", "Energia", "Medo" -> elemento
    else -> "Sangue"
}

@Composable
fun MesaTaticaScreen(repo: Repositorio) {
    val contexto = LocalContext.current
    val scope = rememberCoroutineScope()
    val cen by repo.mesa
    val mestre = repo.ehMestre
    var pincel by remember { mutableStateOf(Pincel.MOVER) }
    var selecionadoId by remember { mutableStateOf<String?>(null) }
    var criandoToken by remember { mutableStateOf(false) }
    var carregandoMapa by remember { mutableStateOf(false) }
    var erroMapa by remember { mutableStateOf<String?>(null) }

    val seletorArquivo = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        erroMapa = null
        carregandoMapa = true

        scope.launch(Dispatchers.IO) {
            val resultado = runCatching {
                val tipo = contexto.contentResolver.getType(uri)
                if (tipo != null && tipo != "application/pdf" && !tipo.startsWith("image/")) {
                    "Formato não suportado. Escolha PNG, JPG ou PDF." to null
                } else {
                    MapaImportador.importar(contexto, uri, tipo)?.let {
                        null to it
                    } ?: ("Não foi possível carregar esse arquivo." to null)
                }
            }.getOrElse {
                "Não foi possível carregar esse arquivo." to null
            }

            withContext(Dispatchers.Main.immediate) {
                val (mensagem, caminho) = resultado
                erroMapa = mensagem
                if (caminho != null) {
                    repo.definirMapa(caminho, MapaImportador.nomeDoArquivo(contexto, uri))
                }
                carregandoMapa = false
            }
        }
    }

    // O mapa pode ter vários MB. A leitura acontece em background e usa uma
    // miniatura limitada à resolução útil da mesa, evitando decodificação gigante na UI.
    val mapaBitmapBruto = lembrarImagemLocal(cen.mapaArquivo, maxDimensionPx = 4096)
    val mapaBitmap = remember(mapaBitmapBruto) { mapaBitmapBruto?.asImageBitmap() }
    var mapaOpacidade by remember(cen.mapaOpacidade) { mutableStateOf(cen.mapaOpacidade.toFloat()) }
    var intensidade by remember(cen.intensidade) { mutableStateOf(cen.intensidade.toFloat()) }
    val selecionado = cen.tokens.firstOrNull { it.id == selecionadoId }
    val densidade = LocalDensity.current
    val celulaPx = with(densidade) { CELULA.toPx() }

    LazyColumn(
        Modifier.fillMaxSize().padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 14.dp),
    ) {
        item {
            Painel(titulo = "Mapa da mesa") {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = { seletorArquivo.launch(arrayOf("image/png", "image/jpeg", "application/pdf")) },
                    ) {
                        Icon(Icons.Default.Image, contentDescription = "Importar mapa", tint = Primaria)
                    }
                    Text(
                        when {
                            carregandoMapa -> "Processando arquivo…"
                            cen.mapaNome != null -> cen.mapaNome.orEmpty()
                            else -> "Importar mapa (PNG, JPG ou PDF)"
                        },
                        color = if (cen.mapaArquivo != null) TextoClaro else TextoFraco,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f),
                    )
                    if (cen.mapaArquivo != null) {
                        IconButton(onClick = { repo.definirMapa(null, null) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Remover mapa", tint = Perigo)
                        }
                    }
                }

                erroMapa?.let { Text(it, color = Perigo, style = MaterialTheme.typography.bodySmall) }

                if (cen.mapaArquivo != null) {
                    Campo("Opacidade do mapa — ${mapaOpacidade.roundToInt()}%") {
                        Slider(
                            value = mapaOpacidade,
                            onValueChange = { mapaOpacidade = it },
                            onValueChangeFinished = {
                                repo.atualizarMesa(cen.copy(mapaOpacidade = mapaOpacidade.roundToInt()))
                            },
                            valueRange = 10f..100f,
                            colors = SliderDefaults.colors(thumbColor = Primaria, activeTrackColor = Primaria),
                        )
                    }
                    Campo("Ajuste do mapa") {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Chip(
                                "Mostrar inteiro",
                                cen.mapaAjuste == "conter",
                                { if (mestre) repo.atualizarMesa(cen.copy(mapaAjuste = "conter")) },
                                Acento,
                            )
                            Chip(
                                "Cobrir grade",
                                cen.mapaAjuste == "cobrir",
                                { if (mestre) repo.atualizarMesa(cen.copy(mapaAjuste = "cobrir")) },
                                Primaria,
                            )
                        }
                    }
                } else {
                    Text(
                        "A primeira página do PDF vira a imagem do mapa. Fica salvo neste aparelho.",
                        color = TextoFraco,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }

        item {
            Painel(
                titulo = "Grade tática — rodada ${repo.rodada}",
                acao = if (mestre) ({
                    IconButton(onClick = { repo.proximaRodada() }) {
                        Icon(Icons.Default.Add, contentDescription = "Próxima rodada", tint = Primaria)
                    }
                }) else null,
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Chip("Mover", pincel == Pincel.MOVER, { if (mestre) pincel = Pincel.MOVER })
                    Chip("Pintar névoa", pincel == Pincel.OCULTAR, { if (mestre) pincel = Pincel.OCULTAR }, Perigo)
                    Chip("Revelar", pincel == Pincel.REVELAR, { if (mestre) pincel = Pincel.REVELAR }, Acento)
                    Chip("Limpar névoa", false, { if (mestre) repo.atualizarMesa(cen.copy(ocultos = emptyList())) })
                }

                Spacer(Modifier.height(2.dp))

                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(320.dp)
                        .border(1.dp, Borda)
                        .background(Fundo)
                        .horizontalScroll(rememberScrollState())
                        .verticalScroll(rememberScrollState()),
                ) {
                    Box(Modifier.width(CELULA * cen.cols).height(CELULA * cen.rows)) {
                        if (mapaBitmap != null) {
                            Image(
                                bitmap = mapaBitmap,
                                contentDescription = "Mapa importado",
                                modifier = Modifier.matchParentSize().alpha(mapaOpacidade / 100f),
                                contentScale = if (cen.mapaAjuste == "cobrir") {
                                    ContentScale.Crop
                                } else {
                                    ContentScale.Fit
                                },
                            )
                        }

                        Canvas(
                            Modifier
                                .matchParentSize()
                                .pointerInput(mestre, pincel, cen.cols, cen.rows, cen.ocultos) {
                                    detectTapGestures { offset ->
                                        if (!mestre || pincel == Pincel.MOVER) return@detectTapGestures
                                        val x = floor(offset.x / celulaPx).toInt().coerceIn(0, cen.cols - 1)
                                        val y = floor(offset.y / celulaPx).toInt().coerceIn(0, cen.rows - 1)
                                        val chave = "$x,$y"
                                        val novo = if (pincel == Pincel.OCULTAR) {
                                            if (chave in cen.ocultos) cen.ocultos else cen.ocultos + chave
                                        } else {
                                            cen.ocultos - chave
                                        }
                                        repo.atualizarMesa(cen.copy(ocultos = novo))
                                    }
                                },
                        ) {
                            for (i in 0..cen.cols) {
                                val x = i * celulaPx
                                drawLine(Borda, Offset(x, 0f), Offset(x, size.height), strokeWidth = 1f)
                            }
                            for (j in 0..cen.rows) {
                                val y = j * celulaPx
                                drawLine(Borda, Offset(0f, y), Offset(size.width, y), strokeWidth = 1f)
                            }
                            cen.ocultos.forEach { chave ->
                                val partes = chave.split(",")
                                val x = partes.getOrNull(0)?.toIntOrNull() ?: return@forEach
                                val y = partes.getOrNull(1)?.toIntOrNull() ?: return@forEach
                                drawRect(
                                    color = Color.Black.copy(alpha = 0.88f),
                                    topLeft = Offset(x * celulaPx, y * celulaPx),
                                    size = Size(celulaPx, celulaPx),
                                )
                            }
                        }

                        cen.tokens.forEach { token ->
                            TokenMesaVisual(
                                token = token,
                                celula = CELULA,
                                selecionado = token.id == selecionadoId,
                                arrastavel = mestre && pincel == Pincel.MOVER,
                                limiteCols = cen.cols,
                                limiteRows = cen.rows,
                                aoSelecionar = { selecionadoId = token.id },
                                aoMover = { novoX, novoY ->
                                    repo.atualizarTokenMesa(token.id) { it.copy(x = novoX, y = novoY) }
                                },
                            )
                        }
                    }
                }
            }
        }

        item {
            Painel(titulo = "Efeitos de ambiente") {
                Campo("Atmosfera") {
                    Selecao(cen.ambiente, AMBIENTES_MESA, { repo.atualizarMesa(cen.copy(ambiente = it)) })
                }
                Campo("Intensidade — ${intensidade.roundToInt()}%") {
                    Slider(
                        value = intensidade,
                        onValueChange = { intensidade = it },
                        onValueChangeFinished = {
                            repo.atualizarMesa(cen.copy(intensidade = intensidade.roundToInt()))
                        },
                        valueRange = 0f..100f,
                        colors = SliderDefaults.colors(thumbColor = Primaria, activeTrackColor = Primaria),
                    )
                }
            }
        }

        item {
            Painel(
                titulo = "Tokens",
                acao = if (mestre) ({
                    IconButton(onClick = { criandoToken = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Criar token", tint = Primaria)
                    }
                }) else null,
            ) {
                if (mestre) {
                    Chip("Criar e personalizar token", false, { criandoToken = true }, Acento)
                    Chip("Ameaça rápida", false, {
                        val (x, y) = repo.proximaCelulaLivreMesa()
                        repo.adicionarTokenComCombatente(
                            TokenMesa(nome = "Ameaça", tipo = "ameaca", corNome = "Sangue", pvAtual = 20, pvMax = 20, x = x, y = y),
                            aliado = false,
                        )
                    }, Perigo)
                } else {
                    Text(
                        "🔒 Jogador: criação e edição de tokens são exclusivas do Mestre.",
                        color = TextoFraco,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }

                if (mestre && (repo.personagens.isNotEmpty() || repo.monstros.isNotEmpty())) {
                    RotuloOP("Importar da coleção")
                    androidx.compose.foundation.lazy.LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        items(repo.personagens) { p ->
                            Chip(p.nome, false, {
                                if (mestre) {
                                    val (x, y) = repo.proximaCelulaLivreMesa()
                                    repo.adicionarTokenComCombatente(
                                        TokenMesa(
                                            nome = p.nome,
                                            tipo = "agente",
                                            corNome = "Agente",
                                            pvAtual = p.pvAtual,
                                            pvMax = p.pvMax,
                                            imagemArquivo = p.fotoArquivo,
                                            x = x,
                                            y = y,
                                        ),
                                        aliado = true,
                                        iniciativa = iniciativaDoPersonagem(p),
                                    )
                                }
                            })
                        }
                        items(repo.monstros) { m ->
                            Chip(m.nome, false, {
                                if (mestre) {
                                    val (x, y) = repo.proximaCelulaLivreMesa()
                                    repo.adicionarTokenComCombatente(
                                        TokenMesa(
                                            nome = m.nome,
                                            tipo = "ameaca",
                                            corNome = corDoElemento(m.elemento),
                                            pvAtual = m.pv,
                                            pvMax = m.pv,
                                            imagemArquivo = m.fotoArquivo,
                                            x = x,
                                            y = y,
                                        ),
                                        aliado = false,
                                        iniciativa = m.iniciativa,
                                    )
                                }
                            }, Perigo)
                        }
                    }
                }

                if (cen.tokens.isEmpty()) {
                    Text("Nenhum token na mesa ainda.", color = TextoFraco, style = MaterialTheme.typography.bodySmall)
                }

                cen.tokens.forEach { t ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable { selecionadoId = t.id }
                            .border(1.dp, if (t.id == selecionadoId) Primaria else Borda)
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (t.imagem != null || t.imagemArquivo != null) {
                                Retrato(t.imagem ?: t.imagemArquivo, tamanho = 28.dp, forma = CircleShape)
                            } else {
                                Box(Modifier.size(10.dp).clip(CircleShape).background(corDoToken(t.corNome)))
                            }
                            Text(t.nome, color = TextoClaro, style = MaterialTheme.typography.bodyMedium)
                        }
                        Text(
                            "${t.pvAtual}/${t.pvMax}",
                            color = TextoFraco,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }
        }

        if (selecionado != null) {
            item {
                Painel(
                    titulo = "Token selecionado",
                    acao = if (mestre) ({
                        IconButton(
                            onClick = {
                                repo.removerTokenMesa(selecionado.id)
                                selecionadoId = null
                            },
                        ) { Icon(Icons.Default.Delete, contentDescription = "Remover token", tint = Perigo) }
                    }) else null,
                ) {
                    Campo(if (mestre) "Imagem do token" else "Imagem do token — somente leitura") {
                        if (mestre) {
                            SeletorImagem(
                                caminho = selecionado.imagem ?: selecionado.imagemArquivo,
                                aoDefinir = { caminho ->
                                    repo.atualizarTokenMesa(selecionado.id) { tk ->
                                        tk.copy(imagem = null, imagemArquivo = caminho)
                                    }
                                },
                                rotuloVazio = "Personalizar com PNG, JPG ou PDF",
                                pasta = "tokens",
                            )
                        } else {
                            Retrato(
                                caminho = selecionado.imagem ?: selecionado.imagemArquivo,
                                tamanho = 96.dp,
                            )
                        }
                    }
                    if (mestre) {
                        Campo("Nome") {
                            com.arquivoparanormal.app.ui.Texto(selecionado.nome) {
                                repo.atualizarTokenMesa(selecionado.id) { tk -> tk.copy(nome = it) }
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Campo("PV atual", Modifier.weight(1f)) {
                                com.arquivoparanormal.app.ui.Numero(selecionado.pvAtual) {
                                    repo.atualizarTokenMesa(selecionado.id) { tk -> tk.copy(pvAtual = it) }
                                }
                            }
                            Campo("PV máximo", Modifier.weight(1f)) {
                                com.arquivoparanormal.app.ui.Numero(selecionado.pvMax) {
                                    repo.atualizarTokenMesa(selecionado.id) { tk -> tk.copy(pvMax = it) }
                                }
                            }
                        }
                        RotuloOP("Cor")
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            com.arquivoparanormal.app.data.PALETA_TOKEN.forEach { nomeCor ->
                                Box(
                                    Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(corDoToken(nomeCor))
                                        .border(
                                            2.dp,
                                            if (selecionado.corNome == nomeCor) Acento else Color.Transparent,
                                            CircleShape,
                                        )
                                        .clickable {
                                            repo.atualizarTokenMesa(selecionado.id) { tk -> tk.copy(corNome = nomeCor) }
                                        },
                                )
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Chip("Agente", selecionado.tipo == "agente", {
                                repo.atualizarTokenMesa(selecionado.id) { tk -> tk.copy(tipo = "agente") }
                            })
                            Chip("Ameaça", selecionado.tipo == "ameaca", {
                                repo.atualizarTokenMesa(selecionado.id) { tk -> tk.copy(tipo = "ameaca") }
                            }, Perigo)
                        }
                    } else {
                        Text("Nome: ${selecionado.nome}", color = TextoClaro)
                        Text("PV: ${selecionado.pvAtual} / ${selecionado.pvMax}", color = TextoClaro)
                        Text("Tipo: ${if (selecionado.tipo == "ameaca") "Ameaça" else "Agente"}", color = TextoClaro)
                        Text("🔒 Este token é controlado pelo Mestre.", color = TextoFraco, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }

    if (criandoToken && mestre) {
        DialogoCriarToken(
            aoFechar = { criandoToken = false },
            aoCriar = { novo, aliado ->
                val (x, y) = repo.proximaCelulaLivreMesa()
                repo.adicionarTokenComCombatente(novo.copy(x = x, y = y), aliado = aliado)
                selecionadoId = null
                criandoToken = false
            },
        )
    } else if (criandoToken && !mestre) {
        criandoToken = false
    }
}

/** Criação de token com personalização completa (nome, tipo, cor, PV e imagem PNG/JPG/PDF). */
@Composable
private fun DialogoCriarToken(aoFechar: () -> Unit, aoCriar: (TokenMesa, Boolean) -> Unit) {
    var nome by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("agente") }
    var corNome by remember { mutableStateOf("Agente") }
    var pv by remember { mutableStateOf(10) }
    var imagem by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = aoFechar,
        containerColor = Superficie,
        title = { Text("Criar token", color = TextoClaro, style = MaterialTheme.typography.titleMedium) },
        text = {
            androidx.compose.foundation.layout.Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.verticalScroll(rememberScrollState()),
            ) {
                Campo("Imagem do token (PNG, JPG ou PDF)") {
                    SeletorImagem(
                        caminho = imagem,
                        aoDefinir = { imagem = it },
                        rotuloVazio = "Escolher arquivo",
                        tamanhoPreview = 72.dp,
                        pasta = "tokens",
                    )
                }
                Campo("Nome") {
                    com.arquivoparanormal.app.ui.Texto(nome, placeholder = "Ex.: Agente Vinícius") { nome = it }
                }
                Campo("PV") { com.arquivoparanormal.app.ui.Numero(pv) { pv = it } }
                RotuloOP("Tipo")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Chip("Agente", tipo == "agente", { tipo = "agente"; corNome = "Agente" })
                    Chip("Ameaça", tipo == "ameaca", { tipo = "ameaca"; corNome = "Sangue" }, Perigo)
                }
                RotuloOP("Cor da borda")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    com.arquivoparanormal.app.data.PALETA_TOKEN.forEach { cor ->
                        Box(
                            Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(corDoToken(cor))
                                .border(2.dp, if (corNome == cor) Acento else Color.Transparent, CircleShape)
                                .clickable { corNome = cor },
                        )
                    }
                }
                Text(
                    "O token ocupa exatamente 1 quadrado da grade.",
                    color = TextoFraco,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                aoCriar(
                    TokenMesa(
                        nome = nome.ifBlank { if (tipo == "ameaca") "Ameaça" else "Token" },
                        tipo = tipo,
                        corNome = corNome,
                        pvAtual = pv,
                        pvMax = pv,
                        imagemArquivo = imagem,
                    ),
                    tipo != "ameaca",
                )
            }) { Text("Criar", color = Primaria) }
        },
        dismissButton = {
            TextButton(onClick = aoFechar) { Text("Cancelar", color = TextoFraco) }
        },
    )
}

@Composable
private fun TokenMesaVisual(
    token: TokenMesa,
    celula: Dp,
    selecionado: Boolean,
    arrastavel: Boolean,
    limiteCols: Int,
    limiteRows: Int,
    aoSelecionar: () -> Unit,
    aoMover: (Int, Int) -> Unit,
) {
    val densidade = LocalDensity.current
    val celulaPx = with(densidade) { celula.toPx() }
    var offsetX by remember(token.id) { mutableStateOf(token.x * celulaPx) }
    var offsetY by remember(token.id) { mutableStateOf(token.y * celulaPx) }

    LaunchedEffect(token.x, token.y) {
        offsetX = token.x * celulaPx
        offsetY = token.y * celulaPx
    }

    val imagemBruta = lembrarImagemLocal(token.imagem ?: token.imagemArquivo)
    val imagem = remember(imagemBruta) { imagemBruta?.asImageBitmap() }

    Box(
        Modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .size(celula)
            .padding(2.dp)
            .clip(CircleShape)
            .background(corDoToken(token.corNome))
            .border(2.dp, if (selecionado) Acento else if (token.tipo == "ameaca") Perigo else Borda, CircleShape)
            .pointerInput(arrastavel, limiteCols, limiteRows) {
                if (arrastavel) {
                    detectDragGestures(
                        onDragStart = { aoSelecionar() },
                        onDrag = { change, drag ->
                            change.consume()
                            offsetX = (offsetX + drag.x).coerceIn(0f, (limiteCols - 1) * celulaPx)
                            offsetY = (offsetY + drag.y).coerceIn(0f, (limiteRows - 1) * celulaPx)
                        },
                        onDragEnd = {
                            val novoX = (offsetX / celulaPx).roundToInt().coerceIn(0, limiteCols - 1)
                            val novoY = (offsetY / celulaPx).roundToInt().coerceIn(0, limiteRows - 1)
                            offsetX = novoX * celulaPx
                            offsetY = novoY * celulaPx
                            aoMover(novoX, novoY)
                        },
                    )
                } else {
                    detectTapGestures(onTap = { aoSelecionar() })
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        if (imagem != null) {
            Image(
                bitmap = imagem,
                contentDescription = token.nome,
                modifier = Modifier.matchParentSize().clip(CircleShape),
                contentScale = ContentScale.Crop,
            )
        } else {
            Text(
                token.nome.take(3).uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                maxLines = 1,
            )
        }
    }
}
