package com.arquivoparanormal.app.data

/** Valores derivados da ficha. Tudo pode ser sobrescrito em Personagem.overrides. */
data class CalculosFicha(
    val pvMax: Int,
    val peMax: Int,
    val sanMax: Int,
    val defesa: Int,
    val esquiva: Int,
    val bloqueio: Int,
    val rdGeral: Int,
    val deslocamento: Int,
    val defesaEquipamentoAuto: Int,
    val limitePE: Int,
    val resistenciasAutomaticas: Map<String, Int>,
    val limiteCarga: Int,
    val cargaMaxima: Int,
    val cargaSobrecarregada: Boolean,
    val penalidadeCarga: Int,
    val reducaoDeslocamentoCarga: Int,
    val defesaCorpoACorpo: Int,
    val defesaDistancia: Int,
    val bloqueiaAcoes: Boolean,
    val penalidadeTestesCondicao: Int,
    val penalidadeAtaqueDados: Int,
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
    return pericia.treino + pericia.outros + efeitosDosPoderesV36(p).bonusPericias[def.nome].orZero()
}

private fun Int?.orZero(): Int = this ?: 0

fun dadosDoAtributo(p: Personagem, def: PericiaDef): Int =
    (p.atributos[def.attr] ?: 1).let { if (it <= 0) 2 else it }

fun usaPiorDadoDoAtributo(p: Personagem, def: PericiaDef): Boolean =
    (p.atributos[def.attr] ?: 1) == 0

/** Bônus fixo de Iniciativa. A Agilidade define os dados rolados; treino/outros definem o bônus numérico. */
fun iniciativaDoPersonagem(p: Personagem): Int {
    val def = PERICIAS.firstOrNull { it.nome == "Iniciativa" } ?: return 0
    return bonusPericiaComAtributo(p, def)
}

/** Limite de círculo para o poder paranormal Aprender Ritual.
 * O Livro de Regras define 1º círculo normalmente, até 2º a partir de NEX 45%
 * e até 3º a partir de NEX 75%. O 4º círculo é obtido por progressões próprias
 * (como a classe Ocultista) e não deve aparecer no seletor genérico de Aprender Ritual.
 */
fun maxCirculoAprenderRitual(nex: Int): Int = when {
    nex >= 75 -> 3
    nex >= 45 -> 2
    else -> 1
}

/** Rituais que podem ser adicionados manualmente pela mesma regra usada na criação. */

fun quantidadeRituaisClasseOcultistaV36(p: Personagem): Int = if (p.classe == "Ocultista") {
    3 + if (p.nex == 99) 19 else ((p.nex - 5) / 5).coerceAtLeast(0)
} else 0

fun rituaisClasseOcultistaConhecidosV36(p: Personagem): Int =
    p.rituais.count { it.origem == "CLASSE_OCULTISTA" }

fun rituaisNormaisConhecidosV36(p: Personagem): Int =
    p.rituais.count { it.origem == "APRENDER_RITUAL" }

/** Quantidade de escolhas de poder de classe previstas pela tabela de NEX.
 * NEX 15, 30, 45, 60, 75 e 90 = seis escolhas. Trilhas e Versatilidade
 * são habilidades separadas e não consomem esses seis slots automaticamente. */
fun quantidadePoderesDeClasseV36(p: Personagem): Int = listOf(15, 30, 45, 60, 75, 90).count { it <= p.nex }

fun poderesDeClasseManuaisConhecidosV36(p: Personagem): Int {
    val nomesClasse = PODERES_CLASSE_REGRAS.filter { it.classe == p.classe }.map { it.nome }.toSet() - setOf("Transcender")
    // Cada escolha de Transcender também consome uma vaga de poder de classe prevista pelo NEX,
    // mesmo não aparecendo na lista de texto de habilidades (ele é rastreado por transcenderCount).
    return p.habilidades.lines().map { it.trim() }.count { it.isNotBlank() && it in nomesClasse } + p.transcenderCount
}

