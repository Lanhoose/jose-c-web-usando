package com.arquivoparanormal.app.data

/**
 * Catálogo ampliado conferido contra os PDFs fornecidos pelo usuário.
 *
 * O catálogo mantém os nomes, categorias e páginas de referência para que o
 * aplicativo não dependa apenas de uma lista de sumário. As descrições longas
 * continuam no leitor PDF, evitando duplicar o livro inteiro dentro do APK.
 */

data class ConteudoCatalogado(
    val nome: String,
    val tipo: String,
    val livro: LivroPdf,
    val pagina: Int,
    val classe: String = "",
    val elemento: String = "",
    val observacao: String = "",
)

val ORIGENS_SOBREVIVENDO = listOf(
    OrigemDef("Amigo dos Animais", "Adestramento, Percepção", "Companheiro Animal"),
    OrigemDef("Astronauta", "Ciências, Fortitude", "Acostumado ao Extremo"),
    OrigemDef("Chef do Outro Lado", "Ocultismo, Profissão (cozinheiro)", "Fome do Outro Lado"),
    OrigemDef("Colegial", "Atualidades, Tecnologia", "Poder da Amizade"),
    OrigemDef("Cosplayer", "Artes, Vontade", "Não é fantasia, é cosplay!"),
    OrigemDef("Diplomata", "Atualidades, Diplomacia", "Conexões"),
    OrigemDef("Explorador", "Fortitude, Sobrevivência", "Manual do Sobrevivente"),
    OrigemDef("Experimento", "Atletismo, Fortitude", "Mutação"),
    OrigemDef("Fanático por Criaturas", "Investigação, Ocultismo", "Conhecimento Oculto"),
    OrigemDef("Fotógrafo", "Artes, Percepção", "Através da Lente"),
    OrigemDef("Inventor Paranormal", "Profissão (engenheiro), Vontade", "Invenção Paranormal"),
    OrigemDef("Jovem Místico", "Ocultismo, Religião", "A Culpa é das Estrelas"),
    OrigemDef("Legista do Turno da Noite", "Ciências, Medicina", "Luto Habitual"),
    OrigemDef("Mateiro", "Percepção, Sobrevivência", "Mapa Celeste"),
    OrigemDef("Mergulhador", "Atletismo, Fortitude", "Fôlego de Nadador"),
    OrigemDef("Motorista", "Pilotagem, Reflexos", "Mãos no Volante"),
    OrigemDef("Nerd Entusiasta", "Ciências, Tecnologia", "O Inteligentão"),
    OrigemDef("Profetizado", "Vontade, uma perícia relacionada à premonição", "Luta ou Fuga"),
    OrigemDef("Psicólogo", "Intuição, Profissão (psicólogo)", "Terapia"),
    OrigemDef("Repórter Investigativo", "Atualidades, Investigação", "Encontrar a Verdade"),
)

val ORIGENS_ARQUIVOS_SECRETOS = listOf(
    OrigemDef("Exorcizado", "Fortitude, Ocultismo", "O Que Restou"),
    OrigemDef("Sensitivo Rebelde", "Intuição, Vontade", "Sussurros e Vultos"),
)

/**
 * Lista usada pelos seletores da ficha. Remove duplicatas por nome para que
 * origens como Astronauta, Colegial e Mateiro não apareçam duas vezes.
 */
val ORIGENS_COMPLETAS: List<OrigemDef> = buildList {
    (ORIGENS + ORIGENS_SOBREVIVENDO + ORIGENS_ARQUIVOS_SECRETOS)
        .distinctBy { it.nome }
        .forEach(::add)
}

data class TrilhaCatalogada(
    val nome: String,
    val classe: String,
    val livro: LivroPdf,
    val pagina: Int,
    val observacao: String = "",
)

val TRILHAS_SOBREVIVENDO = listOf(
    TrilhaCatalogada("Agente Secreto", "Combatente/Especialista", LivroPdf.SOBREVIVENDO, 15),
    TrilhaCatalogada("Caçador", "Especialista", LivroPdf.SOBREVIVENDO, 18),
    TrilhaCatalogada("Erudito", "Especialista", LivroPdf.SOBREVIVENDO, 24),
    TrilhaCatalogada("Perseverante", "Especialista", LivroPdf.SOBREVIVENDO, 25),
)

