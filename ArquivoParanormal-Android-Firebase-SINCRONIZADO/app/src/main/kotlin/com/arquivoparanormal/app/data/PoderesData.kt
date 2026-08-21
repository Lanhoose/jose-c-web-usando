package com.arquivoparanormal.app.data

/**
 * Poderes exibidos na criação da ficha. O catálogo é separado por origem,
 * classe e trilha para impedir que o jogador escolha poderes que não pertencem
 * à construção atual.
 *
 * As descrições são resumos próprios para a interface; o texto integral deve
 * ser consultado no livro indicado.
 */
data class PoderDisponivel(
    val nome: String,
    val categoria: String, // Origem | Classe | Trilha
    val classe: String = "",
    val trilha: String = "",
    val origem: String = "",
    val nexMin: Int = 5,
    val descricao: String,
    val requisito: String = "",
    val custo: String = "",
    val automatico: Boolean = false,
    val pagina: Int = 0,
    val livro: LivroPdf = LivroPdf.REGRAS,
)

private fun classe(nome: String, pagina: Int, vararg poderes: Pair<String, String>) =
    poderes.map { PoderDisponivel(it.first, "Classe", classe = nome, nexMin = 15, descricao = it.second, pagina = pagina) }

private fun trilha(
    nome: String,
    classe: String,
    pagina: Int,
    poderes: List<Triple<Int, String, String>>,
    livro: LivroPdf = LivroPdf.REGRAS,
) = poderes.map {
    PoderDisponivel(
        it.second, "Trilha", classe = classe, trilha = nome, nexMin = it.first,
        descricao = it.third, automatico = true, pagina = pagina, livro = livro,
    )
}

val PODERES_CLASSE_REGRAS_BASE: List<PoderDisponivel> =
    classe("Combatente", 25,
        "Armamento Pesado" to "Concede proficiência com armas pesadas. Requer Força 2.",
        "Ataque de Oportunidade" to "Permite reagir quando um inimigo sai voluntariamente do seu alcance corpo a corpo.",
        "Combate Defensivo" to "Ao atacar de forma defensiva, reduz seus ataques e aumenta sua Defesa até o próximo turno.",
        "Golpe Demolidor" to "Melhora ataques contra objetos e a manobra quebrar.",
        "Golpe Pesado" to "Aumenta o dano de uma arma corpo a corpo em um dado.",
        "Incansável" to "Permite uma ação adicional de investigação uma vez por cena, usando Força ou Agilidade.",
        "Presteza Atlética" to "Permite usar Força ou Agilidade em certas ações de investigação e ajudar um aliado.",
        "Proteção Pesada" to "Concede proficiência com proteções pesadas.",
        "Reflexos Defensivos" to "Aumenta Defesa e testes de resistência.",
        "Segurar o Gatilho" to "Permite ataques adicionais com arma de fogo, pagando PE progressivamente.",
        "Sentido Tático" to "Analisa o ambiente e recebe bônus defensivos e de resistência até o fim da cena.",
        "Tanque de Guerra" to "Melhora Defesa e resistência a dano de proteções pesadas.",
        "Tiro de Cobertura" to "Usa fogo de cobertura para dificultar a movimentação e os ataques do alvo.",
        "Transcender" to "Escolha um poder paranormal elegível. Não recebe o aumento normal de Sanidade desse NEX.",
        "Treinamento em Perícia" to "Treina duas perícias; em NEX maiores pode elevar o grau de treinamento."
    ) + classe("Especialista", 29,
        "Balística Avançada" to "Concede proficiência com armas táticas de fogo e bônus de dano com armas de fogo.",
        "Conhecimento Aplicado" to "Permite usar Intelecto como atributo-base de várias perícias.",
        "Hacker" to "Melhora testes e velocidade de invasão de sistemas.",
        "Mãos Rápidas" to "Permite realizar testes de Crime como ação livre pagando PE.",
        "Mochila de Utilidades" to "Reduz categoria e espaço ocupado por um item escolhido.",
        "Movimento Tático" to "Ignora certas penalidades de deslocamento até o fim do turno.",
        "Na Trilha Certa" to "Acumula bônus para o próximo teste depois de obter sucesso procurando pistas.",
        "Nerd" to "Permite buscar uma informação útil sobre uma cena uma vez por cena.",
        "Ninja Urbano" to "Concede proficiência e bônus de dano com determinados grupos de armas.",
        "Pensamento Ágil" to "Permite uma ação adicional de procurar pistas em cenas de investigação.",
        "Perito em Explosivos" to "Melhora a DT dos seus explosivos e permite excluir alvos da explosão.",
        "Primeira Impressão" to "Concede um grande bônus no primeiro teste social/intuitivo apropriado de uma cena.",
        "Transcender" to "Escolha um poder paranormal elegível. Não recebe o aumento normal de Sanidade desse NEX.",
        "Treinamento em Perícia" to "Treina duas perícias; em NEX maiores pode elevar o grau de treinamento."
    ) + classe("Ocultista", 33,
        "Camuflar Ocultismo" to "Oculta símbolos e permite conjuração discreta pagando PE adicional.",
        "Criar Selo" to "Permite fabricar selos paranormais de rituais conhecidos.",
        "Envolto em Mistério" to "Melhora Enganação e Intimidação contra pessoas não treinadas em Ocultismo.",
        "Especialista em Elemento" to "Escolha um elemento e aumente a DT dos seus rituais desse elemento.",
        "Ferramentas Paranormais" to "Reduz a categoria de um item paranormal e facilita sua ativação.",
        "Fluxo de Poder" to "Permite manter dois efeitos sustentados com uma única ação livre.",
        "Guiado pelo Paranormal" to "Permite uma ação adicional de investigação uma vez por cena.",
        "Identificação Paranormal" to "Concede grande bônus para identificar criaturas, objetos e rituais paranormais.",
        "Improvisar Componentes" to "Permite encontrar componentes ritualísticos improvisados uma vez por cena.",
        "Intuição Paranormal" to "Permite usar Intelecto ou Presença na ação facilitar investigação.",
        "Mestre em Elemento" to "Reduz o custo de rituais de um elemento escolhido.",
        "Ritual Potente" to "Soma Intelecto aos efeitos de dano ou cura dos seus rituais.",
        "Ritual Predileto" to "Reduz o custo de um ritual conhecido.",
        "Tatuagem Ritualística" to "Reduz o custo de rituais pessoais que tenham você como alvo.",
        "Transcender" to "Escolha um poder paranormal elegível. Não recebe o aumento normal de Sanidade desse NEX.",
        "Treinamento em Perícia" to "Treina duas perícias; em NEX maiores pode elevar o grau de treinamento."
    )


