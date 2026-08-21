package com.arquivoparanormal.app.data

/** Valores derivados da ficha. Tudo pode ser sobrescrito em Personagem.overrides. */
data class CalculosFicha(
    val pvMax: Int,
    val peMax: Int,
    val sanMax: Int,
    val defesa: Int,
    val esquiva: Int,
    val bloqueio: Int,
    val deslocamento: Int,
    val defesaEquipamentoAuto: Int,
    val limitePE: Int,
    val resistenciasAutomaticas: Map<String, Int>,
    val limiteCarga: Int,
    val dtRituais: Int,
    val dtHabilidades: Int,
)

private fun classeDef(p: Personagem): ClasseDef? = CLASSES.firstOrNull { it.nome == p.classe }
private fun niveisDepoisDoInicial(nex: Int): Int {
    val n = nex.coerceIn(5, 99)
    // 99% é um último marco após 95%, portanto conta como mais um avanço.
    return if (n == 99) 19 else ((n - 5) / 5).coerceAtLeast(0)
}
private fun baseDaFormula(formula: String): Int = formula.substringBefore(" ").toIntOrNull() ?: 0

fun bonusPericia(p: Personagem, nome: String): Int =
    (p.pericias[nome]?.treino ?: 0) + (p.pericias[nome]?.outros ?: 0)

/**
 * Bônus exibido em perícias. Perícia destreinada não recebe o atributo como
 * bônus separado; o bônus do atributo só entra quando a perícia está treinada.
 * Assim a ficha não exibe, por exemplo, Atletismo +3 apenas porque FOR = 3.
 */
fun bonusPericiaComAtributo(p: Personagem, def: PericiaDef): Int {
    // Em Ordem Paranormal, o atributo define a quantidade de d20 rolados;
    // ele NÃO é somado como bônus numérico. O bônus fixo vem do grau de
    // treinamento e de modificadores adicionais.
    val pericia = p.pericias[def.nome] ?: Pericia()
    return pericia.treino + pericia.outros
}

fun dadosDoAtributo(p: Personagem, def: PericiaDef): Int =
    (p.atributos[def.attr] ?: 1).let { if (it <= 0) 2 else it }

fun usaPiorDadoDoAtributo(p: Personagem, def: PericiaDef): Boolean =
    (p.atributos[def.attr] ?: 1) == 0

/** Bônus fixo de Iniciativa. A Agilidade define os dados rolados; treino/outros definem o bônus numérico. */
fun iniciativaDoPersonagem(p: Personagem): Int {
    val def = PERICIAS.firstOrNull { it.nome == "Iniciativa" } ?: return 0
    return bonusPericiaComAtributo(p, def)
}

