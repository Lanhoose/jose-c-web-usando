package com.arquivoparanormal.app.data

import kotlinx.serialization.Serializable

@Serializable
data class EfeitosRegraV36(
    val bonusDefesa: Int = 0,
    val bonusDT: Int = 0,
    val bonusLimitePE: Int = 0,
    val bonusPVMax: Int = 0,
    val bonusPEMax: Int = 0,
    val bonusSANMax: Int = 0,
    val bonusCarga: Int = 0,
    val bonusAtaque: Int = 0,
    val bonusDano: Int = 0,
    val bonusPericias: Map<String, Int> = emptyMap(),
    val resistencias: Map<String, Int> = emptyMap(),
    val deslocamento: Int = 0,
    val regrasEspeciais: List<String> = emptyList(),
)

data class ValidacaoPersonagemV36(val erros: List<String> = emptyList(), val avisos: List<String> = emptyList()) { val valido get() = erros.isEmpty() }

fun Personagem.registrarAlteracao(tipo: String, descricao: String): Personagem =
    copy(historicoAlteracoes = (historicoAlteracoes + HistoricoAlteracao(tipo = tipo, descricao = descricao)).takeLast(200), atualizadoEm = System.currentTimeMillis())

fun Personagem.registrarEscolhaProgressao(nexMarco: Int, escolha: String): Personagem {
    val atual = escolhasProgressao[nexMarco].orEmpty()
    if (escolha.isBlank() || escolha in atual) return this
    return copy(escolhasProgressao = escolhasProgressao + (nexMarco to (atual + escolha)))
        .registrarAlteracao("PROGRESSAO", "NEX $nexMarco%: $escolha")
}

fun escolhasNoMarco(p: Personagem, nexMarco: Int): List<String> = p.escolhasProgressao[nexMarco].orEmpty()

fun validarPersonagemV36(p: Personagem): ValidacaoPersonagemV36 {
    val erros = mutableListOf<String>(); val avisos = mutableListOf<String>()
    if (p.nex !in 5..99) erros += "NEX deve estar entre 5% e 99%."
    if (p.prestigio < 0) erros += "Prestígio não pode ser negativo."
    if (patentePorPrestigio(p.prestigio) != p.patente) avisos += "A patente será sincronizada com o Prestígio."
    if (p.classe.isBlank()) erros += "Escolha uma classe."
    if (p.origem.isBlank()) erros += "Escolha uma origem."
    val classe = CLASSES.firstOrNull { it.nome == p.classe }
    if (classe == null) erros += "Classe inválida: ${p.classe}."
    if (p.trilha.isNotBlank() && classe != null && p.trilha !in classe.trilhas) erros += "A trilha ${p.trilha} não pertence à classe ${p.classe}."
    if (p.nex >= 10 && p.trilha.isBlank()) avisos += "NEX 10% disponível: falta escolher uma trilha."
    if (p.nex >= 35 && escolhasNoMarco(p, 35).isEmpty()) avisos += "NEX 35% disponível: falta registrar a escolha de Grau de Treinamento."
    if (p.nex >= 70 && escolhasNoMarco(p, 70).isEmpty()) avisos += "NEX 70% disponível: falta registrar a escolha de Grau de Treinamento."
    val empunhados = p.itens.count { it.empunhado }
    if (empunhados > 2) erros += "No máximo dois itens podem estar empunhados."
    return ValidacaoPersonagemV36(erros, avisos)
}

