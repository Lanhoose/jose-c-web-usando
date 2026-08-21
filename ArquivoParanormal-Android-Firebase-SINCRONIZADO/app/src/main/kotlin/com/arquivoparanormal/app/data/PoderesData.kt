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

private fun trilha(nome: String, classe: String, pagina: Int, poderes: List<Triple<Int, String, String>>) =
    poderes.map { PoderDisponivel(it.second, "Trilha", classe = classe, trilha = nome, nexMin = it.first, descricao = it.third, automatico = true, pagina = pagina) }

val PODERES_CLASSE_REGRAS: List<PoderDisponivel> =
    classe("Combatente", 25,
        "Artista Marcial" to "Seus ataques desarmados ficam mais eficientes e o dano aumenta com o NEX.",
        "Ataque de Oportunidade" to "Permite reagir quando um inimigo sai voluntariamente do seu alcance corpo a corpo.",
        "Combater com Duas Armas" to "Permite realizar ataques com duas armas, seguindo os requisitos do poder.",
        "Combate Defensivo" to "Ao atacar de forma defensiva, reduz seus ataques e aumenta sua Defesa até o próximo turno.",
        "Golpe Demolidor" to "Melhora ataques contra objetos e a manobra quebrar.",
        "Golpe Pesado" to "Aumenta o dano de uma arma corpo a corpo em um dado.",
        "Incansável" to "Permite uma ação adicional de investigação uma vez por cena, usando Força ou Agilidade.",
        "Presteza Atlética" to "Permite usar Força ou Agilidade em certas ações de investigação e ajudar um aliado.",
        "Proteção Pesada" to "Concede proficiência com proteções pesadas.",
        "Reflexos Defensivos" to "Aumenta Defesa e testes de resistência.",
        "Saque Rápido" to "Permite sacar e guardar itens como ação livre e facilita recarga em regras de munição.",
        "Segurar o Gatilho" to "Permite ataques adicionais com arma de fogo, pagando PE progressivamente.",
        "Sentido Tático" to "Analisa o ambiente e recebe bônus defensivos e de resistência até o fim da cena.",
        "Tanque de Guerra" to "Melhora Defesa e resistência a dano de proteções pesadas.",
        "Tiro Certeiro" to "Soma Agilidade ao dano de armas de disparo e remove uma penalidade contra alvos engajados.",
        "Tiro de Cobertura" to "Usa fogo de cobertura para dificultar a movimentação e os ataques do alvo.",
        "Transcender" to "Escolha um poder paranormal elegível. Não recebe o aumento normal de Sanidade desse NEX.",
        "Treinamento em Perícia" to "Treina duas perícias; em NEX maiores pode elevar o grau de treinamento."
    ) + classe("Especialista", 29,
        "Artista Marcial" to "Melhora ataques desarmados e o dano deles conforme o NEX.",
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
    )) +
    trilha("Caçador", "Especialista", 18, listOf(
        Triple(10, "Rastrear o Paranormal", "Treina Sobrevivência ou a aprimora e usa a perícia para rastrear sinais paranormais."),
        Triple(40, "Estudar Fraquezas", "Estuda uma criatura ou alvo específico para obter informações e bônus contra ele."),
        Triple(65, "Atacar das Sombras", "Melhora Furtividade e reduz penalidades ao atacar de forma discreta."),
        Triple(99, "Estudar a Presa", "Transforma um tipo de criatura/cultista em presa e recebe grandes bônus contra esse tipo."),
    )) +
    trilha("Erudito", "Especialista", 24, listOf(
        Triple(10, "Bibliotecário", "Usa conhecimento e pesquisa para obter mais informação e adaptar testes de perícia."),
        Triple(40, "Acostumado com Bibliotecas", "Extrai informação com maior eficiência durante investigações e pesquisas."),
        Triple(65, "Força do Saber", "Aumenta Intelecto e melhora o uso desse atributo em perícias."),
        Triple(99, "Conhecimento Desesperador", "Usa conhecimento extremo para obter respostas quando a situação exige."),
    )) +
    trilha("Perseverante", "Especialista", 25, listOf(
        Triple(10, "Soluções Improvisadas", "Encontra soluções inesperadas para continuar avançando quando tudo dá errado."),
        Triple(40, "Último Sobrevivente", "Aumenta sua capacidade de resistir e agir em situações críticas."),
        Triple(65, "Não Desista", "Permite continuar atuando quando outros personagens já não conseguiriam."),
        Triple(99, "Determinação Inabalável", "Leva sua resistência ao limite em situações desesperadoras."),
    )) +
    trilha("Monstruoso — Combatente", "Combatente", 80, listOf(
        Triple(10, "Ser Amaldiçoado", "Uma mutação paranormal altera seu corpo e concede benefícios conforme o elemento escolhido."),
        Triple(40, "Ser Experimentado", "Seu corpo se adapta ainda mais à transformação e ao elemento escolhido."),
        Triple(65, "Ser Expulso", "A transformação avança e concede capacidades monstruosas mais fortes."),
        Triple(99, "Ser Apavorante", "A transformação alcança um estágio extremo entre humano e criatura."),
    )) +
    trilha("Monstruoso — Especialista", "Especialista", 81, listOf(
        Triple(10, "Ser Experimentado", "O corpo começa a ser alterado para produzir vantagens paranormais."),
        Triple(40, "Ser Testado", "As experimentações aumentam suas capacidades conforme o elemento escolhido."),
        Triple(65, "Ser Expulso", "A transformação corporal se aprofunda e libera novas capacidades."),
        Triple(99, "Ser Apavorante", "A transformação alcança seu estágio mais extremo."),
    )) +
    trilha("Monstruoso — Ocultista", "Ocultista", 84, listOf(
        Triple(10, "Ser Escarificado", "Escarificações transformam o corpo em uma porta para o paranormal."),
        Triple(40, "Ser Perfurado", "Os símbolos ritualísticos ocupam cada vez mais espaço no corpo."),
        Triple(65, "Ser Rasgado", "A transformação ritualística se aprofunda e aumenta o acesso ao paranormal."),
        Triple(99, "Ser Mutilado", "As escarificações chegam ao limite e ampliam drasticamente a conexão paranormal."),
    ))

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
            descricao = "Habilidade concedida automaticamente por esta origem. Consulte o PDF para requisitos e efeitos completos.",
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
    "Impulso de Dor" to "1 PE.",
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
    "Impulso de Dor" to "Ao fazer Acrobacia, Atletismo ou Furtividade, pode gastar 1 PE para receber +1d6, desde que tenha pelo menos 5 pontos de dano em PV. Requisito: VIG 2.",
    "Nos Olhos do Monstro" to "Em uma cena com criatura paranormal, pode gastar uma rodada e 3 PE para encarar a criatura e receber +5 em testes contra ela, exceto ataques, até o fim da cena.",
    "Olhar Sinistro" to "Pode usar Presença no lugar de Intelecto para Ocultismo e usar Ocultismo para coagir. Requisito: PRE 1.",
    "Sentido Premonitório" to "Pode gastar 3 PE para ativar um sentido que antecipa uma rodada de eventos em investigação, furtividade e perseguição; não funciona em combate e exige 1 PE por rodada para manter o efeito."
)

