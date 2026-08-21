package com.arquivoparanormal.app.data

data class Atributo(val key: String, val nome: String, val sigla: String)

val ATRIBUTOS = listOf(
    Atributo("for", "Força", "FOR"),
    Atributo("agi", "Agilidade", "AGI"),
    Atributo("int", "Intelecto", "INT"),
    Atributo("vig", "Vigor", "VIG"),
    Atributo("pre", "Presença", "PRE"),
)

data class PericiaDef(val nome: String, val attr: String, val kit: Boolean = false)

val PERICIAS = listOf(
    PericiaDef("Acrobacia", "agi"),
    PericiaDef("Adestramento", "pre"),
    PericiaDef("Artes", "pre"),
    PericiaDef("Atletismo", "for"),
    PericiaDef("Atualidades", "int"),
    PericiaDef("Ciências", "int"),
    PericiaDef("Crime", "agi", kit = true),
    PericiaDef("Diplomacia", "pre"),
    PericiaDef("Enganação", "pre"),
    PericiaDef("Fortitude", "vig"),
    PericiaDef("Furtividade", "agi"),
    PericiaDef("Iniciativa", "agi"),
    PericiaDef("Intimidação", "pre"),
    PericiaDef("Intuição", "pre"),
    PericiaDef("Investigação", "int"),
    PericiaDef("Luta", "for"),
    PericiaDef("Medicina", "int"),
    PericiaDef("Ocultismo", "int"),
    PericiaDef("Percepção", "pre"),
    PericiaDef("Pilotagem", "agi"),
    PericiaDef("Pontaria", "agi"),
    PericiaDef("Profissão", "int"),
    PericiaDef("Reflexos", "agi"),
    PericiaDef("Religião", "pre"),
    PericiaDef("Sobrevivência", "int"),
    PericiaDef("Tática", "int"),
    PericiaDef("Tecnologia", "int", kit = true),
    PericiaDef("Vontade", "pre"),
)

data class Treinamento(val nome: String, val bonus: Int)

val TREINAMENTOS = listOf(
    Treinamento("Destreinado", 0),
    Treinamento("Treinado", 5),
    Treinamento("Veterano", 10),
    Treinamento("Expert", 15),
)

data class ClasseDef(
    val nome: String,
    val pv: String,
    val pvNivel: String,
    val pe: String,
    val peNivel: String = "3",
    val sanidade: String,
    val sanNivel: String,
    val pericias: String,
    val trilhas: List<String>,
    val desc: String,
)

val CLASSES = listOf(
    ClasseDef(
        nome = "Combatente",
        pv = "20 + VIG",
        pvNivel = "4 + VIG",
        pe = "2 + PRE",
        peNivel = "2",
        sanidade = "12",
        sanNivel = "3",
        pericias = "1 + INT (entre Atletismo, Fortitude, Luta, Pontaria, Reflexos...)",
        trilhas = listOf(
            "Aniquilador", "Comandante de Campo", "Guerreiro",
            "Operações Especiais", "Tropa de Choque",
            "Agente Secreto", "Monstruoso — Combatente",
        ),
        desc = "Especialista em levar e distribuir dano. A linha de frente contra o Outro Lado.",
    ),
    ClasseDef(
        nome = "Especialista",
        pv = "16 + VIG",
        pvNivel = "3 + VIG",
        pe = "3 + PRE",
        peNivel = "3",
        sanidade = "16",
        sanNivel = "4",
        pericias = "7 + INT",
        trilhas = listOf(
            "Atirador de Elite", "Infiltrador", "Médico de Campo", "Negociador", "Técnico",
            "Agente Secreto", "Caçador", "Erudito", "Perseverante", "Monstruoso — Especialista",
        ),
        desc = "Versátil e cheio de perícias. Resolve o que soco e bala não resolvem.",
    ),
    ClasseDef(
        nome = "Ocultista",
        pv = "12 + VIG",
        pvNivel = "2 + VIG",
        pe = "4 + PRE",
        peNivel = "4",
        sanidade = "20",
        sanNivel = "5",
        pericias = "3 + INT",
        trilhas = listOf(
            "Conduíte", "Flagelador", "Graduado", "Intuitivo", "Lâmina Paranormal",
            "Monstruoso — Ocultista",
        ),
        desc = "Usa o paranormal contra o paranormal. Rituais, afinidade e um preço a pagar.",
    ),
    ClasseDef(
        nome = "Sobrevivente",
        pv = "16 + VIG",
        pvNivel = "3 + VIG",
        pe = "3 + PRE",
        peNivel = "3",
        sanidade = "16",
        sanNivel = "4",
        pericias = "7 + INT",
        trilhas = listOf("Durão", "Esperto", "Esotérico"),
        desc = "Classe adicional de Sobrevivendo ao Horror.",
    ),
)