fun podeAdicionarPoderDeClasseV36(p: Personagem, poder: PoderDisponivel): Boolean {
    if (poder.automatico || poder.nome == "Transcender") return false
    if (poder.classe != p.classe) return false
    return poderesDeClasseManuaisConhecidosV36(p) < quantidadePoderesDeClasseV36(p)
}

fun motivoBloqueioRitual(p: Personagem, ritual: RitualDef): String? {
    val podeAprender = p.classe == "Ocultista" || p.poderesParanormais.any { it.nome == "Aprender Ritual" } ||
        p.habilidades.lines().map { it.trim() }.contains("Aprender Ritual")
    if (!podeAprender) return "Você precisa ser Ocultista ou possuir a habilidade/poder Aprender Ritual para aprender rituais manualmente."
    fun circuloNumero(c: String) = when (c) { "1º" -> 1; "2º" -> 2; "3º" -> 3; "4º" -> 4; else -> 99 }
    val numero = circuloNumero(ritual.circulo)
    val max = maxCirculoAprenderRitual(p.nex)
    if (p.classe == "Ocultista" && numero > when { p.nex >= 85 -> 4; p.nex >= 55 -> 3; p.nex >= 25 -> 2; else -> 1 }) {
        val req = when (numero) { 2 -> 25; 3 -> 55; 4 -> 85; else -> 5 }
        return "Requer NEX $req% para lançar o ${ritual.circulo} círculo como Ocultista."
    }
    if (numero > max) {
        val req = when (numero) { 2 -> 45; 3 -> 75; 4 -> 100; else -> 5 }
        return if (numero == 4) "O 4º círculo não é liberado pelo poder Aprender Ritual. Ele é obtido pela progressão própria do Ocultista em NEX 85%."
        else "Requer NEX $req% para Aprender Ritual de ${ritual.circulo} círculo. Seu NEX atual é ${p.nex}%."
    }
    val conhecido = p.rituais.any { r ->
        if (ritual.nome == "Amaldiçoar Arma") r.nome == ritual.nome && r.circulo == ritual.circulo
        else r.nome == ritual.nome && r.elemento == ritual.elemento && r.circulo == ritual.circulo
    }
    if (conhecido) return "Você já conhece este ritual."
    return null
}

fun rituaisElegiveisParaAprender(p: Personagem, ritualParaSubstituirId: String? = null): List<RitualDef> {
    val max = maxCirculoAprenderRitual(p.nex)
    fun circuloNumero(c: String) = when (c) { "1º" -> 1; "2º" -> 2; "3º" -> 3; "4º" -> 4; else -> 99 }
    val conhecidos = p.rituais
        .filterNot { it.id == ritualParaSubstituirId }
        .map { if (it.nome == "Amaldiçoar Arma") "${it.nome}|${it.circulo}" else "${it.nome}|${it.elemento}|${it.circulo}" }
        .toSet()
    return RITUAIS_COMPLETOS
        .filter { circuloNumero(it.circulo) <= max }
        .filterNot {
            val chave = if (it.nome == "Amaldiçoar Arma") "${it.nome}|${it.circulo}" else "${it.nome}|${it.elemento}|${it.circulo}"
            chave in conhecidos
        }
        .distinctBy { if (it.nome == "Amaldiçoar Arma") "${it.nome}|${it.circulo}" else "${it.nome}|${it.elemento}|${it.circulo}" }
}



data class NexDesbloqueio(
    val nex: Int,
    val titulo: String,
    val descricao: String,
)

/**
 * Mapa visual da progressão de NEX. Baseado na Tabela de Progressão do Livro
 * de Regras e nas tabelas individuais das três classes básicas.
 */
