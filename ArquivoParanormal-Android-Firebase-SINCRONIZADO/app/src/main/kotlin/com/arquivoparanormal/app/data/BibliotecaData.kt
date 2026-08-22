package com.arquivoparanormal.app.data

/** Um item do sumário de um dos livros adicionais. */
data class ItemSumario(
    val nome: String,
    val pagina: Int,
    val nivel: Int = 0,
)

data class SecaoLivro(
    val titulo: String,
    val pagina: Int,
    val secoes: List<ItemSumario> = emptyList(),
)

data class LivroSumario(
    val id: LivroPdf,
    val titulo: String,
    val descricao: String,
    val secoes: List<SecaoLivro>,
)

data class ReferenciaConteudo(
    val nome: String,
    val tipo: String,
    val livro: LivroPdf,
    val pagina: Int,
    val detalhe: String = "",
)

/** Livros disponíveis no leitor PDF. */
enum class LivroPdf(
    val titulo: String,
    val nomeArquivo: String,
    val driveFileId: String,
    val ultimaPagina: Int,
    val offsetFisicoZeroBased: Int,
) {
    REGRAS(
        titulo = "Livro de Regras",
        nomeArquivo = "livro_de_regras_oficial.pdf",
        driveFileId = "1iFHjO9DbzO173EGiIXg5dVG5vUi4Tjti",
        ultimaPagina = 319,
        // A página impressa 6 começa na folha física 16 (índice 15).
        offsetFisicoZeroBased = 9,
    ),
    SOBREVIVENDO(
        titulo = "Sobrevivendo ao Horror",
        nomeArquivo = "sobrevivendo_ao_horror.pdf",
        driveFileId = "1_z1iRQ5W82PkaQgSiMM3BP82Ajv4PvFq",
        ultimaPagina = 224,
        // Página impressa 7 = folha física 8 (índice 7).
        offsetFisicoZeroBased = 0,
    ),
    ARQUIVOS_SECRETOS(
        titulo = "Arquivos Secretos",
        nomeArquivo = "arquivos_secretos_07.pdf",
        driveFileId = "1U3mg3FaA_K_epxz3zOllxqmOnk7nHk3f",
        ultimaPagina = 92,
        // Página impressa 1 = folha física 1 (índice 0).
        offsetFisicoZeroBased = -1,
    ),
}

