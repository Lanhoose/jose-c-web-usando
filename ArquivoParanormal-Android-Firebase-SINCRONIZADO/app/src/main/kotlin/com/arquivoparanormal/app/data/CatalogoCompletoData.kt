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
    TrilhaCatalogada("Monstruoso — Combatente", "Combatente", LivroPdf.SOBREVIVENDO, 17),
    TrilhaCatalogada("Caçador", "Especialista", LivroPdf.SOBREVIVENDO, 18),
    TrilhaCatalogada("Erudito", "Especialista", LivroPdf.SOBREVIVENDO, 24),
    TrilhaCatalogada("Perseverante", "Especialista", LivroPdf.SOBREVIVENDO, 25),
    TrilhaCatalogada("Bibliotecário", "Especialista", LivroPdf.SOBREVIVENDO, 24),
    TrilhaCatalogada("Muambeiro", "Especialista", LivroPdf.SOBREVIVENDO, 26),
    TrilhaCatalogada("Exorcista", "Ocultista", LivroPdf.SOBREVIVENDO, 28),
    TrilhaCatalogada("Possuído", "Ocultista", LivroPdf.SOBREVIVENDO, 29),
    TrilhaCatalogada("Parapsicólogo", "Especialista", LivroPdf.SOBREVIVENDO, 30),
)

val TRILHAS_ARQUIVOS_SECRETOS = listOf(
    TrilhaCatalogada("Monstruoso — Especialista", "Especialista", LivroPdf.ARQUIVOS_SECRETOS, 81),
    TrilhaCatalogada("Monstruoso — Ocultista", "Ocultista", LivroPdf.ARQUIVOS_SECRETOS, 85),
)

val CLASSES_ADICIONAIS = listOf(
    ConteudoCatalogado("Sobrevivente", "Classe especial por Estágios", LivroPdf.SOBREVIVENDO, 30, observacao = "Classe alternativa com progressão por Estágios, diferente da progressão normal por NEX. Não deve ser tratada como uma das três classes básicas."),
)

data class PoderCatalogado(
    val nome: String,
    val classe: String,
    val livro: LivroPdf,
    val pagina: Int,
    val observacao: String = "",
)

