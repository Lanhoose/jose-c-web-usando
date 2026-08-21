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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.arquivoparanormal.app.data.PODERES_SOBREVIVENDO
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
    fun patch(novo: Personagem) { repo.salvar(novo) }

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
                        poderes = if (nome in poderes) poderes - nome else poderes + nome
                        patch(personagem.copy(habilidades = poderes.joinToString("\n")))
                    })
                    8 -> PericiasCriacao(p = personagem, selecionadas = pericias, onToggle = { nome ->
                        pericias = if (nome in pericias) pericias - nome else pericias + nome
                        val mapa = personagem.pericias.toMutableMap()
                        mapa[nome] = (mapa[nome] ?: com.arquivoparanormal.app.data.Pericia()).copy(treino = 5)
                        if (nome !in pericias) mapa[nome] = (mapa[nome] ?: com.arquivoparanormal.app.data.Pericia()).copy(treino = 0)
                        patch(personagem.copy(pericias = mapa))
                    })
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
            SeletorImagem(caminho=p.fotoArquivo, aoDefinir={ patch(p.copy(fotoArquivo=it)) }, rotuloVazio="Adicionar imagem", tamanhoPreview=110.dp)
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
private fun PoderesCriacao(p: Personagem, selecionados:Set<String>, onToggle:(String)->Unit) {
    val classePoder="Poder de ${p.classe}"
    val extras=PODERES_SOBREVIVENDO.filter{it.classe==p.classe}.map{it.nome}
    Painel(titulo="Etapa 7 — Poderes") {
        Text("Escolha os poderes disponíveis para a construção. Os pré-requisitos do livro devem ser conferidos antes de confirmar.", color=TextoFraco, style=MaterialTheme.typography.bodySmall)
        Chip(classePoder, classePoder in selecionados, { onToggle(classePoder) }, Primaria)
        if(extras.isEmpty()) Text("Poderes específicos desta classe serão exibidos aqui conforme o catálogo do aplicativo for expandido.", color=TextoFraco)
        extras.forEach { nome -> Chip(nome, nome in selecionados, { onToggle(nome) }, Acento) }
        if(p.trilha.isNotBlank()) Text("Trilha selecionada: ${p.trilha}", color=TextoClaro)
    }
}

@Composable
private fun PericiasCriacao(p: Personagem, selecionadas:Set<String>, onToggle:(String)->Unit) {
    val limite=when(p.classe){"Combatente"->1+(p.atributos["int"]?:1); "Especialista"->7+(p.atributos["int"]?:1); "Ocultista"->3+(p.atributos["int"]?:1); "Sobrevivente"->7+(p.atributos["int"]?:1); else->0}
    val automaticas = p.periciasAutomaticas.toSet()
    val jaTreinadas = p.pericias.filterValues { it.treino > 0 }.keys - automaticas
    val selecionadasManuais = selecionadas.filter { it !in automaticas }.size
    Painel(titulo="Etapa 8 — Perícias") {
        Text("Escolhidas: $selecionadasManuais/$limite (perícias automáticas da origem não gastam pontos).", color=Acento)
        Text("Perícias que você já possui treinadas aparecem cinzas e não podem ser selecionadas novamente. As concedidas pela origem também ficam bloqueadas.", color=TextoFraco, style=MaterialTheme.typography.bodySmall)
        PERICIAS.forEach { def ->
            val treinada = def.nome in automaticas || def.nome in jaTreinadas
            val selecionada = def.nome in selecionadas
            Chip("${def.nome} (${def.attr.uppercase()})", selecionada || treinada, { if (!treinada && (def.nome in selecionadas || selecionadasManuais < limite)) onToggle(def.nome) }, Primaria, enabled = !treinada)
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