private val METADADOS_PODERES_CLASSE: Map<String, Triple<Int, String, String>> = mapOf(
    "Armamento Pesado" to Triple(15, "For 2", ""),
    "Artista Marcial" to Triple(15, "", ""),
    "Ataque de Oportunidade" to Triple(15, "", "1 PE"),
    "Combater com Duas Armas" to Triple(15, "Agi 3; treinado em Luta ou Pontaria", ""),
    "Combate Defensivo" to Triple(15, "Int 2", ""),
    "Golpe Demolidor" to Triple(15, "For 2; treinado em Luta", "1 PE"),
    "Golpe Pesado" to Triple(15, "", ""),
    "Incansável" to Triple(15, "", "2 PE"),
    "Presteza Atlética" to Triple(15, "", "1 PE"),
    "Proteção Pesada" to Triple(30, "NEX 30%", ""),
    "Reflexos Defensivos" to Triple(15, "Agi 2", ""),
    "Saque Rápido" to Triple(15, "Treinado em Iniciativa", ""),
    "Segurar o Gatilho" to Triple(60, "NEX 60%", "2 PE por ataque extra, aumentando progressivamente"),
    "Sentido Tático" to Triple(15, "Int 2; treinado em Percepção e Tática", "2 PE"),
    "Tanque de Guerra" to Triple(15, "Proteção Pesada", ""),
    "Tiro Certeiro" to Triple(15, "Treinado em Pontaria", ""),
    "Tiro de Cobertura" to Triple(15, "", "1 PE"),
    "Transcender" to Triple(15, "", ""),
    "Treinamento em Perícia" to Triple(15, "", ""),
    "Balística Avançada" to Triple(15, "", ""),
    "Conhecimento Aplicado" to Triple(15, "Int 2", "2 PE"),
    "Hacker" to Triple(15, "Treinado em Tecnologia", ""),
    "Mãos Rápidas" to Triple(15, "Agi 3; treinado em Crime", "1 PE"),
    "Mochila de Utilidades" to Triple(15, "", ""),
    "Movimento Tático" to Triple(15, "Treinado em Atletismo", "1 PE"),
    "Na Trilha Certa" to Triple(15, "", "1 PE ou mais, conforme o bônus acumulado"),
    "Nerd" to Triple(15, "", "2 PE"),
    "Ninja Urbano" to Triple(15, "", ""),
    "Pensamento Ágil" to Triple(15, "", "2 PE"),
    "Perito em Explosivos" to Triple(15, "", ""),
    "Primeira Impressão" to Triple(15, "", ""),
    "Camuflar Ocultismo" to Triple(15, "", "+2 PE para conjuração discreta"),
    "Criar Selo" to Triple(15, "", "PE igual ao custo do ritual"),
    "Envolto em Mistério" to Triple(15, "", ""),
    "Especialista em Elemento" to Triple(15, "", ""),
    "Ferramentas Paranormais" to Triple(15, "", ""),
    "Fluxo de Poder" to Triple(15, "NEX 60%", ""),
    "Guiado pelo Paranormal" to Triple(15, "", "2 PE"),
    "Identificação Paranormal" to Triple(15, "", ""),
    "Improvisar Componentes" to Triple(15, "", ""),
    "Intuição Paranormal" to Triple(15, "", ""),
    "Mestre em Elemento" to Triple(45, "Especialista em Elemento no elemento escolhido; NEX 45%", ""),
    "Ritual Potente" to Triple(15, "Int 2", ""),
    "Ritual Predileto" to Triple(15, "", ""),
    "Tatuagem Ritualística" to Triple(15, "", ""),
)

val PODERES_CLASSE_REGRAS: List<PoderDisponivel> = PODERES_CLASSE_REGRAS_BASE.map { poder ->
    val meta = METADADOS_PODERES_CLASSE[poder.nome]
    if (meta == null) poder else poder.copy(nexMin = meta.first, requisito = meta.second, custo = meta.third)
}