private val DETALHES_PODERES_SOBREVIVENDO = mapOf(
    "Apego Angustiado" to "Enquanto estiver Morrendo e consciente, você não fica inconsciente; ao terminar uma rodada consciente nessa condição, perde 2 SAN.",
    "Caminho para Forca" to "Em perseguições, melhora o bônus que você fornece com Sacrifício e pode reduzir a visibilidade dos aliados ao chamar atenção.",
    "Ciente das Cicatrizes" to "Ao procurar pistas relacionadas a armas ou ferimentos, pode usar Luta ou Pontaria no lugar da perícia original. Requisito: treinado em Luta ou Pontaria.",
    "Correria Desesperada" to "Recebe +3m de deslocamento e +2d20 em testes de perícia para fugir em uma perseguição.",
    "Engolir o Choro" to "Não sofre penalidades de condições em testes de perícia para fugir nem em testes de Furtividade.",
    "Instinto de Fuga" to "No início de uma cena de perseguição, recebe +2d20 em todos os testes de perícia durante a cena. Requisito: treinado em Intuição.",
    "Mochileiro" to "Aumenta seu limite de carga em 5 espaços e permite beneficiar-se de uma vestimenta adicional. Requisito: Vig 2.",
    "Paranoia Defensiva" to "Uma vez por cena, pode gastar 3 PE para receber +5 na Defesa contra o próximo ataque da cena ou +5 em um único teste de perícia até o fim da cena.",
    "Sacrificar os Joelhos" to "Em uma cena de perseguição, ao fazer a ação esforço extra, pode gastar 2 PE para passar automaticamente no teste de perícia. Requisito: treinado em Atletismo.",
    "Sem Tempo, Irmão" to "Uma vez por cena de investigação, pode prestar ajuda de forma apressada e fazer uma rolagem adicional na tabela de eventos de investigação.",
    "Valentão" to "Pode usar Força no lugar de Presença para Intimidação e, uma vez por cena, gastar 1 PE para usar Intimidação para assustar como ação livre.",
    "Acolher o Terror" to "Pode se entregar para o medo uma vez por sessão.",
    "Contatos Oportunos" to "Durante um interlúdio, aciona contatos locais e recebe um aliado até o fim da missão ou até ser dispensado. Requisito: treinado em Crime.",
    "Disfarce Sutil" to "Ao fazer um disfarce em si mesmo, pode gastar 1 PE para fazê-lo como ação completa sem kit; com kit, recebe +5 no teste. Requisitos: Pre 2 e treinado em Enganação.",
    "Esconderijo Desesperado" to "Não sofre a penalidade de Furtividade por se mover no deslocamento normal; ao passar em teste para esconder-se, sua visibilidade diminui 2 em vez de 1.",
    "Especialista Diletante" to "Aprende um poder de outra classe, exceto poderes de trilha ou paranormais, desde que cumpra seus pré-requisitos. Requisito: NEX 30%.",
    "Flashback" to "Escolha uma origem diferente da sua e receba o poder dessa origem.",
    "Leitura Fria" to "Uma vez por interlúdio, após observar/interagir com um NPC por alguns minutos, pode fazer três perguntas pessoais; cada pergunta não respondida concede 2 PE temporários até o fim da missão. Requisito: treinado em Intuição.",
    "Mãos Firmes" to "Ao fazer Furtividade para se esconder ou uma ação discreta envolvendo manipular um objeto, pode gastar 2 PE para receber +2d20 no teste. Requisito: treinado em Furtividade.",
    "Plano de Fuga" to "Em perseguições, pode usar Intelecto no lugar de Força para criar obstáculos; uma vez por cena, pode gastar 2 PE para ser bem-sucedido na ação esforço extra.",
    "Remoer Memórias" to "Uma vez por cena, em um teste de perícia baseado em Intelecto ou Presença, pode gastar 2 PE para substituir o teste por Intelecto DT 15. Requisito: Int 1.",
    "Resistir à Pressão" to "Uma vez por cena de investigação, pode gastar 5 PE para coordenar o grupo; a urgência aumenta em 1 rodada e, nessa rodada, todos recebem +2 em testes de perícia. Requisito: treinado em Investigação.",
    "Deixe os Sussurros Guiarem" to "Uma vez por cena, pode gastar 2 PE e uma rodada para receber +2 em testes de perícia para investigação até o fim da cena; enquanto ativo, falhar em um teste de perícia custa 1 SAN.",
    "Domínio Esotérico" to "Ao lançar um ritual, pode combinar os efeitos de até dois catalisadores ritualísticos diferentes. Requisito: Int 3.",
    "Estalos Macabros" to "Ao tentar chamar a atenção de um ser ou fintar em combate, pode gastar 1 PE para usar Ocultismo no lugar da perícia original; contra pessoa/animal distraído, recebe +5.",
    "Minha Dor me Impulsiona" to "Com pelo menos 5 PV, ao fazer Acrobacia, Atletismo ou Furtividade pode gastar 1 PE para receber +1d6 no teste. Requisito: Vig 2.",
    "Nos Olhos do Monstro" to "Em uma cena envolvendo criatura paranormal, pode gastar uma rodada e 3 PE para encará-la; até o fim da cena recebe +5 em testes contra ela, exceto testes de ataque.",
    "Olhar Sinistro" to "Pode usar Presença no lugar de Intelecto para Ocultismo e usar Ocultismo para coagir. Requisito: Pre 1.",
    "Sentido Premonitório" to "Gasta 3 PE para ativar um sentido premonitório por uma rodada; recebe uma rodada de antecedência em eventos de investigação e pode decidir algumas ações futuras conforme aprovação do mestre. Manter custa 1 PE por rodada.",
    "Sincronia Paranormal" to "Pode gastar uma ação padrão e 2 PE para sincronizar mentalmente com outros sobreviventes de encontros paranormais; a conexão distribui um bônus baseado em Presença a cada rodada. Requisito: Pre 2.",
    "Traçado Conjuratório" to "Pode gastar 1 PE e uma ação completa para traçar um símbolo paranormal; dentro dele recebe +2 em Ocultismo e resistência, e a DT para resistir aos seus rituais aumenta em +2.",
)

