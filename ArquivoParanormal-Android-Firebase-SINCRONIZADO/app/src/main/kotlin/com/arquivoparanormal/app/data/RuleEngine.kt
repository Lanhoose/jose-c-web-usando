package com.arquivoparanormal.app.data


data class ResultadoDano(
    val personagem: Personagem,
    val danoAplicado: Int,
    val danoMassivo: Boolean,
    val dtDanoMassivo: Int?,
    val entrouMorrendo: Boolean,
)

fun dtDanoMassivo(dano: Int): Int = 15 + 2 * (dano.coerceAtLeast(0) / 10)

/** Aplica dano letal básico. O teste de Fortitude de Dano Massivo é separado para o Mestre decidir/rolar. */
fun receberDano(p: Personagem, dano: Int): ResultadoDano {
    val bruto = dano.coerceAtLeast(0)
    val novoPv = (p.pvAtual - bruto).coerceAtLeast(0)
    val massivo = bruto * 2 >= p.pvMax && novoPv > 0
    val entrou = novoPv == 0 && p.pvAtual > 0
    val atualizado = if (entrou) {
        p.copy(
            pvAtual = 0,
            turnosMorrendo = 0,
            condicoes = (p.condicoes - "Morrendo" - "Inconsciente") + listOf("Morrendo", "Inconsciente"),
        )
    } else p.copy(pvAtual = novoPv)
    return ResultadoDano(atualizado, bruto, massivo, if (massivo) dtDanoMassivo(bruto) else null, entrou)
}

fun iniciarTurnoMorrendo(p: Personagem): Personagem {
    if (p.morto || "Morrendo" !in p.condicoes || p.pvAtual > 0) return p
    val turnos = (p.turnosMorrendo + 1).coerceAtMost(3)
    return p.copy(turnosMorrendo = turnos, morto = turnos >= 3)
}

fun estabilizarComMedicina(p: Personagem): Personagem =
    p.copy(turnosMorrendo = 0, condicoes = p.condicoes - "Morrendo")

fun curarPV(p: Personagem, quantidade: Int): Personagem {
    val novoPv = (p.pvAtual + quantidade.coerceAtLeast(0)).coerceAtMost(p.pvMax)
    return if (novoPv > 0) p.copy(pvAtual = novoPv, condicoes = p.condicoes - "Inconsciente") else p.copy(pvAtual = novoPv)
}

/**
 * Motor de regras centralizado. Regras derivadas devem passar por aqui em vez
 * de ficarem espalhadas por comparações de texto na UI.
 */
data class EfeitoRegra(
    val bonusDefesa: Int = 0,
    val bonusAtaque: Int = 0,
    val bonusDano: Int = 0,
    val bonusDT: Int = 0,
    val bonusPE: Int = 0,
    val bonusPV: Int = 0,
    val bonusSAN: Int = 0,
    val bonusPericia: Map<String, Int> = emptyMap(),
    val resistencia: Map<String, Int> = emptyMap(),
    val deslocamento: Int = 0,
    val carga: Int = 0,
    val bloqueiaAcoes: Boolean = false,
    val testesPenalidade: Int = 0,
    val regrasEspeciais: Set<String> = emptySet(),
)

data class EfeitosCondicao(
    val penalidadePericias: Int = 0,
    /** Penalidade em dados d20 de testes de perícia/resistência quando a regra usa –O/–OO. */
    val penalidadeDadosTestes: Int = 0,
    val penalidadeDefesa: Int = 0,
    val penalidadeAtaques: Int = 0,
    val deslocamento: Int = 0,
    val bloqueiaAcoes: Boolean = false,
    val desprevenido: Boolean = false,
    val imovel: Boolean = false,
    val regrasEspeciais: Set<String> = emptySet(),
)