data class OrigemDef(val nome: String, val pericias: String, val poder: String)

val ORIGENS = listOf(
    OrigemDef("Acadêmico", "Ciências, Investigação", "Saber é Poder"),
    OrigemDef("Agente de Saúde", "Intuição, Medicina", "Técnica Medicinal"),
    OrigemDef("Amnésico", "duas à escolha do mestre", "Vislumbres do Passado"),
    OrigemDef("Artista", "Artes, Enganação", "Magnum Opus"),
    OrigemDef("Atleta", "Acrobacia, Atletismo", "Condicionamento Atlético"),
    OrigemDef("Chef", "Fortitude, Profissão", "Ingrediente Secreto"),
    OrigemDef("Criminoso", "Crime, Furtividade", "Sempre Espere o Pior"),
    OrigemDef("Cultista Arrependido", "Ocultismo, Religião", "Conhecimento Proibido"),
    OrigemDef("Desgarrado", "Furtividade, Sobrevivência", "Wanderlust"),
    OrigemDef("Engenheiro", "Profissão, Tecnologia", "Nível Superior"),
    OrigemDef("Executivo", "Diplomacia, Profissão", "Recursos Financeiros"),
    OrigemDef("Fazendeiro", "Adestramento, Fortitude", "Vida No Campo"),
    OrigemDef("Ganancioso", "Diplomacia, Pontaria", "Dinheiro é Tudo"),
    OrigemDef("Investigador", "Investigação, Percepção", "Faro para Pistas"),
    OrigemDef("Lutador", "Luta, Reflexos", "Estilo de Luta"),
    OrigemDef("Magnata", "Diplomacia, Pilotagem", "Patrocinador da Ordem"),
    OrigemDef("Mateiro", "Percepção, Sobrevivência", "Vida Selvagem"),
    OrigemDef("Mercenário", "Iniciativa, Intimidação", "Posso Conseguir"),
    OrigemDef("Militar", "Luta ou Pontaria, Tática", "Para Sempre Militar"),
    OrigemDef("Operário", "Fortitude, Profissão", "Ferramenta de Trabalho"),
    OrigemDef("Policial", "Percepção, Pontaria", "Patrulha"),
    OrigemDef("Religioso", "Religião, Vontade", "Acalentar"),
    OrigemDef("Servidor Público", "Intuição, Vontade", "Rede de Contatos"),
    OrigemDef("T.I.", "Investigação, Tecnologia", "Hacker"),
    OrigemDef("Teórico da Conspiração", "Investigação, Ocultismo", "Eu Já Sabia"),
    OrigemDef("Trabalhador Rural", "Adestramento, Sobrevivência", "Faz Tudo"),
    OrigemDef("Trombadinha", "Crime, Reflexos", "Vai Ter Que Me Pegar"),
    OrigemDef("Universitário", "Atualidades, duas à escolha", "Dedicação"),
    OrigemDef("Vítima", "Reflexos, Vontade", "Cicatrizes do Passado"),
    OrigemDef("Astronauta", "Ciências, Pilotagem", "Treinamento Espacial"),
    OrigemDef("Colegial", "Atualidades, Reflexos", "Melhores Amigos"),
    OrigemDef("Ermitão", "Sobrevivência, Vontade", "Isolamento"),
)

data class ElementoDef(val nome: String, val desc: String)

