package com.arquivoparanormal.app.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.random.Random
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
import com.arquivoparanormal.app.data.PERICIAS
import com.arquivoparanormal.app.data.RESISTENCIAS
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
import com.arquivoparanormal.app.data.testeAtaqueDaArma
import com.arquivoparanormal.app.data.ataqueDaArma
import com.arquivoparanormal.app.data.margemCritico
import com.arquivoparanormal.app.data.periciasConcedidasPelaOrigem
import com.arquivoparanormal.app.data.formatBonus
import com.arquivoparanormal.app.data.limiteDeCarga
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
fun FichaScreen(repo: Repositorio, id: String) {
    val p = repo.personagem(id)
    if (p == null) {
        Text("Ficha não encontrada.", color = TextoFraco, modifier = Modifier.padding(20.dp))
        return
    }
    val upd: (Personagem) -> Unit = { repo.salvar(it) }

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
                    com.arquivoparanormal.app.ui.SeletorImagem(
                        caminho = p.fotoArquivo,
                        aoDefinir = { upd(p.copy(fotoArquivo = it)) },
                        rotuloVazio = "Adicionar foto (imagem ou PDF)",
                        tamanhoPreview = 88.dp,
                    )
                }
                Campo("Nome do agente") { CampoTexto(p.nome) { upd(p.copy(nome = it)) } }
                Campo("Jogador") { CampoTexto(p.jogador) { upd(p.copy(jogador = it)) } }
                Campo("Idade") { CampoTexto(p.idade) { upd(p.copy(idade = it)) } }
                Campo("Descrição") { AreaTexto(p.descricao, { upd(p.copy(descricao = it)) }) }
                Campo("Patente") { Selecao(p.patente, PATENTES, { upd(p.copy(patente = it)) }) }
                Campo("NEX %") { Numero(p.nex) { upd(p.copy(nex = it)) } }
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
                    Text("${o.pericias} · ${o.poder}", style = MaterialTheme.typography.bodySmall, color = Acento)
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
                    Campo("Esquiva", Modifier.weight(1f)) { Text("${calc.esquiva}", color = TextoClaro) }
                    Campo("Bloqueio / RD", Modifier.weight(1f)) { Text("${calc.bloqueio}", color = TextoClaro) }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Campo("DT de Rituais", Modifier.weight(1f)) { Text("${calc.dtRituais}", color = TextoClaro) }
                    Campo("DT de Habilidades", Modifier.weight(1f)) { Text("${calc.dtHabilidades}", color = TextoClaro) }
                }
                val limite = calc.limiteCarga
                Text(
                    "Carga: ${espacosUsados(p)} / $limite espaços",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (espacosUsados(p) > limite) Perigo else TextoFraco,
                )
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
            Painel(titulo = "Afinidade") {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    ELEMENTOS.forEach { e ->
                        Chip(e.nome, p.afinidade == e.nome,
                            { upd(p.copy(afinidade = if (p.afinidade == e.nome) "" else e.nome)) },
                            corElemento(e.nome))
                    }
                }
                ELEMENTOS.firstOrNull { it.nome == p.afinidade }?.let {
                    Text(it.desc, style = MaterialTheme.typography.bodySmall, color = TextoFraco)
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
                            Text("1d20 ${formatBonus(total)}${if (def.nome in p.periciasAutomaticas) " · origem" else ""}",
                                style = MaterialTheme.typography.bodySmall, color = Acento)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            TREINAMENTOS.forEach { t ->
                                Chip(t.nome.take(4), per.treino == t.bonus, {
                                    upd(p.copy(pericias = p.pericias + (def.nome to per.copy(treino = t.bonus))))
                                })
                            }
                        }
                    }
                }
            }
        }

        item {
            Painel(titulo = "Resistências") {
                RESISTENCIAS.chunked(2).forEach { linha ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        linha.forEach { r ->
                            Campo(r, Modifier.weight(1f)) {
                                Numero(p.resistencias[r] ?: 0) {
                                    upd(p.copy(resistencias = p.resistencias + (r to it)))
                                }
                            }
                        }
                    }
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
                            Text(def.descricao, color = TextoClaro, style = MaterialTheme.typography.bodyMedium)
                        }
                        Button(
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
                                val bonus = ataqueDaArma(p, a)
                                val rolagem = Random.nextInt(1, 21)
                                val total = rolagem + bonus
                                resultadoAtaque = "Rolagem: $rolagem ${formatBonus(bonus)} = $total"
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
                            { set(i.copy(equipado = !i.equipado)) })
                        Chip("Remover", false, { upd(p.copy(itens = p.itens.filterNot { it.id == i.id })) }, Perigo)
                    }
                }
                if (p.itens.isEmpty()) Text("Mochila vazia.", color = TextoFraco,
                    style = MaterialTheme.typography.bodySmall)
            }
        }

        item {
            var ritualLivroSelecionado by remember { mutableStateOf("") }
            val ritualCatalogo = RITUAIS_COMPLETOS.firstOrNull {
                "${it.simbolo} ${it.nome} — ${it.circulo} · ${it.elemento}" == ritualLivroSelecionado
            }

            Painel(
                titulo = "Rituais",
                acao = {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(onClick = {
                            upd(p.copy(rituais = p.rituais + Ritual(nome = "Novo ritual")))
                        }) {
                            Icon(Icons.Default.Add, contentDescription = "Novo ritual", tint = Primaria)
                        }
                    }
                },
            ) {
                Text(
                    "Adicionar ritual do livro",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextoFraco,
                )
                Selecao(
                    valor = ritualLivroSelecionado,
                    opcoes = RITUAIS_COMPLETOS.map { "${it.simbolo} ${it.nome} — ${it.circulo} · ${it.elemento}" },
                    aoMudar = { ritualLivroSelecionado = it },
                    placeholder = "Selecione um ritual do PDF",
                )

                ritualCatalogo?.let { def ->
                    Painel(
                        titulo = "${def.simbolo} ${def.nome}",
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Chip("Círculo ${def.circulo}", true, {}, Primaria)
                            Chip("${def.simbolo} ${def.elemento}", true, {}, Primaria)
                            Chip("Afinidade: ${def.afinidade}", true, {}, Primaria)
                        }
                        Campo("Descrição") {
                            Text(def.descricao, color = TextoClaro, style = MaterialTheme.typography.bodyMedium)
                        }
                        Button(
                            onClick = {
                                upd(
                                    p.copy(
                                        rituais = p.rituais + Ritual(
                                            nome = def.nome,
                                            circulo = def.circulo,
                                            elemento = def.elemento,
                                            afinidade = def.afinidade,
                                            simbolo = def.simbolo,
                                            descricao = def.descricao,
                                            efeito = def.descricao,
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

                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Campo("Círculo", Modifier.weight(1f)) {
                            Selecao(r.circulo, CIRCULOS, { set(r.copy(circulo = it)) })
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
                                listOf("Nenhuma", "Conhecimento", "Energia", "Morte", "Sangue", "Medo"),
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
            Painel(titulo = "Perfil do agente") {
                Campo("Aparência") { AreaTexto(p.aparencia, { upd(p.copy(aparencia = it)) }, linhas = 3) }
                Campo("Personalidade") { AreaTexto(p.personalidade, { upd(p.copy(personalidade = it)) }, linhas = 3) }
                Campo("História") { AreaTexto(p.historia, { upd(p.copy(historia = it)) }, linhas = 4) }
                Campo("Objetivo") { AreaTexto(p.objetivo, { upd(p.copy(objetivo = it)) }, linhas = 2) }
                Campo("Medos e traumas") { AreaTexto(p.medos, { upd(p.copy(medos = it)) }, linhas = 2) }
                var poderSelecionado by remember { mutableStateOf("") }
                val poderCatalogado = PODERES_SOBREVIVENDO.firstOrNull { "${it.nome} — ${it.classe}" == poderSelecionado }
                Campo("Poderes de Sobrevivendo ao Horror") {
                    Selecao(poderSelecionado, PODERES_SOBREVIVENDO.map { "${it.nome} — ${it.classe}" }, { poderSelecionado = it }, placeholder = "Selecione um poder")
                    poderCatalogado?.let { def ->
                        Text("${def.livro.titulo} · pág. ${def.pagina}", color = TextoFraco, style = MaterialTheme.typography.bodySmall)
                        Button(onClick = {
                            val linha = "${def.nome} (${def.classe})"
                            val novo = if (p.habilidades.isBlank()) linha else p.habilidades + "\n" + linha
                            upd(p.copy(habilidades = novo)); poderSelecionado = ""
                        }, colors = ButtonDefaults.buttonColors(containerColor = Primaria)) {
                            Icon(Icons.Default.Add, contentDescription = null); Text("  Adicionar poder")
                        }
                    }
                }
                Campo("Habilidades, poderes e anotações") {
                    AreaTexto(p.habilidades, { upd(p.copy(habilidades = it)) }, linhas = 5)
                }
            }
        }

        item { RotuloOP("Tudo salvo automaticamente neste aparelho") }
    }
}