/** Valores mecânicos das condições, seguindo a estrutura das regras. */
fun efeitosDaCondicao(nome: String): EfeitosCondicao = when (nome) {
    "Abalado" -> EfeitosCondicao(penalidadeDadosTestes = -1, regrasEspeciais = setOf("piora-para-apavorado"))
    "Agarrado" -> EfeitosCondicao(desprevenido = true, imovel = true, penalidadeDadosTestes = -1, regrasEspeciais = setOf("ataques-afetados", "somente-contra-agressor"))
    "Alquebrado" -> EfeitosCondicao(regrasEspeciais = setOf("habilidades-custam-mais-1-pe"))
    "Apavorado" -> EfeitosCondicao(penalidadeDadosTestes = -2, regrasEspeciais = setOf("nao-se-aproxima-da-fonte", "deve-fugir-da-fonte"))
    "Atordoado" -> EfeitosCondicao(bloqueiaAcoes = true)
    "Caído" -> EfeitosCondicao(penalidadeAtaques = -5, deslocamento = -9, regrasEspeciais = setOf("ataques-a-distancia-contra-voce-plus5"))
    "Cego" -> EfeitosCondicao(penalidadePericias = -5, penalidadeAtaques = -5, regrasEspeciais = setOf("falha-testes-visao", "camuflagem-total"))
    "Confuso" -> EfeitosCondicao(regrasEspeciais = setOf("acao-aleatoria"))
    "Debilitado" -> EfeitosCondicao(penalidadePericias = -5, regrasEspeciais = setOf("testes-atributos-fisicos"))
    "Desprevenido" -> EfeitosCondicao(penalidadeDefesa = -5)
    "Fraco" -> EfeitosCondicao(penalidadePericias = -5, regrasEspeciais = setOf("testes-atributos-fisicos"))
    "Imóvel" -> EfeitosCondicao(desprevenido = true, imovel = true, deslocamento = -9)
    "Inconsciente" -> EfeitosCondicao(desprevenido = true, imovel = true, bloqueiaAcoes = true, regrasEspeciais = setOf("indefeso"))
    "Machucado" -> EfeitosCondicao()
    "Morrendo" -> EfeitosCondicao(desprevenido = true, imovel = true, regrasEspeciais = setOf("teste-morte"))
    "Ofuscado" -> EfeitosCondicao(penalidadePericias = -2, penalidadeAtaques = -2)
    "Paralisado" -> EfeitosCondicao(desprevenido = true, imovel = true, bloqueiaAcoes = true)
    "Surdo" -> EfeitosCondicao(regrasEspeciais = setOf("falha-testes-audicao", "–5-percepcao"))
    "Vulnerável" -> EfeitosCondicao(penalidadeDefesa = -2)
    "Insano" -> EfeitosCondicao(desprevenido = true, imovel = true, bloqueiaAcoes = true, regrasEspeciais = setOf("npc-mestre"))
    else -> EfeitosCondicao()
}

fun efeitosDasCondicoes(p: Personagem): EfeitosCondicao {
    var out = EfeitosCondicao()
    p.condicoes.forEach { c ->
        val e = efeitosDaCondicao(c)
        out = EfeitosCondicao(
            penalidadePericias = out.penalidadePericias + e.penalidadePericias,
            penalidadeDadosTestes = out.penalidadeDadosTestes + e.penalidadeDadosTestes,
            penalidadeDefesa = out.penalidadeDefesa + e.penalidadeDefesa,
            penalidadeAtaques = out.penalidadeAtaques + e.penalidadeAtaques,
            deslocamento = out.deslocamento + e.deslocamento,
            bloqueiaAcoes = out.bloqueiaAcoes || e.bloqueiaAcoes,
            desprevenido = out.desprevenido || e.desprevenido,
            imovel = out.imovel || e.imovel,
            regrasEspeciais = out.regrasEspeciais + e.regrasEspeciais,
        )
    }
    return out
}

data class EstadoSanidade(
    val atual: Int,
    val maxima: Int,
    val perturbado: Boolean,
    val enlouquecendo: Boolean,
    val turnosEnlouquecendo: Int,
    val insano: Boolean,
)

fun estadoSanidade(p: Personagem): EstadoSanidade {
    val atual = p.sanAtual.coerceIn(0, p.sanMax)
    val perturbado = atual > 0 && atual < (p.sanMax / 2.0)
    val enlouquecendo = atual == 0 && !p.insano
    val insano = p.insano || p.turnosEnlouquecendo >= 3
    return EstadoSanidade(atual, p.sanMax, perturbado, enlouquecendo, p.turnosEnlouquecendo.coerceIn(0, 3), insano)
}

/** Chame ao fim de um turno do personagem para avançar Enlouquecendo. */
fun avancarTurnoSanidade(p: Personagem): Personagem {
    val estado = estadoSanidade(p)
    if (!estado.enlouquecendo || estado.insano) return p.copy(perturbado = estado.perturbado, enlouquecendo = false, insano = estado.insano)
    val turnos = (p.turnosEnlouquecendo + 1).coerceAtMost(3)
    return p.copy(
        perturbado = false,
        enlouquecendo = turnos < 3,
        turnosEnlouquecendo = turnos,
        insano = turnos >= 3,
        condicoes = if (turnos >= 3 && "Insano" !in p.condicoes) p.condicoes + "Insano" else p.condicoes,
    )
}

fun aplicarDanoSanidade(p: Personagem, dano: Int): Personagem {
    val nova = (p.sanAtual - dano.coerceAtLeast(0)).coerceAtLeast(0)
    val perturbado = nova > 0 && nova < (p.sanMax / 2.0)
    val enlouquecendo = nova == 0 && !p.insano
    return p.copy(
        sanAtual = nova,
        perturbado = perturbado,
        enlouquecendo = enlouquecendo,
        turnosEnlouquecendo = if (enlouquecendo) 0 else p.turnosEnlouquecendo,
    )
}

fun registrarInsanidade(p: Personagem, efeito: String): Personagem =
    p.copy(historicoInsanidade = (p.historicoInsanidade + efeito).distinct())