fun efeitosDosPoderesV36(p: Personagem): EfeitosRegraV36 {
    val nomes = (p.habilidades.lines().map { it.trim() } + p.poderesAutomaticos + p.poderesParanormais.map { it.nome }).filter { it.isNotBlank() }.toSet()
    var e = EfeitosRegraV36()
    fun pericia(nome: String, valor: Int) { e = e.copy(bonusPericias = e.bonusPericias + (nome to ((e.bonusPericias[nome] ?: 0) + valor))) }
    if ("Precognição" in nomes) e = e.copy(bonusDefesa = e.bonusDefesa + 2)
    if ("Sensitivo" in nomes) { pericia("Diplomacia",5); pericia("Enganação",5); pericia("Intimidação",5); pericia("Intuição",5) }
    if ("Visão do Oculto" in nomes) pericia("Percepção",5)
    if ("Potencial Aprimorado" in nomes) e = e.copy(bonusPEMax = e.bonusPEMax + p.nex)
    if ("Sangue de Ferro" in nomes) e = e.copy(bonusPVMax = e.bonusPVMax + 2 * p.nex)
    if ("Rituais Eficientes" in nomes) e = e.copy(bonusDT = e.bonusDT + 5)
    if ("Presença Poderosa" in nomes) e = e.copy(bonusLimitePE = e.bonusLimitePE + (p.atributos["pre"] ?: 0))
    return e
}

fun limiteRituaisConhecidosV36(p: Personagem): Int = (p.atributos["int"] ?: 0).coerceAtLeast(0)
fun podeAdicionarRitualV36(p: Personagem): Boolean = p.classe == "Ocultista" || p.poderesParanormais.any { it.nome == "Aprender Ritual" }
fun podeRelembrarGrimorioV36(p: Personagem, ritualId: String): Boolean = p.grimorioEmpunhado && p.rituaisGrimorio.any { it.id == ritualId }
fun custoRitualV36(r: Ritual): Int = (r.custoPE + when (r.formaSelecionada) { "Discente" -> r.discenteExtraPE; "Verdadeira" -> r.verdadeiroExtraPE; else -> 0 }).coerceAtLeast(0)
fun podeConjurarRitualV36(p: Personagem, ritual: Ritual): String? {
    if (ritual.origem == "GRIMORIO" && !podeRelembrarGrimorioV36(p, ritual.id)) return "Relembre o ritual no Grimório com o Grimório empunhado."
    if (p.peAtual < custoRitualV36(ritual)) return "PE insuficientes para conjurar este ritual."
    return null
}
fun aplicarRitualAtivoV36(p: Personagem, ritualId: String, ativo: Boolean): Personagem =
    p.copy(rituais = p.rituais.map { if (it.id == ritualId) it.copy(ativo = ativo) else it }).registrarAlteracao("RITUAL", if (ativo) "Ritual ativado: $ritualId" else "Ritual desativado: $ritualId")

fun atributoParanormalMonstruosoV36(p: Personagem): String = when (p.monstruosoElemento) {
    "Sangue" -> "for"; "Morte" -> "vig"; "Conhecimento" -> "int"; "Energia" -> "agi"; else -> p.monstruosoAtributoPE.ifBlank { "pre" }
}
fun calcularEfeitosMonstruosoV36(p: Personagem): EfeitosRegraV36 = if (p.monstruosoElemento.isBlank()) EfeitosRegraV36() else EfeitosRegraV36(regrasEspeciais = listOf("Atributo paranormal: ${atributoParanormalMonstruosoV36(p)}", "Escarificações: ${p.monstruosoEscarificacoes.size}", "Componentes: ${p.monstruosoComponentes.size}"))

fun danoFinalV36(p: Personagem, danoBruto: Int, tipo: String): Int = (danoBruto - resistenciaTotal(p, tipo)).coerceAtLeast(0)
fun aplicarDanoV36(p: Personagem, danoBruto: Int, tipo: String): Personagem {
    val dano = danoFinalV36(p, danoBruto, tipo); val novoPv = p.pvAtual - dano
    val cond = if (novoPv <= 0 && "Morrendo" !in p.condicoes) p.condicoes + "Morrendo" else p.condicoes
    return p.copy(pvAtual = novoPv, condicoes = cond).registrarAlteracao("COMBATE", "Sofreu $dano de dano $tipo (bruto $danoBruto, RD aplicada).")
}