val PODERES_TRILHA_REGRAS: List<PoderDisponivel> =
    trilha("Aniquilador", "Combatente", 26, listOf(
        Triple(10, "A Favorita", "Escolhe uma arma favorita e reduz sua categoria."),
        Triple(40, "Técnica Secreta", "Permite aplicar efeitos especiais em ataques com a arma favorita."),
        Triple(65, "Técnica Sublime", "Adiciona efeitos ofensivos ainda mais fortes à técnica secreta."),
        Triple(99, "Máquina de Matar", "Aprimora novamente a categoria, margem de ameaça e dano da arma favorita."),
    )) +
    trilha("Comandante de Campo", "Combatente", 27, listOf(
        Triple(10, "Inspirar Confiança", "Permite ajudar um aliado a repetir um teste recém-realizado."),
        Triple(40, "Estrategista", "Direciona aliados e fornece ações de movimento adicionais."),
        Triple(65, "Brecha na Guarda", "Cria uma oportunidade para um ataque adicional de um aliado."),
        Triple(99, "Oficial Comandante", "Pode conceder uma ação padrão adicional aos aliados em alcance médio."),
    )) +
    trilha("Guerreiro", "Combatente", 27, listOf(
        Triple(10, "Técnica Letal", "Aumenta a margem de ameaça dos ataques corpo a corpo."),
        Triple(40, "Revidar", "Permite contra-atacar depois de bloquear um ataque."),
        Triple(65, "Força Opressora", "Melhora manobras depois de acertar ataques corpo a corpo."),
        Triple(99, "Potência Máxima", "Dobra os bônus numéricos do Ataque Especial com armas corpo a corpo."),
    )) +
    trilha("Operações Especiais", "Combatente", 28, listOf(
        Triple(10, "Iniciativa Aprimorada", "Concede bônus de Iniciativa e movimento extra na primeira rodada."),
        Triple(40, "Ataque Extra", "Permite um ataque adicional uma vez por rodada mediante custo de PE."),
        Triple(65, "Surto de Adrenalina", "Permite uma ação adicional pagando PE."),
        Triple(99, "Sempre Alerta", "Concede uma ação padrão adicional no início de cada cena de combate."),
    )) +
    trilha("Tropa de Choque", "Combatente", 28, listOf(
        Triple(10, "Casca Grossa", "Aumenta PV e melhora o bloqueio."),
        Triple(40, "Cai Dentro", "Pode atrair ataques de inimigos que ameaçam seus aliados."),
        Triple(65, "Duro de Matar", "Permite reduzir dano sofrido e depois também dano paranormal."),
        Triple(99, "Inquebrável", "Fica muito mais resistente quando machucado ou morrendo."),
    )) +
    trilha("Atirador de Elite", "Especialista", 30, listOf(
        Triple(10, "Mira de Elite", "Concede proficiência com armas de fogo de balas longas e soma Intelecto ao dano."),
        Triple(40, "Disparo Letal", "Melhora a margem de ameaça após mirar."),
        Triple(65, "Disparo Impactante", "Pode trocar o dano de um disparo por uma manobra."),
        Triple(99, "Atirar para Matar", "Acertos críticos com armas de fogo causam dano máximo."),
    )) +
    trilha("Infiltrador", "Especialista", 30, listOf(
        Triple(10, "Ataque Furtivo", "Causa dano extra ao atacar alvos desprevenidos ou flanqueados."),
        Triple(40, "Gatuno", "Aumenta Atletismo e Crime e facilita esconder-se."),
        Triple(65, "Assassinar", "Analisa um alvo e pode dobrar o dano extra do Ataque Furtivo."),
        Triple(99, "Sombra Fugaz", "Evita a penalidade de Furtividade depois de uma ação chamativa."),
    )) +
    trilha("Médico de Campo", "Especialista", 31, listOf(
        Triple(10, "Paramédico", "Cura você ou um aliado e melhora a cura nos NEX seguintes."),
        Triple(40, "Equipe de Trauma", "Remove uma condição negativa de um aliado adjacente."),
        Triple(65, "Resgate", "Aproxima-se de aliados machucados e melhora Defesa após curá-los."),
        Triple(99, "Reanimação", "Pode trazer de volta um personagem morto na mesma cena, salvo morte por dano massivo."),
    )) +
    trilha("Negociador", "Especialista", 31, listOf(
        Triple(10, "Eloquência", "Pode fascinar alvos usando Diplomacia, Enganação ou Intimidação."),
        Triple(40, "Discurso Motivador", "Inspira aliados e concede bônus em perícias."),
        Triple(65, "Eu Conheço um Cara", "Ativa uma rede de contatos para conseguir favores relevantes."),
        Triple(99, "Truque de Mestre", "Simula temporariamente uma habilidade vista em um aliado."),
    )) +
    trilha("Técnico", "Especialista", 31, listOf(
        Triple(10, "Inventário Otimizado", "Soma Intelecto à Força para calcular capacidade de carga."),
        Triple(40, "Remendão", "Conserta equipamento e reduz categoria de equipamentos gerais."),
        Triple(65, "Improvisar", "Cria temporariamente equipamentos gerais usando materiais da cena."),
        Triple(99, "Preparado para Tudo", "Pode declarar que possui um item geral adequado pagando PE."),
    )) +
    trilha("Conduíte", "Ocultista", 34, listOf(
        Triple(10, "Ampliar Ritual", "Aumenta alcance ou área de um ritual pagando PE."),
        Triple(40, "Acelerar Ritual", "Pode conjurar um ritual como ação livre aumentando seu custo."),
        Triple(65, "Anular Ritual", "Pode tentar cancelar um ritual que o tenha como alvo."),
        Triple(99, "Canalizar o Medo", "Aprende o ritual Canalizar o Medo."),
    )) +
    trilha("Flagelador", "Ocultista", 34, listOf(
        Triple(10, "Poder do Flagelo", "Pode pagar custos de rituais com seus próprios PV."),
        Triple(40, "Abraçar a Dor", "Reduz à metade dano não paranormal sofrido."),
        Triple(65, "Absorver Agonia", "Ganha PE temporários ao reduzir inimigos a 0 PV com rituais."),
        Triple(99, "Medo Tangível", "Aprende o ritual Medo Tangível."),
    )) +
    trilha("Graduado", "Ocultista", 35, listOf(
        Triple(10, "Saber Ampliado", "Aprende rituais adicionais fora do limite normal."),
        Triple(40, "Grimório Ritualístico", "Cria um grimório com rituais extras armazenados."),
        Triple(65, "Rituais Eficientes", "Aumenta a DT para resistir aos seus rituais."),
        Triple(99, "Conhecendo o Medo", "Aprende o ritual Conhecendo o Medo."),
    )) +
    trilha("Intuitivo", "Ocultista", 35, listOf(
        Triple(10, "Mente Sã", "Aumenta a resistência contra efeitos paranormais."),
        Triple(40, "Presença Poderosa", "Adiciona Presença ao limite de PE por turno para conjuração."),
        Triple(65, "Inabalável", "Aumenta resistências mental e paranormal e melhora defesas contra efeitos paranormais."),
        Triple(99, "Presença do Medo", "Aprende o ritual Presença do Medo."),
    )) +
    trilha("Lâmina Paranormal", "Ocultista", 35, listOf(
        Triple(10, "Lâmina Maldita", "Aprende Amaldiçoar Arma e pode usar Ocultismo nos ataques da arma."),
        Triple(40, "Gladiador Paranormal", "Ganha PE temporários ao acertar ataques corpo a corpo."),
        Triple(65, "Conjuração Marcial", "Depois de conjurar um ritual, pode realizar um ataque corpo a corpo adicional."),
        Triple(99, "Lâmina do Medo", "Aprende o ritual Lâmina do Medo."),
    )) +
    trilha("Agente Secreto", "Combatente/Especialista", 15, listOf(
        Triple(10, "Carteirada", "Recebe treinamento ou bônus em Diplomacia/Enganação e documentos especiais para missões."),
        Triple(40, "O Sorriso", "Melhora Diplomacia e Enganação e permite repetir certos testes sociais pagando PE."),
        Triple(65, "Método Investigativo", "Aumenta a urgência de cenas de investigação e pode impedir um evento pagando PE."),
        Triple(99, "Multifacetado", "Usa Sanidade para receber temporariamente habilidades de outra trilha elegível."),
    ), LivroPdf.SOBREVIVENDO) +
    trilha("Caçador", "Especialista", 18, listOf(
        Triple(10, "Rastrear o Paranormal", "Treina Sobrevivência ou a aprimora e usa a perícia para rastrear sinais paranormais."),
        Triple(40, "Estudar Fraquezas", "Estuda uma criatura ou alvo específico para obter informações e bônus contra ele."),
        Triple(65, "Atacar das Sombras", "Melhora Furtividade e reduz penalidades ao atacar de forma discreta."),
        Triple(99, "Estudar a Presa", "Transforma um tipo de criatura/cultista em presa e recebe grandes bônus contra esse tipo."),
    ), LivroPdf.SOBREVIVENDO) +
    trilha("Erudito", "Especialista", 24, listOf(
        Triple(10, "Conhecimento Prático", "Quando faz um teste de perícia (exceto Luta e Pontaria), pode gastar 2 PE para trocar o atributo-base da perícia por Intelecto; se tiver Conhecimento Aplicado, o custo é reduzido em 1 PE."),
        Triple(40, "Leitor Contumaz", "Ao usar a ação de interlúdio ler, recebe um bônus que aumenta para 1d8 e pode gastar 2 PE para aumentar esse bônus em mais 1 dado."),
        Triple(65, "Rato de Biblioteca", "Em um ambiente com muitos livros, pode usar alguns minutos para obter os benefícios da ação de interlúdio ler uma vez por cena."),
        Triple(99, "A Força do Saber", "Recebe +1 em Intelecto, soma Intelecto ao total de PE e pode trocar o atributo-base de uma perícia por Intelecto."),
    ), LivroPdf.SOBREVIVENDO) +
    trilha("Perseverante", "Especialista", 25, listOf(
        Triple(10, "Soluções Improvisadas", "Pode gastar 2 PE para rolar novamente dois dados de um teste recém-realizado e ficar com o melhor resultado entre as duas rolagens."),
        Triple(40, "Fuga Obstinada", "Recebe +2d20 em testes de perícia para fugir de um inimigo; em perseguições, se for a presa, pode acumular até 4 falhas antes de ser pego."),
        Triple(65, "Determinação Inquestionável", "Uma vez por cena, pode gastar 5 PE e uma ação padrão para remover uma condição de medo, mental ou paralisia, conforme o critério do mestre."),
        Triple(99, "Só Mais Um Passo...", "Uma vez por rodada, quando sofreria dano que reduziria seus PV a 0, pode gastar 5 PE para ficar com 1 PV; não funciona contra dano massivo."),
    ), LivroPdf.SOBREVIVENDO) +
    trilha("Bibliotecário", "Especialista", 24, listOf(
        Triple(10, "Conhecimento Prático", "Pode gastar 2 PE para trocar o atributo-base de uma perícia (exceto Luta e Pontaria) por Intelecto; com Conhecimento Aplicado, o custo é reduzido em 1 PE."),
        Triple(40, "Leitor Contumaz", "Ao usar a ação de interlúdio ler, recebe um bônus que aumenta para 1d8 e pode gastar 2 PE para aumentar esse bônus em mais 1 dado."),
        Triple(65, "Rato de Biblioteca", "Em um ambiente com muitos livros, pode obter os benefícios da ação de interlúdio ler em poucos minutos, uma vez por cena."),
        Triple(99, "A Força do Saber", "Recebe +1 em Intelecto, soma Intelecto ao total de PE e pode trocar o atributo-base de uma perícia por Intelecto."),
    ), LivroPdf.SOBREVIVENDO) +
    trilha("Muambeiro", "Especialista", 26, listOf(
        Triple(10, "Mascate", "Recebe treinamento em uma Profissão adequada, +5 na capacidade de carga e reduz a DT de fabricar item improvisado em 10."),
        Triple(40, "Fabricação Própria", "Fabrica itens mundanos em metade do tempo e pode produzir munições, explosivos e consumíveis com ações de manutenção."),
        Triple(65, "Laboratório de Campo", "Recebe treinamento em uma Profissão adequada e pode fabricar ou consertar itens paranormais durante o interlúdio, usando fabricação em campo."),
        Triple(99, "Achado Conveniente", "Uma vez por missão, pode produzir com 5 PE uma ferramenta ou item adequado encontrado/provavelmente disponível, a critério do mestre, que funciona até o fim da cena."),
    ), LivroPdf.SOBREVIVENDO) +
    trilha("Parapsicólogo", "Especialista", 30, listOf(
        Triple(10, "Terapia", "Requer treinamento em Profissão (psicólogo). Pode usar essa perícia como Diplomacia e ajudar a recuperar Sanidade ou repetir certos testes de resistência mental."),
        Triple(40, "Palavras-Chave", "Ao passar em um teste de perícia para acalmar uma pessoa, pode gastar PE até seu limite para recuperar Sanidade dela."),
        Triple(65, "Reprogramação Mental", "Pode usar um interlúdio e PE para manipular voluntariamente a mente de outra pessoa, que recebe um poder temporário até o próximo interlúdio."),
        Triple(99, "A Sanidade Está Lá Fora", "Pode gastar uma ação de movimento e 5 PE para remover todas as condições de medo ou mentais de uma pessoa adjacente."),
    ), LivroPdf.SOBREVIVENDO) +
    trilha("Exorcista", "Ocultista", 28, listOf(
        Triple(10, "Revelação do Mal", "Recebe treinamento em Religião (ou +2), pode usar Religião no lugar de Investigação e Percepção para perceber sinais paranormais."),
        Triple(40, "Poder da Fé", "Torna-se veterano em Religião (ou recebe +2) e pode repetir um teste de resistência falho usando Religião, pagando 2 PE."),
        Triple(65, "Parareligiosidade", "Ao conjurar um ritual, pode gastar +2 PE para adicionar um efeito equivalente ao de um catalisador ritualístico escolhido."),
        Triple(99, "Chagas da Resistência", "Pode gastar 10 PV quando sua Sanidade seria reduzida a 0 para ficar com SAN 1."),
    ), LivroPdf.SOBREVIVENDO) +
    trilha("Possuído", "Ocultista", 29, listOf(
        Triple(10, "Poder Não Desejado", "Recebe o poder Transcender em vez de um poder de ocultista, mas ganha uma reserva de Pontos de Possessão igual a 3 + 2 por poder Transcender; o limite gasto por turno é sua Presença."),
        Triple(40, "As Sombras Dentro de Mim", "A recuperação de Pontos de Possessão ao dormir aumenta e você pode gastar PE para assumir controle temporário do próprio corpo, recebendo bônus físicos e furtivos."),
        Triple(65, "Ele Me Ensina", "Pode transcender ou receber o primeiro poder de uma trilha de ocultista que não seja a sua, respeitando os pré-requisitos."),
        Triple(99, "Tornamo-nos Um", "Conforme sua afinidade, recebe um dos poderes finais da entidade; a forma final pode conceder cura, turno adicional ou um poder temporário, conforme o elemento."),
    ), LivroPdf.SOBREVIVENDO) +
    trilha("Monstruoso — Combatente", "Combatente", 17, listOf(
        Triple(10, "Ser Amaldiçoado", "Você recebe +2 em Ocultismo (ou torna-se treinado), escolhe um elemento e precisa realizar diariamente uma etapa ritualística para obter os efeitos do elemento. Sem a preparação, sofre efeitos semelhantes a fome e sede."),
        Triple(40, "Ser Macabro", "A etapa ritualística passa a conceder resistência 10 ao elemento e os efeitos do elemento escolhido ficam mais intensos; a penalidade em perícias também aumenta."),
        Triple(65, "Ser Assustador", "A resistência concedida pela etapa ritualística aumenta para 15 e a transformação passa a reduzir permanentemente sua Presença em 1, além dos efeitos do elemento escolhido."),
        Triple(99, "Ser Aterrorizante", "A transformação alcança o limite e você passa a ser considerado uma criatura paranormal para efeitos de habilidades e itens; a resistência aumenta para 20 e os efeitos do elemento chegam ao estágio final."),
    ), LivroPdf.SOBREVIVENDO) +
    trilha("Monstruoso — Especialista", "Especialista", 81, listOf(
        Triple(10, "Ser Experimentado", "Você se torna treinado em Ocultismo (ou recebe +2 se já for treinado), escolhe Sangue, Morte, Conhecimento ou Energia e sofre –2 em Diplomacia, Enganação e Intuição. Uma vez por dia faz um experimento com componente ritualístico; se fizer, recupera 1d8+1 PV e recebe efeitos do elemento escolhido até o fim do dia. O elemento escolhido também determina sua afinidade, se adquirir afinidade."),
        Triple(40, "Ser Testado", "A penalidade social passa de –2 para –5. O experimento passa a recuperar 2d8+2 PV e concede efeitos mais fortes conforme o elemento, incluindo alterações temporárias de atributo que não alteram PV, PE, SAN ou número de perícias."),
        Triple(65, "Ser Expurgado", "Você expurga um braço e reduz permanentemente sua Presença em 1. A penalidade social passa a –5 e, ao experimentar, recupera 3d8+3 PV. O braço recebe um benefício específico conforme o elemento; alguns efeitos têm custos de 1 a 3 PE."),
        Triple(99, "Ser Apavorante", "Você reduz permanentemente sua Presença em 1 e a penalidade social passa a –10. O experimento recupera 4d8+4 PV. Os efeitos do elemento chegam ao estágio máximo e incluem +1 em um atributo e um ritual específico para cada elemento."),
    ), LivroPdf.ARQUIVOS_SECRETOS) +
    trilha("Monstruoso — Ocultista", "Ocultista", 85, listOf(
        Triple(10, "Ser Escarificado", "Você recebe +2 em Ocultismo, escolhe Sangue, Morte, Conhecimento ou Energia e sofre –2 em Diplomacia, Enganação e Intuição. Uma vez por dia faz uma escarificação com componente ritualístico; se fizer, recupera 1d4 PE e recebe os efeitos do elemento escolhido até o fim do dia. O elemento escolhido também determina sua afinidade, se adquirir afinidade."),
        Triple(40, "Ser Perfurado", "A progressão de Monstruoso altera os efeitos de Ser Escarificado conforme o elemento escolhido, aumentando a transformação e mantendo a exigência diária de preparação ritualística."),
        Triple(65, "Ser Rasgado", "A progressão aumenta a transformação corporal e substitui os efeitos anteriores por benefícios específicos do elemento escolhido, conforme a progressão da trilha."),
        Triple(99, "Ser Mutilado", "A transformação chega ao estágio máximo. Você recebe os efeitos finais do elemento escolhido, incluindo mudança do atributo usado para PE e DT dos rituais e +1 ponto no atributo correspondente."),
    ), LivroPdf.ARQUIVOS_SECRETOS)
