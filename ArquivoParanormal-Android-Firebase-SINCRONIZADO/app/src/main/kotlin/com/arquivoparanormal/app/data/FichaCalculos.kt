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
    val limiteCarga: Int,
    val dtRituais: Int,
    val dtHabilidades: Int,
)

private fun classeDef(p: Personagem): ClasseDef? = CLASSES.firstOrNull { it.nome == p.classe }
private fun niveisDepoisDoInicial(nex: Int): Int = ((nex.coerceIn(5, 99) - 5) / 5).coerceAtLeast(0)
private fun baseDaFormula(formula: String): Int = formula.substringBefore(" ").toIntOrNull() ?: 0

fun bonusPericia(p: Personagem, nome: String): Int =
    (p.pericias[nome]?.treino ?: 0) + (p.pericias[nome]?.outros ?: 0)

/**
 * Bônus exibido em perícias. Perícia destreinada não recebe o atributo como
 * bônus separado; o bônus do atributo só entra quando a perícia está treinada.
 * Assim a ficha não exibe, por exemplo, Atletismo +3 apenas porque FOR = 3.
 */
fun bonusPericiaComAtributo(p: Personagem, def: PericiaDef): Int {
    val pericia = p.pericias[def.nome] ?: Pericia()
    if (pericia.treino <= 0) return pericia.outros
    return (p.atributos[def.attr] ?: 1) + pericia.treino + pericia.outros
}

/** Bônus de Iniciativa do agente (AGI + treino/outros na perícia Iniciativa da ficha). */
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

    val defesa = 10 + agi + p.defesaEquipamento + p.defesaOutros
    val reflexosDef = PERICIAS.firstOrNull { it.nome == "Reflexos" }
    val fortitudeDef = PERICIAS.firstOrNull { it.nome == "Fortitude" }
    val reflexos = reflexosDef?.let { bonusPericiaComAtributo(p, it) } ?: 0
    val fortitude = fortitudeDef?.let { bonusPericiaComAtributo(p, it) } ?: 0
    val esquiva = defesa + reflexos
    val bloqueio = fortitude
    val dtRituais = 10 + (p.nex.coerceIn(5, 99) / 5) + pre
    val dtHabilidades = 10 + (p.nex.coerceIn(5, 99) / 5) + pre

    fun valor(chave: String, automatico: Int) = p.overrides[chave] ?: automatico

    return CalculosFicha(
        pvMax = valor("pvMax", pvBase + pvNivel * niveis),
        peMax = valor("peMax", peBase + peNivel * niveis),
        sanMax = valor("sanMax", sanBase + sanNivel * niveis),
        defesa = valor("defesa", defesa),
        esquiva = valor("esquiva", esquiva),
        bloqueio = valor("bloqueio", bloqueio),
        deslocamento = valor("deslocamento", 9),
        limiteCarga = valor("limiteCarga", 5 + forca * 5),
        dtRituais = valor("dtRituais", dtRituais),
        dtHabilidades = valor("dtHabilidades", dtHabilidades),
    )
}

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

/** Bônus total de ataque com a arma: atributo (modificador) + bônus da perícia usada. */
fun ataqueDaArma(p: Personagem, arma: Arma): Int {
    val def = PERICIAS.firstOrNull { it.nome.equals(arma.pericia, ignoreCase = true) }
    return if (def != null) bonusPericiaComAtributo(p, def) else 0
}

fun testeAtaqueDaArma(p: Personagem, arma: Arma): String =
    "1d20${formatBonus(ataqueDaArma(p, arma))}"

fun margemCritico(critico: String): String = critico.substringBefore('/').ifBlank { critico }

fun periciasConcedidasPelaOrigem(origem: String): List<String> {
    val def = ORIGENS_COMPLETAS.firstOrNull { it.nome == origem } ?: return emptyList()
    return PERICIAS.filter { def.pericias.contains(it.nome, ignoreCase = true) }.map { it.nome }
}