val SUMARIOS_LIVROS = listOf(
    LivroSumario(
        id = LivroPdf.SOBREVIVENDO,
        titulo = "Sobrevivendo ao Horror",
        descricao = "Novas regras, sobreviventes, opções de classes, rituais, itens, ameaças e missões.",
        secoes = listOf(
            SecaoLivro("Prefácio", 4),
            SecaoLivro("Capítulo 1: Sobreviventes", 6, listOf(
                ItemSumario("Novas origens", 7),
                ItemSumario("Novas opções para classes", 14),
                ItemSumario("O Combatente Sobrevivente", 14, 1),
                ItemSumario("O Especialista Sobrevivente", 22, 1),
                ItemSumario("O Ocultista Sobrevivente", 26, 1),
                ItemSumario("Nova classe: O Sobrevivente", 30),
                ItemSumario("Poderes gerais", 33),
                ItemSumario("Equipamentos", 37),
                ItemSumario("Poderes paranormais", 46),
                ItemSumario("Novos rituais", 48),
                ItemSumario("Novos itens amaldiçoados", 57),
            )),
            SecaoLivro("Capítulo 2: O Horror", 62, listOf(
                ItemSumario("Mestrando Terror", 63),
                ItemSumario("Novas regras", 81),
                ItemSumario("Novas ações de investigação", 81, 1),
                ItemSumario("Eventos de investigação", 82, 1),
                ItemSumario("Medo em jogo", 87),
                ItemSumario("Perseguições", 90),
                ItemSumario("Furtividade", 92),
                ItemSumario("Fabricação em campo", 94),
                ItemSumario("Vida além da Ordem", 94),
                ItemSumario("Novas regras opcionais", 98),
                ItemSumario("NEX & experiência", 98, 1),
                ItemSumario("Jogando sem Sanidade", 104, 1),
                ItemSumario("Ferimentos debilitantes", 105),
                ItemSumario("Jogando sem mapa", 106),
                ItemSumario("Evolução por patentes", 108),
                ItemSumario("Os limites da compreensão humana", 113),
                ItemSumario("Conjuração complexa", 114),
                ItemSumario("Conjurando rituais desconhecidos", 117),
                ItemSumario("Desastres paranormais", 117),
                ItemSumario("Combate narrativo", 119),
            )),
            SecaoLivro("Capítulo 3: Novas ameaças", 124, listOf(
                ItemSumario("Sepultado", 126),
                ItemSumario("Mescla", 128),
                ItemSumario("Espectro Inesquecido", 130),
                ItemSumario("Uivar", 134),
                ItemSumario("Derretido", 136),
                ItemSumario("Melancolia", 138),
                ItemSumario("Quibungo", 142),
                ItemSumario("Profundo", 144),
                ItemSumario("Memento Mori", 148),
                ItemSumario("Rascunho", 150),
                ItemSumario("Medusa", 152),
                ItemSumario("Amigo Imaginário", 154),
                ItemSumario("Novas ameaças da Realidade", 158),
                ItemSumario("Bêbado local / Burocrata", 158, 1),
                ItemSumario("Fazendeiro isolado / Investigador", 159, 1),
                ItemSumario("Médico / Religioso", 160, 1),
                ItemSumario("Serial Killer", 161),
                ItemSumario("Predador sofisticado", 161, 1),
                ItemSumario("Caçador de gente / Artista da morte", 162, 1),
                ItemSumario("Animais", 163),
                ItemSumario("Ariranha / Cavalo", 163, 1),
                ItemSumario("Enxame de Tocandiras / Gorila / Leão", 164, 1),
                ItemSumario("Lobo / Touro / Urso Pardo", 165, 1),
            )),
            SecaoLivro("Capítulo 4: Missões", 166, listOf(
                ItemSumario("Missão 1: Noite de Compras", 168),
                ItemSumario("Missão 2: O Terminal", 194),
            )),
            SecaoLivro("Apêndices", 222, listOf(
                ItemSumario("Ficha de personagem", 222),
                ItemSumario("Ficha de série", 224),
            )),
        ),
    ),
    LivroSumario(
        id = LivroPdf.ARQUIVOS_SECRETOS,
        titulo = "Arquivos Secretos",
        descricao = "Pacote com Vampyrus, Terribilis Fides, Regulae Obscurae e conteúdos de agentes.",
        secoes = listOf(
            SecaoLivro("Vampyrus", 4),
            SecaoLivro("Terribilis Fides", 46),
            SecaoLivro("Regulae Obscurae", 74, listOf(
                ItemSumario("Ritual e regras de fé", 76),
                ItemSumario("Itens", 77),
                ItemSumario("Origens", 79),
                ItemSumario("Trilha Monstruoso — Especialista", 81),
                ItemSumario("Trilha Monstruoso — Ocultista", 85),
            )),
            SecaoLivro("Mural dos Agentes", 90),
            SecaoLivro("Regras debaixo d'água", 91),
            SecaoLivro("Inquérito mensal", 92),
        ),
    ),
)

private fun ref(nome: String, tipo: String, livro: LivroPdf, pagina: Int, detalhe: String = "") =
    ReferenciaConteudo(nome, tipo, livro, pagina, detalhe)