val TRILHAS_ARQUIVOS_SECRETOS = listOf(
    TrilhaCatalogada("Monstruoso — Combatente", "Combatente", LivroPdf.ARQUIVOS_SECRETOS, 80),
    TrilhaCatalogada("Monstruoso — Especialista", "Especialista", LivroPdf.ARQUIVOS_SECRETOS, 81),
    TrilhaCatalogada("Monstruoso — Ocultista", "Ocultista", LivroPdf.ARQUIVOS_SECRETOS, 84),
)

val CLASSES_ADICIONAIS = listOf(
    ConteudoCatalogado("Sobrevivente", "Classe", LivroPdf.SOBREVIVENDO, 30),
)

data class PoderCatalogado(
    val nome: String,
    val classe: String,
    val livro: LivroPdf,
    val pagina: Int,
    val observacao: String = "",
)

val PODERES_SOBREVIVENDO = listOf(
    PoderCatalogado("Apego Angustiado", "Combatente", LivroPdf.SOBREVIVENDO, 14),
    PoderCatalogado("Caminho para Forca", "Combatente", LivroPdf.SOBREVIVENDO, 14),
    PoderCatalogado("Crente das Cicatrizes", "Combatente", LivroPdf.SOBREVIVENDO, 14),
    PoderCatalogado("Correria Desesperada", "Combatente", LivroPdf.SOBREVIVENDO, 14),
    PoderCatalogado("Engolir o Choro", "Combatente", LivroPdf.SOBREVIVENDO, 14),
    PoderCatalogado("Instinto de Fuga", "Combatente", LivroPdf.SOBREVIVENDO, 14),
    PoderCatalogado("Mochileiro", "Combatente", LivroPdf.SOBREVIVENDO, 14),
    PoderCatalogado("Paranoia Defensiva", "Combatente", LivroPdf.SOBREVIVENDO, 14),
    PoderCatalogado("Sacrificar os Joelhos", "Combatente", LivroPdf.SOBREVIVENDO, 14),
    PoderCatalogado("Acolher o Terror", "Especialista", LivroPdf.SOBREVIVENDO, 22),
    PoderCatalogado("Contatos Oportunos", "Especialista", LivroPdf.SOBREVIVENDO, 22),
    PoderCatalogado("Disfarce Sutil", "Especialista", LivroPdf.SOBREVIVENDO, 22),
    PoderCatalogado("Esconderijo Desesperado", "Especialista", LivroPdf.SOBREVIVENDO, 22),
    PoderCatalogado("Especialista Diletante", "Especialista", LivroPdf.SOBREVIVENDO, 22),
    PoderCatalogado("Flashback", "Especialista", LivroPdf.SOBREVIVENDO, 22),
    PoderCatalogado("Leitura Fria", "Especialista", LivroPdf.SOBREVIVENDO, 22),
    PoderCatalogado("Mãos Firmes", "Especialista", LivroPdf.SOBREVIVENDO, 22),
    PoderCatalogado("Plano B", "Especialista", LivroPdf.SOBREVIVENDO, 22),
)

val RITUAIS_SOBREVIVENDO = listOf(
    ConteudoCatalogado("Esfolar", "Ritual", LivroPdf.SOBREVIVENDO, 49, elemento = "Sangue"),
    ConteudoCatalogado("O Odor da Caçada", "Ritual", LivroPdf.SOBREVIVENDO, 50, elemento = "Sangue"),
    ConteudoCatalogado("Forma Monstruosa", "Ritual", LivroPdf.SOBREVIVENDO, 51, elemento = "Sangue"),
    ConteudoCatalogado("Apagar as Luzes", "Ritual", LivroPdf.SOBREVIVENDO, 51, elemento = "Morte"),
    ConteudoCatalogado("Língua Morta", "Ritual", LivroPdf.SOBREVIVENDO, 52, elemento = "Morte"),
    ConteudoCatalogado("Fedor Pútrido", "Ritual", LivroPdf.SOBREVIVENDO, 53, elemento = "Morte"),
    ConteudoCatalogado("Overclock", "Ritual", LivroPdf.SOBREVIVENDO, 56, elemento = "Energia"),
    ConteudoCatalogado("Tremeluzir", "Ritual", LivroPdf.SOBREVIVENDO, 56, elemento = "Energia"),
    ConteudoCatalogado("Mutar", "Ritual", LivroPdf.SOBREVIVENDO, 57, elemento = "Energia"),
    ConteudoCatalogado("Milagre Ionizante", "Ritual", LivroPdf.SOBREVIVENDO, 57, elemento = "Energia"),
)



val RITUAIS_ARQUIVOS_SECRETOS = listOf(
    RitualDef("Vampirismo", "2º", "Sangue", "Absorve fragmentos de memórias e instintos através do sangue.", "Nenhuma"),
)