private val REGRAS_ORIGEM_PODER = mapOf(
    "Acadêmico" to "Saber é Poder", "Agente de Saúde" to "Técnica Medicinal", "Amnésico" to "Vislumbres do Passado",
    "Artista" to "Magnum Opus", "Atleta" to "110%", "Chef" to "Ingrediente Secreto", "Criminoso" to "O Crime Compensa",
    "Cultista Arrependido" to "Traços do Outro Lado", "Desgarrado" to "Calejado", "Engenheiro" to "Ferramenta Favorita",
    "Executivo" to "Processo Otimizado", "Fazendeiro" to "Calejado", "Investigador" to "Faro para Pistas", "Lutador" to "Mão Pesada",
    "Magnata" to "Patrocinador da Ordem", "Mercenário" to "Posição de Combate", "Militar" to "Para Bellum",
    "Operário" to "Ferramenta de Trabalho", "Policial" to "Patrulha", "Religioso" to "Acalentar", "Servidor Público" to "Espírito Cívico",
    "Teórico da Conspiração" to "Eu Já Sabia", "T.I." to "Motor de Busca", "Trabalhador Rural" to "Desbravador",
    "Trambiqueiro" to "Impostor", "Universitário" to "Dedicação", "Vítima" to "Cicatrizes Psicológicas",
)

