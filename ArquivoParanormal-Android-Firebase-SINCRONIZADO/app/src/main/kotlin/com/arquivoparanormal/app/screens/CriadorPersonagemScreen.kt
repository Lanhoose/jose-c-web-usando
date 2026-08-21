package com.arquivoparanormal.app.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import com.arquivoparanormal.app.data.ARMAS_LIVRO
import com.arquivoparanormal.app.data.ATRIBUTOS
import com.arquivoparanormal.app.data.CLASSES
import com.arquivoparanormal.app.data.ORIGENS_COMPLETAS
import com.arquivoparanormal.app.data.PoderDisponivel
import com.arquivoparanormal.app.data.poderesDisponiveisPara
import com.arquivoparanormal.app.data.sincronizarRecursosDerivados
import com.arquivoparanormal.app.data.PERICIAS
import com.arquivoparanormal.app.data.DESCRICOES_TRILHAS
import com.arquivoparanormal.app.data.espacosUsados
import com.arquivoparanormal.app.data.limiteDeCarga
import com.arquivoparanormal.app.data.Personagem
import com.arquivoparanormal.app.data.Pericia
import com.arquivoparanormal.app.data.periciasConcedidasPelaOrigem
import com.arquivoparanormal.app.data.Repositorio
import com.arquivoparanormal.app.data.Arma
import com.arquivoparanormal.app.data.Item
import com.arquivoparanormal.app.ui.Acento
import com.arquivoparanormal.app.ui.AreaTexto
import com.arquivoparanormal.app.ui.Campo
import com.arquivoparanormal.app.ui.Chip
import com.arquivoparanormal.app.ui.Painel
import com.arquivoparanormal.app.ui.Primaria
import com.arquivoparanormal.app.ui.Selecao
import com.arquivoparanormal.app.ui.Texto as CampoTexto
import com.arquivoparanormal.app.ui.TextoClaro
import com.arquivoparanormal.app.ui.TextoFraco
import com.arquivoparanormal.app.ui.SeletorImagem