fun progressaoDeNex(p: Personagem): List<NexDesbloqueio> {
    val marcos = listOf(5,10,15,20,25,30,35,40,45,50,55,60,65,70,75,80,85,90,95,99)
    val classe = p.classe
    val trilha = p.trilha.ifBlank { "sua trilha" }
    val classeDef = CLASSES.firstOrNull { it.nome == classe }
    val pvPorNex = baseDaFormula(classeDef?.pvNivel ?: "0")
    val pePorNex = baseDaFormula(classeDef?.peNivel ?: "0")
    val sanPorNex = baseDaFormula(classeDef?.sanNivel ?: "0")
    val limitePe = { n: Int -> limitePEBasePorNex(n) + if (p.origem == "Universitário") 1 else 0 }
    val trilhaPoder = { n: Int ->
        PODERES_TRILHA_REGRAS.firstOrNull {
            it.trilha == p.trilha && it.classe.split("/").contains(classe) && it.nexMin == n
        }?.nome
    }
    val itensOrigem = { n: Int ->
        buildList {
            when (p.origem) {
                "Desgarrado" -> if (n >= 5) add("Calejado: +${n / 5} PV pela origem neste NEX")
                "Vítima" -> if (n >= 5) add("Cicatrizes Psicológicas: +${n / 5} SAN pela origem neste NEX")
                "Universitário" -> {
                    if (n == 5) add("Dedicação: +1 PE e +1 no limite de PE por turno")
                    if (n > 5 && n % 2 == 5 % 2) add("Dedicação: +1 PE adicional neste marco; o limite de PE por turno permanece +1")
                }
                "Sobrevivente" -> Unit // Estágios próprios; não usar a progressão normal de NEX.
                else -> Unit
            }
            if (p.poderesAutomaticos.contains("Companheiro Animal") || p.habilidades.lines().any { it.trim() == "Companheiro Animal" }) {
                if (n == 35) add("Companheiro Animal: o companheiro passa a conceder o bônus de um tipo de aliado")
                if (n == 70) add("Companheiro Animal: o companheiro também concede a habilidade do tipo de aliado")
            }
        }
    }
    return marcos.map { n ->
        val itens = mutableListOf<String>()
        if (n == 5) {
            when (classe) {
                "Combatente" -> itens += "Ataque Especial"
                "Especialista" -> itens += "Eclético e Perito"
                "Ocultista" -> itens += "Escolhido pelo Outro Lado: 1º círculo + 3 rituais de 1º círculo"
                else -> itens += "Benefícios iniciais da classe"
            }
            itens += "PV ${classeDef?.pv ?: "—"} · PE ${classeDef?.pe ?: "—"} · SAN ${classeDef?.sanidade ?: "—"}"
            itens += "Limite de PE por turno: ${limitePe(5)}"
        } else {
            itens += "PV +$pvPorNex · PE +$pePorNex · SAN +$sanPorNex neste avanço"
            itens += "Limite de PE por turno: ${limitePe(n)}"
        }
        when (n) {
            10 -> itens += if (p.trilha.isBlank()) "Escolher uma trilha e receber o primeiro poder dela" else "Trilha: ${trilhaPoder(10) ?: "primeiro poder de $trilha"}"
            15,30,45,60,75,90 -> itens += "Poder de $classe à sua escolha"
            20,50,80,95 -> {
                itens += "Aumento de atributo +1"
                itens += "Se aumentar Intelecto: +1 perícia e aumenta a capacidade de aprender rituais; Presença aumenta PE retroativamente; Vigor aumenta PV retroativamente"
            }
            35,70 -> {
                val qtd = when (classe) { "Combatente" -> "2 + INT"; "Especialista" -> "5 + INT"; "Ocultista" -> "3 + INT"; else -> "perícias elegíveis" }
                itens += "Grau de treinamento: escolha $qtd perícias treinadas para subir um grau"
            }
            40,65,99 -> itens += "Trilha: ${trilhaPoder(n) ?: "novo poder de $trilha"}"
            50 -> itens += "Versatilidade: poder de $classe ou primeiro poder de outra trilha da classe"
        }
        when (classe) {
            "Combatente" -> when (n) {
                25 -> itens += "Ataque Especial: 3 PE, +10"
                55 -> itens += "Ataque Especial: 4 PE, +15"
                85 -> itens += "Ataque Especial: 5 PE, +20"
            }
            "Especialista" -> when (n) {
                25 -> itens += "Perito: 3 PE, +1d8"
                40 -> itens += "Engenhosidade: pode alcançar benefícios de veterano"
                55 -> itens += "Perito: 4 PE, +1d10"
                75 -> itens += "Engenhosidade: pode alcançar benefícios de expert"
                85 -> itens += "Perito: 5 PE, +1d12"
            }
            "Ocultista" -> {
                if (n > 5) itens += "Escolhido pelo Outro Lado: aprende 1 ritual que possa lançar (não conta no limite de rituais conhecidos)"
                when (n) {
                    25 -> itens += "Acesso ao 2º círculo"
                    55 -> itens += "Acesso ao 3º círculo"
                    85 -> itens += "Acesso ao 4º círculo"
                }
            }
        }
        itens += itensOrigem(n)
        NexDesbloqueio(n, if (n == p.nex) "NEX atual" else "NEX $n%", itens.distinct().joinToString(" • "))
    }
}