private val REGRAS_ORIGEM_DESC = mapOf(
    "Saber é Poder" to "Recebe bônus ao gastar PE em testes usando Intelecto.",
    "Técnica Medicinal" to "Adiciona Intelecto às curas realizadas pelo agente.",
    "Vislumbres do Passado" to "Uma vez por sessão, pode tentar reconhecer pessoas ou lugares ligados ao passado perdido.",
    "Magnum Opus" to "Uma obra famosa pode fazer uma pessoa reconhecê-lo e melhorar seus testes sociais contra ela.",
    "110%" to "Aprimora testes de perícia baseados em Força ou Agilidade, exceto Luta e Pontaria, gastando PE.",
    "Ingrediente Secreto" to "Melhora os benefícios obtidos pela ação alimentar no interlúdio.",
    "O Crime Compensa" to "Permite carregar um item encontrado na missão seguinte sem contar no limite normal de itens por patente.",
    "Traços do Outro Lado" to "Concede um poder paranormal à escolha, mas reduz a Sanidade inicial pela metade.",
    "Calejado" to "Aumenta PV conforme o NEX.",
    "Ferramenta Favorita" to "Escolha um item que passa a contar como uma categoria abaixo.",
    "Processo Otimizado" to "Melhora testes de perícia em testes estendidos e revisão de documentos pagando PE.",
    "Faro para Pistas" to "Uma vez por cena, melhora um teste para procurar pistas pagando PE.",
    "Mão Pesada" to "Aumenta o dano de ataques corpo a corpo.",
    "Patrocinador da Ordem" to "Seu limite de crédito é considerado um nível acima do atual.",
    "Posição de Combate" to "Concede uma ação de movimento adicional no primeiro turno de uma cena de ação mediante PE.",
    "Para Bellum" to "Aumenta o dano causado com armas de fogo.",
    "Ferramenta de Trabalho" to "Escolha uma arma simples ou tática apropriada à profissão; ela recebe bônus de ataque, dano e margem de ameaça.",
    "Patrulha" to "Aumenta Defesa.",
    "Acalentar" to "Melhora testes de Religião para acalmar e recupera Sanidade de quem é acalmado.",
    "Espírito Cívico" to "Melhora o bônus concedido ao ajudar aliados pagando PE.",
    "Eu Já Sabia" to "Concede resistência a dano mental igual ao Intelecto.",
    "Motor de Busca" to "Pode substituir certos testes por Tecnologia quando tiver acesso à internet.",
    "Desbravador" to "Melhora Adestramento e Sobrevivência e ignora penalidade de terreno difícil.",
    "Impostor" to "Pode substituir um teste de perícia por Enganação uma vez por cena.",
    "Dedicação" to "Aumenta PE ao longo dos NEX ímpares e o limite de PE por turno.",
    "Cicatrizes Psicológicas" to "Aumenta Sanidade conforme o NEX.",
)

val PODERES_ORIGEM_REGRAS: List<PoderDisponivel> =
    REGRAS_ORIGEM_PODER.map { (origem, poder) ->
        PoderDisponivel(
            nome = poder,
            categoria = "Origem",
            origem = origem,
            descricao = REGRAS_ORIGEM_DESC[poder] ?: "Habilidade concedida pela origem.",
            automatico = true,
            pagina = 16,
        )
    } + (ORIGENS_SOBREVIVENDO + ORIGENS_ARQUIVOS_SECRETOS).map { origem ->
        PoderDisponivel(
            nome = origem.poder,
            categoria = "Origem",
            origem = origem.nome,
            descricao = when (origem.nome) {
                "Exorcizado" -> "Uma parte da criatura paranormal que o possuiu continua dentro de você. Escolha um elemento exceto Medo: você recebe RD 5 contra ele, mas perde 2 SAN ao entrar em contato com esse elemento pela primeira vez na cena."
                "Sensitivo Rebelde" -> "Ao fazer Diplomacia, Enganação, Intimidação ou Intuição, você pode perder 2 SAN para receber +5 no teste."
                else -> "Habilidade concedida automaticamente por esta origem. Consulte o livro indicado para a regra completa."
            },
            automatico = true,
            pagina = if (origem in ORIGENS_ARQUIVOS_SECRETOS) 80 else 7,
            livro = if (origem in ORIGENS_ARQUIVOS_SECRETOS) LivroPdf.ARQUIVOS_SECRETOS else LivroPdf.SOBREVIVENDO,
        )
    }.distinctBy { "${it.origem}|${it.nome}" }

val PODERES_DISPONIVEIS_REGRAS: List<PoderDisponivel> =
    PODERES_ORIGEM_REGRAS + PODERES_CLASSE_REGRAS + PODERES_TRILHA_REGRAS

private val CUSTOS_PODERES = mapOf(
    "Na Trilha Certa" to "1 PE por bônus acumulado; o custo e o bônus aumentam conforme os sucessos consecutivos.",
    "Caminho para Forca" to "1 PE quando usado para aprimorar a ação de Sacrifício ou Chamar Atenção.",
    "Disfarce Sutil" to "1 PE para disfarce rápido.",
    "Mãos Firmes" to "2 PE.",
    "Deixe os Sussurros Guiarem" to "2 PE + 1 rodada; manutenção conforme a cena.",
    "Estalos Macabros" to "1 PE.",
    "Minha Dor me Impulsiona" to "1 PE.",
    "Nos Olhos do Monstro" to "3 PE + 1 rodada.",
    "Sentido Premonitório" to "3 PE para ativar; 1 PE por rodada para manter.",
)