@Composable
fun CriadorPersonagemScreen(
    repo: Repositorio,
    id: String,
    aoConcluir: () -> Unit,
    aoCancelar: () -> Unit,
) {
    val personagem = repo.personagem(id)
    if (personagem == null) {
        Text("Rascunho não encontrado.", color = TextoFraco, modifier = Modifier.padding(20.dp))
        return
    }
    var etapa by remember { mutableStateOf(1) }
    var poderes by remember { mutableStateOf(personagem.habilidades.split("\n").filter { it.isNotBlank() }.toSet()) }
    var pericias by remember { mutableStateOf(personagem.pericias.filterValues { it.treino > 0 }.keys.toSet()) }
    var armas by remember { mutableStateOf(personagem.armas.map { it.nome }.toSet()) }
    val totalEtapas = 10

    // As telas de cada etapa (Identidade, AtributosCriacao, ...) esperam um "setter"
    // direto: patch: (Personagem) -> Unit, chamado como patch(novoPersonagem). A versão
    // antiga aceitava uma função de transformação — (Personagem) -> Personagem — o que
    // não batia com o tipo esperado ao passar ::patch, causando erro de compilação.
    fun patch(novo: Personagem) {
        val anterior = repo.personagem(id)
        val disponiveis = poderesDisponiveisPara(novo)
        val automaticos = disponiveis.filter { it.automatico }.map { it.nome }.distinct()
        val permitidos = disponiveis.filter { !it.automatico }.map { it.nome }.toSet()
        val manuais = novo.habilidades.lines()
            .map { it.trim() }
            .filter { it.isNotBlank() && it in permitidos }
            .distinct()
        val normalizado = novo.copy(
            habilidades = (automaticos + manuais).distinct().joinToString("\n"),
            poderesAutomaticos = automaticos,
        )
        repo.salvar(sincronizarRecursosDerivados(anterior, normalizado))
    }

    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text("Criar agente", style = MaterialTheme.typography.titleLarge, color = TextoClaro)
            Text("Etapa $etapa/$totalEtapas", color = Acento)
        }
        LazyColumn(
            Modifier.weight(1f).padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 18.dp),
        ) {
            item {
                when (etapa) {
                    1 -> Identidade(p = personagem, patch = ::patch)
                    2 -> AtributosCriacao(p = personagem, patch = ::patch)
                    3 -> OrigemCriacao(p = personagem, patch = ::patch)
                    4 -> ClasseCriacao(p = personagem, patch = ::patch)
                    5 -> NexCriacao(p = personagem, patch = ::patch)
                    6 -> TrilhaCriacao(p = personagem, patch = ::patch)
                    7 -> PoderesCriacao(p = personagem, selecionados = poderes, onToggle = { nome ->
                        val novoSet = if (nome in poderes) poderes - nome else poderes + nome
                        poderes = novoSet
                        patch(personagem.copy(habilidades = novoSet.joinToString("\n")))
                    })
                    8 -> PericiasCriacao(
                        p = personagem,
                        selecionadas = pericias,
                        onChange = { novoSet, antiga, nova ->
                            pericias = novoSet
                            val mapa = personagem.pericias.toMutableMap()
                            if (antiga != null) mapa[antiga] = (mapa[antiga] ?: Pericia()).copy(treino = 0)
                            if (nova != null) mapa[nova] = (mapa[nova] ?: Pericia()).copy(treino = 5)
                            patch(personagem.copy(pericias = mapa))
                        },
                    )
                    9 -> EquipamentosCriacao(p = personagem, selecionadas = armas, onToggle = { nome ->
                        armas = if (nome in armas) armas - nome else armas + nome
                        val selecionadas = armas.mapNotNull { n -> ARMAS_LIVRO.firstOrNull { it.nome == n } }
                        val lista = selecionadas.map { d ->
                            Arma(nome=d.nome, tipo=d.grupo.substringAfter(" · ", d.grupo), grupo=d.grupo, pericia=d.pericia,
                                dano=d.dano, critico=d.critico, alcance=d.alcance, tipoDano=d.tipoDano, categoria=d.categoria,
                                espacos=d.espacos, icone=d.icone, municao=d.municao, descricao=d.descricao)
                        }
                        patch(personagem.copy(armas = lista))
                    })
                    10 -> RevisaoCriacao(p = personagem, onFinish = aoConcluir)
                }
            }
        }
        Row(
            Modifier.fillMaxWidth().padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Button(
                onClick = { if (etapa == 1) aoCancelar() else etapa-- },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = TextoClaro),
            ) { Text(if (etapa == 1) "Cancelar" else "Voltar") }
            Button(
                onClick = { if (etapa == totalEtapas) aoConcluir() else etapa++ },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Primaria),
            ) { Text(if (etapa == totalEtapas) "Criar agente" else "Continuar") }
        }
    }
}

@Composable
private fun Identidade(p: Personagem, patch: ((Personagem)->Unit)) {
    Painel(titulo = "Etapa 1 — Identidade") {
        Campo("Nome") { CampoTexto(p.nome) { patch(p.copy(nome = it)) } }
        Campo("Jogador") { CampoTexto(p.jogador) { patch(p.copy(jogador = it)) } }
        Campo("Idade") { CampoTexto(p.idade) { patch(p.copy(idade = it)) } }
        Campo("Descrição") { AreaTexto(p.descricao, { patch(p.copy(descricao = it)) }) }
        Campo("Imagem") {
            SeletorImagem(
                caminho = p.fotoArquivo,
                aoDefinir = { caminho ->
                    val thumb = caminho?.let { com.arquivoparanormal.app.data.ImagemImportador.gerarMiniaturaDataUrl(it) }
                    patch(p.copy(fotoArquivo = caminho, fotoAgenteThumb = thumb))
                },
                rotuloVazio = "Adicionar imagem",
                tamanhoPreview = 110.dp,
            )
        }
    }
}