fun calcularFicha(p: Personagem): CalculosFicha {
    val classe = classeDef(p)
    val vig = p.atributos["vig"] ?: 1
    val agi = p.atributos["agi"] ?: 1
    val pre = p.atributos["pre"] ?: 1
    val forca = p.atributos["for"] ?: 1
    val niveis = niveisDepoisDoInicial(p.nex)

    val pvBase = baseDaFormula(classe?.pv ?: "16") + vig
    val pvNivel = baseDaFormula(classe?.pvNivel ?: "3") + vig
    val peBase = baseDaFormula(classe?.pe ?: "3") + pre
    val peNivel = baseDaFormula(classe?.peNivel ?: "3")
    val sanBase = baseDaFormula(classe?.sanidade ?: "16")
    val sanNivel = baseDaFormula(classe?.sanNivel ?: "4")

    val defesaEquipamentoAuto = p.itens.filter { it.equipado }.sumOf { it.defesaBonus }
    val defesa = 10 + agi + defesaEquipamentoAuto + p.defesaEquipamento + p.defesaOutros
    val reflexosDef = PERICIAS.firstOrNull { it.nome == "Reflexos" }
    val fortitudeDef = PERICIAS.firstOrNull { it.nome == "Fortitude" }
    val reflexos = reflexosDef?.let { bonusPericiaComAtributo(p, it) } ?: 0
    val fortitude = fortitudeDef?.let { bonusPericiaComAtributo(p, it) } ?: 0
    val esquiva = defesa + reflexos
    val bloqueio = fortitude
    val limitePE = p.overrides["limitePE"] ?: limitePEBasePorNex(p.nex)
    val dtRituais = 10 + limitePE + pre
    val dtHabilidades = 10 + limitePE + pre

    val resistAuto = mutableMapOf<String, Int>()
    fun addRes(tipo: String, valor: Int) {
        if (valor == 0) return
        resistAuto[tipo] = (resistAuto[tipo] ?: 0) + valor
    }
    p.itens.filter { it.equipado }.forEach { item ->
        val geral = item.resistencias["Geral"] ?: 0
        if (geral != 0) RESISTENCIAS.forEach { addRes(it, geral) }
        item.resistencias.forEach { (tipo, valor) -> if (tipo != "Geral") addRes(tipo, valor) }
    }
    fun resistenciasDoRitualAtivo(ritual: Ritual): Map<String, Int> {
        val base = ritual.resistenciasConcedidas.toMutableMap()
        when (ritual.nome) {
            "Armadura de Sangue" -> {
                val rd = when (ritual.formaSelecionada) {
                    "Discente" -> 5
                    "Verdadeira" -> 10
                    else -> 0
                }
                if (rd > 0) listOf("Balístico", "Corte", "Impacto", "Perfuração").forEach { base[it] = maxOf(base[it] ?: 0, rd) }
            }
            "Ódio Incontrolável" -> {
                // A forma normal e a Discente fornecem RD 5 aos quatro danos físicos.
                // A forma Verdadeira troca isso por redução à metade, que não é RD e
                // por isso não é convertida artificialmente em um número.
                if (ritual.formaSelecionada != "Verdadeira") {
                    listOf("Balístico", "Corte", "Impacto", "Perfuração").forEach { base[it] = maxOf(base[it] ?: 0, 5) }
                }
            }
        }
        return base
    }
    p.rituais.filter { it.ativo }.forEach { ritual ->
        val fontes = resistenciasDoRitualAtivo(ritual)
        val geral = fontes["Geral"] ?: 0
        if (geral != 0) RESISTENCIAS.forEach { addRes(it, geral) }
        fontes.forEach { (tipo, valor) -> if (tipo != "Geral") addRes(tipo, valor) }
    }

    fun valor(chave: String, automatico: Int) = p.overrides[chave] ?: automatico

    return CalculosFicha(
        pvMax = valor("pvMax", pvBase + pvNivel * niveis),
        peMax = valor("peMax", peBase + peNivel * niveis),
        sanMax = valor("sanMax", sanBase + sanNivel * niveis),
        defesa = valor("defesa", defesa),
        esquiva = valor("esquiva", esquiva),
        bloqueio = valor("bloqueio", bloqueio),
        deslocamento = valor("deslocamento", 9),
        defesaEquipamentoAuto = defesaEquipamentoAuto,
        limitePE = limitePE,
        resistenciasAutomaticas = resistAuto,
        limiteCarga = valor("limiteCarga", 5 + forca * 5),
        dtRituais = valor("dtRituais", dtRituais),
        dtHabilidades = valor("dtHabilidades", dtHabilidades),
    )
}


fun fontesResistencia(p: Personagem, tipo: String): List<Pair<String, Int>> {
    val fontes = mutableListOf<Pair<String, Int>>()
    p.itens.filter { it.equipado }.forEach { item ->
        val geral = item.resistencias["Geral"] ?: 0
        val especifica = item.resistencias[tipo] ?: 0
        if (geral != 0) fontes += "${item.nome} · geral" to geral
        if (especifica != 0) fontes += "${item.nome} · $tipo" to especifica
    }
    p.rituais.filter { it.ativo }.forEach { ritual ->
        val fontesR = ritual.resistenciasConcedidas
        val geral = fontesR["Geral"] ?: 0
        if (geral != 0) fontes += "${ritual.nome} · ritual" to geral
        val especifica = fontesR[tipo] ?: 0
        if (especifica != 0) fontes += "${ritual.nome} · ritual" to especifica
        when (ritual.nome) {
            "Armadura de Sangue" -> {
                val rd = when (ritual.formaSelecionada) { "Discente" -> 5; "Verdadeira" -> 10; else -> 0 }
                if (rd > 0 && tipo in listOf("Balístico", "Corte", "Impacto", "Perfuração")) fontes += "Armadura de Sangue · ${ritual.formaSelecionada}" to rd
            }
            "Ódio Incontrolável" -> {
                if (ritual.formaSelecionada != "Verdadeira" && tipo in listOf("Balístico", "Corte", "Impacto", "Perfuração")) fontes += "Ódio Incontrolável · ${ritual.formaSelecionada}" to 5
            }
        }
    }
    val manual = p.resistencias[tipo] ?: 0
    if (manual != 0) fontes += "Ajuste manual" to manual
    return fontes
}