val ELEMENTOS = listOf(
    ElementoDef("Sangue", "Violência, dor e vitalidade. Rituais de corte, fúria e sacrifício."),
    ElementoDef("Morte", "Fim, apodrecimento e o vazio. Rituais que corroem e drenam."),
    ElementoDef("Conhecimento", "Verdades que a mente não suporta. Ilusão, adivinhação e loucura."),
    ElementoDef("Energia", "Caos, distorção e realidade instável. Rituais explosivos e imprevisíveis."),
    ElementoDef("Medo", "O terror puro que precede tudo. Rituais que paralisam a alma."),
)

data class CondicaoDef(val nome: String, val desc: String, val grave: Boolean = false)

val CONDICOES = listOf(
    CondicaoDef("Abalado", "-2 em testes. Piora para Apavorado."),
    CondicaoDef("Agarrado", "Fica imóvel e desprevenido; só ações contra quem agarrou."),
    CondicaoDef("Alquebrado", "Custo de PE de habilidades aumenta em +1.", grave = true),
    CondicaoDef("Apavorado", "Não pode se aproximar da fonte; -5 em testes.", grave = true),
    CondicaoDef("Atordoado", "Não pode realizar ações.", grave = true),
    CondicaoDef("Caído", "-5 em Luta, +5 de defesa contra ataques à distância."),
    CondicaoDef("Cego", "Falha em testes que dependam de visão; -5 em ataques.", grave = true),
    CondicaoDef("Confuso", "Age de forma aleatória no turno."),
    CondicaoDef("Debilitado", "-5 em testes de atributos físicos."),
    CondicaoDef("Desprevenido", "-5 na Defesa."),
    CondicaoDef("Doente", "Sofre os efeitos de uma moléstia."),
    CondicaoDef("Em Chamas", "Sofre 1d6 de dano de fogo por rodada."),
    CondicaoDef("Enredado", "Fica lento e não pode usar deslocamento."),
    CondicaoDef("Envenenado", "Sofre dano contínuo e penalidades."),
    CondicaoDef("Esmorecido", "-1 dado em rolagens de dano."),
    CondicaoDef("Exausto", "Debilitado + Lento + Vulnerável.", grave = true),
    CondicaoDef("Fatigado", "Vulnerável e Lento."),
    CondicaoDef("Fraco", "-2 em testes de atributos físicos."),
    CondicaoDef("Frustrado", "Não pode usar habilidades que custem PE."),
    CondicaoDef("Imóvel", "Não pode se mover voluntariamente."),
    CondicaoDef("Inconsciente", "Indefeso e sem percepção do ambiente.", grave = true),
    CondicaoDef("Lento", "Deslocamento reduzido pela metade."),
    CondicaoDef("Morrendo", "Teste de morte no início de cada turno.", grave = true),
    CondicaoDef("Ofuscado", "-2 em testes de Percepção e ataques."),
    CondicaoDef("Paralisado", "Imóvel, indefeso e sem ações.", grave = true),
    CondicaoDef("Pasmo", "Só pode realizar uma ação por rodada."),
    CondicaoDef("Sangrando", "Perde PV a cada rodada até ser estabilizado.", grave = true),
    CondicaoDef("Surdo", "Falha em testes que dependam de audição."),
    CondicaoDef("Vulnerável", "-2 na Defesa."),
    CondicaoDef("Insano", "Sanidade zerada: fora de controle.", grave = true),
)

data class CategoriaItem(val cat: Int, val label: String, val espacos: Double)

val CATEGORIAS_ITEM = listOf(
    CategoriaItem(0, "0 — Trivial", 0.5),
    CategoriaItem(1, "I — Comum", 1.0),
    CategoriaItem(2, "II — Restrito", 2.0),
    CategoriaItem(3, "III — Proibido", 3.0),
    CategoriaItem(4, "IV — Paranormal", 4.0),
)

val PATENTES = listOf(
    "Recruta (NEX 5%)",
    "Operador (NEX 10-35%)",
    "Agente Especial (NEX 40-65%)",
    "Oficial de Operações (NEX 70-95%)",
    "Agente de Elite (NEX 99%)",
)