private val DETALHES_PODERES = mapOf(
    "Na Trilha Certa" to "Sempre que obtiver sucesso em um teste para procurar pistas, você pode gastar 1 PE para receber +1d20 no próximo teste. Os custos e bônus são cumulativos: se passar novamente, pode pagar 2 PE para um total de +2d20 no próximo teste, e assim por diante.",
    "Apego Angustiado" to "Você não fica inconsciente por estar Morrendo. Sempre que terminar uma rodada consciente nessa condição, perde 2 pontos de Sanidade.",
    "Caminho para Forca" to "Ao usar Sacrifício em uma cena de perseguição, pode gastar 1 PE para aumentar o bônus fornecido aos outros personagens. Ao chamar atenção em furtividade, pode gastar 1 PE para reduzir ainda mais a visibilidade dos aliados próximos.",
    "Ciente das Cicatrizes" to "Ao procurar uma pista relacionada a armas ou ferimentos, pode usar Luta ou Pontaria no lugar da perícia original. Requisito: treinado em Luta ou Pontaria.",
    "Correria Desesperada" to "+3m de deslocamento e bônus em testes de perícia para fugir em uma perseguição.",
    "Engolir o Choro" to "Não sofre penalidades de condições em testes de perícia para fugir e em testes de Furtividade.",
    "Instinto de Fuga" to "No início de uma cena de perseguição, recebe +2 em todos os testes de perícia durante a cena. Requisito: treinado em Intuição.",
    "Mochileiro" to "Aumenta o limite de carga em 5 espaços e permite beneficiar-se de uma vestimenta adicional. Requisito: VIG 2.",
    "Acolher o Terror" to "Pode se entregar ao medo uma vez adicional por sessão de jogo.",
    "Contatos Oportunos" to "Durante um interlúdio, pode acionar contatos locais para obter um aliado até o fim da missão ou até ser dispensado. Só pode ter um desses aliados por vez. Requisito: treinado em Crime.",
    "Disfarce Sutil" to "Pode gastar 1 PE para fazer um disfarce em si mesmo como ação completa sem kit; com kit, recebe +5 no teste. Requisito: PRE 2 e treinado em Enganação.",
    "Esconderijo Desesperado" to "Não sofre a penalidade de Furtividade por se mover no deslocamento normal. Ao passar em um teste para esconder-se em furtividade, reduz a visibilidade em 2 em vez de 1.",
    "Especialista Diletante" to "Aprende um poder que não pertença à sua classe, exceto poderes de trilha ou paranormais, desde que cumpra seus requisitos. Requisito: NEX 30%.",
    "Flashback" to "Escolha uma origem diferente da sua e receba o poder dessa origem.",
    "Leitura Fria" to "Após alguns minutos interagindo ou observando um NPC durante um interlúdio, pode fazer três perguntas pessoais. Para cada pergunta que o Mestre não responder, recebe 2 PE temporários até o fim da missão. Requisito: treinado em Intuição.",
    "Mãos Firmes" to "Ao fazer Furtividade para esconder-se ou executar uma ação discreta manipulando um objeto, pode gastar 2 PE para receber bônus no teste. Requisito: treinado em Furtividade.",
    "Deixe os Sussurros Guiarem" to "Uma vez por cena, pode gastar 2 PE e uma rodada para receber +2 em testes de perícia de investigação até o fim da cena; enquanto ativo, falhas nesses testes fazem você perder 1 Sanidade.",
    "Domínio Esotérico" to "Ao lançar um ritual, pode combinar os efeitos de até dois catalisadores ritualísticos diferentes. Requisito: INT 3.",
    "Estalos Macabros" to "Ao atrapalhar a atenção de outro ser, pode gastar 1 PE para usar Ocultismo no lugar da perícia original; contra pessoa ou animal, recebe +5 no teste.",
    "Minha Dor me Impulsiona" to "Ao fazer Acrobacia, Atletismo ou Furtividade, pode gastar 1 PE para receber +1d6, desde que tenha pelo menos 5 pontos de dano em PV. Requisito: VIG 2.",
    "Nos Olhos do Monstro" to "Em uma cena com criatura paranormal, pode gastar uma rodada e 3 PE para encarar a criatura e receber +5 em testes contra ela, exceto ataques, até o fim da cena.",
    "Olhar Sinistro" to "Pode usar Presença no lugar de Intelecto para Ocultismo e usar Ocultismo para coagir. Requisito: PRE 1.",
    "Sentido Premonitório" to "Pode gastar 3 PE para ativar um sentido que antecipa uma rodada de eventos em investigação, furtividade e perseguição; não funciona em combate e exige 1 PE por rodada para manter o efeito."
)

private val PODERES_GERAIS_SOBREVIVENDO = listOf(
    "Artista Marcial" to "", "Combater com Duas Armas" to "Agi 3; treinado em Luta ou Pontaria", "Saque Rápido" to "Treinado em Iniciativa", "Tiro Certeiro" to "Treinado em Pontaria",
    "Acrobático" to "Agi 2", "Ás do Volante" to "Agi 2", "Atlético" to "For 2", "Atraente" to "Pre 2", "Dedos Ágeis" to "Agi 2", "Detector de Mentiras" to "Pre 2",
    "Especialista em Emergências" to "Int 2", "Estigmatizado" to "", "Foco em Perícia" to "Treinado na perícia escolhida", "Inventário Organizado" to "Int 2", "Informado" to "Int 2",
    "Interrogador" to "For 2", "Mentiroso Nato" to "Pre 2", "Observador" to "Int 2", "Pai de Pet" to "Pre 2", "Palavras de Devoção" to "Pre 2",
    "Parceiro" to "Treinado em Diplomacia; NEX 30%", "Pensamento Tático" to "Int 2", "Personalidade Esotérica" to "Int 2", "Persuasivo" to "Pre 2",
    "Pesquisador Científico" to "Int 2", "Proativo" to "Agi 2", "Provisões de Emergência" to "", "Racionalidade Inflexível" to "Int 3",
    "Rato de Computador" to "Int 2", "Resposta Rápida" to "Agi 2", "Talentoso" to "Pre 2", "Teimosia Obstinada" to "Pre 2", "Tenacidade" to "Vig 2",
    "Sentidos Aguçados" to "Pre 2", "Sobrevivencialista" to "Int 2", "Sorrateiro" to "Agi 2", "Vitalidade Reforçada" to "Vig 2", "Vontade Inabalável" to "Pre 2",
)