private fun personagemCumpreRequisitos(p: Personagem, poder: PoderDisponivel): Boolean {
    val nome = poder.nome
    val treinado = { pericia: String -> (p.pericias[pericia]?.treino ?: 0) > 0 }
    return when (nome) {
        "Ciente das Cicatrizes" -> treinado("Luta") || treinado("Pontaria")
        "Instinto de Fuga" -> treinado("Intuição")
        "Mochileiro" -> (p.atributos["vig"] ?: 1) >= 2
        "Contatos Oportunos" -> treinado("Crime")
        "Disfarce Sutil" -> (p.atributos["pre"] ?: 1) >= 2 && treinado("Enganação")
        "Especialista Diletante" -> p.nex >= 30
        "Leitura Fria" -> treinado("Intuição")
        "Mãos Firmes" -> treinado("Furtividade")
        "Domínio Esotérico" -> (p.atributos["int"] ?: 1) >= 3
        "Impulso de Dor" -> (p.atributos["vig"] ?: 1) >= 2
        "Olhar Sinistro" -> (p.atributos["pre"] ?: 1) >= 1
        else -> true
    }
}

fun poderesDisponiveisPara(p: Personagem): List<PoderDisponivel> {
    val origem = PODERES_ORIGEM_REGRAS.filter { it.origem == p.origem }
    val classe = PODERES_CLASSE_REGRAS.filter { it.classe == p.classe && p.nex >= it.nexMin }
    val trilha = PODERES_TRILHA_REGRAS.filter { it.trilha == p.trilha && (it.classe.isBlank() || it.classe.split("/").any { c -> c == p.classe }) && p.nex >= it.nexMin }
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
            it.nome == "Domínio Esotérico" -> "INT 3"
            it.nome == "Impulso de Dor" -> "VIG 2"
            it.nome == "Olhar Sinistro" -> "PRE 1"
            else -> ""
        }
        val custo = Regex("(\\d+)\\s*PE").findAll(texto).map { "${it.groupValues[1]} PE" }.distinct().joinToString(" / ")
        PoderDisponivel(
            nome = it.nome,
            categoria = "Classe",
            classe = it.classe,
            nexMin = 15,
            descricao = texto.ifBlank { "Poder de Sobrevivendo ao Horror. Consulte a página ${it.pagina} para a regra completa." },
            requisito = requisito,
            custo = custo,
            pagina = it.pagina,
            livro = it.livro,
        )
    }.filter { p.nex >= it.nexMin }
    return (origem + classe + trilha + extras)
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
