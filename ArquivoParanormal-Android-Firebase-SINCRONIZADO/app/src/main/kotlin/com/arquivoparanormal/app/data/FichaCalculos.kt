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

/** Bônus total de um teste de perícia: atributo (modificador) + treino + outros. */
fun bonusPericiaComAtributo(p: Personagem, def: PericiaDef): Int =
    (p.atributos[def.attr] ?: 1) + bonusPericia(p, def.nome)

/** Bônus de Iniciativa do agente (AGI + treino/outros na perícia Iniciativa da ficha). */
fun iniciativaDoPersonagem(p: Personagem): Int {
    val def = PERICIAS.firstOrNull { it.nome == "Iniciativa" } ?: return p.atributos["agi"] ?: 1
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
    val reflexos = bonusPericia(p, "Reflexos")
    val fortitude = bonusPericia(p, "Fortitude")
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

/** Bônus total de ataque com a arma: atributo (modificador) + bônus da perícia usada. */
fun ataqueDaArma(p: Personagem, arma: Arma): Int {
    val atributo = if (arma.pericia.equals("Pontaria", ignoreCase = true)) "agi" else "for"
    return (p.atributos[atributo] ?: 1) + bonusPericia(p, arma.pericia)
}

fun testeAtaqueDaArma(p: Personagem, arma: Arma): String =
    "1d20${formatBonus(ataqueDaArma(p, arma))}"

fun margemCritico(critico: String): String = critico.substringBefore('/').ifBlank { critico }

fun periciasConcedidasPelaOrigem(origem: String): List<String> {
    val def = ORIGENS_COMPLETAS.firstOrNull { it.nome == origem } ?: return emptyList()
    return PERICIAS.filter { def.pericias.contains(it.nome, ignoreCase = true) }.map { it.nome }
}