val PODERES_SOBREVIVENDO = listOf(
    PoderCatalogado("Apego Angustiado", "Combatente", LivroPdf.SOBREVIVENDO, 14),
    PoderCatalogado("Caminho para Forca", "Combatente", LivroPdf.SOBREVIVENDO, 14),
    PoderCatalogado("Ciente das Cicatrizes", "Combatente", LivroPdf.SOBREVIVENDO, 14),
    PoderCatalogado("Correria Desesperada", "Combatente", LivroPdf.SOBREVIVENDO, 14),
    PoderCatalogado("Engolir o Choro", "Combatente", LivroPdf.SOBREVIVENDO, 14),
    PoderCatalogado("Instinto de Fuga", "Combatente", LivroPdf.SOBREVIVENDO, 14),
    PoderCatalogado("Mochileiro", "Combatente", LivroPdf.SOBREVIVENDO, 14),
    PoderCatalogado("Paranoia Defensiva", "Combatente", LivroPdf.SOBREVIVENDO, 14),
    PoderCatalogado("Sacrificar os Joelhos", "Combatente", LivroPdf.SOBREVIVENDO, 14),
    PoderCatalogado("Sem Tempo, Irmão", "Combatente", LivroPdf.SOBREVIVENDO, 14),
    PoderCatalogado("Valentão", "Combatente", LivroPdf.SOBREVIVENDO, 14),
    PoderCatalogado("Acolher o Terror", "Especialista", LivroPdf.SOBREVIVENDO, 22),
    PoderCatalogado("Contatos Oportunos", "Especialista", LivroPdf.SOBREVIVENDO, 22),
    PoderCatalogado("Disfarce Sutil", "Especialista", LivroPdf.SOBREVIVENDO, 22),
    PoderCatalogado("Esconderijo Desesperado", "Especialista", LivroPdf.SOBREVIVENDO, 22),
    PoderCatalogado("Especialista Diletante", "Especialista", LivroPdf.SOBREVIVENDO, 22),
    PoderCatalogado("Flashback", "Especialista", LivroPdf.SOBREVIVENDO, 22),
    PoderCatalogado("Leitura Fria", "Especialista", LivroPdf.SOBREVIVENDO, 22),
    PoderCatalogado("Mãos Firmes", "Especialista", LivroPdf.SOBREVIVENDO, 22),
    PoderCatalogado("Plano de Fuga", "Especialista", LivroPdf.SOBREVIVENDO, 23),
    PoderCatalogado("Remoer Memórias", "Especialista", LivroPdf.SOBREVIVENDO, 23),
    PoderCatalogado("Resistir à Pressão", "Especialista", LivroPdf.SOBREVIVENDO, 23),
    PoderCatalogado("Deixe os Sussurros Guiarem", "Ocultista", LivroPdf.SOBREVIVENDO, 27),
    PoderCatalogado("Domínio Esotérico", "Ocultista", LivroPdf.SOBREVIVENDO, 27),
    PoderCatalogado("Estalos Macabros", "Ocultista", LivroPdf.SOBREVIVENDO, 27),
    PoderCatalogado("Minha Dor me Impulsiona", "Ocultista", LivroPdf.SOBREVIVENDO, 27),
    PoderCatalogado("Nos Olhos do Monstro", "Ocultista", LivroPdf.SOBREVIVENDO, 27),
    PoderCatalogado("Olhar Sinistro", "Ocultista", LivroPdf.SOBREVIVENDO, 27),
    PoderCatalogado("Sentido Premonitório", "Ocultista", LivroPdf.SOBREVIVENDO, 27),
    PoderCatalogado("Sincronia Paranormal", "Ocultista", LivroPdf.SOBREVIVENDO, 28),
    PoderCatalogado("Traçado Conjuratório", "Ocultista", LivroPdf.SOBREVIVENDO, 28),
).map { it.copy(observacao = DETALHES_PODERES_SOBREVIVENDO[it.nome].orEmpty()) }

val PODERES_GERAIS_CATALOGO = listOf(
    "Artista Marcial" to "", "Combater com Duas Armas" to "Agi 3; treinado em Luta ou Pontaria", "Saque Rápido" to "Treinado em Iniciativa", "Tiro Certeiro" to "Treinado em Pontaria",
    "Acrobático" to "Agi 2", "Ás do Volante" to "Agi 2", "Atlético" to "For 2", "Atraente" to "Pre 2",
    "Dedos Ágeis" to "Agi 2", "Detector de Mentiras" to "Pre 2", "Especialista em Emergências" to "Int 2",
    "Estigmatizado" to "", "Foco em Perícia" to "Treinado na perícia escolhida", "Inventário Organizado" to "Int 2",
    "Informado" to "Int 2", "Interrogador" to "For 2", "Mentiroso Nato" to "Pre 2", "Observador" to "Int 2",
    "Pai de Pet" to "Pre 2", "Palavras de Devoção" to "Pre 2", "Parceiro" to "Treinado em Diplomacia; NEX 30%",
    "Pensamento Tático" to "Int 2", "Personalidade Esotérica" to "Int 2", "Persuasivo" to "Pre 2",
    "Pesquisador Científico" to "Int 2", "Proativo" to "Agi 2", "Provisões de Emergência" to "",
    "Racionalidade Inflexível" to "Int 3", "Rato de Computador" to "Int 2", "Resposta Rápida" to "Agi 2",
    "Talentoso" to "Pre 2", "Teimosia Obstinada" to "Pre 2", "Tenacidade" to "Vig 2", "Sentidos Aguçados" to "Pre 2",
    "Sobrevivencialista" to "Int 2", "Sorrateiro" to "Agi 2", "Vitalidade Reforçada" to "Vig 2", "Vontade Inabalável" to "Pre 2",
).map { (nome, requisito) -> ConteudoCatalogado(nome, "Poder Geral", LivroPdf.SOBREVIVENDO, 34, observacao = requisito) }

