package com.arquivoparanormal.app.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.random.Random
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.arquivoparanormal.app.data.ATRIBUTOS
import com.arquivoparanormal.app.data.ARMAS_LIVRO
import com.arquivoparanormal.app.data.ITENS_ARQUIVOS_SECRETOS
import com.arquivoparanormal.app.data.PODERES_SOBREVIVENDO
import com.arquivoparanormal.app.data.MUNICOES_LIVRO
import com.arquivoparanormal.app.data.CIRCULOS
import com.arquivoparanormal.app.data.CATEGORIAS_ITEM
import com.arquivoparanormal.app.data.CLASSES
import com.arquivoparanormal.app.data.DESCRICOES_TRILHAS
import com.arquivoparanormal.app.data.CONDICOES
import com.arquivoparanormal.app.data.ELEMENTOS
import com.arquivoparanormal.app.data.ORIGENS_COMPLETAS
import com.arquivoparanormal.app.data.PATENTES
import com.arquivoparanormal.app.data.patentePorPrestigio
import com.arquivoparanormal.app.data.limitesItensPorCategoria
import com.arquivoparanormal.app.data.limiteRituaisConhecidosV36
import com.arquivoparanormal.app.data.PERICIAS
import com.arquivoparanormal.app.data.RESISTENCIAS
import com.arquivoparanormal.app.data.PROTECOES_LIVRO
import com.arquivoparanormal.app.data.ELEMENTOS_AFINIDADE
import com.arquivoparanormal.app.data.elementoOpressor
import com.arquivoparanormal.app.data.fontesResistencia
import com.arquivoparanormal.app.data.fontesRdGeral
import com.arquivoparanormal.app.data.relacaoEntreElementos
import com.arquivoparanormal.app.data.resistenciaAutomatica
import com.arquivoparanormal.app.data.resistenciaTotal
import com.arquivoparanormal.app.data.TREINAMENTOS
import com.arquivoparanormal.app.data.Arma
import com.arquivoparanormal.app.data.Item
import com.arquivoparanormal.app.data.Pericia
import com.arquivoparanormal.app.data.Personagem
import com.arquivoparanormal.app.data.Repositorio
import com.arquivoparanormal.app.data.Ritual
import com.arquivoparanormal.app.data.RITUAIS_COMPLETOS
import com.arquivoparanormal.app.data.espacosUsados
import com.arquivoparanormal.app.data.calcularFicha
import com.arquivoparanormal.app.data.bonusPericiaComAtributo
import com.arquivoparanormal.app.data.dadosDoAtributo
import com.arquivoparanormal.app.data.usaPiorDadoDoAtributo
import com.arquivoparanormal.app.data.testeAtaqueDaArma
import com.arquivoparanormal.app.data.ataqueDaArma
import com.arquivoparanormal.app.data.margemCritico
import com.arquivoparanormal.app.data.periciasConcedidasPelaOrigem
import com.arquivoparanormal.app.data.descricaoPoderDaOrigem
import com.arquivoparanormal.app.data.sincronizarRecursosDerivados
import com.arquivoparanormal.app.data.corrigirRecursosLegados
import com.arquivoparanormal.app.data.formatBonus
import com.arquivoparanormal.app.data.limiteDeCarga
import com.arquivoparanormal.app.data.custoRitualPorCirculo
import com.arquivoparanormal.app.data.rituaisElegiveisParaAprender
import com.arquivoparanormal.app.data.maxCirculoAprenderRitual
import com.arquivoparanormal.app.data.circuloMaximoOcultista
import com.arquivoparanormal.app.data.motivoBloqueioRitual
import com.arquivoparanormal.app.data.estadoSanidade
import com.arquivoparanormal.app.data.registrarTurnoEnlouquecendo
import com.arquivoparanormal.app.data.registrarTurnoMorrendo
import com.arquivoparanormal.app.data.estabilizar
import com.arquivoparanormal.app.data.fonteDoRitual
import com.arquivoparanormal.app.data.motivoBloqueioPoder
import com.arquivoparanormal.app.data.catalogoCompletoDePoderes
import com.arquivoparanormal.app.data.origemCatalogadaDoPoder
import com.arquivoparanormal.app.data.quantidadePoderesDeClasseV36
import com.arquivoparanormal.app.data.quantidadeRituaisClasseOcultistaV36
import com.arquivoparanormal.app.data.rituaisClasseOcultistaConhecidosV36
import com.arquivoparanormal.app.data.poderesDeClasseManuaisConhecidosV36
import com.arquivoparanormal.app.data.podeAdicionarPoderDeClasseV36
import com.arquivoparanormal.app.data.progressaoDeNex
import com.arquivoparanormal.app.data.proximoNex
import com.arquivoparanormal.app.data.registrarAlteracao
import com.arquivoparanormal.app.data.ITENS_AMALDICOADOS_LIVRO
import com.arquivoparanormal.app.data.MALDICOES_CATALOGO_COMPLETO
import com.arquivoparanormal.app.data.patentePermiteItemAmaldicoado
import com.arquivoparanormal.app.ui.AreaTexto
import com.arquivoparanormal.app.ui.Acento
import com.arquivoparanormal.app.ui.BarraRecurso
import com.arquivoparanormal.app.ui.Campo
import com.arquivoparanormal.app.ui.Chip
import com.arquivoparanormal.app.ui.CorEnergia
import com.arquivoparanormal.app.ui.Numero
import com.arquivoparanormal.app.ui.Painel
import com.arquivoparanormal.app.ui.Perigo
import com.arquivoparanormal.app.ui.Primaria
import com.arquivoparanormal.app.ui.RotuloOP
import com.arquivoparanormal.app.ui.Selecao
import com.arquivoparanormal.app.ui.TextoClaro
import com.arquivoparanormal.app.ui.TextoFraco
import com.arquivoparanormal.app.ui.corElemento
import com.arquivoparanormal.app.ui.Texto as CampoTexto

private fun rolarDano(formula: String): String {
    val match = Regex("""(\d+)d(\d+)([+-]\d+)?""", RegexOption.IGNORE_CASE).find(formula.replace(" ", ""))
        ?: return formula
    val dados = match.groupValues[1].toInt()
    val faces = match.groupValues[2].toInt()
    val modificador = match.groupValues[3].toIntOrNull() ?: 0
    val rolagens = List(dados.coerceIn(1, 30)) { Random.nextInt(1, faces.coerceAtLeast(2) + 1) }
    val total = rolagens.sum() + modificador
    return "${rolagens.joinToString(" + ")} ${formatBonus(modificador)} = $total"
}

