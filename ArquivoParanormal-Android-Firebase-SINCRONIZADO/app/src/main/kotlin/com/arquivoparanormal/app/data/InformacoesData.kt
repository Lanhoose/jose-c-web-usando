package com.arquivoparanormal.app.data

/** Uma seção (ou subseção) de um capítulo do livro de regras, com a página onde começa. */
data class SecaoInfo(val nome: String, val pagina: Int, val nivel: Int = 0)

/** Um capítulo do livro de regras, com uma breve descrição e suas seções internas. */
data class CapituloInfo(
    val titulo: String,
    val pagina: Int,
    val descricao: String,
    val secoes: List<SecaoInfo>,
)

/** Avisos e observações importantes exibidos no topo da aba Informações. */
data class AvisoInfo(val titulo: String, val texto: String)

val AVISOS_IMPORTANTES = listOf(
    AvisoInfo(
        "Sobre este app",
        "O Arquivo Paranormal é uma ferramenta de apoio não oficial para jogar Ordem Paranormal RPG. " +
            "Ele reúne fichas, compêndio de referência rápida e o sumário do livro de regras, mas não substitui o livro oficial.",
    ),
    AvisoInfo(
        "Versão de referência",
        "As seções abaixo seguem a organização do Livro de Regras na versão v1.3 (dezembro de 2024). " +
            "Consulte sempre o livro físico ou digital oficial para o texto completo das regras.",
    ),
    AvisoInfo(
        "Como usar o Compêndio",
        "A aba Compêndio traz resumos rápidos de elementos, classes, origens, perícias e condições. " +
            "Para regras detalhadas, rituais, ameaças e capítulos completos, use esta aba como um índice de navegação.",
    ),
)

val CAPITULOS_INFO = listOf(
    CapituloInfo(
        titulo = "Introdução",
        pagina = 6,
        descricao = "Apresenta o que é RPG, a mecânica básica de testes, termos importantes e como começar a jogar.",
        secoes = listOf(
            SecaoInfo("O que é RPG", 8),
            SecaoInfo("Mecânica básica", 9),
            SecaoInfo("Termos importantes", 10),
            SecaoInfo("Começando", 11),
        ),
    ),
    CapituloInfo(
        titulo = "Capítulo 1: Criação de Personagem",
        pagina = 12,
        descricao = "Passo a passo para criar um agente: conceito, atributos, origens, classes e toques finais.",
        secoes = listOf(
            SecaoInfo("Passo a passo para criar seu agente", 13),
            SecaoInfo("Conceito de personagem", 14),
            SecaoInfo("Atributos", 14),
            SecaoInfo("Origens", 16),
            SecaoInfo("Classes", 22),
            SecaoInfo("Combatente", 24, nivel = 1),
            SecaoInfo("Especialista", 28, nivel = 1),
            SecaoInfo("Ocultista", 32, nivel = 1),
            SecaoInfo("Toques finais", 36),
        ),
    ),
    CapituloInfo(
        titulo = "Capítulo 2: Perícias",
        pagina = 38,
        descricao = "Como usar perícias nos testes e a descrição de cada uma delas.",
        secoes = listOf(
            SecaoInfo("Usando perícias", 39),
            SecaoInfo("Descrição das perícias", 41),
        ),
    ),
    CapituloInfo(
        titulo = "Capítulo 3: Equipamento",
        pagina = 50,
        descricao = "Patentes, capacidade de carga, armas, proteções e equipamentos gerais dos agentes.",
        secoes = listOf(
            SecaoInfo("Patente", 51),
            SecaoInfo("Capacidade de carga", 53),
            SecaoInfo("Armas", 54),
            SecaoInfo("Proteções", 62),
            SecaoInfo("Equipamento geral", 63),
        ),
    ),
    CapituloInfo(
        titulo = "Capítulo 4: Regras",
        pagina = 68,
        descricao = "O papel do jogador, testes e habilidades, investigação, combate e interlúdio entre missões.",
        secoes = listOf(
            SecaoInfo("O papel do jogador", 70),
            SecaoInfo("Testes & habilidades", 75),
            SecaoInfo("Investigação", 79),
            SecaoInfo("Combate", 82),
            SecaoInfo("Interlúdio", 92),
        ),
    ),
    CapituloInfo(
        titulo = "Capítulo 5: O Outro Lado",
        pagina = 94,
        descricao = "A Membrana, entidades do Outro Lado, exposição paranormal, sanidade, poderes e rituais.",
        secoes = listOf(
            SecaoInfo("A Membrana", 95),
            SecaoInfo("Entidades do Outro Lado", 98),
            SecaoInfo("Exposição paranormal", 110),
            SecaoInfo("Sanidade", 111),
            SecaoInfo("Poderes paranormais", 114),
            SecaoInfo("Rituais", 117),
            SecaoInfo("Lista de rituais", 122, nivel = 1),
            SecaoInfo("Itens amaldiçoados", 144),
        ),
    ),
    CapituloInfo(
        titulo = "Capítulo 6: O Mestre",
        pagina = 152,
        descricao = "Papel do mestre, preparação de sessão, geração de missões, narração, regras opcionais e aliados.",
        secoes = listOf(
            SecaoInfo("O papel do mestre", 153),
            SecaoInfo("Preparando a sessão", 154),
            SecaoInfo("Gerador de missões", 155, nivel = 1),
            SecaoInfo("Narrando", 160),
            SecaoInfo("Arbitrando regras", 162),
            SecaoInfo("Aliados", 170),
            SecaoInfo("Regras opcionais", 171),
        ),
    ),
    CapituloInfo(
        titulo = "Capítulo 7: Ameaças",
        pagina = 176,
        descricao = "Construção de combates, fichas de ameaças e o bestiário completo por categoria.",
        secoes = listOf(
            SecaoInfo("Construindo combates", 177),
            SecaoInfo("Fichas de ameaças", 178),
            SecaoInfo("Criaturas de Sangue", 182),
            SecaoInfo("Criaturas de Morte", 208),
            SecaoInfo("Criaturas de Conhecimento", 232),
            SecaoInfo("Criaturas de Energia", 254),
            SecaoInfo("Ameaças da Realidade", 282),
            SecaoInfo("Perigos", 290),
        ),
    ),
    CapituloInfo(
        titulo = "Capítulo 8: O Mundo de Ordem Paranormal",
        pagina = 294,
        descricao = "Marcas da Realidade, organizações paranormais e a Ordo Realitas.",
        secoes = listOf(
            SecaoInfo("Marcas da Realidade", 296),
            SecaoInfo("Organizações paranormais", 300),
            SecaoInfo("A Ordem da Realidade", 305),
        ),
    ),
    CapituloInfo(
        titulo = "Apêndice",
        pagina = 310,
        descricao = "Índice remissivo e ficha de personagem para consulta rápida.",
        secoes = listOf(
            SecaoInfo("Índice remissivo", 314),
            SecaoInfo("Ficha de personagem", 319),
        ),
    ),
)