val TIPOS_MONSTRO = listOf(
    "Criatura Paranormal",
    "Humano",
    "Cultista",
    "Aberração",
    "Contaminado",
    "Entidade",
    "Construto",
)

val RESISTENCIAS = listOf(
    "Balístico", "Corte", "Impacto", "Perfuração", "Fogo",
    "Mental", "Sangue", "Morte", "Conhecimento", "Energia",
)

val CIRCULOS = listOf("1º", "2º", "3º", "4º")

fun formatBonus(n: Int) = if (n >= 0) "+$n" else "$n"


/**
 * Símbolos usados na ficha para identificar rapidamente os elementos dos rituais.
 * O símbolo é exibido junto ao nome no seletor e no cartão do ritual escolhido.
 */
val SIMBOLOS_ELEMENTO = mapOf(
    "Conhecimento" to "◉",
    "Energia" to "⚡",
    "Morte" to "⌛",
    "Sangue" to "♥",
    "Medo" to "◈",
)

data class ArmaDef(
    val nome: String,
    val grupo: String,
    val categoria: Int,
    val dano: String,
    val critico: String,
    val alcance: String,
    val tipoDano: String,
    val espacos: Double,
    val pericia: String,
    val descricao: String,
    val icone: String,
    val municao: String = "—",
)