@Composable
fun FichaScreen(repo: Repositorio, id: String, isMestre: Boolean = repo.ehMestre) {
    val p = repo.personagem(id)
    if (p == null) {
        Text("Ficha não encontrada.", color = TextoFraco, modifier = Modifier.padding(20.dp))
        return
    }
    val upd: (Personagem) -> Unit = { novo ->
        val auditado = if (novo == p) novo else {
            val eventos = buildList {
                if (novo.nex != p.nex) add("NEX: ${p.nex}% → ${novo.nex}%")
                if (novo.prestigio != p.prestigio) add("Prestígio: ${p.prestigio} → ${novo.prestigio}")
                if (novo.classe != p.classe) add("Classe: ${p.classe} → ${novo.classe}")
                if (novo.trilha != p.trilha) add("Trilha: ${p.trilha.ifBlank { "nenhuma" }} → ${novo.trilha.ifBlank { "nenhuma" }}")
                if (novo.rituais != p.rituais) add("Rituais alterados")
                if (novo.itens != p.itens) add("Inventário/equipamento alterado")
                if (novo.condicoes != p.condicoes) add("Condições alteradas")
                if (novo.pvAtual != p.pvAtual || novo.peAtual != p.peAtual || novo.sanAtual != p.sanAtual) add("Recursos PV/PE/SAN alterados")
            }
            eventos.fold(novo) { acc, evento -> acc.registrarAlteracao("FICHA", evento) }
        }
        repo.salvar(sincronizarRecursosDerivados(p, auditado))
    }

    LaunchedEffect(p.id, p.nex, p.classe, p.atributos, p.fotoArquivo, p.fotoAgenteThumb) {
        var corrigida = corrigirRecursosLegados(p)
        if (corrigida.fotoAgenteThumb.isNullOrBlank() && !corrigida.fotoArquivo.isNullOrBlank() && java.io.File(corrigida.fotoArquivo!!).exists()) {
            val thumb = withContext(Dispatchers.IO) {
                com.arquivoparanormal.app.data.ImagemImportador.gerarMiniaturaDataUrl(corrigida.fotoArquivo!!)
            }
            corrigida = corrigida.copy(fotoAgenteThumb = thumb)
        }
        if (corrigida.pvAtual != p.pvAtual || corrigida.pvMax != p.pvMax ||
            corrigida.peAtual != p.peAtual || corrigida.peMax != p.peMax ||
            corrigida.sanAtual != p.sanAtual || corrigida.sanMax != p.sanMax ||
            corrigida.fotoAgenteThumb != p.fotoAgenteThumb) {
            repo.salvar(corrigida)
        }
    }

    LazyColumn(
        Modifier.fillMaxSize().padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 14.dp),
    ) {
        item {
            Painel(titulo = "Identificação") {
                if (repo.ehMestre && p.fotoJogadorThumb != null) {
                    Campo("Foto do jogador") {
                        com.arquivoparanormal.app.ui.RetratoDataUrl(p.fotoJogadorThumb, tamanho = 88.dp)
                        Text("Miniatura sincronizada do perfil do jogador.", color = TextoFraco, style = MaterialTheme.typography.bodySmall)
                    }
                }
                Campo("Foto do agente") {
                    val caminhoFotoVisivel = p.fotoArquivo?.takeIf { java.io.File(it).exists() } ?: p.fotoAgenteThumb
                    com.arquivoparanormal.app.ui.SeletorImagem(
                        caminho = caminhoFotoVisivel,
                        aoDefinir = { caminho ->
                            val thumb = caminho?.let { com.arquivoparanormal.app.data.ImagemImportador.gerarMiniaturaDataUrl(it) }
                            upd(p.copy(fotoArquivo = caminho, fotoAgenteThumb = thumb))
                        },
                        rotuloVazio = "Adicionar foto (imagem ou PDF)",
                        tamanhoPreview = 88.dp,
                    )
                    if (p.fotoArquivo == null && p.fotoAgenteThumb != null) {
                        Text("Foto sincronizada disponível:", color = TextoFraco, style = MaterialTheme.typography.bodySmall)
                        com.arquivoparanormal.app.ui.RetratoDataUrl(p.fotoAgenteThumb, tamanho = 88.dp)
                    }
                }
                Campo("Nome do agente") { CampoTexto(p.nome) { upd(p.copy(nome = it)) } }
                Campo("Jogador") { CampoTexto(p.jogador) { upd(p.copy(jogador = it)) } }
                Campo("Idade") { CampoTexto(p.idade) { upd(p.copy(idade = it)) } }
                Campo("Descrição") { AreaTexto(p.descricao, { upd(p.copy(descricao = it)) }) }
                Campo("Prestígio (PP)") { Numero(p.prestigio) { novo -> val pp = novo.coerceAtLeast(0); upd(p.copy(prestigio = pp, patente = patentePorPrestigio(pp))) } }
                Campo("Patente") {
                    Text(patentePorPrestigio(p.prestigio), color = Acento)
                    Text("Calculada por Prestígio; NEX não altera a patente.", color = TextoFraco, style = MaterialTheme.typography.labelSmall)
                }
                Campo("NEX %") { Numero(p.nex) { upd(p.copy(nex = it)) } }
                val marcos = progressaoDeNex(p)
                val atual = marcos.firstOrNull { it.nex == p.nex }
                val proximo = proximoNex(p)
                var mostrarProgressao by remember(p.nex, p.classe, p.trilha) { mutableStateOf(false) }
                Painel(titulo = "Progressão de NEX") {
                        Text("Veja o que foi desbloqueado no seu NEX e o que vem a seguir. Os marcos seguem a progressão do Livro de Regras.", color = TextoFraco, style = MaterialTheme.typography.bodySmall)
                        atual?.let {
                            Text("🔓 Desbloqueios do NEX ${it.nex}%", color = Acento, style = MaterialTheme.typography.titleSmall)
                            Text(it.descricao, color = TextoClaro, style = MaterialTheme.typography.bodySmall)
                        }
                        proximo?.let { n ->
                            val marco = marcos.first { it.nex == n }
                            Text("🔔 Próximo desbloqueio — NEX $n%", color = Acento, style = MaterialTheme.typography.titleSmall)
                            Text(marco.descricao, color = TextoFraco, style = MaterialTheme.typography.bodySmall)
                        } ?: Text("Você alcançou o último marco de NEX da progressão normal (99%).", color = TextoFraco, style = MaterialTheme.typography.bodySmall)
                        TextButton(onClick = { mostrarProgressao = !mostrarProgressao }) {
                            Text(if (mostrarProgressao) "Ocultar progressão completa" else "Ver progressão completa")
                        }
                        if (mostrarProgressao) {
                            marcos.forEach { marco ->
                                val desbloqueado = marco.nex <= p.nex
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(if (desbloqueado) "🔓" else "🔒", modifier = Modifier.padding(top = 2.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text("NEX ${marco.nex}%", color = if (desbloqueado) TextoClaro else TextoFraco, style = MaterialTheme.typography.labelLarge)
                                        Text(marco.descricao, color = TextoFraco, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                    }
                Campo("Classe") {
                    Selecao(p.classe, CLASSES.map { it.nome }, { upd(p.copy(classe = it, trilha = "")) })
                }
                Campo("Trilha") {
                    val trilhas = CLASSES.firstOrNull { it.nome == p.classe }?.trilhas ?: emptyList()
                    Selecao(p.trilha, trilhas, { upd(p.copy(trilha = it)) }, placeholder = "Nenhuma")
                }
                Campo("Origem") {
                    Selecao(p.origem, ORIGENS_COMPLETAS.map { it.nome }, { novaOrigem ->
                        val novas = periciasConcedidasPelaOrigem(novaOrigem)
                        val antigasAuto = p.periciasAutomaticas.toSet()
                        val mapa = p.pericias.toMutableMap()
                        antigasAuto.forEach { nome ->
                            val atual = mapa[nome]
                            if (atual != null && atual.outros == 0 && atual.treino == 5) mapa.remove(nome)
                        }
                        novas.forEach { nome ->
                            val atual = mapa[nome] ?: Pericia()
                            if (atual.treino == 0 && atual.outros == 0) mapa[nome] = atual.copy(treino = 5)
                        }
                        upd(p.copy(origem = novaOrigem, pericias = mapa, periciasAutomaticas = novas))
                    }, placeholder = "Nenhuma")
                }
                ORIGENS_COMPLETAS.firstOrNull { it.nome == p.origem }?.let { o ->
                    var detalhesOrigem by remember(o.nome, o.poder) { mutableStateOf(false) }
                    Text("Perícias: ${o.pericias}", style = MaterialTheme.typography.bodySmall, color = Acento)
                    Text("Habilidade: ${o.poder}", style = MaterialTheme.typography.titleSmall, color = TextoClaro, modifier = Modifier.padding(top = 4.dp))
                    Text(
                        descricaoPoderDaOrigem(o.nome, o.poder),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextoFraco,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                    TextButton(onClick = { detalhesOrigem = true }) { Text("Ver detalhes") }
                    if (detalhesOrigem) {
                        AlertDialog(
                            onDismissRequest = { detalhesOrigem = false },
                            title = { Text(o.poder) },
                            text = {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("Origem: ${o.nome}", color = Acento)
                                    Text("Perícias treinadas: ${o.pericias}")
                                    Text(descricaoPoderDaOrigem(o.nome, o.poder), color = TextoClaro)
                                }
                            },
                            confirmButton = { TextButton(onClick = { detalhesOrigem = false }) { Text("Fechar") } },
                        )
                    }
                }
            }
        }

        item {
            Painel(titulo = "Atributos") {
                ATRIBUTOS.chunked(3).forEach { linha ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        linha.forEach { a ->
                            Campo(a.sigla, Modifier.weight(1f)) {
                                Numero(p.atributos[a.key] ?: 1) {
                                    upd(p.copy(atributos = p.atributos + (a.key to it)))
                                }
                            }
                        }
                        repeat(3 - linha.size) { androidx.compose.foundation.layout.Spacer(Modifier.weight(1f)) }
                    }
                }
            }
        }

        item {
            val calc = calcularFicha(p)
            Painel(titulo = "Vitalidade e cálculos automáticos") {
                Text("Os valores derivados acompanham atributos, classe e NEX. Edite qualquer valor para criar um ajuste manual; use Auto para voltar ao cálculo.", color = TextoFraco, style = MaterialTheme.typography.bodySmall)
                BarraRecurso("Pontos de Vida", p.pvAtual.coerceAtMost(calc.pvMax), calc.pvMax, Primaria,
                    { upd(p.copy(pvAtual = it.coerceAtMost(calc.pvMax))) },
                    { upd(p.copy(pvMax = it, overrides = p.overrides + ("pvMax" to it))) })
                Chip(if ("pvMax" in p.overrides) "PV máximo: Manual" else "PV máximo: Auto", "pvMax" in p.overrides,
                    { upd(p.copy(overrides = p.overrides - "pvMax")) }, if ("pvMax" in p.overrides) Acento else Primaria)
                BarraRecurso("Pontos de Esforço", p.peAtual.coerceAtMost(calc.peMax), calc.peMax, Acento,
                    { upd(p.copy(peAtual = it.coerceAtMost(calc.peMax))) },
                    { upd(p.copy(peMax = it, overrides = p.overrides + ("peMax" to it))) })
                Chip(if ("peMax" in p.overrides) "PE máximo: Manual" else "PE máximo: Auto", "peMax" in p.overrides,
                    { upd(p.copy(overrides = p.overrides - "peMax")) }, if ("peMax" in p.overrides) Acento else Primaria)
                BarraRecurso("Sanidade", p.sanAtual.coerceAtMost(calc.sanMax), calc.sanMax, CorEnergia,
                    { upd(p.copy(sanAtual = it.coerceAtMost(calc.sanMax))) },
                    { upd(p.copy(sanMax = it, overrides = p.overrides + ("sanMax" to it))) })
                Chip(if ("sanMax" in p.overrides) "Sanidade máxima: Manual" else "Sanidade máxima: Auto", "sanMax" in p.overrides,
                    { upd(p.copy(overrides = p.overrides - "sanMax")) }, if ("sanMax" in p.overrides) Acento else Primaria)
                val perturbadoAuto = p.sanAtual * 2 < calc.sanMax
                val enlouquecendoAuto = p.sanAtual <= 0
                Text("Estado de SAN: ${when { p.insano -> "Insano"; enlouquecendoAuto || p.enlouquecendo -> "Enlouquecendo · ${p.turnosEnlouquecendo} turno(s)"; perturbadoAuto || p.perturbado -> "Perturbado"; else -> "estável" }}", color = if (p.insano || enlouquecendoAuto) Perigo else Acento, style = MaterialTheme.typography.labelSmall)

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Campo("Defesa", Modifier.weight(1f)) {
                        Column {
                            Numero(calc.defesa) { upd(p.copy(defesa = it, overrides = p.overrides + ("defesa" to it))) }
                            Chip(if ("defesa" in p.overrides) "Manual" else "Auto", "defesa" in p.overrides,
                                { upd(p.copy(overrides = p.overrides - "defesa")) })
                        }
                    }
                    Campo("Desloc. (m)", Modifier.weight(1f)) {
                        Column {
                            Numero(calc.deslocamento) { upd(p.copy(deslocamento = it, overrides = p.overrides + ("deslocamento" to it))) }
                            Chip(if ("deslocamento" in p.overrides) "Manual" else "Auto", "deslocamento" in p.overrides,
                                { upd(p.copy(overrides = p.overrides - "deslocamento")) })
                        }
                    }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Campo("Defesa equip.", Modifier.weight(1f)) { Numero(p.defesaEquipamento) { upd(p.copy(defesaEquipamento = it)) } }
                    Campo("Defesa outros", Modifier.weight(1f)) { Numero(p.defesaOutros) { upd(p.copy(defesaOutros = it)) } }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Campo("Bloqueio", Modifier.weight(1f)) {
                        if (isMestre) {
                            Numero(calc.bloqueio) { upd(p.copy(overrides = p.overrides + ("bloqueio" to it))) }
                            Chip(if ("bloqueio" in p.overrides) "Manual · Mestre" else "Auto · Mestre", "bloqueio" in p.overrides, { upd(p.copy(overrides = p.overrides - "bloqueio")) })
                        } else {
                            Text("${calc.bloqueio}", color = TextoClaro)
                            Text("Automático · Fortitude", color = TextoFraco, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                    Campo("RD geral", Modifier.weight(1f)) {
                        if (isMestre) {
                            Numero(calc.rdGeral) { upd(p.copy(overrides = p.overrides + ("rdGeral" to it.coerceAtLeast(0)))) }
                            Chip(if ("rdGeral" in p.overrides) "Manual · Mestre" else "Auto · Mestre", "rdGeral" in p.overrides, { upd(p.copy(overrides = p.overrides - "rdGeral")) })
                        } else {
                            Text("${calc.rdGeral}", color = TextoClaro)
                            Text("Automático · universal", color = TextoFraco, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Campo("DT de Rituais", Modifier.weight(1f)) {
                        if (isMestre) {
                            Numero(calc.dtRituais) { upd(p.copy(overrides = p.overrides + ("dtRituais" to it.coerceAtLeast(0)))) }
                            Chip(if ("dtRituais" in p.overrides) "Manual · Mestre" else "Auto · Mestre", "dtRituais" in p.overrides, { upd(p.copy(overrides = p.overrides - "dtRituais")) })
                        } else Text("${calc.dtRituais}", color = TextoClaro)
                    }
                    Campo("DT de Habilidades", Modifier.weight(1f)) {
                        if (isMestre) {
                            Numero(calc.dtHabilidades) { upd(p.copy(overrides = p.overrides + ("dtHabilidades" to it.coerceAtLeast(0)))) }
                            Chip(if ("dtHabilidades" in p.overrides) "Manual · Mestre" else "Auto · Mestre", "dtHabilidades" in p.overrides, { upd(p.copy(overrides = p.overrides - "dtHabilidades")) })
                        } else Text("${calc.dtHabilidades}", color = TextoClaro)
                    }
                }
                val limite = calc.limiteCarga
                Text(
                    "Carga: ${espacosUsados(p)} / ${calc.limiteCarga} espaços · máximo absoluto ${calc.cargaMaxima}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (espacosUsados(p) > calc.limiteCarga) Perigo else TextoFraco,
                )
                if (calc.cargaSobrecarregada) {
                    Text("⚠ Sobrecarga: –5 Defesa, –5 em testes de perícias afetadas por carga e –3m de deslocamento. Não é permitido ultrapassar ${calc.cargaMaxima} espaços.", color = Perigo, style = MaterialTheme.typography.bodySmall)
                }
                if (p.classe == "Especialista" && p.trilha == "Técnico") Text("Técnico — Inventário Otimizado: INT somado à FOR para capacidade de carga.", color = Acento, style = MaterialTheme.typography.labelSmall)
                val limitesCat = limitesItensPorCategoria(p.prestigio)
                val contagemCat = p.itens.groupingBy { it.categoria }.eachCount() + p.armas.groupingBy { it.categoria }.eachCount()
                Text("Limite de itens por categoria (Prestígio): I ${limitesCat[1] ?: 0} · II ${limitesCat[2] ?: 0} · III ${limitesCat[3] ?: 0} · IV ${limitesCat[4] ?: 0}", color = TextoFraco, style = MaterialTheme.typography.labelSmall)
                val excedeuCat = contagemCat.any { (cat, qtd) -> cat in 1..4 && qtd > (limitesCat[cat] ?: 0) }
                if (excedeuCat) Text("⚠ Inventário acima do limite de categoria da patente atual. O Mestre pode manter itens já adquiridos, mas novas aquisições devem respeitar o limite.", color = Perigo, style = MaterialTheme.typography.labelSmall)
                val efeitosCond = com.arquivoparanormal.app.data.efeitosDasCondicoes(p.condicoes)
                if (p.condicoes.isNotEmpty()) Text("Efeitos mecânicos das condições: ${efeitosCond.penalidadeTestes} em testes · ${efeitosCond.penalidadeAtaqueDados} dado(s) em ataques · ações bloqueadas: ${if (efeitosCond.bloqueiaAcoes) "sim" else "não"}.", color = Acento, style = MaterialTheme.typography.labelSmall)
                Text("Perícias de origem automáticas: ${p.periciasAutomaticas.joinToString(", ").ifBlank { "nenhuma" }}", color = TextoFraco, style = MaterialTheme.typography.bodySmall)
                ORIGENS_COMPLETAS.firstOrNull { it.nome == p.origem }?.let { Text("Habilidade da origem: ${it.poder}", color = Acento, style = MaterialTheme.typography.bodySmall) }
                CLASSES.firstOrNull { it.nome == p.classe }?.let { Text("Classe: ${it.desc}", color = TextoFraco, style = MaterialTheme.typography.bodySmall) }
                if (p.trilha.isNotBlank()) {
                    Text("Trilha selecionada: ${p.trilha}", color = Acento, style = MaterialTheme.typography.bodySmall)
                    Text(DESCRICOES_TRILHAS[p.trilha] ?: "Consulte o Compêndio para detalhes da trilha.", color = TextoFraco, style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        item {
            Painel(titulo = "Conexão e Afinidade Elemental") {
                Text(
                    "A conexão é escolhida a partir de NEX 50% e não pode ser alterada. Medo não pode ser escolhido como afinidade. A afinidade efetiva surge quando você transcende novamente após a conexão.",
                    style = MaterialTheme.typography.bodySmall, color = TextoFraco
                )
                if (p.nex < 50) {
                    Text("Disponível a partir de NEX 50%.", color = Acento)
                } else {
                    Text("Conexão: ${p.elementoConexao.ifBlank { "não escolhida" }}", color = TextoClaro)
                    if (p.elementoConexao.isBlank()) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            ELEMENTOS_AFINIDADE.forEach { e ->
                                Chip(e, false, { upd(p.copy(elementoConexao = e)) }, corElemento(e))
                            }
                        }
                    }
                    if (p.elementoConexao.isNotBlank()) {
                        val atual = p.elementoConexao
                        val opressor = elementoOpressor(atual)
                        Text("Afinidade efetiva: ${p.afinidade.ifBlank { "ainda não desenvolvida" }}", color = Acento)
                        if (p.afinidade.isBlank()) {
                            Button(onClick = { upd(p.copy(afinidade = atual)) }, colors = ButtonDefaults.buttonColors(containerColor = Primaria)) {
                                Text("Desenvolver afinidade com $atual")
                            }
                        }
                        if (opressor != null) Text("Elemento opressor: $opressor", color = TextoFraco, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        item {
            Painel(titulo = "Estados e condições") {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    CONDICOES.chunked(3).forEach { linha ->
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            linha.forEach { c ->
                                Chip(c.nome, p.condicoes.contains(c.nome), {
                                    upd(p.copy(condicoes = if (p.condicoes.contains(c.nome))
                                        p.condicoes - c.nome else p.condicoes + c.nome))
                                }, if (c.grave) Perigo else Primaria)
                            }
                        }
                    }
                    p.condicoes.mapNotNull { nome -> CONDICOES.firstOrNull { it.nome == nome } }.forEach { c ->
                        Painel(titulo = "${c.nome}${if (c.grave) " · grave" else ""}", modifier = Modifier.fillMaxWidth()) {
                            Text(c.desc, color = TextoClaro, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }

        item {
            Painel(titulo = "Perícias") {
                Text(
                    TREINAMENTOS.joinToString(" · ") { "${it.nome} +${it.bonus}" },
                    style = MaterialTheme.typography.bodySmall,
                    color = TextoFraco,
                )
                PERICIAS.forEach { def ->
                    val per = p.pericias[def.nome] ?: Pericia()
                    val total = bonusPericiaComAtributo(p, def)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("${def.nome} (${def.attr.uppercase()})",
                                style = MaterialTheme.typography.bodyMedium, color = TextoClaro)
                            val dados = dadosDoAtributo(p, def)
                            val pior = usaPiorDadoDoAtributo(p, def)
                            Text("${dados}d20${if (pior) " (pior)" else ""} ${formatBonus(total)}${if (def.nome in p.periciasAutomaticas) " · origem" else ""}",
                                style = MaterialTheme.typography.bodySmall, color = Acento)
                        }
                        Text(
                            if (per.treino > 0) {
                                "Atributo ${formatBonus(p.atributos[def.attr] ?: 1)} · Treinamento ${formatBonus(per.treino)} · Outros ${formatBonus(per.outros)}"
                            } else {
                                "Destreinada · bônus de teste: ${formatBonus(per.outros)} · atributo não entra no bônus enquanto não estiver treinada"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = TextoFraco,
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                            TREINAMENTOS.forEach { t ->
                                val automatica = def.nome in p.periciasAutomaticas
                                Chip(t.nome.take(4), per.treino == t.bonus, {
                                    if (!automatica) upd(p.copy(pericias = p.pericias + (def.nome to per.copy(treino = t.bonus))))
                                }, enabled = !automatica)
                            }
                            if (per.treino > 0 && def.nome !in p.periciasAutomaticas) {
                                TextButton(onClick = {
                                    upd(p.copy(pericias = p.pericias + (def.nome to per.copy(treino = 0))))
                                }) { Text("Trocar") }
                            }
                        }
                    }
                }
            }
        }

        item {
            val calc = calcularFicha(p)
            Painel(titulo = "Resistências a dano") {
                var mostrarFontesRD by remember { mutableStateOf(false) }
                var tipoFontesRD by remember { mutableStateOf("Geral") }
                Text(if (isMestre) "Os valores são automáticos. O Mestre pode sobrescrever qualquer RD desta ficha; o jogador não pode alterar esses valores." else "Os valores são calculados automaticamente. RD geral é universal; RD física vale para balístico, corte, impacto e perfuração; RD específica vale apenas para seu tipo.", color = TextoFraco, style = MaterialTheme.typography.bodySmall)
                val fontesGeral = fontesRdGeral(p)
                Campo("RD geral") {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (isMestre) {
                            Numero(calc.rdGeral) { upd(p.copy(overrides = p.overrides + ("rdGeral" to it.coerceAtLeast(0)))) }
                            TextButton(onClick = { tipoFontesRD = "Geral"; mostrarFontesRD = true }) { Text("Origem") }
                        } else {
                            Column(Modifier.weight(1f)) {
                                Text("${calc.rdGeral}", color = if (calc.rdGeral > 0) Acento else TextoClaro, style = MaterialTheme.typography.titleLarge)
                                Text("Aplica-se a qualquer tipo de dano", color = TextoFraco, style = MaterialTheme.typography.labelSmall)
                            }
                            TextButton(onClick = { tipoFontesRD = "Geral"; mostrarFontesRD = true }) { Text("Ver origem") }
                        }
                    }
                    if (isMestre) {
                        Chip(if ("rdGeral" in p.overrides) "Manual · Mestre" else "Auto · Mestre", "rdGeral" in p.overrides, { upd(p.copy(overrides = p.overrides - "rdGeral")) })
                    }
                }
                val gruposRD = listOf(
                    "Físicas" to listOf("Balístico", "Corte", "Impacto", "Perfuração"),
                    "Elementais" to listOf("Eletricidade", "Fogo", "Frio", "Químico"),
                    "Mentais / Paranormais" to listOf("Mental", "Paranormal"),
                    "Elementos Paranormais" to listOf("Sangue", "Morte", "Conhecimento", "Energia", "Medo"),
                )
                gruposRD.forEach { (tituloGrupo, tipos) ->
                    Text(tituloGrupo, color = TextoFraco, style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(top = 6.dp, bottom = 2.dp))
                    tipos.chunked(2).forEach { linha ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            linha.forEach { r ->
                                val total = resistenciaTotal(p, r)
                                val fontes = fontesResistencia(p, r)
                                val manualKey = "rd:$r"
                                Campo("RD $r", Modifier.weight(1f)) {
                                    if (isMestre) {
                                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Numero(total) { upd(p.copy(overrides = p.overrides + (manualKey to it.coerceAtLeast(0)))) }
                                            TextButton(onClick = { tipoFontesRD = r; mostrarFontesRD = true }) { Text("Origem") }
                                        }
                                        Text(if (manualKey in p.overrides) "Manual · Mestre" else "Auto", color = if (manualKey in p.overrides) Acento else TextoFraco, style = MaterialTheme.typography.labelSmall)
                                        if (manualKey in p.overrides) {
                                            TextButton(onClick = { upd(p.copy(overrides = p.overrides - manualKey)) }) { Text("Voltar ao automático") }
                                        }
                                    } else if (total > 0 || fontes.isNotEmpty()) {
                                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("$total", color = if (total > 0) Acento else TextoClaro, style = MaterialTheme.typography.titleMedium)
                                            TextButton(onClick = { tipoFontesRD = r; mostrarFontesRD = true }) { Text("Detalhes") }
                                        }
                                    } else {
                                        Text("0", color = TextoFraco, style = MaterialTheme.typography.titleMedium)
                                    }
                                }
                            }
                            repeat(2 - linha.size) { Spacer(Modifier.weight(1f)) }
                        }
                    }
                }
                if (mostrarFontesRD) {
                    val tipo = tipoFontesRD
                    val fontesBase = if (tipo == "Geral") fontesGeral else fontesResistencia(p, tipo)
                    val manual = if (tipo == "Geral") p.overrides["rdGeral"] else p.overrides["rd:$tipo"]
                    val fontes = if (manual != null) fontesBase + ("Ajuste manual do Mestre" to manual) else fontesBase
                    AlertDialog(
                        onDismissRequest = { mostrarFontesRD = false },
                        title = { Text("Origem das resistências") },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("RD ${if (tipo == "Geral") "geral" else tipo}: ${if (tipo == "Geral") calc.rdGeral else resistenciaTotal(p, tipo)}", color = Acento)
                                if (fontes.isEmpty()) Text("Nenhuma fonte automática concede esta resistência.", color = TextoFraco)
                                fontes.forEach { (nome, valor) -> Text("• $nome: +$valor", color = TextoClaro) }
                            }
                        },
                        confirmButton = { TextButton(onClick = { mostrarFontesRD = false }) { Text("Fechar") } },
                    )
                }
                var tipoDanoCalc by remember { mutableStateOf("Balístico") }
                var danoBruto by remember { mutableStateOf(0) }
                var elementoAlvo by remember { mutableStateOf("") }
                val rdCalc = resistenciaTotal(p, tipoDanoCalc)
                val danoFinal = (danoBruto - rdCalc).coerceAtLeast(0)
                Painel(titulo = "Calcular dano após resistência", modifier = Modifier.fillMaxWidth()) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Campo("Tipo", Modifier.weight(1f)) { Selecao(tipoDanoCalc, RESISTENCIAS, { tipoDanoCalc = it }) }
                        Campo("Dano bruto", Modifier.weight(1f)) { Numero(danoBruto) { danoBruto = it.coerceAtLeast(0) } }
                    }
                    Campo("Elemento do alvo (opcional)") {
                        Selecao(elementoAlvo.ifBlank { "Nenhum" }, listOf("Nenhum") + ELEMENTOS_AFINIDADE + listOf("Medo"), { elementoAlvo = if (it == "Nenhum") "" else it })
                    }
                    if (elementoAlvo.isNotBlank()) {
                        val elementoAtaque = when (tipoDanoCalc) { "Sangue", "Morte", "Conhecimento", "Energia", "Medo" -> tipoDanoCalc else -> "" }
                        if (elementoAtaque.isNotBlank()) {
                            Text("Relação: ${relacaoEntreElementos(elementoAtaque, elementoAlvo)}", color = Acento, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                    Text("RD total: $rdCalc", color = Acento)
                    Text("Origem: ${fontesResistencia(p, tipoDanoCalc).joinToString(" · ") { "${it.first} +${it.second}" }.ifBlank { "nenhuma" }}", color = TextoFraco, style = MaterialTheme.typography.labelSmall)
                    Text("Dano final: $danoFinal", color = if (danoFinal > 0) Perigo else Acento, style = MaterialTheme.typography.titleMedium)
                    Text("Relações elementais alteram testes de resistência quando aplicáveis; dano só é dobrado se o alvo tiver vulnerabilidade específica.", color = TextoFraco, style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        item {
            var armaLivroSelecionada by remember { mutableStateOf("") }
            val armaCatalogo = ARMAS_LIVRO.firstOrNull {
                "${it.icone} ${it.nome} — Cat. ${it.categoria} · ${it.grupo}" == armaLivroSelecionada
            }

            Painel(
                titulo = "Armas",
                acao = {
                    IconButton(onClick = { upd(p.copy(armas = p.armas + Arma(nome = "Nova arma"))) }) {
                        Icon(Icons.Default.Add, contentDescription = "Nova arma", tint = Primaria)
                    }
                },
            ) {
                Text(
                    "Adicionar arma do livro · Capítulo 3",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextoFraco,
                )
                Selecao(
                    valor = armaLivroSelecionada,
                    opcoes = ARMAS_LIVRO.map { "${it.icone} ${it.nome} — Cat. ${it.categoria} · ${it.grupo}" },
                    aoMudar = { armaLivroSelecionada = it },
                    placeholder = "Selecione uma arma do PDF",
                )

                armaCatalogo?.let { def ->
                    Painel(
                        titulo = "${def.icone} ${def.nome}",
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Chip("Categoria ${def.categoria}", true, {}, Primaria)
                            Chip(def.grupo.substringBefore(" ·"), true, {}, Primaria)
                            Chip("${def.tipoDano}", true, {}, Primaria)
                        }
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Campo("Dano", Modifier.weight(1f)) { Text(def.dano, color = TextoClaro) }
                            Campo("Crítico", Modifier.weight(1f)) { Text(def.critico, color = TextoClaro) }
                            Campo("Alcance", Modifier.weight(1f)) { Text(def.alcance, color = TextoClaro) }
                        }
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Campo("Tipo", Modifier.weight(1f)) { Text(def.tipoDano, color = TextoClaro) }
                            Campo("Espaços", Modifier.weight(1f)) { Text(def.espacos.toString().removeSuffix(".0"), color = TextoClaro) }
                            Campo("Perícia", Modifier.weight(1f)) { Text(def.pericia, color = TextoClaro) }
                        }
                        if (def.municao != "—") {
                            val mun = MUNICOES_LIVRO.firstOrNull { it.nome == def.municao }
                            Campo("Munição") {
                                Text(
                                    if (mun != null) "${mun.nome} · pacote: ${mun.duracao}" else def.municao,
                                    color = TextoClaro,
                                )
                            }
                            mun?.let {
                                Text(it.descricao, color = TextoFraco, style = MaterialTheme.typography.bodySmall)
                            }
                        } else {
                            Campo("Munição") { Text("Não utiliza munição", color = TextoFraco) }
                        }
                        Campo("Descrição") {
                            Text(def.descricao, color = TextoClaro, style = MaterialTheme.typography.bodyMedium, maxLines = 5)
                        }
                        Button(
                            enabled = true,
                            onClick = {
                                upd(
                                    p.copy(
                                        armas = p.armas + Arma(
                                            nome = def.nome,
                                            tipo = def.grupo.substringAfter(" · ", def.grupo),
                                            grupo = def.grupo,
                                            pericia = def.pericia,
                                            dano = def.dano,
                                            critico = def.critico,
                                            alcance = def.alcance,
                                            tipoDano = def.tipoDano,
                                            categoria = def.categoria,
                                            espacos = def.espacos,
                                            icone = def.icone,
                                            municao = def.municao,
                                            pacotesMunicao = if (def.municao == "—") 0 else 1,
                                            descricao = def.descricao,
                                        )
                                    )
                                )
                                armaLivroSelecionada = ""
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Primaria),
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Text("  Adicionar arma")
                        }
                    }
                }

                p.armas.forEach { a ->
                    val set: (Arma) -> Unit = { nova -> upd(p.copy(armas = p.armas.map { if (it.id == a.id) nova else it })) }
                    Row(Modifier.fillMaxWidth(), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        Text(a.icone.ifBlank { "⚔" }, style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(end = 8.dp))
                        Campo("Nome", Modifier.weight(1f)) { CampoTexto(a.nome) { set(a.copy(nome = it)) } }
                    }
                    Campo("Grupo") { CampoTexto(a.grupo) { set(a.copy(grupo = it)) } }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Campo("Dano", Modifier.weight(1f)) { CampoTexto(a.dano) { set(a.copy(dano = it)) } }
                        Campo("Crítico", Modifier.weight(1f)) { CampoTexto(a.critico) { set(a.copy(critico = it)) } }
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Campo("Teste de ataque", Modifier.weight(1f)) { Text(testeAtaqueDaArma(p, a), color = Acento) }
                        Campo("Margem crítica", Modifier.weight(1f)) { Text(margemCritico(a.critico), color = Acento) }
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        var resultadoAtaque by remember(a.id) { mutableStateOf<String?>(null) }
                        Button(
                            onClick = {
                                val defPericia = PERICIAS.firstOrNull { it.nome.equals(a.pericia, ignoreCase = true) }
                                val atributo = defPericia?.let { p.atributos[it.attr] ?: 1 } ?: 1
                                val dados = if (atributo <= 0) 2 else atributo
                                val rolagens = List(dados) { Random.nextInt(1, 21) }
                                val escolhido = if (atributo <= 0) rolagens.minOrNull() ?: 1 else rolagens.maxOrNull() ?: 1
                                val bonus = ataqueDaArma(p, a)
                                val total = escolhido + bonus
                                resultadoAtaque = "Rolagem: ${rolagens.joinToString(" + ")} ${if (atributo <= 0) "(pior)" else "(melhor)"} ${formatBonus(bonus)} = $total"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Primaria),
                            modifier = Modifier.weight(1f),
                        ) { Text("🎯 ATACAR") }
                        Button(
                            onClick = {
                                resultadoAtaque = "Dano: ${rolarDano(a.dano)}"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CorEnergia),
                            modifier = Modifier.weight(1f),
                        ) { Text("💥 DANO") }
                        resultadoAtaque?.let { resultado ->
                            AlertDialog(
                                onDismissRequest = { resultadoAtaque = null },
                                title = { Text(a.nome.ifBlank { "Ataque" }) },
                                text = { Text(resultado) },
                                confirmButton = { TextButton(onClick = { resultadoAtaque = null }) { Text("OK") } },
                            )
                        }
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Campo("Alcance", Modifier.weight(1f)) { CampoTexto(a.alcance) { set(a.copy(alcance = it)) } }
                        Campo("Tipo de dano", Modifier.weight(1f)) { CampoTexto(a.tipoDano) { set(a.copy(tipoDano = it)) } }
                        Campo("Perícia", Modifier.weight(1f)) { Selecao(a.pericia, listOf("Luta", "Pontaria"), { set(a.copy(pericia = it)) }) }
                    }
                    Campo("Categoria") {
                        Selecao(
                            CATEGORIAS_ITEM.firstOrNull { it.cat == a.categoria }?.label ?: "Categoria ${a.categoria}",
                            CATEGORIAS_ITEM.map { it.label },
                            { label ->
                                val c = CATEGORIAS_ITEM.first { it.label == label }
                                set(a.copy(categoria = c.cat, espacos = c.espacos))
                            },
                        )
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Campo("Espaços", Modifier.weight(1f)) { Numero(a.espacos.toInt()) { set(a.copy(espacos = it.toDouble())) } }
                        Campo("Ícone", Modifier.weight(1f)) { CampoTexto(a.icone) { set(a.copy(icone = it)) } }
                    }
                    Campo("Munição") {
                        Selecao(
                            a.municao,
                            listOf("—") + MUNICOES_LIVRO.map { it.nome },
                            { nova -> set(a.copy(municao = nova, pacotesMunicao = if (nova == "—") 0 else maxOf(1, a.pacotesMunicao))) },
                            placeholder = "Selecione a munição",
                        )
                    }
                    if (a.municao != "—") {
                        val mun = MUNICOES_LIVRO.firstOrNull { it.nome == a.municao }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Campo("Pacotes", Modifier.weight(1f)) { Numero(a.pacotesMunicao) { set(a.copy(pacotesMunicao = it.coerceAtLeast(0))) } }
                            Campo("Duração", Modifier.weight(1f)) { Text(mun?.duracao ?: "—", color = TextoClaro) }
                        }
                        mun?.let { Text(it.descricao, color = TextoFraco, style = MaterialTheme.typography.bodySmall) }
                    }
                    Campo("Descrição") { AreaTexto(a.descricao, { set(a.copy(descricao = it)) }, linhas = 2) }
                    Campo("Observações") { AreaTexto(a.obs, { set(a.copy(obs = it)) }, linhas = 2) }
                    Button(
                        onClick = { upd(p.copy(armas = p.armas.filterNot { it.id == a.id })) },
                        colors = ButtonDefaults.buttonColors(containerColor = Perigo),
                    ) { Icon(Icons.Default.Delete, contentDescription = null); Text("  Remover arma") }
                }
                if (p.armas.isEmpty()) Text("Nenhuma arma registrada.", color = TextoFraco, style = MaterialTheme.typography.bodySmall)
            }
        }

        item {
            Painel(
                titulo = "Inventário",
                acao = {
                    IconButton(onClick = { upd(p.copy(itens = p.itens + Item(nome = "Novo item"))) }) {
                        Icon(Icons.Default.Add, contentDescription = "Novo item", tint = Primaria)
                    }
                },
            ) {
                val carga = espacosUsados(p)
                val limite = calcularFicha(p).limiteCarga.toDouble()
                val cargaExcedida = carga > limite
                var categoriaInventario by remember { mutableStateOf("Todos") }
                val categoriasInventario = listOf("Todos", "Armas", "Proteções", "Itens", "Munições", "Itens amaldiçoados", "Outros")
                Text(
                    "Carga: ${String.format(java.util.Locale.US, "%.1f", carga)} / ${String.format(java.util.Locale.US, "%.1f", limite)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (cargaExcedida) Perigo else Acento,
                )
                if (cargaExcedida) {
                    Text("⚠️ Carga excedida — reduza o inventário ou aumente a Força.", color = Perigo)
                }
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                    items(categoriasInventario) { categoria ->
                        Chip(categoria, categoriaInventario == categoria, { categoriaInventario = categoria })
                    }
                }
                if (categoriaInventario == "Armas") {
                    p.armas.forEach { arma ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("${arma.icone} ${arma.nome}", color = TextoClaro)
                            Text("${arma.espacos} espaços", color = TextoFraco)
                        }
                    }
                    if (p.armas.isEmpty()) Text("Nenhuma arma registrada.", color = TextoFraco)
                }
                var protecaoSelecionada by remember { mutableStateOf("") }
                val protecaoDef = PROTECOES_LIVRO.firstOrNull { it.nome == protecaoSelecionada }
                Text("Adicionar proteção do livro", style = MaterialTheme.typography.labelSmall, color = TextoFraco)
                Selecao(protecaoSelecionada, PROTECOES_LIVRO.map { it.nome }, { protecaoSelecionada = it }, placeholder = "Selecione uma proteção")
                protecaoDef?.let { def ->
                    Button(onClick = {
                        val resist = if (def.resistenciaGeral > 0) mapOf("Fisico" to def.resistenciaGeral) else emptyMap()
                        upd(p.copy(itens = p.itens + Item(nome = def.nome, categoria = def.categoria, espacos = def.espacos, tipoInventario = "Proteções", tipoEquipamento = "Proteção", equipado = true, defesaBonus = def.defesaBonus, resistencias = resist)))
                        protecaoSelecionada = ""
                    }, colors = ButtonDefaults.buttonColors(containerColor = Primaria)) {
                        Text("Adicionar e equipar")
                    }
                }

                var itemCatalogoSelecionado by remember { mutableStateOf("") }
                val itemCatalogado = ITENS_ARQUIVOS_SECRETOS.firstOrNull { "${it.nome} — ${it.tipo}" == itemCatalogoSelecionado }
                Text("Adicionar item de Arquivos Secretos", style = MaterialTheme.typography.labelSmall, color = TextoFraco)
                Selecao(itemCatalogoSelecionado, ITENS_ARQUIVOS_SECRETOS.map { "${it.nome} — ${it.tipo}" }, { itemCatalogoSelecionado = it }, placeholder = "Selecione um item")
                itemCatalogado?.let { def ->
                    Button(onClick = {
                        upd(p.copy(itens = p.itens + Item(nome = def.nome, categoria = 1, espacos = 1.0, desc = "${def.tipo} — ${def.livro.titulo}, pág. ${def.pagina}")))
                        itemCatalogoSelecionado = ""
                    }, colors = ButtonDefaults.buttonColors(containerColor = Primaria)) {
                        Icon(Icons.Default.Add, contentDescription = null); Text("  Adicionar item")
                    }
                }


                // Catálogo oficial de itens amaldiçoados especiais: somente itens realmente
                // cadastrados aqui aparecem como opção de aquisição. Itens únicos são bloqueados
                // quando já existem na ficha, salvo edição pelo Mestre.
                var itemAmaldicoadoSelecionado by remember { mutableStateOf("") }
                val catalogoAmaldicoado = ITENS_AMALDICOADOS_LIVRO.filter { def ->
                    isMestre || patentePermiteItemAmaldicoado(p.patente)
                }
                Text("Adicionar item amaldiçoado catalogado", style = MaterialTheme.typography.labelSmall, color = TextoFraco)
                Selecao(
                    itemAmaldicoadoSelecionado,
                    catalogoAmaldicoado.map { "${it.nome} — ${it.elemento.ifBlank { "Varia" }}" },
                    { itemAmaldicoadoSelecionado = it },
                    placeholder = if (patentePermiteItemAmaldicoado(p.patente) || isMestre) "Selecione um item catalogado" else "Requer Agente Especial ou patente superior",
                )
                val itemAmaldicoadoDef = catalogoAmaldicoado.firstOrNull { "${it.nome} — ${it.elemento.ifBlank { "Varia" }}" == itemAmaldicoadoSelecionado }
                itemAmaldicoadoDef?.let { def ->
                    val jaExiste = p.itens.any { it.nome == def.nome } || p.armas.any { it.nome == def.nome }
                    if (def.unico && jaExiste) {
                        Text("Este é um item único e já está na ficha.", color = Perigo, style = MaterialTheme.typography.labelSmall)
                    }
                    Text(def.descricao, color = TextoFraco, style = MaterialTheme.typography.bodySmall)
                    Button(
                        enabled = !jaExiste || !def.unico,
                        onClick = {
                            val novo = Item(
                                nome = def.nome,
                                categoria = def.categoria,
                                espacos = def.espacos,
                                tipoInventario = def.tipoInventario,
                                tipoEquipamento = if (def.tipoInventario == "Armas") "Outro" else "Outro",
                                equipado = false,
                                desc = "${def.descricao} — ${def.livro}, pág. ${def.pagina}",
                                resistencias = def.resistencias,
                                vulnerabilidades = def.vulnerabilidades,
                            )
                            upd(p.copy(itens = p.itens + novo))
                            itemAmaldicoadoSelecionado = ""
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Primaria),
                    ) { Icon(Icons.Default.Add, contentDescription = null); Text("  Adicionar ao catálogo da ficha") }
                }
                if (!patentePermiteItemAmaldicoado(p.patente) && !isMestre) {
                    Text("Itens amaldiçoados são liberados pelo livro apenas para Agente Especial, Oficial de Operações e Agente de Elite.", color = TextoFraco, style = MaterialTheme.typography.labelSmall)
                }

                // Maldições são modificações, não itens independentes.
                // Pela regra do livro, armas, proteções e acessórios amaldiçoados
                // só são disponibilizados para Agente Especial, Oficial de Operações
                // e Agente de Elite. Mestre pode administrar a ficha sem essa trava.
                val podeAplicarMaldicao = isMestre || patentePermiteItemAmaldicoado(p.patente)
                if (podeAplicarMaldicao) {
                    var alvoMaldicao by remember { mutableStateOf("") }
                    var maldicaoSelecionada by remember { mutableStateOf("") }
                    var elementoMaldicao by remember { mutableStateOf("") }

                    val alvosProtecoes = p.itens.filter { it.tipoInventario == "Proteções" || it.tipoEquipamento == "Proteção" }
                    val alvosAcessorios = p.itens.filter { it.tipoEquipamento == "Acessório" && it.tipoInventario != "Proteções" }
                    val alvosArmas = p.armas
                    val opcoesAlvo =
                        alvosArmas.map { "Arma: ${it.nome}" } +
                        alvosProtecoes.map { "Proteção: ${it.nome}" } +
                        alvosAcessorios.map { "Acessório: ${it.nome}" }

                    Text("Aplicar maldição catalogada", style = MaterialTheme.typography.labelSmall, color = TextoFraco)
                    Text("Maldições não podem ser aplicadas livremente a qualquer item: apenas armas, proteções e acessórios. A primeira maldição aumenta a categoria em II; as seguintes, em I.", color = TextoFraco, style = MaterialTheme.typography.bodySmall)
                    Selecao(alvoMaldicao, opcoesAlvo, { alvoMaldicao = it; maldicaoSelecionada = "" }, placeholder = "Escolha arma, proteção ou acessório")
                    val tipoAlvoMaldicao = alvoMaldicao.substringBefore(": ")
                    val nomeAlvoMaldicao = alvoMaldicao.substringAfter(": ", "")
                    val maldicoesElegiveis = MALDICOES_CATALOGO_COMPLETO.filter { def ->
                        (tipoAlvoMaldicao == "Arma" && def.tipoAplicavel == "Arma") ||
                            (tipoAlvoMaldicao == "Proteção" && def.tipoAplicavel == "Proteção") ||
                            (tipoAlvoMaldicao == "Acessório" && def.tipoAplicavel == "Acessório")
                    }
                    Selecao(maldicaoSelecionada, maldicoesElegiveis.map { "${it.nome} — ${it.tipoAplicavel} · ${it.elemento}" }, { maldicaoSelecionada = it }, placeholder = "Escolha uma maldição")
                    val maldicaoDef = maldicoesElegiveis.firstOrNull { "${it.nome} — ${it.tipoAplicavel} · ${it.elemento}" == maldicaoSelecionada }
                    maldicaoDef?.let { def ->
                        val alvoProtecao = alvosProtecoes.firstOrNull { it.nome == nomeAlvoMaldicao }
                        val alvoAcessorio = alvosAcessorios.firstOrNull { it.nome == nomeAlvoMaldicao }
                        val alvoArma = alvosArmas.firstOrNull { it.nome == nomeAlvoMaldicao }
                        val maldicoesExistentes = when {
                            alvoProtecao != null -> alvoProtecao.maldicoes
                            alvoAcessorio != null -> alvoAcessorio.maldicoes
                            else -> alvoArma?.maldicoes ?: emptyList()
                        }
                        val conflitoElemento = maldicoesExistentes.mapNotNull { n -> MALDICOES_CATALOGO_COMPLETO.firstOrNull { it.nome == n }?.elemento }
                            .any { e -> e.isNotBlank() && def.elemento.isNotBlank() && (elementoOpressor(e) == def.elemento || elementoOpressor(def.elemento) == e) }
                        if (def.descricao.isNotBlank()) Text(def.descricao, color = TextoFraco, style = MaterialTheme.typography.bodySmall)
                        if (def.nome == "Proteção Elemental") {
                            Text("Escolha o elemento da resistência.", color = TextoFraco, style = MaterialTheme.typography.labelSmall)
                            Selecao(elementoMaldicao, ELEMENTOS_AFINIDADE, { elementoMaldicao = it }, placeholder = "Elemento")
                        }
                        if (maldicoesExistentes.contains(def.nome)) Text("Esta maldição já está aplicada e não pode ser duplicada.", color = Perigo, style = MaterialTheme.typography.labelSmall)
                        if (conflitoElemento) Text("Não pode combinar maldições de elementos opressores.", color = Perigo, style = MaterialTheme.typography.labelSmall)
                        Button(
                            enabled = (alvoProtecao != null || alvoAcessorio != null || alvoArma != null) &&
                                def.nome !in maldicoesExistentes && !conflitoElemento &&
                                (def.nome != "Proteção Elemental" || elementoMaldicao.isNotBlank()),
                            onClick = {
                                val aumentoCategoria = if (maldicoesExistentes.isEmpty()) def.categoriaAumentoInicial else def.categoriaAumentoAdicional
                                if (alvoProtecao != null) {
                                    val rd = if (def.nome == "Proteção Elemental" && elementoMaldicao.isNotBlank()) mapOf(elementoMaldicao to 10) else def.resistencias
                                    val atualizado = alvoProtecao.copy(
                                        categoria = alvoProtecao.categoria + aumentoCategoria,
                                        maldicoes = alvoProtecao.maldicoes + def.nome,
                                        resistencias = alvoProtecao.resistencias + rd,
                                    )
                                    upd(p.copy(itens = p.itens.map { if (it.id == alvoProtecao.id) atualizado else it }))
                                } else if (alvoAcessorio != null) {
                                    val rd = if (def.nome == "Proteção Elemental" && elementoMaldicao.isNotBlank()) mapOf(elementoMaldicao to 10) else def.resistencias
                                    val atualizado = alvoAcessorio.copy(
                                        categoria = alvoAcessorio.categoria + aumentoCategoria,
                                        maldicoes = alvoAcessorio.maldicoes + def.nome,
                                        resistencias = alvoAcessorio.resistencias + rd,
                                    )
                                    upd(p.copy(itens = p.itens.map { if (it.id == alvoAcessorio.id) atualizado else it }))
                                } else if (alvoArma != null) {
                                    val atualizado = alvoArma.copy(
                                        categoria = alvoArma.categoria + aumentoCategoria,
                                        maldicoes = alvoArma.maldicoes + def.nome,
                                    )
                                    upd(p.copy(armas = p.armas.map { if (it.id == alvoArma.id) atualizado else it }))
                                }
                                maldicaoSelecionada = ""
                                elementoMaldicao = ""
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Primaria),
                        ) { Text("Aplicar maldição") }
                    }
                } else {
                    Text("Maldições de armas, proteções e acessórios são liberadas pelo livro apenas para Agente Especial, Oficial de Operações e Agente de Elite. A ficha atual não possui patente suficiente.", color = TextoFraco, style = MaterialTheme.typography.bodySmall)
                }

                val itensVisiveis = p.itens.filter { item ->
                    categoriaInventario == "Todos" ||
                        item.tipoInventario == categoriaInventario ||
                        (categoriaInventario == "Armas" && false)
                }
                itensVisiveis.forEach { i ->
                    val set: (Item) -> Unit = { novo -> upd(p.copy(itens = p.itens.map { if (it.id == i.id) novo else it })) }
                    Campo("Item") { CampoTexto(i.nome) { set(i.copy(nome = it)) } }
                    Campo("Tipo no inventário") {
                        Selecao(
                            i.tipoInventario,
                            listOf("Proteções", "Itens", "Munições", "Itens amaldiçoados", "Outros"),
                            { set(i.copy(tipoInventario = it)) },
                        )
                    }
                    Campo("Tipo para maldições") {
                        Selecao(
                            i.tipoEquipamento,
                            listOf("Outro", "Acessório", "Proteção"),
                            { novoTipo ->
                                set(i.copy(tipoEquipamento = if (i.tipoInventario == "Proteções") "Proteção" else novoTipo))
                            },
                        )
                    }
                    if (i.tipoInventario == "Proteções" && i.tipoEquipamento != "Proteção") {
                        Text("Proteções são tratadas automaticamente como Proteção para fins de maldições.", color = TextoFraco, style = MaterialTheme.typography.labelSmall)
                    }
                    if (i.tipoInventario == "Proteções") {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Campo("Defesa do item", Modifier.weight(1f)) { Text("${i.defesaBonus}", color = TextoClaro) }
                            Campo("RD geral", Modifier.weight(1f)) { Text("${i.resistencias["Geral"] ?: 0}", color = TextoClaro) }
                        }
                        Text("Valores da proteção vêm do catálogo. Equipe o item para entrar automaticamente na Defesa e nas Resistências.", color = TextoFraco, style = MaterialTheme.typography.labelSmall)
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Campo("Qtd.", Modifier.weight(1f)) { Numero(i.qtd) { set(i.copy(qtd = it)) } }
                        Campo("Espaços", Modifier.weight(1f)) {
                            Numero(i.espacos.toInt()) { set(i.copy(espacos = it.toDouble())) }
                        }
                    }
                    Campo("Categoria") {
                        Selecao(
                            CATEGORIAS_ITEM.first { it.cat == i.categoria }.label,
                            CATEGORIAS_ITEM.map { it.label },
                            { label ->
                                val c = CATEGORIAS_ITEM.first { it.label == label }
                                set(i.copy(categoria = c.cat, espacos = c.espacos))
                            },
                        )
                    }
                    Campo("Descrição") { AreaTexto(i.desc, { set(i.copy(desc = it)) }, linhas = 2) }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Chip(if (i.equipado) "Equipado" else "Guardado", i.equipado,
                            { set(i.copy(equipado = !i.equipado, vestido = !i.equipado && i.vestido)) })
                        Chip(
                            if (i.empunhado) "Empunhado · mão ${i.mao}" else "Empunhar",
                            i.empunhado,
                            {
                                if (!i.empunhado && p.itens.count { it.empunhado } < 2) {
                                    val maoLivre = if (p.itens.none { it.empunhado && it.mao == 1 }) 1 else 2
                                    set(i.copy(empunhado = true, mao = maoLivre))
                                } else if (i.empunhado) set(i.copy(empunhado = false, mao = 0))
                            },
                            if (i.empunhado) Acento else Primaria
                        )
                        if (i.proficiencia.isNotBlank() && !i.proficiente) Text("⚠ Não proficiente: ${i.proficiencia}", color = Perigo, style = MaterialTheme.typography.labelSmall)
                        Chip("Remover", false, { upd(p.copy(itens = p.itens.filterNot { it.id == i.id })) }, Perigo)
                    }
                }
                if (p.itens.isEmpty()) Text("Mochila vazia.", color = TextoFraco,
                    style = MaterialTheme.typography.bodySmall)
            }
        }

        item {
            var ritualLivroSelecionado by remember { mutableStateOf("") }
            var ritualElementoEscolhido by remember { mutableStateOf("") }
            val rituaisNormaisConhecidos = p.rituais.count { it.origem == "APRENDER_RITUAL" }
            val limiteRituais = limiteRituaisConhecidosV36(p)
            val rituaisClasseConhecidos = rituaisClasseOcultistaConhecidosV36(p)
            val limiteRituaisClasse = quantidadeRituaisClasseOcultistaV36(p)
            val podeUsarClasse = p.classe == "Ocultista" && rituaisClasseConhecidos < limiteRituaisClasse
            val podeUsarAprender = p.poderesParanormais.any { it.nome == "Aprender Ritual" } && rituaisNormaisConhecidos < limiteRituais
            var origemRitualSelecionada by remember(p.classe, podeUsarClasse, podeUsarAprender) { mutableStateOf(if (podeUsarClasse) "CLASSE_OCULTISTA" else "APRENDER_RITUAL") }
            val rituaisPermitidos = when {
                origemRitualSelecionada == "CLASSE_OCULTISTA" && podeUsarClasse -> {
                    val ordem = mapOf("1º" to 1, "2º" to 2, "3º" to 3, "4º" to 4)
                    val max = ordem[circuloMaximoOcultista(p.nex)] ?: 1
                    RITUAIS_COMPLETOS.filter { (ordem[it.circulo] ?: 99) <= max }
                        .filterNot { def -> p.rituais.any { it.nome == def.nome && it.elemento == def.elemento && it.circulo == def.circulo } }
                        .distinctBy { "${it.nome}|${it.elemento}|${it.circulo}" }
                }
                podeUsarAprender -> rituaisElegiveisParaAprender(p)
                else -> emptyList()
            }
            val opcoesRituais = rituaisPermitidos
                .map { if (it.nome == "Amaldiçoar Arma") "${it.simbolo} ${it.nome} — ${it.circulo} · escolha o elemento" else "${it.simbolo} ${it.nome} — ${it.circulo} · ${it.elemento}" }
                .distinct()
            val ritualCatalogoBase = rituaisPermitidos.firstOrNull {
                if (it.nome == "Amaldiçoar Arma") "${it.simbolo} ${it.nome} — ${it.circulo} · escolha o elemento" == ritualLivroSelecionado
                else "${it.simbolo} ${it.nome} — ${it.circulo} · ${it.elemento}" == ritualLivroSelecionado
            }
            val ritualCatalogo = if (ritualCatalogoBase?.nome == "Amaldiçoar Arma" && ritualElementoEscolhido.isNotBlank()) ritualCatalogoBase.copy(elemento = ritualElementoEscolhido) else ritualCatalogoBase

            val ritualProntoParaAdicionar = ritualCatalogo != null &&
                (ritualCatalogo.nome != "Amaldiçoar Arma" || ritualElementoEscolhido.isNotBlank())
            val limiteRitualAtingido = !podeUsarClasse && !podeUsarAprender

            Painel(
                titulo = "Rituais",
                acao = {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(onClick = {
                            upd(p.copy(rituais = p.rituais + Ritual(nome = "Novo ritual", origem = "MESTRE_MANUAL", fonte = "Cadastro do Mestre")))
                        }) {
                            Icon(Icons.Default.Add, contentDescription = "Novo ritual", tint = Primaria)
                        }
                    }
                },
            ) {
                Text(
                    "Adicionar ritual permitido pelo NEX",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextoFraco,
                )
                if (p.classe == "Ocultista" && (podeUsarClasse || podeUsarAprender)) {
                    Text("Origem da aquisição", color = TextoClaro, style = MaterialTheme.typography.labelSmall)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        if (podeUsarClasse) Chip("Progressão do Ocultista", origemRitualSelecionada == "CLASSE_OCULTISTA", { origemRitualSelecionada = "CLASSE_OCULTISTA"; ritualLivroSelecionado = ""; ritualElementoEscolhido = "" }, Primaria)
                        if (podeUsarAprender) Chip("Aprender Ritual", origemRitualSelecionada == "APRENDER_RITUAL", { origemRitualSelecionada = "APRENDER_RITUAL"; ritualLivroSelecionado = ""; ritualElementoEscolhido = "" }, Primaria)
                    }
                }
                Text(
                    if (p.classe == "Ocultista") "Rituais da classe: $rituaisClasseConhecidos/$limiteRituaisClasse · rituais por Aprender Ritual: $rituaisNormaisConhecidos/$limiteRituais"
                    else "Rituais por Aprender Ritual: $rituaisNormaisConhecidos/$limiteRituais",
                    color = if (limiteRitualAtingido) Perigo else Acento,
                    style = MaterialTheme.typography.labelSmall,
                )
                Text(
                    if (p.classe == "Ocultista") "Progressão do Ocultista: $rituaisClasseConhecidos/$limiteRituaisClasse · Aprender Ritual: $rituaisNormaisConhecidos/$limiteRituais. Cada origem tem seu próprio limite e círculo permitido." else if (podeUsarAprender) "NEX ${p.nex}% · Aprender Ritual permite até o ${maxCirculoAprenderRitual(p.nex)}º círculo e usa o limite de rituais conhecidos por Intelecto." else "Esta ficha não possui uma fonte que permita aprender rituais manualmente.",
                    style = MaterialTheme.typography.labelSmall,
                    color = Acento,
                )
                if (opcoesRituais.isEmpty()) {
                    Text("Nenhum ritual novo está disponível pelas regras atuais.", color = TextoFraco, style = MaterialTheme.typography.bodySmall)
                }
                Selecao(
                    valor = ritualLivroSelecionado,
                    opcoes = opcoesRituais,
                    aoMudar = { ritualLivroSelecionado = it; ritualElementoEscolhido = "" },
                    placeholder = "Selecione um ritual permitido",
                )
                var mostrarRituaisBloqueados by remember(p.nex, p.classe, p.poderesParanormais, p.rituais.size) { mutableStateOf(false) }
                var ritualBloqueadoSelecionado by remember { mutableStateOf<com.arquivoparanormal.app.data.RitualDef?>(null) }
                val rituaisBloqueados = RITUAIS_COMPLETOS
                    .filter { motivoBloqueioRitual(p, it) != null }
                    .distinctBy { if (it.nome == "Amaldiçoar Arma") "${it.nome}|${it.circulo}" else "${it.nome}|${it.elemento}|${it.circulo}" }
                if (rituaisBloqueados.isNotEmpty()) {
                    TextButton(onClick = { mostrarRituaisBloqueados = !mostrarRituaisBloqueados }) {
                        Text(if (mostrarRituaisBloqueados) "Ocultar rituais bloqueados (${rituaisBloqueados.size})" else "🔒 Ver rituais bloqueados (${rituaisBloqueados.size})")
                    }
                    if (mostrarRituaisBloqueados) {
                        Text("Eles continuam visíveis para você saber o que existe e o que falta para desbloquear. Tocar em um deles mostra o requisito.", color = TextoFraco, style = MaterialTheme.typography.bodySmall)
                        rituaisBloqueados.forEach { bloqueado ->
                            Row(
                                Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                            ) {
                                Column(Modifier.weight(1f)) {
                                    Text("🔒 ${bloqueado.simbolo} ${bloqueado.nome}", color = TextoFraco, style = MaterialTheme.typography.bodySmall)
                                    Text("${bloqueado.circulo} círculo · ${bloqueado.elemento}", color = TextoFraco, style = MaterialTheme.typography.labelSmall)
                                }
                                TextButton(onClick = { ritualBloqueadoSelecionado = bloqueado }) { Text("Ver requisito") }
                            }
                        }
                    }
                }
                ritualBloqueadoSelecionado?.let { bloqueado ->
                    AlertDialog(
                        onDismissRequest = { ritualBloqueadoSelecionado = null },
                        title = { Text("🔒 ${bloqueado.nome}") },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("${bloqueado.circulo} círculo · ${bloqueado.elemento}", color = Acento)
                                Text(motivoBloqueioRitual(p, bloqueado) ?: "Disponível", color = TextoClaro)
                                Text("O ritual permanece no catálogo, mas não pode ser aprendido por esta ficha enquanto o requisito não for atendido.", color = TextoFraco)
                            }
                        },
                        confirmButton = { TextButton(onClick = { ritualBloqueadoSelecionado = null }) { Text("Fechar") } },
                    )
                }

                ritualCatalogo?.let { def ->
                    var detalhesCatalogoAbertos by remember(ritualLivroSelecionado) { mutableStateOf(false) }
                    Painel(
                        titulo = "${def.simbolo} ${def.nome}",
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        if (def.nome == "Amaldiçoar Arma") {
                            Text("Escolha o elemento do ritual ao aprendê-lo. Essa escolha define o tipo do dano adicional.", color = TextoFraco, style = MaterialTheme.typography.bodySmall)
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                ELEMENTOS_AFINIDADE.forEach { e -> Chip(e, ritualElementoEscolhido == e, { ritualElementoEscolhido = e }, corElemento(e)) }
                            }
                        }
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Chip("Círculo ${def.circulo}", true, {}, Primaria)
                            Chip("${def.simbolo} ${def.elemento}", true, {}, Primaria)
                            Chip("Afinidade: ${def.afinidade}", true, {}, Primaria)
                        }
                        if (p.classe == "Ocultista") {
                            val maxCirculo = com.arquivoparanormal.app.data.circuloMaximoOcultista(p.nex)
                            val ordem = mapOf("1º" to 1, "2º" to 2, "3º" to 3, "4º" to 4)
                            if ((ordem[def.circulo] ?: 1) > (ordem[maxCirculo] ?: 1)) {
                                Text("⚠️ Este círculo ainda não está disponível para o Ocultista neste NEX. O ritual pode ser cadastrado manualmente, mas não está liberado pelas regras de progressão.", color = Perigo, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Campo("Custo base", Modifier.weight(1f)) { Text("${def.custoPE} PE", color = TextoClaro) }
                            Campo("DT / custo paranormal", Modifier.weight(1f)) {
                                if (def.elemento == "Medo") {
                                    Text("Sem teste; custo também gera perda de SAN", color = Acento, style = MaterialTheme.typography.bodySmall)
                                } else {
                                    Text("20 + ${def.custoPE} = ${20 + def.custoPE}", color = Acento)
                                }
                            }
                        }
                        Campo("Resistência / DT do ritual") {
                            Text(
                                when {
                                    def.resistencia.isNotBlank() && def.resistencia.contains("DT 30") -> def.resistencia
                                    def.resistencia.isNotBlank() -> "${def.resistencia} · DT da ficha: ${calcularFicha(p).dtRituais}"
                                    def.elemento == "Medo" -> "Ritual de Medo não usa teste de resistência; consulte a perda de SAN e o efeito do ritual."
                                    else -> "Se o ritual indicar resistência, use a DT de Rituais da ficha: ${calcularFicha(p).dtRituais}."
                                },
                                color = TextoFraco,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                        if (def.discenteExtraPE > 0 || def.verdadeiroExtraPE > 0) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                if (def.discenteExtraPE > 0) Campo("Discente", Modifier.weight(1f)) { Text("+${def.discenteExtraPE} PE · total ${def.custoPE + def.discenteExtraPE}", color = Acento) }
                                if (def.verdadeiroExtraPE > 0) Campo("Verdadeira", Modifier.weight(1f)) { Text("+${def.verdadeiroExtraPE} PE · total ${def.custoPE + def.verdadeiroExtraPE}", color = Acento) }
                            }
                        }
                        Campo("Descrição") {
                            Text(def.descricao, color = TextoClaro, style = MaterialTheme.typography.bodyMedium)
                        }
                        Button(
                            enabled = ritualProntoParaAdicionar && !limiteRitualAtingido,
                            onClick = {
                                upd(
                                    p.copy(
                                        rituais = p.rituais + Ritual(
                                            nome = def.nome,
                                            circulo = def.circulo,
                                            elemento = def.elemento,
                                            afinidade = def.afinidade,
                                            simbolo = def.simbolo,
                                            execucao = def.execucao,
                                            alcance = def.alcance,
                                            alvo = def.alvo,
                                            area = def.area,
                                            efeitoCampo = def.efeito,
                                            duracao = def.duracao,
                                            descricao = def.descricao,
                                            efeito = def.descricao,
                                            discenteDescricao = def.discenteDescricao,
                                            verdadeiroDescricao = def.verdadeiroDescricao,
                                            resistencia = def.resistencia,
                                            custoPE = def.custoPE,
                                            discenteExtraPE = def.discenteExtraPE,
                                            verdadeiroExtraPE = def.verdadeiroExtraPE,
                                            formaSelecionada = "Normal",
                                            requisitoDiscente = def.requisitoDiscente,
                                            requisitoVerdadeiro = def.requisitoVerdadeiro,
                                            resistenciasConcedidas = def.resistenciasConcedidas,
                                            fonte = fonteDoRitual(def),
                                            origem = origemRitualSelecionada,
                                        )
                                    )
                                )
                                ritualLivroSelecionado = ""
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Primaria),
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Text("  Adicionar ritual")
                        }
                    }
                }

                p.rituais.forEach { r ->
                    val set: (Ritual) -> Unit = { novo ->
                        upd(p.copy(rituais = p.rituais.map { if (it.id == r.id) novo else it }))
                    }
                    val simboloAtual = r.simbolo.ifBlank {
                        com.arquivoparanormal.app.data.SIMBOLOS_ELEMENTO[r.elemento].orEmpty()
                    }

                    Campo("Nome") { CampoTexto(r.nome) { set(r.copy(nome = it)) } }
                    Text("Fonte: ${r.fonte.ifBlank { "não informada" }} · Origem: ${r.origem.ifBlank { "cadastro manual" }}", color = TextoFraco, style = MaterialTheme.typography.labelSmall)

                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Campo("Círculo", Modifier.weight(1f)) {
                            Selecao(r.circulo, CIRCULOS, { set(r.copy(circulo = it, custoPE = custoRitualPorCirculo(it))) })
                        }
                        Campo("Elemento", Modifier.weight(1f)) {
                            Selecao(
                                r.elemento,
                                ELEMENTOS.map { it.nome },
                                {
                                    set(
                                        r.copy(
                                            elemento = it,
                                            simbolo = com.arquivoparanormal.app.data.SIMBOLOS_ELEMENTO[it].orEmpty(),
                                        )
                                    )
                                },
                                placeholder = "Nenhum",
                            )
                        }
                    }

                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Campo("Afinidade", Modifier.weight(1f)) {
                            Selecao(
                                r.afinidade,
                                listOf("Nenhuma", "Conhecimento", "Energia", "Morte", "Sangue"),
                                { set(r.copy(afinidade = it)) },
                            )
                        }
                        Campo("Símbolo", Modifier.weight(1f)) {
                            Text(
                                simboloAtual.ifBlank { "◇" },
                                color = TextoClaro,
                                style = MaterialTheme.typography.headlineSmall,
                            )
                        }
                    }

                    val custoEfetivo = when (r.formaSelecionada) {
                        "Discente" -> r.custoPE + r.discenteExtraPE
                        "Verdadeira" -> r.custoPE + r.verdadeiroExtraPE
                        else -> r.custoPE
                    }
                    var detalhesRitualAbertos by remember(r.id) { mutableStateOf(false) }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Campo("Forma", Modifier.weight(1f)) {
                            val formas = buildList {
                                add("Normal")
                                if (r.discenteExtraPE > 0) add("Discente")
                                if (r.verdadeiroExtraPE > 0) add("Verdadeira")
                            }
                            Selecao(r.formaSelecionada, formas, { forma ->
                                val custoBase = custoRitualPorCirculo(r.circulo)
                                val custo = when (forma) {
                                    "Discente" -> custoBase + r.discenteExtraPE
                                    "Verdadeira" -> custoBase + r.verdadeiroExtraPE
                                    else -> custoBase
                                }
                                set(r.copy(formaSelecionada = forma, custoPE = custo))
                            })
                        }
                        Campo("Custo", Modifier.weight(1f)) { Text("$custoEfetivo PE", color = Acento) }
                        Campo("DT custo paranormal", Modifier.weight(1f)) {
                            Text(if (r.elemento == "Medo") "Perde SAN" else "${20 + custoEfetivo}", color = Acento)
                        }
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Campo("Execução", Modifier.weight(1f)) { Text(r.execucao, color = TextoClaro) }
                        Campo("Alcance", Modifier.weight(1f)) { Text(r.alcance, color = TextoClaro) }
                    }
                    if (r.alvo.isNotBlank() || r.area.isNotBlank() || r.efeitoCampo.isNotBlank() || r.duracao.isNotBlank()) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (r.alvo.isNotBlank()) Campo("Alvo", Modifier.weight(1f)) { Text(r.alvo, color = TextoClaro, style = MaterialTheme.typography.bodySmall) }
                            if (r.area.isNotBlank()) Campo("Área", Modifier.weight(1f)) { Text(r.area, color = TextoClaro, style = MaterialTheme.typography.bodySmall) }
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (r.duracao.isNotBlank()) Campo("Duração", Modifier.weight(1f)) { Text(r.duracao, color = TextoClaro, style = MaterialTheme.typography.bodySmall) }
                            if (r.efeitoCampo.isNotBlank()) Campo("Efeito", Modifier.weight(1f)) { Text(r.efeitoCampo, color = TextoClaro, style = MaterialTheme.typography.bodySmall) }
                        }
                    }
                    Campo("Resistência") {
                        Text(
                            if (r.resistencia.isNotBlank()) "${r.resistencia} · DT ${calcularFicha(p).dtRituais}"
                            else if (r.elemento == "Medo") "Não indicada; consulte o efeito específico."
                            else "Não indicada no ritual.",
                            color = TextoClaro, style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Campo("Descrição") {
                        Text(r.descricao, color = TextoClaro, style = MaterialTheme.typography.bodySmall, maxLines = 5)
                    }
                    if (r.formaSelecionada == "Discente" && r.discenteDescricao.isNotBlank()) {
                        Campo("Discente") {
                            Text(r.discenteDescricao, color = TextoClaro, style = MaterialTheme.typography.bodySmall)
                            if (r.requisitoDiscente.isNotBlank()) Text("Requisito: ${r.requisitoDiscente}", color = Acento, style = MaterialTheme.typography.labelSmall)
                        }
                    } else if (r.formaSelecionada == "Verdadeira" && r.verdadeiroDescricao.isNotBlank()) {
                        Campo("Verdadeira") {
                            Text(r.verdadeiroDescricao, color = TextoClaro, style = MaterialTheme.typography.bodySmall)
                            if (r.requisitoVerdadeiro.isNotBlank()) Text("Requisito: ${r.requisitoVerdadeiro}", color = Acento, style = MaterialTheme.typography.labelSmall)
                        }
                    } else if (r.discenteDescricao.isNotBlank() || r.verdadeiroDescricao.isNotBlank()) {
                        TextButton(onClick = { detalhesRitualAbertos = true }) { Text("Ver detalhes das formas avançadas") }
                    }
                    if (detalhesRitualAbertos) {
                        AlertDialog(
                            onDismissRequest = { detalhesRitualAbertos = false },
                            title = { Text("${r.nome} — detalhes") },
                            text = {
                                androidx.compose.foundation.layout.Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(r.descricao, color = TextoClaro)
                                    if (r.discenteDescricao.isNotBlank()) {
                                        Text("Discente (+${r.discenteExtraPE} PE)", color = Acento)
                                        Text(r.discenteDescricao, color = TextoClaro)
                                        if (r.requisitoDiscente.isNotBlank()) Text("Requisito: ${r.requisitoDiscente}", color = TextoFraco)
                                    }
                                    if (r.verdadeiroDescricao.isNotBlank()) {
                                        Text("Verdadeira (+${r.verdadeiroExtraPE} PE)", color = Acento)
                                        Text(r.verdadeiroDescricao, color = TextoClaro)
                                        if (r.requisitoVerdadeiro.isNotBlank()) Text("Requisito: ${r.requisitoVerdadeiro}", color = TextoFraco)
                                    }
                                }
                            },
                            confirmButton = { TextButton(onClick = { detalhesRitualAbertos = false }) { Text("Fechar") } },
                        )
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Chip(if (r.ativo) "Ritual ativo" else "Ritual inativo", r.ativo, { set(r.copy(ativo = !r.ativo)) }, if (r.ativo) Acento else Primaria)
                        Text("Somente rituais marcados como ativos alimentam resistências automáticas.", color = TextoFraco, style = MaterialTheme.typography.labelSmall)
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Campo("Forma", Modifier.weight(1f)) {
                            val formas = buildList {
                                add("Normal")
                                if (r.discenteExtraPE > 0) add("Discente")
                                if (r.verdadeiroExtraPE > 0) add("Verdadeira")
                            }
                            Selecao(r.formaSelecionada, formas, { forma ->
                                val custo = when (forma) {
                                    "Discente" -> custoRitualPorCirculo(r.circulo) + r.discenteExtraPE
                                    "Verdadeira" -> custoRitualPorCirculo(r.circulo) + r.verdadeiroExtraPE
                                    else -> custoRitualPorCirculo(r.circulo)
                                }
                                set(r.copy(formaSelecionada = forma, custoPE = custo))
                            })
                        }
                        Campo("Custo da forma", Modifier.weight(1f)) { Text("${r.custoPE} PE", color = Acento) }
                    }
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Campo("Execução", Modifier.weight(1f)) {
                            CampoTexto(r.execucao) { set(r.copy(execucao = it)) }
                        }
                        Campo("Alcance", Modifier.weight(1f)) {
                            CampoTexto(r.alcance) { set(r.copy(alcance = it)) }
                        }
                    }

                    Campo("Descrição") {
                        AreaTexto(r.descricao, { set(r.copy(descricao = it)) }, linhas = 3)
                    }
                    Campo("Efeito / Observações") {
                        AreaTexto(r.efeito, { set(r.copy(efeito = it)) }, linhas = 3)
                    }

                    Chip(
                        "Remover ritual",
                        false,
                        { upd(p.copy(rituais = p.rituais.filterNot { it.id == r.id })) },
                        Perigo,
                    )
                }

                if (p.rituais.isEmpty()) {
                    Text(
                        "Nenhum ritual conhecido.",
                        color = TextoFraco,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }

        item {
            if (p.trilha == "Graduado" && p.nex >= 40) {
                Painel(titulo = "Grimório Ritualístico") {
                    val capacidade = (p.atributos["int"] ?: 0) + if (p.nex >= 55) 1 else 0 + if (p.nex >= 85) 1 else 0
                    Text("Slots: ${p.rituaisGrimorio.size}/$capacidade · esses rituais não contam no limite normal.", color = Acento)
                    Text("Para conjurar: empunhe o grimório e use uma ação completa para relembrar o ritual.", color = TextoFraco, style = MaterialTheme.typography.bodySmall)
                    var grimSelecionado by remember(p.rituaisGrimorio) { mutableStateOf("") }
                    val maxCirculoGrim = if (p.nex >= 85) 4 else if (p.nex >= 55) 3 else 2
                    val opcoes = RITUAIS_COMPLETOS.filter { c ->
                        val n = when(c.circulo) { "1º"->1; "2º"->2; "3º"->3; "4º"->4; else -> 99 }
                        n <= maxCirculoGrim && p.rituaisGrimorio.none { it.nome == c.nome && it.elemento == c.elemento && it.circulo == c.circulo }
                    }.map { "${it.nome} — ${it.circulo} · ${it.elemento}" }.distinct()
                    Selecao(grimSelecionado, opcoes, { grimSelecionado = it }, placeholder = "Adicionar ritual ao grimório")
                    val grimDef = RITUAIS_COMPLETOS.firstOrNull { "${it.nome} — ${it.circulo} · ${it.elemento}" == grimSelecionado }
                    grimDef?.let { def ->
                        Button(enabled = p.rituaisGrimorio.size < capacidade, onClick = {
                            val ritual = Ritual(nome = def.nome, circulo = def.circulo, elemento = def.elemento, execucao = def.execucao, alcance = def.alcance, alvo = def.alvo, area = def.area, efeitoCampo = def.efeito, duracao = def.duracao, afinidade = def.afinidade, simbolo = def.simbolo, descricao = def.descricao, efeito = def.efeito, discenteDescricao = def.discenteDescricao, verdadeiroDescricao = def.verdadeiroDescricao, resistencia = def.resistencia, custoPE = def.custoPE, discenteExtraPE = def.discenteExtraPE, verdadeiroExtraPE = def.verdadeiroExtraPE, requisitoDiscente = def.requisitoDiscente, requisitoVerdadeiro = def.requisitoVerdadeiro, fonte = fonteDoRitual(def), origem = "GRIMORIO")
                            upd(p.copy(rituaisGrimorio = p.rituaisGrimorio + ritual)); grimSelecionado = ""
                        }) { Text("Adicionar ao grimório") }
                    }
                    p.rituaisGrimorio.forEach { r ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) { Text("${r.nome} · ${r.circulo} · ${r.elemento}", color = TextoClaro); Text(if (p.ritualGrimorioRelembradoId == r.id) "Relembrado" else "Armazenado", color = TextoFraco, style = MaterialTheme.typography.labelSmall) }
                            TextButton(onClick = { upd(p.copy(grimorioEmpunhado = true, ritualGrimorioRelembradoId = r.id)) }) { Text("Ação completa: relembrar") }
                            TextButton(onClick = { upd(p.copy(rituaisGrimorio = p.rituaisGrimorio.filterNot { it.id == r.id }, ritualGrimorioRelembradoId = p.ritualGrimorioRelembradoId.takeUnless { it == r.id })) }) { Text("Remover") }
                        }
                    }
                    Chip(if (p.grimorioEmpunhado) "Grimório empunhado" else "Grimório guardado", p.grimorioEmpunhado, { upd(p.copy(grimorioEmpunhado = !p.grimorioEmpunhado)) }, if (p.grimorioEmpunhado) Acento else Primaria)
                }
            }
        }

        item {
            Painel(titulo = "Monstruoso / variante paranormal") {
                Text("Estado separado da trilha normal. O atributo usado para PE/DT é definido pelo elemento.", color = TextoFraco, style = MaterialTheme.typography.bodySmall)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Sangue", "Morte", "Conhecimento", "Energia").forEach { elemento ->
                        Chip(elemento, p.monstruosoElemento == elemento, { upd(p.copy(monstruosoElemento = elemento)) })
                    }
                }
                if (p.monstruosoElemento.isNotBlank()) {
                    Text("Atributo paranormal: ${when (p.monstruosoElemento) { "Sangue" -> "Força"; "Morte" -> "Vigor"; "Conhecimento" -> "Intelecto"; "Energia" -> "Agilidade"; else -> "—" }}", color = Acento)
                    Text("Escarificações registradas: ${p.monstruosoEscarificacoes.size} · Componentes: ${p.monstruosoComponentes.size}", color = TextoFraco, style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        item {
            Painel(titulo = "Histórico de alterações") {
                if (p.historicoAlteracoes.isEmpty()) Text("Nenhuma alteração auditada ainda.", color = TextoFraco)
                p.historicoAlteracoes.takeLast(20).asReversed().forEach { h ->
                    Text("${h.tipo} · ${java.text.SimpleDateFormat("dd/MM HH:mm", java.util.Locale.getDefault()).format(java.util.Date(h.quando))}", color = Acento, style = MaterialTheme.typography.labelSmall)
                    Text(h.descricao, color = TextoClaro, style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        item {
            Painel(titulo = "Estados de Sanidade e Morte") {
                val calcEstado = calcularFicha(p)
                Text("Sanidade: ${estadoSanidade(p, calcEstado.sanMax)}", color = if (p.insano || p.sanAtual <= 0) Perigo else Acento)
                Text("Perturbado: ${if (p.sanAtual * 2 < calcEstado.sanMax) "sim" else "não"} · Enlouquecendo: ${if (p.enlouquecendo || p.sanAtual <= 0) "sim" else "não"} · Insano: ${if (p.insano) "sim" else "não"}", color = TextoFraco, style = MaterialTheme.typography.bodySmall)
                if (p.sanAtual <= 0 && !p.insano) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { upd(registrarTurnoEnlouquecendo(p)) }, modifier = Modifier.weight(1f)) { Text("+1 turno Enlouquecendo") }
                        Text("${p.turnosEnlouquecendo}/3", color = Acento, modifier = Modifier.padding(top = 12.dp))
                    }
                    Text("Ao completar 3 turnos Enlouquecendo na mesma cena, o personagem fica Insano.", color = TextoFraco, style = MaterialTheme.typography.bodySmall)
                }
                Text("Morrendo: ${p.turnosMorrendo}/3 turnos nesta cena", color = if (p.turnosMorrendo > 0) Perigo else TextoFraco)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { upd(registrarTurnoMorrendo(p.copy(condicoes = if ("Morrendo" in p.condicoes) p.condicoes else p.condicoes + "Morrendo"))) }, modifier = Modifier.weight(1f)) { Text("Registrar turno Morrendo") }
                    TextButton(onClick = { upd(estabilizar(p)) }) { Text("Estabilizar") }
                }
                Text("Morrendo termina com estabilização conforme a regra; 3 turnos na mesma cena causam morte.", color = TextoFraco, style = MaterialTheme.typography.bodySmall)
            }
        }

        item {
            Painel(titulo = "Habilidades") {
                val nomes = p.habilidades.lines().map(String::trim).filter { it.isNotBlank() }.distinct()
                if (nomes.isEmpty()) {
                    Text("Nenhuma habilidade adicionada.", color = TextoFraco)
                } else {
                    nomes.forEach { nome ->
                        var detalhesAbertos by remember(nome) { mutableStateOf(false) }
                        val def = com.arquivoparanormal.app.data.poderesDisponiveisPara(p).firstOrNull { it.nome == nome }
                        Painel(titulo = nome, modifier = Modifier.fillMaxWidth()) {
                            if (def != null) {
                                Text(def.descricao, color = TextoClaro, style = MaterialTheme.typography.bodySmall, maxLines = 4)
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text("${origemCatalogadaDoPoder(p, nome)}${if (def.trilha.isNotBlank()) " · ${def.trilha}" else ""}", color = Acento, style = MaterialTheme.typography.labelSmall)
                                    if (def.custo.isNotBlank()) Text("Custo: ${def.custo}", color = Acento, style = MaterialTheme.typography.labelSmall)
                                    TextButton(onClick = { detalhesAbertos = true }) { Text("Ver detalhes") }
                                }
                                if (def.requisito.isNotBlank()) Text("Requisito: ${def.requisito}", color = TextoFraco, style = MaterialTheme.typography.labelSmall)
                            } else {
                                Text("Habilidade registrada manualmente. Adicione uma descrição nas anotações do perfil se necessário.", color = TextoFraco, style = MaterialTheme.typography.bodySmall)
                            }
                            TextButton(onClick = {
                                val restante = nomes.filterNot { it == nome }.joinToString("\n")
                                upd(p.copy(habilidades = restante))
                            }) { Text("Remover") }
                        }
                        if (detalhesAbertos && def != null) {
                            AlertDialog(
                                onDismissRequest = { detalhesAbertos = false },
                                title = { Text(def.nome) },
                                text = {
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text(def.descricao, color = TextoClaro)
                                        if (def.requisito.isNotBlank()) Text("Requisitos: ${def.requisito}")
                                        if (def.custo.isNotBlank()) Text("Custo: ${def.custo}")
                                        Text("NEX mínimo: ${def.nexMin}%")
                                        if (def.pagina > 0) Text("Referência: ${def.livro.titulo}, pág. ${def.pagina}", color = TextoFraco)
                                    }
                                },
                                confirmButton = { TextButton(onClick = { detalhesAbertos = false }) { Text("Fechar") } },
                            )
                        }
                    }
                }

                if (p.poderesParanormais.isNotEmpty()) {
                    Text("Poderes paranormais", color = Acento, style = MaterialTheme.typography.titleSmall)
                    p.poderesParanormais.forEach { pp ->
                        val def = com.arquivoparanormal.app.data.PODERES_PARANORMAIS.firstOrNull { it.nome == pp.nome }
                        Painel(titulo = "${pp.nome}${if (pp.afinidade) " · Afinidade" else ""}", modifier = Modifier.fillMaxWidth()) {
                            Text("Elemento: ${pp.elemento}", color = Acento, style = MaterialTheme.typography.labelSmall)
                            if (pp.ritualNome.isNotBlank()) Text("Ritual aprendido: ${pp.ritualNome}", color = Acento, style = MaterialTheme.typography.labelSmall)
                            if (def != null) {
                                Text(def.descricao, color = TextoClaro, style = MaterialTheme.typography.bodySmall, maxLines = 5)
                                if (def.requisito.isNotBlank()) Text("Requisito: ${def.requisito}", color = TextoFraco, style = MaterialTheme.typography.labelSmall)
                                if (pp.afinidade && def.afinidade.isNotBlank()) Text("Afinidade: ${def.afinidade}", color = TextoFraco, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }

        item {
            Painel(titulo = "Perfil do agente") {
                Campo("Aparência") { AreaTexto(p.aparencia, { upd(p.copy(aparencia = it)) }, linhas = 3) }
                Campo("Personalidade") { AreaTexto(p.personalidade, { upd(p.copy(personalidade = it)) }, linhas = 3) }
                Campo("História") { AreaTexto(p.historia, { upd(p.copy(historia = it)) }, linhas = 4) }
                Campo("Objetivo") { AreaTexto(p.objetivo, { upd(p.copy(objetivo = it)) }, linhas = 2) }
                Campo("Medos e traumas") { AreaTexto(p.medos, { upd(p.copy(medos = it)) }, linhas = 2) }
                var poderSelecionado by remember { mutableStateOf("") }
                val poderesJaConhecidos = p.habilidades.lines().map(String::trim).filter { it.isNotBlank() }.toSet()
                val slotsClasse = quantidadePoderesDeClasseV36(p)
                val usadosClasse = poderesDeClasseManuaisConhecidosV36(p)
                val podeEscolherClasse = usadosClasse < slotsClasse
                val poderesPermitidos = com.arquivoparanormal.app.data.poderesDisponiveisPara(p)
                    .filter { !it.automatico }
                    .filter { it.nome != "Transcender" }
                    .filter { it.nome !in poderesJaConhecidos }
                    .filter { it.classe != p.classe || podeEscolherClasse }
                val poderCatalogado = poderesPermitidos.firstOrNull { "${it.nome} — ${it.categoria}" == poderSelecionado }
                val poderesBloqueados = catalogoCompletoDePoderes()
                    .filter { it.nome != "Transcender" }
                    .filter { motivoBloqueioPoder(p, it) != null }
                var mostrarPoderesBloqueados by remember(p.nex, p.classe, p.trilha, p.origem, p.atributos, p.habilidades) { mutableStateOf(false) }
                var poderBloqueadoSelecionado by remember { mutableStateOf<com.arquivoparanormal.app.data.PoderDisponivel?>(null) }
                Text("O seletor da ficha usa as mesmas restrições de classe, trilha, origem, NEX e pré-requisitos da criação. Transcender não aparece aqui porque exige uma escolha específica de poder paranormal.", color = TextoFraco, style = MaterialTheme.typography.bodySmall)
                Text("Poderes de classe: $usadosClasse/$slotsClasse escolhas previstas pelo NEX. Desbloquear um poder apenas o torna elegível; ele só entra na ficha após ser escolhido.", color = Acento, style = MaterialTheme.typography.bodySmall)
                Campo("Escolher poder") {
                    Selecao(
                        poderSelecionado,
                        poderesPermitidos.map { "${it.nome} — ${it.categoria}" },
                        { poderSelecionado = it },
                        placeholder = if (poderesPermitidos.isEmpty()) "Nenhum poder disponível" else "Selecione um poder compatível",
                    )
                    poderCatalogado?.let { def ->
                        Text("${def.categoria} · ${def.classe.ifBlank { "—" }}${if (def.trilha.isNotBlank()) " · ${def.trilha}" else ""}", color = Acento, style = MaterialTheme.typography.labelSmall)
                        Text("NEX mínimo: ${def.nexMin}%", color = TextoFraco, style = MaterialTheme.typography.labelSmall)
                        if (def.requisito.isNotBlank()) Text("Requisito: ${def.requisito}", color = TextoFraco, style = MaterialTheme.typography.labelSmall)
                        Text(def.descricao, color = TextoFraco, style = MaterialTheme.typography.bodySmall, maxLines = 5)
                        val podeAdicionarEstePoder = if (def.classe == p.classe) podeAdicionarPoderDeClasseV36(p, def) else true
                        TextButton(
                            enabled = podeAdicionarEstePoder,
                            onClick = {
                                val linha = def.nome
                                val novo = if (p.habilidades.isBlank()) linha else p.habilidades + "\n" + linha
                                upd(p.copy(habilidades = novo))
                                poderSelecionado = ""
                            }
                        ) { Text(if (podeAdicionarEstePoder) "Adicionar poder" else "Limite de poderes de classe atingido") }
                    }
                }
                TextButton(onClick = { mostrarPoderesBloqueados = !mostrarPoderesBloqueados }) {
                    Text(if (mostrarPoderesBloqueados) "Ocultar poderes bloqueados (${poderesBloqueados.size})" else "🔒 Ver poderes bloqueados (${poderesBloqueados.size})")
                }
                if (mostrarPoderesBloqueados) {
                    Text("Os poderes bloqueados não podem ser adicionados agora. Toque em um cadeado para saber exatamente o que falta.", color = TextoFraco, style = MaterialTheme.typography.bodySmall)
                    poderesBloqueados.forEach { bloqueado ->
                        Row(
                            Modifier.fillMaxWidth().padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text("🔒 ${bloqueado.nome}", color = TextoFraco, style = MaterialTheme.typography.bodySmall)
                                Text("${bloqueado.categoria}${if (bloqueado.trilha.isNotBlank()) " · ${bloqueado.trilha}" else if (bloqueado.classe.isNotBlank()) " · ${bloqueado.classe}" else ""}", color = TextoFraco, style = MaterialTheme.typography.labelSmall)
                            }
                            TextButton(onClick = { poderBloqueadoSelecionado = bloqueado }) { Text("Ver requisito") }
                        }
                    }
                }
                poderBloqueadoSelecionado?.let { bloqueado ->
                    AlertDialog(
                        onDismissRequest = { poderBloqueadoSelecionado = null },
                        title = { Text("🔒 ${bloqueado.nome}") },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("${bloqueado.categoria}${if (bloqueado.trilha.isNotBlank()) " · ${bloqueado.trilha}" else if (bloqueado.classe.isNotBlank()) " · ${bloqueado.classe}" else ""}", color = Acento)
                                Text(motivoBloqueioPoder(p, bloqueado) ?: "Disponível", color = TextoClaro)
                                if (bloqueado.descricao.isNotBlank()) Text(bloqueado.descricao, color = TextoFraco)
                                if (bloqueado.requisito.isNotBlank()) Text("Requisito catalogado: ${bloqueado.requisito}", color = TextoFraco)
                                Text("O cadeado desaparece quando as condições forem atendidas.", color = TextoFraco)
                            }
                        },
                        confirmButton = { TextButton(onClick = { poderBloqueadoSelecionado = null }) { Text("Fechar") } },
                    )
                }
                Text("Poderes automáticos", color = Acento, style = MaterialTheme.typography.titleSmall)
                com.arquivoparanormal.app.data.poderesDisponiveisPara(p).filter { it.automatico }.forEach {
                    Text("🔒 ${it.nome} · ${it.categoria}", color = TextoFraco, style = MaterialTheme.typography.bodySmall)
                }
                Campo("Anotações do perfil") {
                    AreaTexto(p.habilidades, { upd(p.copy(habilidades = it)) }, linhas = 3)
                }
            }
        }

        item { RotuloOP("Tudo salvo automaticamente neste aparelho") }
    }
}