fun proximoNex(p: Personagem): Int? = listOf(10,15,20,25,30,35,40,45,50,55,60,65,70,75,80,85,90,95,99).firstOrNull { it > p.nex }

data class EfeitosCondicoes(
    val penalidadeTestes: Int = 0,
    val penalidadeAtaqueDados: Int = 0,
    val penalidadeDefesa: Int = 0,
    val defesaCorpoACorpo: Int = 0,
    val defesaDistancia: Int = 0,
    val deslocamento: Int? = null,
    val bloqueiaAcoes: Boolean = false,
)

/** Efeitos mecânicos das condições principais, separados da lista visual. */
fun efeitosDasCondicoes(condicoes: List<String>): EfeitosCondicoes {
    var testes = 0
    var ataqueDados = 0
    var defesa = 0
    var corpo = 0
    var distancia = 0
    var deslocamento: Int? = null
    var bloqueia = false
    if ("Abalado" in condicoes) testes -= 1
    if ("Agarrado" in condicoes) { ataqueDados -= 1; defesa -= 5 } // desprevenido
    if ("Apavorado" in condicoes) testes -= 2
    if ("Atordoado" in condicoes) { defesa -= 5; bloqueia = true } // desprevenido + sem ações
    if ("Desprevenido" in condicoes) defesa -= 5
    if ("Vulnerável" in condicoes) defesa -= 2
    if ("Caído" in condicoes) {
        ataqueDados -= 2
        corpo -= 5
        distancia += 5
        deslocamento = 1
    }
    if ("Paralisado" in condicoes) { defesa -= 5; bloqueia = true }
    if ("Inconsciente" in condicoes) { defesa -= 5; bloqueia = true }
    return EfeitosCondicoes(testes, ataqueDados, defesa, corpo, distancia, deslocamento, bloqueia)
}

fun estadoSanidade(p: Personagem, sanMax: Int): String = when {
    p.insano -> "Insano"
    p.enlouquecendo || p.sanAtual <= 0 -> "Enlouquecendo"
    p.perturbado || p.sanAtual * 2 < sanMax -> "Perturbado"
    else -> "Estável"
}

fun registrarTurnoEnlouquecendo(p: Personagem): Personagem {
    if (p.insano) return p
    val novosTurnos = (p.turnosEnlouquecendo + 1).coerceAtMost(3)
    return p.copy(enlouquecendo = true, turnosEnlouquecendo = novosTurnos, insano = novosTurnos >= 3)
}

fun registrarTurnoMorrendo(p: Personagem): Personagem {
    val novos = (p.turnosMorrendo + 1).coerceAtMost(3)
    return p.copy(turnosMorrendo = novos)
}

fun estabilizar(p: Personagem): Personagem = p.copy(turnosMorrendo = 0, condicoes = p.condicoes - "Morrendo")