/** Catálogo das armas da Tabela 3.3 do Capítulo 3: Equipamento. */
val ARMAS_LIVRO: List<ArmaDef> = listOf(
    ArmaDef("Coronhada", "Armas Simples · Corpo a Corpo — Leves", 0, "1d4/1d6", "x2", "—", "Impacto", 0.0, "Luta", "Golpe com a coronha de uma arma de fogo.", "⚔"),
    ArmaDef("Faca", "Armas Simples · Corpo a Corpo — Leves", 0, "1d4", "19", "Curto", "Corte", 1.0, "Luta", "Lâmina leve e fácil de ocultar; pode ser arremessada a curto alcance.", "⚔"),
    ArmaDef("Martelo", "Armas Simples · Corpo a Corpo — Leves", 0, "1d6", "x2", "—", "Impacto", 1.0, "Luta", "Ferramenta pesada usada como arma de impacto.", "⚔"),
    ArmaDef("Punhal", "Armas Simples · Corpo a Corpo — Leves", 0, "1d4", "x3", "—", "Perfuração", 1.0, "Luta", "Lâmina curta, rápida e precisa.", "⚔"),
    ArmaDef("Bastão", "Armas Simples · Corpo a Corpo — Uma Mão", 0, "1d6/1d8", "x2", "—", "Impacto", 1.0, "Luta", "Bastão simples de madeira ou material resistente.", "⚔"),
    ArmaDef("Machete", "Armas Simples · Corpo a Corpo — Uma Mão", 0, "1d6", "19", "—", "Corte", 1.0, "Luta", "Lâmina pesada de corte.", "⚔"),
    ArmaDef("Lança", "Armas Simples · Corpo a Corpo — Uma Mão", 0, "1d6", "x2", "Curto", "Perfuração", 1.0, "Luta", "Arma de haste que também pode ser usada à distância curta.", "⚔"),
    ArmaDef("Cajado", "Armas Simples · Corpo a Corpo — Duas Mãos", 0, "1d6/1d6", "x2", "—", "Impacto", 2.0, "Luta", "Cajado longo empunhado com as duas mãos.", "⚔"),
    ArmaDef("Arco", "Armas Simples · Armas de Disparo — Duas Mãos", 0, "1d6", "x3", "Médio", "Perfuração", 2.0, "Pontaria", "Arma de disparo que usa flechas.", "🏹", "Flechas"),
    ArmaDef("Besta", "Armas Simples · Armas de Disparo — Duas Mãos", 0, "1d8", "19", "Médio", "Perfuração", 2.0, "Pontaria", "Arma de disparo que usa virotes/flechas.", "🏹", "Flechas"),
    ArmaDef("Pistola", "Armas Simples · Armas de Fogo — Leves", 1, "1d12", "18", "Curto", "Balístico", 1.0, "Pontaria", "Arma de fogo leve, usada com uma mão.", "🔫", "Balas curtas"),
    ArmaDef("Revólver", "Armas Simples · Armas de Fogo — Leves", 1, "2d6", "19/x3", "Curto", "Balístico", 1.0, "Pontaria", "Arma de fogo de tambor e alcance curto.", "🔫", "Balas curtas"),
    ArmaDef("Fuzil de caça", "Armas Simples · Armas de Fogo — Duas Mãos", 1, "2d8", "19/x3", "Médio", "Balístico", 2.0, "Pontaria", "Arma longa de caça, usada com as duas mãos.", "🔫", "Balas longas"),
    ArmaDef("Machadinha", "Armas Táticas · Corpo a Corpo — Leves", 0, "1d6", "x3", "Curto", "Corte", 1.0, "Luta", "Machado pequeno que pode ser arremessado a curto alcance.", "⚔"),
    ArmaDef("Nunchaku", "Armas Táticas · Corpo a Corpo — Leves", 0, "1d8", "x2", "—", "Impacto", 1.0, "Luta", "Arma articulada de duas partes.", "⚔"),
    ArmaDef("Corrente", "Armas Táticas · Corpo a Corpo — Uma Mão", 0, "1d8", "x2", "—", "Impacto", 1.0, "Luta", "Corrente usada para golpear e controlar o espaço ao redor.", "⚔"),
    ArmaDef("Espada", "Armas Táticas · Corpo a Corpo — Uma Mão", 1, "1d8/1d10", "19", "—", "Corte", 1.0, "Luta", "Espada de uma mão, equilibrada para combate direto.", "⚔"),
    ArmaDef("Florete", "Armas Táticas · Corpo a Corpo — Uma Mão", 1, "1d6", "18", "—", "Corte", 1.0, "Luta", "Lâmina leve e precisa, favorecendo estocadas.", "⚔"),
    ArmaDef("Machado", "Armas Táticas · Corpo a Corpo — Uma Mão", 1, "1d8", "x3", "—", "Corte", 1.0, "Luta", "Machado de combate de uma mão.", "⚔"),
    ArmaDef("Maça", "Armas Táticas · Corpo a Corpo — Uma Mão", 1, "2d4", "x2", "—", "Impacto", 1.0, "Luta", "Arma contundente de uma mão.", "⚔"),
    ArmaDef("Acha", "Armas Táticas · Corpo a Corpo — Duas Mãos", 1, "1d12", "x3", "—", "Corte", 2.0, "Luta", "Arma pesada de haste, usada com as duas mãos.", "⚔"),
    ArmaDef("Gadanho", "Armas Táticas · Corpo a Corpo — Duas Mãos", 1, "2d4", "x4", "—", "Corte", 2.0, "Luta", "Lâmina curva de grande margem de ameaça.", "⚔"),
    ArmaDef("Katana", "Armas Táticas · Corpo a Corpo — Duas Mãos", 1, "1d10", "19", "—", "Corte", 2.0, "Luta", "Lâmina longa e precisa, empunhada com as duas mãos.", "⚔"),
    ArmaDef("Marreta", "Armas Táticas · Corpo a Corpo — Duas Mãos", 1, "3d4", "x2", "—", "Impacto", 2.0, "Luta", "Arma pesada de impacto.", "⚔"),
    ArmaDef("Montante", "Armas Táticas · Corpo a Corpo — Duas Mãos", 1, "2d6", "19", "—", "Corte", 2.0, "Luta", "Espada muito longa que exige as duas mãos.", "⚔"),
    ArmaDef("Motosserra", "Armas Táticas · Corpo a Corpo — Duas Mãos", 1, "3d6", "x2", "—", "Corte", 2.0, "Luta", "Ferramenta motorizada extremamente perigosa em combate corpo a corpo.", "⚔"),
    ArmaDef("Arco composto", "Armas Táticas · Armas de Disparo — Duas Mãos", 1, "1d10", "x3", "Médio", "Perfuração", 2.0, "Pontaria", "Arco reforçado que aumenta a força do disparo.", "🏹", "Flechas"),
    ArmaDef("Balestra", "Armas Táticas · Armas de Disparo — Duas Mãos", 1, "1d12", "19", "Médio", "Perfuração", 2.0, "Pontaria", "Arma de disparo potente que usa virotes/flechas.", "🏹", "Flechas"),
    ArmaDef("Submetralhadora", "Armas Táticas · Armas de Fogo — Uma Mão", 1, "2d6", "19/x3", "Curto", "Balístico", 1.0, "Pontaria", "Arma automática compacta de fogo.", "🔫", "Balas curtas"),
    ArmaDef("Espingarda", "Armas Táticas · Armas de Fogo — Duas Mãos", 1, "4d6", "x3", "Curto", "Balístico", 2.0, "Pontaria", "Arma de fogo de dispersão e grande dano a curto alcance.", "🔫", "Cartuchos"),
    ArmaDef("Fuzil de assalto", "Armas Táticas · Armas de Fogo — Duas Mãos", 2, "2d10", "19/x3", "Médio", "Balístico", 2.0, "Pontaria", "Fuzil militar de fogo automático.", "🔫", "Balas longas"),
    ArmaDef("Fuzil de precisão", "Armas Táticas · Armas de Fogo — Duas Mãos", 3, "2d10", "19/x3", "Longo", "Balístico", 2.0, "Pontaria", "Fuzil especializado em disparos de longo alcance.", "🔫", "Balas longas"),
    ArmaDef("Bazuca", "Armas Pesadas · Armas de Fogo — Duas Mãos", 3, "10d8", "x2", "Médio", "Impacto", 2.0, "Pontaria", "Lançador pesado de foguetes.", "💥", "Foguete"),
    ArmaDef("Lança-chamas", "Armas Pesadas · Armas de Fogo — Duas Mãos", 3, "6d6", "x2", "Curto", "Fogo", 2.0, "Pontaria", "Arma pesada que projeta uma torrente de fogo.", "🔥", "Combustível"),
    ArmaDef("Metralhadora", "Armas Pesadas · Armas de Fogo — Duas Mãos", 2, "2d12", "19/x3", "Médio", "Balístico", 2.0, "Pontaria", "Arma pesada de fogo automático.", "🔫", "Balas longas"),
)