fun limiteCargaDoPersonagem(p: Personagem): Int {
    val forca = p.atributos["for"] ?: 1
    val inventarioOtimizado = p.trilha.startsWith("Técnico") && p.nex >= 10
    val base = if (inventarioOtimizado) {
        5 * ((forca + (p.atributos["int"] ?: 1)).coerceAtLeast(1))
    } else limiteDeCarga(forca)
    return base + efeitosDePoderesETrilhas(p).carga
}

fun cargaMaxima(p: Personagem): Int = limiteCargaDoPersonagem(p) * 2

fun itemCategoriaPermitida(p: Personagem, categoria: Int): Boolean =
    if (categoria <= 0) true else (limitesCategoriaPorPrestigio(p.prestigio)[categoria] ?: 0) > 0

fun quantidadeCategoriaEquipamentos(p: Personagem, categoria: Int): Int =
    p.itens.filter { it.categoria == categoria }.sumOf { it.qtd } + p.armas.count { it.categoria == categoria }

fun limiteCategoriaEquipamentos(p: Personagem, categoria: Int): Int =
    limitesCategoriaPorPrestigio(p.prestigio)[categoria] ?: 0

fun podeAdicionarEquipamento(p: Personagem, categoria: Int, isMestre: Boolean = false): Boolean =
    isMestre || categoria <= 0 || quantidadeCategoriaEquipamentos(p, categoria) < limiteCategoriaEquipamentos(p, categoria)

fun patenteAtual(p: Personagem): String = patentePorPrestigio(p.prestigio)

fun limiteItensPorPatente(p: Personagem): Map<Int, Int> = limitesCategoriaPorPrestigio(p.prestigio)

fun efeitosDePoderesETrilhas(p: Personagem): EfeitoRegra {
    val habilidades = p.habilidades.lines().map { it.trim() }.filter { it.isNotBlank() }.toSet()
    var e = EfeitoRegra()
    if ("Rituais Eficientes" in habilidades || (p.trilha == "Graduado" && p.nex >= 65)) e = e.copy(bonusDT = e.bonusDT + 5)
    if ("Presença Poderosa" in habilidades || (p.trilha == "Intuitivo" && p.nex >= 40)) e = e.copy(bonusPE = e.bonusPE + (p.atributos["pre"] ?: 1))
    if ("Mascate" in habilidades || (p.trilha == "Muambeiro" && p.nex >= 10)) e = e.copy(carga = e.carga + 5)
    if (p.trilha == "Monstruoso — Especialista" && p.nex >= 10) {
        val penalidade = when { p.nex >= 99 -> -10; p.nex >= 40 -> -5; else -> -2 }
        e = e.copy(bonusPericia = mapOf("Diplomacia" to penalidade, "Enganação" to penalidade, "Intuição" to penalidade))
    }
    if (p.trilha == "Monstruoso — Ocultista" && p.nex >= 10) {
        val penalidade = when { p.nex >= 99 -> -10; p.nex >= 40 -> -5; else -> -2 }
        e = e.copy(bonusPericia = mapOf("Diplomacia" to penalidade, "Enganação" to penalidade, "Intuição" to penalidade))
    }
    return e
}

fun dtRitualBaseComEfeitos(p: Personagem, base: Int): Int {
    var dt = base + efeitosDePoderesETrilhas(p).bonusDT
    // Potência, de maldições/equipamentos, já pode ser representada pelos efeitos
    // de bônus de DT cadastrados como texto no item quando o catálogo evoluir.
    if (p.itens.any { it.equipado && it.maldicoes.any { m -> m.equals("Potência", true) } }) dt += 1
    return dt
}

fun limitePEDeRitual(p: Personagem, limiteBase: Int): Int {
    var limite = limiteBase
    if (p.trilha == "Intuitivo" && p.nex >= 40) limite += (p.atributos["pre"] ?: 1)
    return limite
}

/**
 * Monstruoso: o atributo de PE/DT passa a ser determinado pelo elemento
 * escolhido pela trilha, conforme o livro de Arquivos Secretos #07.
 */
fun atributoParanormalMonstruoso(p: Personagem): String? {
    if (!p.trilha.startsWith("Monstruoso")) return null
    return when (p.elementoMonstruoso.ifBlank { p.elementoConexao }) {
        "Sangue" -> "for"
        "Morte" -> "vig"
        "Conhecimento" -> "int"
        "Energia" -> "agi"
        else -> null
    }
}

fun atributoParanormalValor(p: Personagem): Int =
    atributoParanormalMonstruoso(p)?.let { p.atributos[it] ?: 1 } ?: (p.atributos["pre"] ?: 1)

fun afinidadePodeConjurarSemComponentes(p: Personagem, ritual: Ritual): Boolean =
    p.afinidade.isNotBlank() && ritual.elemento == p.afinidade

fun progressoEscolhasValidas(p: Personagem): List<String> = buildList {
    if (p.nex >= 20) add("Aumento de atributo")
    if (p.nex >= 35) add("Grau de treinamento")
    if (p.nex >= 50) add("Versatilidade")
    if (p.nex >= 50 && p.elementoConexao.isNotBlank()) add("Conexão elemental: ${p.elementoConexao}")
}