val CONTEUDOS_ADICIONAIS = listOf(
    // Sobrevivendo ao Horror — origens
    ref("Amigo dos Animais", "Origem", LivroPdf.SOBREVIVENDO, 7),
    ref("Astronauta", "Origem", LivroPdf.SOBREVIVENDO, 8),
    ref("Chef do Outro Lado", "Origem", LivroPdf.SOBREVIVENDO, 8),
    ref("Colegial", "Origem", LivroPdf.SOBREVIVENDO, 9),
    ref("Cosplayer", "Origem", LivroPdf.SOBREVIVENDO, 9),
    ref("Diplomata", "Origem", LivroPdf.SOBREVIVENDO, 9),
    ref("Explorador", "Origem", LivroPdf.SOBREVIVENDO, 9),
    ref("Experimento", "Origem", LivroPdf.SOBREVIVENDO, 9),
    ref("Fanático por Criaturas", "Origem", LivroPdf.SOBREVIVENDO, 10),
    ref("Fotógrafo", "Origem", LivroPdf.SOBREVIVENDO, 10),
    ref("Inventor Paranormal", "Origem", LivroPdf.SOBREVIVENDO, 11),
    ref("Jovem Místico", "Origem", LivroPdf.SOBREVIVENDO, 11),
    ref("Legista do Turno da Noite", "Origem", LivroPdf.SOBREVIVENDO, 11),
    ref("Mateiro", "Origem", LivroPdf.SOBREVIVENDO, 12),
    ref("Mergulhador", "Origem", LivroPdf.SOBREVIVENDO, 12),
    ref("Motorista", "Origem", LivroPdf.SOBREVIVENDO, 13),
    ref("Nerd Entusiasta", "Origem", LivroPdf.SOBREVIVENDO, 13),
    ref("Profetizado", "Origem", LivroPdf.SOBREVIVENDO, 13),
    ref("Psicólogo", "Origem", LivroPdf.SOBREVIVENDO, 13),
    ref("Repórter Investigativo", "Origem", LivroPdf.SOBREVIVENDO, 13),

    // Sobrevivendo ao Horror — trilhas/classe
    ref("Agente Secreto", "Trilha — Combatente", LivroPdf.SOBREVIVENDO, 15),
    ref("Especialista Sobrevivente", "Trilha — Especialista", LivroPdf.SOBREVIVENDO, 22),
    ref("Ocultista Sobrevivente", "Trilha — Ocultista", LivroPdf.SOBREVIVENDO, 26),
    ref("O Sobrevivente", "Classe", LivroPdf.SOBREVIVENDO, 30),

    // Sobrevivendo ao Horror — rituais
    ref("Esfolar", "Ritual", LivroPdf.SOBREVIVENDO, 48, "Sangue"),
    ref("Sede de Adrenalina", "Ritual", LivroPdf.SOBREVIVENDO, 49, "Sangue"),
    ref("Dor da Caçada", "Ritual", LivroPdf.SOBREVIVENDO, 49, "Sangue"),
    ref("Martírio de Sangue", "Ritual", LivroPdf.SOBREVIVENDO, 50, "Sangue"),
    ref("Apagar as Luzes", "Ritual", LivroPdf.SOBREVIVENDO, 50, "Morte"),
    ref("Língua Morta", "Ritual", LivroPdf.SOBREVIVENDO, 51, "Morte"),
    ref("Fedor Pútrido", "Ritual", LivroPdf.SOBREVIVENDO, 52, "Morte"),
    ref("Singularidade Temporal", "Ritual", LivroPdf.SOBREVIVENDO, 52, "Morte"),
    ref("Desfazer Sinapses", "Ritual", LivroPdf.SOBREVIVENDO, 53, "Conhecimento"),
    ref("Aurora da Verdade", "Ritual", LivroPdf.SOBREVIVENDO, 53, "Conhecimento"),
    ref("Relembrar Fragmento", "Ritual", LivroPdf.SOBREVIVENDO, 54, "Conhecimento"),
    ref("Pronunciar Sigilo", "Ritual", LivroPdf.SOBREVIVENDO, 54, "Conhecimento"),
    ref("Overclock", "Ritual", LivroPdf.SOBREVIVENDO, 55, "Energia"),
    ref("Tremeluzir", "Ritual", LivroPdf.SOBREVIVENDO, 55, "Energia"),
    ref("Mutar", "Ritual", LivroPdf.SOBREVIVENDO, 56, "Energia"),
    ref("Milagre Ionizante", "Ritual", LivroPdf.SOBREVIVENDO, 56, "Energia"),

    // Sobrevivendo ao Horror — itens
    ref("Conector de Membros", "Item", LivroPdf.SOBREVIVENDO, 57, "Sangue"),
    ref("Dose d'A Praga", "Item", LivroPdf.SOBREVIVENDO, 57, "Sangue"),
    ref("Mandíbula Agonizante", "Item", LivroPdf.SOBREVIVENDO, 57, "Sangue"),
    ref("Retalho Tenebroso", "Item", LivroPdf.SOBREVIVENDO, 57, "Sangue"),
    ref("Ampulheta do Tempo Sofrido", "Item", LivroPdf.SOBREVIVENDO, 58, "Morte"),
    ref("Arreio Neural", "Item", LivroPdf.SOBREVIVENDO, 60, "Energia"),
    ref("Câmera Obscura", "Item", LivroPdf.SOBREVIVENDO, 59, "Conhecimento"),
    ref("Centrifugador Existencial", "Item", LivroPdf.SOBREVIVENDO, 60, "Energia"),
    ref("Enxame Fantasmagórico", "Item", LivroPdf.SOBREVIVENDO, 60, "Conhecimento"),
    ref("Espelho Refletor", "Item", LivroPdf.SOBREVIVENDO, 61, "Energia"),
    ref("Fuzil Alheio", "Item", LivroPdf.SOBREVIVENDO, 61, "Energia"),
    ref("Injeção de Lodo", "Item", LivroPdf.SOBREVIVENDO, 59, "Morte"),
    ref("Instantâneo Mortal", "Item", LivroPdf.SOBREVIVENDO, 59, "Morte"),
    ref("Primeira Adaga", "Item", LivroPdf.SOBREVIVENDO, 61, "Medo"),
    ref("Projétil de Lodo — curto", "Item", LivroPdf.SOBREVIVENDO, 59, "Morte"),
    ref("Projétil de Lodo — longo", "Item", LivroPdf.SOBREVIVENDO, 59, "Morte"),
    ref("Rádio Chiador", "Item", LivroPdf.SOBREVIVENDO, 59, "Morte"),
    ref("Repositório do Fracasso", "Item", LivroPdf.SOBREVIVENDO, 60, "Conhecimento"),
    ref("Tábula do Saber Custoso", "Item", LivroPdf.SOBREVIVENDO, 60, "Conhecimento"),

    // Arquivos Secretos
    ref("Vampirismo", "Ritual", LivroPdf.ARQUIVOS_SECRETOS, 76, "Sangue"),
    ref("Carranca Caçadora", "Item", LivroPdf.ARQUIVOS_SECRETOS, 77, "Paranormal"),
    ref("Cajado da Cruz de Sangue", "Item", LivroPdf.ARQUIVOS_SECRETOS, 77, "Amaldiçoado — Sangue"),
    ref("Pé de Coelho", "Item", LivroPdf.ARQUIVOS_SECRETOS, 78, "Paranormal"),
    ref("Sal Dourado", "Item", LivroPdf.ARQUIVOS_SECRETOS, 78, "Paranormal"),
    ref("Terço Maculado", "Item", LivroPdf.ARQUIVOS_SECRETOS, 78, "Amaldiçoado — Sangue"),
    ref("Exorcizado", "Origem", LivroPdf.ARQUIVOS_SECRETOS, 80),
    ref("Sensitivo Rebelde", "Origem", LivroPdf.ARQUIVOS_SECRETOS, 80),
    ref("Monstruoso — Combatente", "Trilha — Combatente", LivroPdf.SOBREVIVENDO, 17),
    ref("Monstruoso — Especialista", "Trilha — Especialista", LivroPdf.ARQUIVOS_SECRETOS, 81),
    ref("Monstruoso — Ocultista", "Trilha — Ocultista", LivroPdf.ARQUIVOS_SECRETOS, 85),
)

val TIPOS_CONTEUDO = listOf("Todos", "Ritual", "Item", "Origem", "Trilha — Combatente", "Trilha — Especialista", "Trilha — Ocultista", "Classe")