data class MunicaoDef(
    val nome: String,
    val categoria: Int,
    val espacos: Double,
    val duracao: String,
    val descricao: String,
)

/** Munições da Tabela 3.4 do Capítulo 3. Cada pacote ocupa 1 espaço. */
val MUNICOES_LIVRO: List<MunicaoDef> = listOf(
    MunicaoDef("Balas curtas", 0, 1.0, "2 cenas", "Munição usada em pistolas, revólveres e submetralhadoras."),
    MunicaoDef("Balas longas", 1, 1.0, "1 cena", "Munição usada em fuzis e metralhadoras."),
    MunicaoDef("Cartuchos", 1, 1.0, "1 cena", "Cartuchos usados em espingardas."),
    MunicaoDef("Combustível", 1, 1.0, "1 cena", "Tanque de combustível para lança-chamas."),
    MunicaoDef("Flechas", 0, 1.0, "1 missão", "Usadas em arcos e bestas; podem ser reaproveitadas após cada combate."),
    MunicaoDef("Foguete", 1, 1.0, "1 disparo", "Disparado por bazucas; cada foguete serve para um único disparo."),
)

data class RitualDef(
    val nome: String,
    val circulo: String,
    val elemento: String,
    val descricao: String,
    val afinidade: String = "Nenhuma",
) {
    val simbolo: String
        get() = SIMBOLOS_ELEMENTO[elemento] ?: "◇"
}

/**
 * Catálogo dos rituais da Lista de Rituais do livro_de_regras_oficial.pdf.
 * As descrições são os resumos apresentados na própria lista do livro.
 */
