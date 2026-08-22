# V34.1 — Correções de UI e motor de rituais/RD/poderes

## Rituais
- O seletor do criador para Aprender Ritual agora usa uma área rolável e mostra, antes da escolha:
  - nome;
  - círculo;
  - elemento;
  - fonte do ritual (livro/suplemento);
  - execução;
  - alcance;
  - resistência;
  - resumo da descrição;
  - indicação de formas Discente/Verdadeira.
- A substituição só lista rituais adquiridos por Aprender Ritual, preservando a separação dos rituais concedidos por classe/trilha.
- Ritual passou a guardar `origem` da aquisição separadamente da `fonte` do catálogo.
- Ocultista e Aprender Ritual foram separados na ficha:
  - Escolhido pelo Outro Lado = slots próprios de classe, sem consumir Intelecto;
  - Aprender Ritual = slots globais limitados pelo Intelecto.
- Aprender Ritual agora respeita o limite global de Intelecto.
- Rituais de classe do Ocultista usam a progressão própria de círculos e quantidade de rituais.

## RD
- Proteção Pesada fornece RD 2 somente contra balístico, corte, impacto e perfuração.
- Não fornece RD geral nem RD contra Conhecimento, Energia, Morte, Sangue, Medo etc.
- Tanque de Guerra aumenta a RD física da Proteção Pesada em +2, não RD geral.
- Fichas antigas que armazenaram Proteção Pesada como `Geral: 2` são interpretadas como RD física, evitando contaminar as resistências paranormais.
- A seleção de Proteção Pesada agora grava `Fisico: 2`.

## Carga
- FOR 0 = 2 espaços.
- FOR 1+ = FOR x 5.
- Técnico com Inventário Otimizado usa FOR + INT para a capacidade de carga a partir do NEX da trilha.

## Poderes na ficha
- O seletor de poderes não mostra novamente poderes já registrados nas etapas de criação/progressão.
- Poderes de classe respeitam a quantidade de slots do NEX.
- Poderes automáticos e Transcender continuam separados.
- O catálogo de poderes usado para identificar escolhas evita tratar texto livre das anotações como se fosse automaticamente um poder adquirido.