@Composable
private fun AtributosCriacao(p: Personagem, patch: ((Personagem)->Unit)) {
    val reduzidoAZero = p.atributos.values.count { it == 0 }
    val usados = p.atributos.values.sumOf { (it - 1).coerceAtLeast(0) }
    val limite = if (reduzidoAZero == 1) 5 else 4
    Painel(titulo = "Etapa 2 — Atributos") {
        Text("Comece com 1 em cada atributo e distribua 4 pontos. Se reduzir exatamente um atributo de 1 para 0, você ganha +1 ponto adicional (5 no total). O máximo inicial é 3.", color=TextoFraco, style=MaterialTheme.typography.bodySmall)
        Text("Pontos usados: $usados/$limite", color=if (usados==limite) Acento else TextoClaro)
        ATRIBUTOS.forEach { a ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.SpaceBetween) {
                Text("${a.nome} (${a.sigla})", color=TextoClaro)
                Row(horizontalArrangement=Arrangement.spacedBy(6.dp)) {
                    Chip("−", true, { val v=(p.atributos[a.key] ?: 1); if(v>0 && (v-1>0 || reduzidoAZero==0)) patch(p.copy(atributos=p.atributos+(a.key to (v-1)))) }, Primaria)
                    Text("${p.atributos[a.key] ?: 1}", color=TextoClaro, modifier=Modifier.padding(horizontal=8.dp, vertical=8.dp))
                    Chip("+", true, { val v=p.atributos[a.key] ?: 1; val pontos=p.atributos.values.sumOf{(it-1).coerceAtLeast(0)}; val zeros=p.atributos.values.count{it==0}; val orcamento=if(zeros==1) 5 else 4; if(v<3 && pontos<orcamento) patch(p.copy(atributos=p.atributos+(a.key to (v+1)))) }, Primaria)
                }
            }
        }
    }
}

@Composable
private fun OrigemCriacao(p: Personagem, patch: ((Personagem)->Unit)) {
    Painel(titulo="Etapa 3 — Origem") {
        Selecao(p.origem, ORIGENS_COMPLETAS.map{it.nome}, { novaOrigem ->
            val novas = periciasConcedidasPelaOrigem(novaOrigem)
            val mapa = p.pericias.toMutableMap()
            p.periciasAutomaticas.forEach { nome ->
                val atual = mapa[nome]
                if (atual != null && atual.outros == 0 && atual.treino == 5) mapa.remove(nome)
            }
            novas.forEach { nome ->
                val atual = mapa[nome] ?: Pericia()
                if (atual.treino == 0 && atual.outros == 0) mapa[nome] = atual.copy(treino = 5)
            }
            patch(p.copy(origem=novaOrigem, pericias=mapa, periciasAutomaticas=novas))
        }, placeholder="Escolha uma origem")
        ORIGENS_COMPLETAS.firstOrNull{it.nome==p.origem}?.let { o ->
            Text("Perícias: ${o.pericias}", color=Acento)
            Text("Habilidade: ${o.poder}", color=TextoFraco)
        }
    }
}