fun calcularFicha(p: Personagem): CalculosFicha {
    val classe = classeDef(p)
    val vig = p.atributos["vig"] ?: 1
    val agi = p.atributos["agi"] ?: 1
    val pre = p.atributos["pre"] ?: 1
    val forca = p.atributos["for"] ?: 1
    val intelecto = p.atributos["int"] ?: 1
    val niveis = niveisDepoisDoInicial(p.nex)
    val efeitosCondicoes = efeitosDasCondicoes(p.condicoes)
    val efeitosPoderes = efeitosDosPoderesV36(p)
    val tecnico = p.classe == "Especialista" && p.trilha == "Técnico"
    val limiteCargaCalculado = limiteDeCarga(forca, intelecto, tecnico)
    val cargaSobrecarregada = espacosUsados(p) > limiteCargaCalculado

    val pvBase = baseDaFormula(classe?.pv ?: "16") + vig
    val pvNivel = baseDaFormula(classe?.pvNivel ?: "3") + vig
    val peBase = baseDaFormula(classe?.pe ?: "3") + pre
    val peNivel = baseDaFormula(classe?.peNivel ?: "3") + pre
    val sanBase = baseDaFormula(classe?.sanidade ?: "16")
    val sanNivel = baseDaFormula(classe?.sanNivel ?: "4")

    val defesaEquipamentoAuto = p.itens.filter { it.equipado }.sumOf { it.defesaBonus }
    val defesaBase = 10 + agi + defesaEquipamentoAuto + p.defesaEquipamento + p.defesaOutros
    val defesa = defesaBase + efeitosPoderes.bonusDefesa + efeitosCondicoes.penalidadeDefesa + if (cargaSobrecarregada) -5 else 0
    val reflexosDef = PERICIAS.firstOrNull { it.nome == "Reflexos" }
    val fortitudeDef = PERICIAS.firstOrNull { it.nome == "Fortitude" }
    val reflexos = reflexosDef?.let { bonusPericiaComAtributo(p, it) } ?: 0
    val fortitude = fortitudeDef?.let { bonusPericiaComAtributo(p, it) } ?: 0
    val esquiva = defesa + reflexos
    val bloqueio = fortitude
    val limitePEBase = limitePEBasePorNex(p.nex)
    val bonusLimitePEOrigem = if (p.origem == "Universitário") 1 else 0
    val limitePE = p.overrides["limitePE"] ?: (limitePEBase + bonusLimitePEOrigem + efeitosPoderes.bonusLimitePE)
    val dtRituais = 10 + limitePE + pre + efeitosPoderes.bonusDT
    val dtHabilidades = 10 + limitePE + pre + efeitosPoderes.bonusDT
    fun valor(chave: String, automatico: Int): Int = p.overrides[chave] ?: automatico

    val resistAuto = mutableMapOf<String, Int>()
    fun addRes(tipo: String, valor: Int) {
        if (valor == 0) return
        if (tipo == "Geral") {
            RESISTENCIAS.forEach { r -> resistAuto[r] = (resistAuto[r] ?: 0) + valor }
        } else if (tipo == "Fisico") {
            listOf("Balístico", "Corte", "Impacto", "Perfuração").forEach { r -> resistAuto[r] = (resistAuto[r] ?: 0) + valor }
        } else {
            resistAuto[tipo] = (resistAuto[tipo] ?: 0) + valor
        }
    }
    // Fontes permanentes que a ficha precisa transformar em RD automaticamente.
    val habilidades = p.habilidades.lines().map { it.trim() }.filter { it.isNotBlank() }.toSet()
    if (p.origem == "Teórico da Conspiração") addRes("Mental", p.atributos["int"] ?: 1)
    if ("Mutação" in habilidades) addRes("Geral", 2)
    // Tanque de Guerra melhora a RD física da Proteção Pesada; não é RD geral.
    if ("Tanque de Guerra" in habilidades && p.itens.any { it.equipado && it.nome == "Proteção Pesada" }) {
        addRes("Fisico", 2)
    }
    // Intuitivo — Mente Sã (NEX 10%): +5 de RD paranormal.
    if (p.trilha == "Intuitivo" && p.nex >= 10) {
        addRes("Paranormal", 5)
    }
    // Intuitivo — Inabalável (NEX 65%): RD mental 10 e paranormal 10.
    if (p.trilha == "Intuitivo" && p.nex >= 65) {
        addRes("Mental", 10)
        addRes("Paranormal", 10)
    }
    // Inquebrável (NEX 99%) só concede RD 5 enquanto machucado.
    // PV <= metade cobre machucado e, em 0 PV, morrendo.
    if (p.trilha == "Tropa de Choque" && p.nex >= 99 && p.pvAtual <= p.pvMax / 2) {
        addRes("Geral", 5)
    }
    p.poderesParanormais.filter { it.nome == "Resistir a Elemento" }.forEach { pp ->
        if (pp.elemento in RESISTENCIAS) addRes(pp.elemento, if (pp.afinidade) 20 else 10)
    }

    p.itens.filter { it.equipado }.forEach { item ->
        val geral = item.resistencias["Geral"] ?: 0
        // "Geral" é realmente universal. "Fisico" é reservado para fontes
        // que cobrem somente os quatro tipos físicos.
        if (geral != 0) addRes("Geral", geral)
        val fisico = item.resistencias["Fisico"] ?: 0
        if (fisico != 0) listOf("Balístico", "Corte", "Impacto", "Perfuração").forEach { addRes(it, fisico) }
        item.resistencias.forEach { (tipo, valor) ->
            if (tipo != "Geral" && tipo != "Fisico") addRes(tipo, valor)
        }
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
        if (geral != 0) addRes("Geral", geral)
        val fisico = fontes["Fisico"] ?: 0
        if (fisico != 0) listOf("Balístico", "Corte", "Impacto", "Perfuração").forEach { addRes(it, fisico) }
        fontes.forEach { (tipo, valor) -> if (tipo != "Geral" && tipo != "Fisico") addRes(tipo, valor) }
    }

    // RD geral vem somente de fontes que explicitamente concedem RD geral.
    // Ela não inclui RD específica (balística, corte etc.).
    val rdGeralCalculada = p.overrides["rdGeral"]?.coerceAtLeast(0) ?: fontesRdGeral(p).sumOf { it.second }.coerceAtLeast(0)


    return CalculosFicha(
        pvMax = valor("pvMax", (pvBase + pvNivel * niveis + (if (p.origem == "Desgarrado") p.nex / 5 else 0) + efeitosPoderes.bonusPVMax)),
        peMax = valor("peMax", (peBase + peNivel * niveis + (if (p.origem == "Universitário") 1 + (((p.nex.coerceIn(5, 99) - 5) / 10).coerceAtLeast(0)) else 0) + efeitosPoderes.bonusPEMax)),
        sanMax = valor("sanMax", sanBase + sanNivel * niveis + if (p.origem == "Vítima") p.nex / 5 else 0),
        defesa = valor("defesa", defesa),
        esquiva = valor("esquiva", esquiva),
        bloqueio = valor("bloqueio", bloqueio),
        rdGeral = rdGeralCalculada,
        deslocamento = valor("deslocamento", ((efeitosCondicoes.deslocamento ?: 9) - if (espacosUsados(p) > limiteDeCarga(forca, intelecto, tecnico)) 3 else 0).coerceAtLeast(0)),
        defesaEquipamentoAuto = defesaEquipamentoAuto,
        limitePE = limitePE,
        resistenciasAutomaticas = resistAuto,
        limiteCarga = valor("limiteCarga", limiteCargaCalculado),
        cargaMaxima = limiteCargaMaxima(forca, intelecto, tecnico),
        cargaSobrecarregada = cargaSobrecarregada,
        penalidadeCarga = if (cargaSobrecarregada) -5 else 0,
        reducaoDeslocamentoCarga = if (cargaSobrecarregada) 3 else 0,
        defesaCorpoACorpo = defesa + efeitosCondicoes.defesaCorpoACorpo,
        defesaDistancia = defesa + efeitosCondicoes.defesaDistancia,
        bloqueiaAcoes = efeitosCondicoes.bloqueiaAcoes,
        penalidadeTestesCondicao = efeitosCondicoes.penalidadeTestes + if (cargaSobrecarregada) -5 else 0,
        penalidadeAtaqueDados = efeitosCondicoes.penalidadeAtaqueDados,
        dtRituais = valor("dtRituais", dtRituais),
        dtHabilidades = valor("dtHabilidades", dtHabilidades),
    )
}