val RITUAIS_SOBREVIVENDO = listOf(
    ConteudoCatalogado("Esfolar", "Ritual", LivroPdf.SOBREVIVENDO, 49, elemento = "Sangue"),
    ConteudoCatalogado("Sede de Adrenalina", "Ritual", LivroPdf.SOBREVIVENDO, 50, elemento = "Sangue"),
    ConteudoCatalogado("O Odor da Caçada", "Ritual", LivroPdf.SOBREVIVENDO, 50, elemento = "Sangue"),
    ConteudoCatalogado("Martírio de Sangue", "Ritual", LivroPdf.SOBREVIVENDO, 51, elemento = "Sangue"),
    ConteudoCatalogado("Apagar as Luzes", "Ritual", LivroPdf.SOBREVIVENDO, 51, elemento = "Morte"),
    ConteudoCatalogado("Língua Morta", "Ritual", LivroPdf.SOBREVIVENDO, 52, elemento = "Morte"),
    ConteudoCatalogado("Fedor Pútrido", "Ritual", LivroPdf.SOBREVIVENDO, 53, elemento = "Morte"),
    ConteudoCatalogado("Singularidade Temporal", "Ritual", LivroPdf.SOBREVIVENDO, 53, elemento = "Morte"),
    ConteudoCatalogado("Desfazer Sinapses", "Ritual", LivroPdf.SOBREVIVENDO, 54, elemento = "Conhecimento"),
    ConteudoCatalogado("Aurora da Verdade", "Ritual", LivroPdf.SOBREVIVENDO, 54, elemento = "Conhecimento"),
    ConteudoCatalogado("Relembrar Fragmento", "Ritual", LivroPdf.SOBREVIVENDO, 55, elemento = "Conhecimento"),
    ConteudoCatalogado("Pronunciar Sigilo", "Ritual", LivroPdf.SOBREVIVENDO, 55, elemento = "Conhecimento"),
    ConteudoCatalogado("Overclock", "Ritual", LivroPdf.SOBREVIVENDO, 56, elemento = "Energia"),
    ConteudoCatalogado("Tremeluzir", "Ritual", LivroPdf.SOBREVIVENDO, 56, elemento = "Energia"),
    ConteudoCatalogado("Mutar", "Ritual", LivroPdf.SOBREVIVENDO, 57, elemento = "Energia"),
    ConteudoCatalogado("Milagre Ionizante", "Ritual", LivroPdf.SOBREVIVENDO, 57, elemento = "Energia"),
)