val RITUAIS_COMPLETOS: List<RitualDef> =
    RITUAIS_LIVRO +
    RITUAIS_SOBREVIVENDO.map {
        RitualDef(
            nome = it.nome,
            circulo = "2º",
            elemento = it.elemento,
            descricao = "Ritual adicional de Sobrevivendo ao Horror. Abra o PDF na página ${it.pagina} para a descrição completa.",
        )
    } +
    RITUAIS_ARQUIVOS_SECRETOS

val ITENS_ARQUIVOS_SECRETOS = listOf(
    ConteudoCatalogado("Carranca Caçadora", "Item", LivroPdf.ARQUIVOS_SECRETOS, 78),
    ConteudoCatalogado("Cajado da Cruz de Sangue", "Item Amaldiçoado", LivroPdf.ARQUIVOS_SECRETOS, 78, elemento = "Sangue"),
    ConteudoCatalogado("Pé de Coelho", "Item", LivroPdf.ARQUIVOS_SECRETOS, 79),
    ConteudoCatalogado("Sal Dourado", "Item", LivroPdf.ARQUIVOS_SECRETOS, 79),
    ConteudoCatalogado("Terço Maculado", "Item Amaldiçoado", LivroPdf.ARQUIVOS_SECRETOS, 79, elemento = "Sangue"),
)

val AMEACAS_SOBREVIVENDO = listOf(
    "Sepultado", "Mescla", "Espectro Inesquecido", "Uivar", "Derretido",
    "Melancolia", "Quibungo", "Profundo", "Memento Mori", "Rascunho",
    "Medusa", "Amigo Imaginário",
).map { ConteudoCatalogado(it, "Ameaça", LivroPdf.SOBREVIVENDO, 124) } +
    listOf(
        "Bêbado Local", "Burocrata", "Fazendeiro Isolado", "Investigador",
        "Médico / Religioso", "Serial Killer", "Predador Sofisticado",
        "Caçador de Gente / Artista da Morte",
        "Ariranha", "Cavalo", "Enxame de Tocandiras", "Gorila",
        "Leão", "Lobo", "Touro", "Urso Pardo",
    ).map { ConteudoCatalogado(it, "Ameaça da Realidade", LivroPdf.SOBREVIVENDO, 158) }

val AMEACAS_LIVRO = listOf(
    "Aberração de Carne", "Carente", "Dama de Sangue", "Enpap-X", "Minotauro",
    "Mulher Afogada", "Titã de Sangue", "Zumbi de Sangue", "Zumbi de Sangue Bestial",
    "O Diabo", "Carniçal Preto da Morte", "Ceifador Espiral", "Espiral",
    "Veneno Pútrido", "Marionete", "Múmia Xipófaga", "Nidere",
    "Sempiternal", "O Deus da Morte", "Senhor do Tempo", "Parasita de Culpa",
    "Máscara do Desespero", "Descontrolado", "Perturbado", "Sukkalgir",
    "Espírito Plasmático", "Espectro Radioativo",
).map { ConteudoCatalogado(it, "Ameaça", LivroPdf.REGRAS, 180) }

val CONTEUDO_COMPLETO_PDFS: List<ConteudoCatalogado> =
    CLASSES_ADICIONAIS +
    RITUAIS_SOBREVIVENDO +
    ITENS_ARQUIVOS_SECRETOS +
    AMEACAS_SOBREVIVENDO +
    AMEACAS_LIVRO +
    TRILHAS_SOBREVIVENDO.map {
        ConteudoCatalogado(it.nome, "Trilha", it.livro, it.pagina, classe = it.classe, observacao = it.observacao)
    } +
    TRILHAS_ARQUIVOS_SECRETOS.map {
        ConteudoCatalogado(it.nome, "Trilha", it.livro, it.pagina, classe = it.classe, observacao = it.observacao)
    } +
    PODERES_SOBREVIVENDO.map {
        ConteudoCatalogado(it.nome, "Poder", it.livro, it.pagina, classe = it.classe, observacao = it.observacao)
    } +
    ORIGENS_SOBREVIVENDO.map {
        ConteudoCatalogado(it.nome, "Origem", LivroPdf.SOBREVIVENDO, 7, observacao = it.poder)
    } +
    ORIGENS_ARQUIVOS_SECRETOS.map {
        ConteudoCatalogado(it.nome, "Origem", LivroPdf.ARQUIVOS_SECRETOS, 80, observacao = it.poder)
    }