fun fontesResistencia(p: Personagem, tipo: String): List<Pair<String, Int>> {
    val fontes = mutableListOf<Pair<String, Int>>()
    val habilidades = p.habilidades.lines().map { it.trim() }.filter { it.isNotBlank() }.toSet()
    if (p.origem == "Teórico da Conspiração" && tipo == "Mental") fontes += "Eu Já Sabia · origem" to (p.atributos["int"] ?: 1)
    if ("Mutação" in habilidades) fontes += "Mutação · origem" to 2
    if ("Tanque de Guerra" in habilidades && p.itens.any { it.equipado && it.nome == "Proteção Pesada" } && tipo in listOf("Balístico", "Corte", "Impacto", "Perfuração")) fontes += "Tanque de Guerra · proteção pesada" to 2
    if (p.trilha == "Intuitivo" && p.nex >= 10 && tipo == "Paranormal") fontes += "Mente Sã · trilha" to 5
    if (p.trilha == "Intuitivo" && p.nex >= 65 && tipo == "Mental") fontes += "Inabalável · trilha" to 10
    if (p.trilha == "Intuitivo" && p.nex >= 65 && tipo == "Paranormal") fontes += "Inabalável · trilha" to 10
    if (p.trilha == "Tropa de Choque" && p.nex >= 99 && p.pvAtual <= p.pvMax / 2) fontes += "Inquebrável · trilha (machucado)" to 5
    p.poderesParanormais.filter { it.nome == "Resistir a Elemento" && it.elemento == tipo }.forEach { pp ->
        fontes += "Resistir a $tipo · poder paranormal" to if (pp.afinidade) 20 else 10
    }
    p.itens.filter { it.equipado }.forEach { item ->
        val geral = item.resistencias["Geral"] ?: 0
        val fisico = item.resistencias["Fisico"] ?: 0
        val especifica = item.resistencias[tipo] ?: 0
        if (geral != 0) fontes += "${item.nome} · geral" to geral
        if (fisico != 0 && tipo in listOf("Balístico", "Corte", "Impacto", "Perfuração")) fontes += "${item.nome} · físico" to fisico
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
    return fontes
}

/** Fontes que fornecem RD geral (aplicável a qualquer tipo de dano). */
fun fontesRdGeral(p: Personagem): List<Pair<String, Int>> {
    val fontes = mutableListOf<Pair<String, Int>>()
    val habilidades = p.habilidades.lines().map { it.trim() }.filter { it.isNotBlank() }.toSet()
    if ("Mutação" in habilidades) fontes += "Mutação · origem" to 2
    if (p.trilha == "Tropa de Choque" && p.nex >= 99 && p.pvAtual <= p.pvMax / 2) {
        fontes += "Inquebrável · trilha (machucado)" to 5
    }

    p.itens.filter { it.equipado }.forEach { item ->
        item.resistencias["Geral"]?.takeIf { it != 0 }?.let { fontes += "${item.nome} · equipamento" to it }
    }
    p.rituais.filter { it.ativo }.forEach { ritual ->
        ritual.resistenciasConcedidas["Geral"]?.takeIf { it != 0 }?.let { fontes += "${ritual.nome} · ritual" to it }
    }
    return fontes
}

fun resistenciaTotal(p: Personagem, tipo: String): Int {
    val geral = p.overrides["rdGeral"]?.coerceAtLeast(0)
        ?: fontesRdGeral(p).sumOf { it.second }.coerceAtLeast(0)
    val fontesTipo = fontesResistencia(p, tipo)
    val nomesGerais = fontesRdGeral(p).map { it.first }.toSet()
    val especificaAutomatica = fontesTipo
        .filter { it.first !in nomesGerais }
        .sumOf { it.second }
        .coerceAtLeast(0)
    val especifica = p.overrides["rd:$tipo"]?.coerceAtLeast(0) ?: especificaAutomatica
    return (geral + especifica).coerceAtLeast(0)
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