private val DESCRICOES_PODERES_GERAIS = mapOf(
    "Artista Marcial" to "Ataques desarmados causam 1d6, podem causar dano letal e se tornam ágeis; o dano aumenta para 1d8 no NEX 35% e 1d10 no NEX 70%.",
    "Combater com Duas Armas" to "Ao agredir empunhando duas armas, sendo pelo menos uma leve, faz dois ataques e sofre –2d20 nos testes de ataque até o próximo turno. Requer Agi 3 e treinamento em Luta ou Pontaria.",
    "Saque Rápido" to "Saca ou guarda itens como ação livre e, usando contagem de munição, pode recarregar uma arma de disparo como ação livre uma vez por rodada. Requer treinamento em Iniciativa.",
    "Tiro Certeiro" to "Soma Agilidade às rolagens de dano com armas de disparo e ignora a penalidade contra alvos envolvidos em combate corpo a corpo. Requer treinamento em Pontaria.",
    "Acrobático" to "Recebe treinamento em Acrobacia ou +2 e reduz penalidades de terreno difícil; requer Agi 2.",
    "Ás do Volante" to "Recebe treinamento em Pilotagem ou +2 e pode evitar dano de impacto ao dirigir; requer Agi 2.",
    "Atlético" to "Recebe treinamento em Atletismo ou +2 e +3m de deslocamento; requer For 2.",
    "Atraente" to "Recebe +5 em Artes, Diplomacia, Enganação e Intimidação contra quem possa se sentir fisicamente atraído; requer Pre 2.",
    "Dedos Ágeis" to "Recebe treinamento em Crime ou +2 e pode furtar como ação livre uma vez por rodada; requer Agi 2.",
    "Detector de Mentiras" to "Recebe treinamento em Intuição ou +2 e impõe penalidade a quem mente para você; requer Pre 2.",
    "Especialista em Emergências" to "Recebe treinamento em Medicina ou +2 e pode aplicar medicamentos e cicatrizantes com mais rapidez; requer Int 2.",
    "Estigmatizado" to "Converte dano mental de medo em dano físico em determinadas situações, conforme a regra do poder.",
    "Foco em Perícia" to "Escolha uma perícia (exceto Luta e Pontaria) e receba +2d20 quando fizer um teste dela; requer treinamento na perícia.",
    "Inventário Organizado" to "Organiza melhor a mochila e reduz o espaço de itens muito leves; requer Int 2.",
    "Informado" to "Recebe treinamento em Atualidades ou +2 e pode usar Atualidades no lugar de outra perícia em testes envolvendo informações; requer Int 2.",
    "Interrogador" to "Recebe treinamento em Intimidação ou +2 e pode usar Intimidação para coagir como ação padrão uma vez por cena contra a mesma pessoa; requer For 2.",
    "Mentiroso Nato" to "Recebe treinamento em Enganação ou +2 e reduz a penalidade sofrida por mentiras muito improváveis; requer Pre 2.",
    "Observador" to "Recebe treinamento em Investigação ou +2 e soma Intelecto em Intuição; requer Int 2.",
    "Pai de Pet" to "Recebe treinamento em Adestramento ou +2 e possui um animal aliado; requer Pre 2.",
    "Palavras de Devoção" to "Recebe treinamento em Religião ou +2 e pode usar palavras de fé para obter vantagens específicas; requer Pre 2.",
    "Parceiro" to "Recebe um aliado que acompanha o personagem. Requer treinamento em Diplomacia e NEX 30%.",
    "Pensamento Tático" to "Recebe treinamento em Tática ou +2 e usa conhecimento tático para obter vantagens; requer Int 2.",
    "Personalidade Esotérica" to "Recebe treinamento em Ocultismo ou +2 e melhora sua interação com elementos paranormais; requer Int 2.",
    "Persuasivo" to "Recebe treinamento em Diplomacia ou +2 e melhora a capacidade de convencer pessoas; requer Pre 2.",
    "Pesquisador Científico" to "Recebe treinamento em Ciências ou +2 e usa conhecimento científico para obter informações; requer Int 2.",
    "Proativo" to "Recebe treinamento em Iniciativa ou +2 e melhora sua prontidão para agir; requer Agi 2.",
    "Provisões de Emergência" to "Uma vez por missão, pode produzir provisões adequadas para uma necessidade de sobrevivência.",
    "Racionalidade Inflexível" to "Recebe resistência mental adicional e pode racionalizar efeitos paranormais; requer Int 3.",
    "Rato de Computador" to "Recebe treinamento em Tecnologia ou +2 e melhora sua capacidade de lidar com computadores; requer Int 2.",
    "Resposta Rápida" to "Recebe treinamento em Reflexos ou +2 e reage mais rapidamente a perigos; requer Agi 2.",
    "Talentoso" to "Recebe treinamento em uma perícia adicional e melhora sua capacidade nessa área; requer Pre 2.",
    "Teimosia Obstinada" to "Recebe treinamento em Vontade ou +2 e resiste melhor a efeitos mentais; requer Pre 2.",
    "Tenacidade" to "Recebe treinamento em Fortitude ou +2 e melhora sua resistência física; requer Vig 2.",
    "Sentidos Aguçados" to "Recebe treinamento em Percepção ou +2 e melhora sua capacidade de notar detalhes; requer Pre 2.",
    "Sobrevivencialista" to "Recebe treinamento em Sobrevivência ou +2 e melhora sua capacidade de lidar com situações de sobrevivência; requer Int 2.",
    "Sorrateiro" to "Recebe treinamento em Furtividade ou +2 e melhora sua capacidade de se esconder; requer Agi 2.",
    "Vitalidade Reforçada" to "Aumenta sua resistência física e seus pontos de vida; requer Vig 2.",
    "Vontade Inabalável" to "Recebe treinamento em Vontade ou +2 e se torna mais resistente a pressão mental; requer Pre 2.",
)