private val RITUAIS_SOBREVIVENDO_DEFINICOES = listOf(
    RitualDef("Esfolar", "1º", "Sangue", "Projeta agulhas e lâminas que causam dano de corte e podem deixar o alvo sangrando; a resistência reduz o dano e evita a condição.", resistencia = "Reflexos parcial", discenteExtraPE = 2, verdadeiroExtraPE = 5, requisitoDiscente = "2º círculo", requisitoVerdadeiro = "3º círculo"),
    RitualDef("Sede de Adrenalina", "2º", "Sangue", "Permite repetir um teste de Acrobacia ou Atletismo ou reduzir dano de impacto; o uso pode deixar o conjurador atordoado.", discenteExtraPE = 3, verdadeiroExtraPE = 7, requisitoVerdadeiro = "4º círculo e afinidade"),
    RitualDef("O Odor da Caçada", "3º", "Sangue", "Concede faro e melhora sua atuação em perseguições enquanto estiver ativo, mas depois cobra um preço físico.", discenteExtraPE = 4, verdadeiroExtraPE = 9, requisitoVerdadeiro = "Afinidade"),
    RitualDef("Martírio de Sangue", "4º", "Sangue", "Transforma o corpo em uma forma monstruosa, concedendo faro, visão no escuro, cura acelerada, bônus defensivos e PV temporários, mas prejudica a interação social e a concentração.", verdadeiroExtraPE = 5, requisitoVerdadeiro = "Afinidade"),
    RitualDef("Apagar as Luzes", "1º", "Morte", "Apaga fontes de luz em alcance curto e concede visão no escuro ao conjurador; versões avançadas ampliam alcance e beneficiários.", discenteExtraPE = 2, verdadeiroExtraPE = 5, requisitoDiscente = "2º círculo", requisitoVerdadeiro = "3º círculo"),
    RitualDef("Língua Morta", "2º", "Morte", "Reanima um cadáver para responder perguntas enquanto o ritual é sustentado, dentro de um limite de rodadas.", discenteExtraPE = 3, verdadeiroExtraPE = 7, requisitoVerdadeiro = "4º círculo e afinidade"),
    RitualDef("Fedor Pútrido", "3º", "Morte", "Torna o conjurador semelhante a um cadáver, afastando animais e concedendo bônus de Furtividade e Enganação, mas causa dano de Morte por rodada enquanto sustentado.", discenteExtraPE = 4, verdadeiroExtraPE = 9, requisitoVerdadeiro = "Afinidade"),
    RitualDef("Singularidade Temporal", "4º", "Morte", "Distorce o tempo de um objeto não paranormal, avançando seu estado conforme a natureza do objeto e a interpretação do mestre.", resistencia = "Fortitude (veja texto)", discenteExtraPE = 5, verdadeiroExtraPE = 10),
    RitualDef("Desfazer Sinapses", "1º", "Conhecimento", "Causa dano de Conhecimento e pode deixar o alvo atormentado; a resistência reduz o dano e evita a condição.", resistencia = "Vontade parcial", discenteExtraPE = 2, verdadeiroExtraPE = 5, requisitoDiscente = "2º círculo", requisitoVerdadeiro = "3º círculo"),
    RitualDef("Aurora da Verdade", "2º", "Conhecimento", "Cria uma área que força seres dentro dela a dizer a verdade e revela tentativas de ocultação.", resistencia = "Vontade parcial", discenteExtraPE = 3, verdadeiroExtraPE = 7, requisitoVerdadeiro = "4º círculo e afinidade"),
    RitualDef("Relembrar Fragmento", "3º", "Conhecimento", "Restaura temporariamente um objeto danificado ou ilegível ao estado de sua última anotação.", discenteExtraPE = 4, verdadeiroExtraPE = 9, requisitoVerdadeiro = "Afinidade"),
    RitualDef("Pronunciar Sigilo", "4º", "Conhecimento", "Profana a Realidade com um sigilo que pode esquecer, cegar ou fazer o alvo inexistir temporariamente.", resistencia = "Vontade parcial", discenteExtraPE = 5, verdadeiroExtraPE = 10, requisitoVerdadeiro = "Afinidade"),
    RitualDef("Overclock", "1º", "Energia", "Permite obter de outro modo informações de um aparelho eletrônico por meio de um desafio durante a conjuração.", discenteExtraPE = 2, verdadeiroExtraPE = 5, requisitoDiscente = "2º círculo", requisitoVerdadeiro = "3º círculo"),
    RitualDef("Tremeluzir", "2º", "Energia", "Permite atravessar objetos sólidos enquanto o corpo está em estado de flickering, mas causa dano de Energia e pode falhar ao atravessar.", discenteExtraPE = 3, verdadeiroExtraPE = 7, requisitoVerdadeiro = "4º círculo"),
    RitualDef("Mutar", "3º", "Energia", "Silencia os sons produzidos pelo conjurador, concede bônus em Furtividade e reduz ganho de visibilidade em cenas de furtividade.", discenteExtraPE = 4, verdadeiroExtraPE = 9, requisitoVerdadeiro = "Afinidade com Energia"),
    RitualDef("Milagre Ionizante", "3º", "Energia", "Pode remover várias condições ou uma doença/veneno, mas após a cura exige um teste de Fortitude DT 30 para evitar uma consequência paranormal.", resistencia = "Fortitude DT 30 (após a cura)"),
)



val RITUAIS_ARQUIVOS_SECRETOS = listOf(
    RitualDef("Vampirismo", "2º", "Sangue", "Absorve fragmentos de memórias e instintos através do sangue.", "Nenhuma"),
)

val RITUAIS_COMPLETOS: List<RitualDef> =
    RITUAIS_LIVRO +
    RITUAIS_SOBREVIVENDO_DEFINICOES +
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
    PODERES_GERAIS_CATALOGO +
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