@Composable
private fun ClasseCriacao(p: Personagem, patch: ((Personagem)->Unit)) {
    Painel(titulo="Etapa 4 — Classe") {
        Selecao(p.classe, CLASSES.map{it.nome}, { patch(p.copy(classe=it, trilha="")) }, placeholder="Escolha uma classe")
        CLASSES.firstOrNull{it.nome==p.classe}?.let { c ->
            Text(c.desc, color=TextoFraco)
            Text("Perícias de classe: ${c.pericias}", color=Acento)
            if (c.nome == "Sobrevivente") {
                Text("⚠ Sobrevivente usa Estágios no PDF, não a progressão normal por NEX. A ficha comum por NEX não deve ser usada para simular os Estágios dessa classe.", color = Acento, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun NexCriacao(p: Personagem, patch: ((Personagem)->Unit)) {
    val nexOptions=(5..95 step 5).map{it.toString()} + "99"
    Painel(titulo="Etapa 5 — NEX") {
        Selecao(p.nex.toString(), nexOptions, { patch(p.copy(nex=it.toInt(), patente=patenteParaNex(it.toInt()))) }, placeholder="Escolha o NEX")
        Text("NEX mede a exposição paranormal. Um agente iniciante começa em 5%.", color=TextoFraco)
    }
}

private fun patenteParaNex(nex:Int)=when { nex<=5 -> "Recruta (NEX 5%)"; nex<=35 -> "Operador (NEX 10-35%)"; nex<=65 -> "Agente Especial (NEX 40-65%)"; nex<=95 -> "Oficial de Operações (NEX 70-95%)"; else -> "Agente de Elite (NEX 99%)" }

@Composable
private fun TrilhaCriacao(p: Personagem, patch: ((Personagem)->Unit)) {
    val trilhas=CLASSES.firstOrNull{it.nome==p.classe}?.trilhas.orEmpty()
    Painel(titulo="Etapa 6 — Trilha") {
        if(p.nex<10) {
            Text("A trilha é escolhida a partir de NEX 10%. Você pode continuar sem trilha por enquanto.", color=TextoFraco)
            Selecao(p.trilha, listOf("Nenhuma"), { patch(p.copy(trilha="")) }, placeholder="Nenhuma")
        } else {
            Selecao(if(p.trilha.isBlank()) "Nenhuma" else p.trilha, listOf("Nenhuma")+trilhas, { patch(p.copy(trilha=if(it=="Nenhuma") "" else it)) }, placeholder="Escolha uma trilha")
            if (p.trilha.isNotBlank()) {
                Text("O que ela faz", color=Acento, style=MaterialTheme.typography.titleSmall, modifier=Modifier.padding(top=8.dp))
                Text(DESCRICOES_TRILHAS[p.trilha] ?: "Consulte os detalhes da trilha no Compêndio.", color=TextoFraco, style=MaterialTheme.typography.bodySmall)
            } else {
                Text("Toque em uma trilha para ver o resumo do que ela faz e os benefícios que oferece.", color=TextoFraco, style=MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun PoderesCriacao(p: Personagem, selecionados: Set<String>, onToggle: (String) -> Unit) {
    val disponiveis = poderesDisponiveisPara(p)
    val automaticos = disponiveis.filter { it.automatico }
    val manuais = disponiveis.filter { !it.automatico }
    val selecionadosManuais = selecionados.filter { it in manuais.map(PoderDisponivel::nome) }.toSet()
    val limite = com.arquivoparanormal.app.data.quantidadePoderesDeClasse(p)
    Painel(titulo = "Etapa 7 — Poderes") {
        Text(
            "Os poderes abaixo são filtrados pela sua origem, classe, trilha e NEX. Poderes incompatíveis não aparecem como opção.",
            color = TextoFraco,
            style = MaterialTheme.typography.bodySmall,
        )
        Text("Poderes de classe escolhidos: ${selecionadosManuais.size}/$limite", color = Acento)

        if (automaticos.isNotEmpty()) {
            Text("Concedidos automaticamente", color = Acento, style = MaterialTheme.typography.titleSmall)
            automaticos.forEach { poder ->
                PainelPoder(poder, selecionado = true, bloqueado = true, onToggle = {})
            }
        }

        if (manuais.isNotEmpty()) {
            Text("Poderes que você pode escolher", color = TextoClaro, style = MaterialTheme.typography.titleSmall)
            manuais.forEach { poder ->
                val selecionado = poder.nome in selecionadosManuais
                val podeSelecionar = selecionado || selecionadosManuais.size < limite
                PainelPoder(
                    poder = poder,
                    selecionado = selecionado,
                    bloqueado = !podeSelecionar,
                    onToggle = { if (podeSelecionar) onToggle(poder.nome) },
                )
            }
        } else {
            Text("Neste NEX você ainda não possui um poder de classe para escolher. Os poderes automáticos continuam visíveis acima.", color = TextoFraco)
        }
    }
}

@Composable
private fun PainelPoder(
    poder: PoderDisponivel,
    selecionado: Boolean,
    bloqueado: Boolean,
    onToggle: () -> Unit,
) {
    var detalhes by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Chip(
            texto = when {
                poder.automatico -> "🔒 ${poder.nome} · ${poder.categoria}"
                selecionado -> "✓ ${poder.nome} · ${poder.categoria}"
                bloqueado -> "${poder.nome} · indisponível"
                else -> poder.nome
            },
            ativo = selecionado || poder.automatico,
            aoClicar = onToggle,
            corAtiva = if (poder.automatico || selecionado) Acento else Primaria,
            enabled = !bloqueado,
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                poder.descricao,
                color = TextoFraco,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 3,
                modifier = Modifier.weight(1f).padding(start = 8.dp),
            )
            TextButton(onClick = { detalhes = true }) { Text("Ver detalhes") }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("NEX mínimo: ${poder.nexMin}%", color = TextoFraco, style = MaterialTheme.typography.labelSmall)
            if (poder.custo.isNotBlank()) Text("Custo: ${poder.custo}", color = Acento, style = MaterialTheme.typography.labelSmall)
            if (poder.trilha.isNotBlank()) Text("Trilha: ${poder.trilha}", color = Acento, style = MaterialTheme.typography.labelSmall)
        }
        if (poder.requisito.isNotBlank()) {
            Text("Requisito: ${poder.requisito}", color = TextoFraco, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(start = 8.dp))
        }
    }
    if (detalhes) {
        AlertDialog(
            onDismissRequest = { detalhes = false },
            title = { Text(poder.nome) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(poder.descricao, color = TextoClaro)
                    Text("Categoria: ${poder.categoria}", color = Acento)
                    if (poder.classe.isNotBlank()) Text("Classe: ${poder.classe}")
                    if (poder.trilha.isNotBlank()) Text("Trilha: ${poder.trilha}")
                    if (poder.origem.isNotBlank()) Text("Origem: ${poder.origem}")
                    Text("NEX mínimo: ${poder.nexMin}%")
                    if (poder.requisito.isNotBlank()) Text("Requisitos: ${poder.requisito}")
                    if (poder.custo.isNotBlank()) Text("Custo: ${poder.custo}")
                    if (poder.pagina > 0) Text("Referência: ${poder.livro.titulo}, pág. ${poder.pagina}", color = TextoFraco)
                }
            },
            confirmButton = { TextButton(onClick = { detalhes = false }) { Text("Fechar") } },
        )
    }
}

@Composable
private fun PericiasCriacao(
    p: Personagem,
    selecionadas: Set<String>,
    onChange: (novoSet: Set<String>, antiga: String?, nova: String?) -> Unit,
) {
    val limite = when (p.classe) {
        "Combatente" -> 1 + (p.atributos["int"] ?: 1)
        "Especialista" -> 7 + (p.atributos["int"] ?: 1)
        "Ocultista" -> 3 + (p.atributos["int"] ?: 1)
        "Sobrevivente" -> 7 + (p.atributos["int"] ?: 1)
        else -> 0
    }
    val automaticas = p.periciasAutomaticas.toSet()
    val selecionadasManuais = selecionadas.filter { it !in automaticas }.toSet()
    var emTroca by remember { mutableStateOf<String?>(null) }

    Painel(titulo = "Etapa 8 — Perícias") {
        Text("Escolhidas: ${selecionadasManuais.size}/$limite · ${automaticas.size} automáticas da origem", color = Acento)
        Text(
            "Perícias concedidas pela origem ficam cinzas e bloqueadas. As que você escolheu manualmente podem ser trocadas a qualquer momento.",
            color = TextoFraco,
            style = MaterialTheme.typography.bodySmall,
        )
        if (emTroca != null) {
            Text("Escolha uma nova perícia para substituir ${emTroca}.", color = Acento, style = MaterialTheme.typography.bodySmall)
        }
        PERICIAS.forEach { def ->
            val automatica = def.nome in automaticas
            val selecionada = def.nome in selecionadasManuais
            val podeEscolher = !automatica && (emTroca != null || selecionada || selecionadasManuais.size < limite)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Chip(
                    "${def.nome} (${def.attr.uppercase()})",
                    selecionada || automatica,
                    {
                        if (!automatica) {
                            if (emTroca != null) {
                            if (def.nome != emTroca && def.nome !in selecionadasManuais) {
                                val novoSet = (selecionadasManuais - emTroca!! + def.nome)
                                onChange(novoSet, emTroca, def.nome)
                                emTroca = null
                            }
                        } else if (!selecionada && selecionadasManuais.size < limite) {
                            onChange(selecionadasManuais + def.nome, null, def.nome)
                        }
                        }
                    },
                    if (automatica) TextoFraco else Primaria,
                    enabled = podeEscolher,
                )
                if (selecionada && !automatica) {
                    TextButton(onClick = { emTroca = def.nome }) { Text("Trocar") }
                }
            }
        }
    }
}

@Composable
private fun EquipamentosCriacao(p: Personagem, selecionadas:Set<String>, onToggle:(String)->Unit) {
    val limite = limiteDeCarga(p.atributos["for"] ?: 1)
    val cargaAtual = espacosUsados(p)
    val selecionadasCarga = ARMAS_LIVRO.filter { it.nome in selecionadas }.sumOf { it.espacos }
    val cargaComSelecao = p.itens.sumOf { it.espacos * it.qtd } + selecionadasCarga
    val excedeu = cargaComSelecao > limite
    Painel(titulo="Etapa 9 — Equipamentos") {
        Text("Capacidade de carga: ${"%.1f".format(limite.toDouble())} espaços", color=TextoClaro)
        Text("Carga atual: ${"%.1f".format(cargaAtual)} · Com as armas selecionadas: ${"%.1f".format(cargaComSelecao)} / ${"%.1f".format(limite.toDouble())}", color=if (excedeu) Color(0xFFE53935) else Acento)
        if (excedeu) {
            Text("⚠ Você ultrapassou sua capacidade de carga em ${"%.1f".format(cargaComSelecao - limite)} espaços. Remova equipamento antes de continuar.", color=Color(0xFFE53935), style=MaterialTheme.typography.bodySmall)
        }
        Text("Escolha as armas iniciais. Itens e munições adicionados depois também contam para a carga.", color=TextoFraco, style=MaterialTheme.typography.bodySmall)
        ARMAS_LIVRO.forEach { arma ->
            val selecionada = arma.nome in selecionadas
            val seriaCarga = if (selecionada) cargaComSelecao - arma.espacos else cargaComSelecao + arma.espacos
            val podeAdicionar = selecionada || seriaCarga <= limite
            val label="${arma.nome} — Cat. ${arma.categoria} · ${arma.espacos} esp."
            Chip(label, selecionada, { if (podeAdicionar) onToggle(arma.nome) }, Primaria, enabled = podeAdicionar)
            Text(arma.descricao, color=TextoFraco, style=MaterialTheme.typography.bodySmall, modifier=Modifier.padding(start=8.dp, bottom=4.dp))
        }
        Spacer(Modifier.height(6.dp))
        Text("Armas selecionadas: ${selecionadas.size}", color=Acento)
    }
}

@Composable
private fun RevisaoCriacao(p: Personagem, onFinish:()->Unit) {
    Painel(titulo="Etapa 10 — Revisão") {
        Text(p.nome.ifBlank{"Novo Agente"}, style=MaterialTheme.typography.headlineSmall, color=TextoClaro)
        Text("Jogador: ${p.jogador.ifBlank{"—"}} · Idade: ${p.idade.ifBlank{"—"}}", color=TextoFraco)
        Text("Origem: ${p.origem.ifBlank{"—"}}", color=TextoClaro)
        Text("Classe: ${p.classe} · NEX ${p.nex}%", color=TextoClaro)
        Text("Trilha: ${p.trilha.ifBlank{"Nenhuma"}}", color=TextoClaro)
        Text("Poderes: ${p.habilidades.lines().filter{it.isNotBlank()}.joinToString(", ").ifBlank{"Nenhum"}}", color=TextoClaro)
        Text("Perícias treinadas: ${p.pericias.filterValues{it.treino>0}.keys.sorted().joinToString(", ").ifBlank{"Nenhuma"}}", color=TextoClaro)
        Text("Armas: ${p.armas.joinToString(", "){it.nome}.ifBlank{"Nenhuma"}}", color=TextoClaro)
        Text("Revise os dados e toque em Criar agente para salvar a ficha.", color=TextoFraco)
    }
}