private fun personagemCumpreRequisitos(p: Personagem, poder: PoderDisponivel): Boolean {
    val treinado = { pericia: String -> (p.pericias[pericia]?.treino ?: 0) > 0 }
    val nome = poder.nome
    if (p.nex < poder.nexMin) return false
    return when (nome) {
        "Artista Marcial" -> true
        "Combater com Duas Armas" -> (p.atributos["agi"] ?: 1) >= 3 && (treinado("Luta") || treinado("Pontaria"))
        "Saque Rápido" -> treinado("Iniciativa")
        "Tiro Certeiro" -> treinado("Pontaria")
        "Armamento Pesado" -> (p.atributos["for"] ?: 1) >= 2
        "Combate Defensivo" -> (p.atributos["int"] ?: 1) >= 2
        "Golpe Demolidor" -> (p.atributos["for"] ?: 1) >= 2 && treinado("Luta")
        "Proteção Pesada" -> p.nex >= 30
        "Reflexos Defensivos" -> (p.atributos["agi"] ?: 1) >= 2
        "Segurar o Gatilho" -> p.nex >= 60
        "Sentido Tático" -> (p.atributos["int"] ?: 1) >= 2 && treinado("Percepção") && treinado("Tática")
        "Tanque de Guerra" -> "Proteção Pesada" in p.habilidades.lines()
        "Conhecimento Aplicado" -> (p.atributos["int"] ?: 1) >= 2
        "Hacker" -> treinado("Tecnologia")
        "Mãos Rápidas" -> (p.atributos["agi"] ?: 1) >= 3 && treinado("Crime")
        "Movimento Tático" -> treinado("Atletismo")
        "Especialista Diletante" -> p.nex >= 30
        "Remoer Memórias" -> (p.atributos["int"] ?: 1) >= 1
        "Resistir à Pressão" -> treinado("Investigação")
        "Domínio Esotérico" -> (p.atributos["int"] ?: 1) >= 3
        "Minha Dor me Impulsiona" -> (p.atributos["vig"] ?: 1) >= 2
        "Olhar Sinistro" -> (p.atributos["pre"] ?: 1) >= 1
        "Sincronia Paranormal" -> (p.atributos["pre"] ?: 1) >= 2
        "Acrobático" -> (p.atributos["agi"] ?: 1) >= 2
        "Ás do Volante" -> (p.atributos["agi"] ?: 1) >= 2
        "Atlético" -> (p.atributos["for"] ?: 1) >= 2
        "Atraente" -> (p.atributos["pre"] ?: 1) >= 2
        "Dedos Ágeis" -> (p.atributos["agi"] ?: 1) >= 2
        "Detector de Mentiras" -> (p.atributos["pre"] ?: 1) >= 2
        "Especialista em Emergências" -> (p.atributos["int"] ?: 1) >= 2
        "Foco em Perícia" -> poder.requisito.contains("Treinado") || p.pericias.values.any { it.treino > 0 }
        "Inventário Organizado" -> (p.atributos["int"] ?: 1) >= 2
        "Informado" -> (p.atributos["int"] ?: 1) >= 2
        "Interrogador" -> (p.atributos["for"] ?: 1) >= 2
        "Mentiroso Nato" -> (p.atributos["pre"] ?: 1) >= 2
        "Observador" -> (p.atributos["int"] ?: 1) >= 2
        "Pai de Pet" -> (p.atributos["pre"] ?: 1) >= 2
        "Palavras de Devoção" -> (p.atributos["pre"] ?: 1) >= 2
        "Parceiro" -> (p.atributos["pre"] ?: 1) >= 1 && treinado("Diplomacia") && p.nex >= 30
        "Pensamento Tático" -> (p.atributos["int"] ?: 1) >= 2
        "Personalidade Esotérica" -> (p.atributos["int"] ?: 1) >= 2
        "Persuasivo" -> (p.atributos["pre"] ?: 1) >= 2
        "Pesquisador Científico" -> (p.atributos["int"] ?: 1) >= 2
        "Proativo" -> (p.atributos["agi"] ?: 1) >= 2
        "Racionalidade Inflexível" -> (p.atributos["int"] ?: 1) >= 3
        "Rato de Computador" -> (p.atributos["int"] ?: 1) >= 2
        "Resposta Rápida" -> (p.atributos["agi"] ?: 1) >= 2
        "Talentoso" -> (p.atributos["pre"] ?: 1) >= 2
        "Teimosia Obstinada" -> (p.atributos["pre"] ?: 1) >= 2
        "Tenacidade" -> (p.atributos["vig"] ?: 1) >= 2
        "Sentidos Aguçados" -> (p.atributos["pre"] ?: 1) >= 2
        "Sobrevivencialista" -> (p.atributos["int"] ?: 1) >= 2
        "Sorrateiro" -> (p.atributos["agi"] ?: 1) >= 2
        "Vitalidade Reforçada" -> (p.atributos["vig"] ?: 1) >= 2
        "Vontade Inabalável" -> (p.atributos["pre"] ?: 1) >= 2
        "Fluxo de Poder" -> p.nex >= 60
        "Mestre em Elemento" -> p.nex >= 45 && p.habilidades.contains("Especialista em Elemento")
        "Ritual Potente" -> (p.atributos["int"] ?: 1) >= 2
        else -> true
    }
}

fun poderesDisponiveisPara(p: Personagem): List<PoderDisponivel> {
    val origem = PODERES_ORIGEM_REGRAS.filter { it.origem == p.origem }
    val classe = PODERES_CLASSE_REGRAS.filter { it.classe == p.classe && p.nex >= it.nexMin }
    val trilha = PODERES_TRILHA_REGRAS.filter { it.trilha == p.trilha && (it.classe.isBlank() || it.classe.split("/").any { c -> c == p.classe }) && p.nex >= it.nexMin }
    val gerais = PODERES_GERAIS_SOBREVIVENDO.map { item ->
        PoderDisponivel(
            nome = item.first,
            categoria = "Geral",
            classe = p.classe,
            nexMin = if (item.first == "Parceiro") 30 else 15,
            descricao = DESCRICOES_PODERES_GERAIS[item.first].orEmpty(),
            requisito = item.second,
            automatico = false,
            pagina = 34,
            livro = LivroPdf.SOBREVIVENDO,
        )
    }.filter { p.nex >= it.nexMin }

    val extras = PODERES_SOBREVIVENDO.filter { it.classe == p.classe }.map {
        val texto = it.observacao
        val requisito = when {
            it.nome == "Ciente das Cicatrizes" -> "Treinado em Luta ou Pontaria"
            it.nome == "Instinto de Fuga" -> "Treinado em Intuição"
            it.nome == "Mochileiro" -> "VIG 2"
            it.nome == "Contatos Oportunos" -> "Treinado em Crime"
            it.nome == "Disfarce Sutil" -> "PRE 2 e treinado em Enganação"
            it.nome == "Especialista Diletante" -> "NEX 30%"
            it.nome == "Leitura Fria" -> "Treinado em Intuição"
            it.nome == "Mãos Firmes" -> "Treinado em Furtividade"
            it.nome == "Plano de Fuga" -> ""
            it.nome == "Remoer Memórias" -> "Int 1"
            it.nome == "Resistir à Pressão" -> "Treinado em Investigação"
            it.nome == "Domínio Esotérico" -> "INT 3"
            it.nome == "Minha Dor me Impulsiona" -> "VIG 2"
            it.nome == "Olhar Sinistro" -> "PRE 1"
            it.nome == "Sincronia Paranormal" -> "PRE 2"
            else -> ""
        }
        val custo = Regex("(\\d+)\\s*PE").findAll(texto).map { "${it.groupValues[1]} PE" }.distinct().joinToString(" / ")
        PoderDisponivel(
            nome = it.nome,
            categoria = "Classe",
            classe = it.classe,
            nexMin = when (it.nome) { "Especialista Diletante" -> 30 else -> 15 },
            descricao = texto.ifBlank { "Poder de Sobrevivendo ao Horror. Consulte a página ${it.pagina} para a regra completa." },
            requisito = requisito,
            custo = custo,
            pagina = it.pagina,
            livro = it.livro,
        )
    }.filter { p.nex >= it.nexMin }
    return (origem + classe + trilha + gerais + extras)
        .filter { it.automatico || personagemCumpreRequisitos(p, it) }
        .map { poder ->
            poder.copy(
                descricao = DETALHES_PODERES[poder.nome] ?: poder.descricao,
                requisito = poder.requisito.ifBlank { if (poder.nexMin > 5) "NEX ${poder.nexMin}%" else "" },
                custo = poder.custo.ifBlank { CUSTOS_PODERES[poder.nome].orEmpty() },
            )
        }
        .distinctBy { "${it.categoria}|${it.origem}|${it.classe}|${it.trilha}|${it.nome}" }
}

fun quantidadePoderesDeClasse(p: Personagem): Int =
    when (p.nex) {
        in 15..99 -> ((p.nex - 15) / 15) + 1
        else -> 0
    }.coerceAtMost(6)

fun poderesManuaisSelecionados(p: Personagem): List<String> =
    p.habilidades.lines().map { it.trim() }.filter { it.isNotBlank() && it !in p.poderesAutomaticos }

fun poderPertenceAoPersonagem(p: Personagem, nome: String): Boolean =
    poderesDisponiveisPara(p).any { it.nome == nome && !it.automatico }
