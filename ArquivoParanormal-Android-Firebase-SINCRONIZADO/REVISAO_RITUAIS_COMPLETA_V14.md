# Revisão completa do sistema de Rituais — V14

## Fonte principal

O catálogo base foi reconstruído a partir das páginas de descrição dos rituais do **Livro de Regras oficial**, incluindo a lista e as páginas de descrição dos rituais.

Foram estruturados **82 rituais do Livro de Regras**.

## Campos estruturados

Cada RitualDef do catálogo base agora possui:

- nome
- círculo
- elemento
- execução
- alcance
- alvo
- área
- efeito
- duração
- resistência
- descrição completa
- custo base em PE
- descrição da forma Discente
- custo adicional da forma Discente
- requisito da forma Discente
- descrição da forma Verdadeira
- custo adicional da forma Verdadeira
- requisito da forma Verdadeira

## Interface

Na ficha do agente:

- a seleção mostra o ritual e seus dados principais;
- Amaldiçoar Arma exige a escolha do elemento antes de adicionar;
- existe **Ver detalhes** para abrir a descrição completa;
- as formas Discente/Verdadeira mostram custo e requisito;
- depois de adicionar, o ritual conserva execução, alcance, alvo, área, efeito, duração, resistência e as descrições das formas avançadas;
- o custo efetivo é recalculado a partir do círculo base ao trocar de forma, evitando acumular o custo duas vezes;
- o custo do Paranormal é calculado sobre o custo efetivo da forma;
- rituais ativos continuam alimentando as resistências automáticas quando aplicável.

## Regras importantes conferidas

- Custo base: 1º círculo = 1 PE; 2º = 3 PE; 3º = 6 PE; 4º = 10 PE.
- Rituais de elementos diferentes de Medo exigem teste de Ocultismo contra DT 20 + custo em PE.
- Rituais de Medo possuem tratamento próprio de perda de Sanidade e não usam afinidade com Medo.
- Amaldiçoar Arma permite escolher entre Conhecimento, Energia, Morte e Sangue ao aprender o ritual.
- Armadura de Sangue: Discente fornece RD 5 nos quatro danos físicos indicados; Verdadeira fornece RD 10.
- Ódio Incontrolável: normal e Discente fornecem RD 5 nos quatro danos físicos; a Verdadeira substitui a RD por redução à metade do dano desses tipos.

## Validação

- OpData.kt foi compilado isoladamente com kotlinc e passou.
- O build Android completo não pôde ser executado neste ambiente porque o Gradle Wrapper precisa acessar services.gradle.org para obter o Gradle 9.3.1.
- A integridade do ZIP final foi verificada após compactação.