val RITUAIS_LIVRO: List<RitualDef> = buildList {
    fun add(c: Int, elemento: String, vararg rs: Pair<String, String>) {
        rs.forEach { (nome, descricao) ->
            add(RitualDef(nome, "${c}º", elemento, descricao))
        }
    }

    add(1, "Conhecimento",
        "Amaldiçoar Arma" to "Arma causa mais dano.",
        "Compreensão Paranormal" to "Você entende qualquer linguagem escrita ou falada.",
        "Enfeitiçar" to "Alvo se torna prestativo.",
        "Perturbação" to "Força o alvo a obedecer a uma ordem.",
        "Ouvir os Sussurros" to "Você se comunica com vozes do Outro Lado para receber informações.",
        "Tecer Ilusão" to "Cria uma ilusão visual ou sonora.",
        "Terceiro Olho" to "Você vê manifestações paranormais."
    )
    add(1, "Energia",
        "Amaldiçoar Arma" to "Arma causa mais dano.",
        "Amaldiçoar Tecnologia" to "Aprimora um item.",
        "Coincidência Forçada" to "Recebe bônus em testes.",
        "Eletrocussão" to "Corrente voltaica eletrocuta o alvo.",
        "Embaralhar" to "Cria duplicatas para confundir os inimigos, oferecendo bônus na Defesa.",
        "Luz" to "Objeto brilha como uma lâmpada.",
        "Polarização Caótica" to "Objetos metálicos são atraídos ou repelidos conforme sua vontade."
    )
    add(1, "Morte",
        "Amaldiçoar Arma" to "Arma causa mais dano.",
        "Cicatrização" to "Acelera a regeneração de um ferimento.",
        "Consumir Manancial" to "Suga o tempo de vida de seres próximos, recebendo PV temporários.",
        "Decadência" to "Acelera o envelhecimento dos órgãos internos do alvo, fazendo seu corpo definhar.",
        "Definhar" to "Alvo fica fatigado ou vulnerável.",
        "Espirais da Perdição" to "Inimigos sofrem penalidade em ataque.",
        "Nuvem de Cinzas" to "Nuvem fornece camuflagem."
    )
    add(1, "Sangue",
        "Amaldiçoar Arma" to "Arma causa mais dano.",
        "Arma Atroz" to "Arma corpo a corpo recebe bônus em testes de ataque e margem de ameaça.",
        "Armadura de Sangue" to "Recobre o corpo com placas de sangue endurecido.",
        "Corpo Adaptado" to "Ignora frio e calor, pode respirar debaixo d’água.",
        "Distorcer Aparência" to "Muda a aparência de um ou mais alvos.",
        "Fortalecimento Sensorial" to "Melhora seus sentidos e sua percepção.",
        "Ódio Incontrolável" to "Aumenta dano corpo a corpo e perícias físicas, mas impede calma e concentração."
    )
    add(1, "Medo", "Cinerária" to "Névoa fortalece rituais na área.")

    add(2, "Conhecimento",
        "Aprimorar Mente" to "Fornece bônus em Intelecto ou Presença.",
        "Detecção de Ameaças" to "Detecta seres hostis e armadilhas na área.",
        "Esconder dos Olhos" to "Torna o usuário invisível aos olhos comuns por determinado tempo.",
        "Invadir Mente" to "Gera uma rajada mental ou se conecta telepaticamente.",
        "Localização" to "Determina em que direção está um objeto ou ser a sua escolha."
    )
    add(2, "Energia",
        "Chamas do Caos" to "Controla o fogo.",
        "Contenção Fantasmagórica" to "Laços de energia prendem o alvo.",
        "Dissonância Acústica" to "Cria uma área em que é impossível ouvir sons.",
        "Sopro do Caos" to "Move o ar de formas impossíveis.",
        "Tela de Ruído" to "Cria uma película protetora que absorve dano."
    )
    add(2, "Morte",
        "Desacelerar Impacto" to "Diminui dano de queda e reduz o dano de projéteis à metade.",
        "Eco Espiral" to "Repete o dano que o alvo sofreu ao longo das rodadas concentrando.",
        "Paradoxo" to "Cria uma área de tempo paradoxal, capaz de envelhecer corpo e alma.",
        "Miasma Entrópico" to "Nuvem tóxica enjoa e sufoca os alvos.",
        "Velocidade Mortal" to "Alvo acelera no tempo, realizando ações adicionais."
    )
    add(2, "Sangue",
        "Aprimorar Físico" to "Bônus em Agilidade ou Força.",
        "Descarnar" to "A pele do alvo é dilacerada, abrindo cortes profundos.",
        "Flagelo de Sangue" to "Alvo precisa obedecer uma ordem.",
        "Hemofagia" to "Absorve o sangue do alvo, causando dano e recuperando seus pontos de vida.",
        "Transfusão Vital" to "Transfere vida do usuário para um ser, curando-o instantaneamente."
    )
    add(2, "Medo",
        "Proteção contra Rituais" to "Alvo recebe resistência contra efeitos e criaturas paranormais.",
        "Rejeitar Névoa" to "Enfraquece a conjuração de rituais."
    )

    add(3, "Conhecimento",
        "Alterar Memória" to "Pode apagar ou modificar a memória recente do alvo.",
        "Contato Paranormal" to "Você barganha com o Outro Lado para obter ajuda.",
        "Mergulho Mental" to "Se infiltra na mente do alvo para vasculhar seus pensamentos.",
        "Vidência" to "Pode observar e ouvir um alvo à distância."
    )
    add(3, "Energia",
        "Convocação Instantânea" to "Teletransporta um objeto marcado para suas mãos.",
        "Salto Fantasma" to "Teletransporta você e outros seres para um ponto dentro do alcance.",
        "Transfigurar Água" to "Água e gelo se comportam de forma caótica.",
        "Transfigurar Terra" to "Rochas, lama e areia se comportam de forma caótica."
    )
    add(3, "Morte",
        "Âncora Temporal" to "Impede o alvo de se afastar de um ponto.",
        "Poeira da Podridão" to "Nuvem de poeira apodrece tudo que toca.",
        "Tentáculos de Lodo" to "Tentáculos pretos atacam e agarram seres na área.",
        "Zerar Entropia" to "O alvo fica lento ou paralisado."
    )
    add(3, "Sangue",
        "Ferver Sangue" to "Faz o sangue do alvo entrar em ebulição, causando dano e deixando-o fraco.",
        "Forma Monstruosa" to "Você assume a aparência e forma de uma criatura monstruosa.",
        "Purgatório" to "Área de sangue deixa alvos vulneráveis a dano e causa dor a quem tentar sair.",
        "Vomitar Pestes" to "Vomita um enxame de pequenas criaturas de Sangue."
    )
    add(3, "Medo", "Dissipar Ritual" to "Cancela os efeitos de rituais em um alvo ou área.")

    add(4, "Conhecimento",
        "Controle Mental" to "Faz com que a mente da vítima seja controlada por outra pessoa.",
        "Inexistir" to "Você toca um alvo e o apaga completamente da existência.",
        "Possessão" to "Transfere sua consciência para o corpo do alvo."
    )
    add(4, "Energia",
        "Alterar Destino" to "Enxerga o futuro próximo, podendo alterar o resultado de um teste.",
        "Deflagração de Energia" to "Explosão de energia bruta causa dano e afeta itens amaldiçoados.",
        "Teletransporte" to "Teletransporta você e outros seres."
    )
    add(4, "Morte",
        "Convocar o Algoz" to "Conjura o maior medo do alvo, que irá persegui-lo e tentar matá-lo.",
        "Distorção Temporal" to "Você age livremente por um curto período de tempo.",
        "Fim Inevitável" to "Abre uma ruptura no espaço que suga tudo ao redor."
    )
    add(4, "Sangue",
        "Capturar o Coração" to "Manipula as emoções e vontades do alvo, fazendo dele seu aliado.",
        "Invólucro de Carne" to "Cria um clone de carne e sangue com as mesmas estatísticas do alvo.",
        "Vínculo de Sangue" to "Alvo sofre todo dano que você sofrer."
    )
    add(4, "Medo",
        "Canalizar o Medo" to "Transfere parte de seu poder paranormal para um alvo.",
        "Conhecendo o Medo" to "Manifesta o Medo absoluto na mente do alvo.",
        "Lâmina do Medo" to "Golpeia o alvo com uma lâmina de medo puro.",
        "Medo Tangível" to "Recebe uma série de imunidades.",
        "Presença do Medo" to "Você assume uma forma impossível dentro da Realidade."
    )
}