fun resistenciaTotal(p: Personagem, tipo: String): Int = fontesResistencia(p, tipo).sumOf { it.second }.coerceAtLeast(0)

/** Mantém PV/PE/SAN atuais sincronizados quando o máximo calculado muda.
 * Se o recurso estava cheio antes da alteração, ele continua cheio; se estava
 * parcialmente gasto, o valor atual é preservado e apenas limitado ao novo máximo.
 */
fun sincronizarRecursosDerivados(anterior: Personagem?, novo: Personagem): Personagem {
    if (anterior == null) {
        val calc = calcularFicha(novo)
        return novo.copy(
            pvMax = calc.pvMax,
            pvAtual = calc.pvMax,
            peMax = calc.peMax,
            peAtual = calc.peMax,
            sanMax = calc.sanMax,
            sanAtual = calc.sanMax,
        )
    }
    val antigoCalc = calcularFicha(anterior)
    val novoCalc = calcularFicha(novo)
    fun atual(oldAtual: Int, oldMax: Int, newMax: Int): Int =
        if (oldAtual >= oldMax) newMax else oldAtual.coerceIn(0, newMax)
    return novo.copy(
        pvMax = novoCalc.pvMax,
        pvAtual = atual(anterior.pvAtual, antigoCalc.pvMax, novoCalc.pvMax),
        peMax = novoCalc.peMax,
        peAtual = atual(anterior.peAtual, antigoCalc.peMax, novoCalc.peMax),
        sanMax = novoCalc.sanMax,
        sanAtual = atual(anterior.sanAtual, antigoCalc.sanMax, novoCalc.sanMax),
    )
}

/** Migração de fichas antigas: versões anteriores podiam calcular o máximo
 * corretamente, mas deixar os atuais nos valores padrão 21/3/12.
 */
fun corrigirRecursosLegados(p: Personagem): Personagem {
    val calc = calcularFicha(p)
    return p.copy(
        pvMax = calc.pvMax,
        pvAtual = if (p.pvAtual == 21 && calc.pvMax > 21) calc.pvMax else p.pvAtual.coerceAtMost(calc.pvMax),
        peMax = calc.peMax,
        peAtual = if (p.peAtual == 3 && calc.peMax > 3) calc.peMax else p.peAtual.coerceAtMost(calc.peMax),
        sanMax = calc.sanMax,
        sanAtual = if (p.sanAtual == 12 && calc.sanMax > 12) calc.sanMax else p.sanAtual.coerceAtMost(calc.sanMax),
    )
}

/** Bônus fixo do ataque: treinamento + outros. O atributo define os d20 rolados. */
fun ataqueDaArma(p: Personagem, arma: Arma): Int {
    val def = PERICIAS.firstOrNull { it.nome.equals(arma.pericia, ignoreCase = true) }
    return if (def != null) bonusPericiaComAtributo(p, def) else 0
}

fun testeAtaqueDaArma(p: Personagem, arma: Arma): String {
    val def = PERICIAS.firstOrNull { it.nome.equals(arma.pericia, ignoreCase = true) }
    val dados = def?.let { dadosDoAtributo(p, it) } ?: 1
    val pior = def?.let { usaPiorDadoDoAtributo(p, it) } == true
    return "${dados}d20${if (pior) " (pior)" else ""}${formatBonus(ataqueDaArma(p, arma))}"
}

fun margemCritico(critico: String): String = critico.substringBefore('/').ifBlank { critico }

fun periciasConcedidasPelaOrigem(origem: String): List<String> {
    val def = ORIGENS_COMPLETAS.firstOrNull { it.nome == origem } ?: return emptyList()
    return PERICIAS.filter { def.pericias.contains(it.nome, ignoreCase = true) }.map { it.nome }
}


fun resistenciaAutomatica(p: Personagem, tipo: String): Int = calcularFicha(p).resistenciasAutomaticas[tipo] ?: 0

fun danoAposResistencia(p: Personagem, tipo: String, dano: Int): Int =
    (dano - resistenciaTotal(p, tipo)).coerceAtLeast(0)
