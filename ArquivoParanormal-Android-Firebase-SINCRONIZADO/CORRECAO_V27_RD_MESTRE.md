# V27 — RD física correta e edição do Mestre

- Proteção Pesada agora é cadastrada como `Fisico`, não `Geral`.
- RD `Geral` é universal apenas quando a fonte realmente declara `Geral`.
- `Fisico` aplica somente a Balístico, Corte, Impacto e Perfuração.
- Tanque de Guerra acrescenta RD física à Proteção Pesada, não RD universal.
- RD específica continua limitada ao respectivo tipo.
- O jogador vê RD automática e não pode editar os valores.
- O Mestre pode sobrescrever RD geral, RD por tipo, Bloqueio e DTs usando `overrides`, com opção de voltar ao automático.
- A origem das resistências mostra as fontes e, quando houver, o ajuste manual do Mestre.
- O calculador de dano usa o valor automático ou o override do Mestre.

## Exemplo

Proteção Pesada equipada:
- Balístico +2
- Corte +2
- Impacto +2
- Perfuração +2
- Fogo +0
- Frio +0
- Mental +0
- Paranormal +0

O valor não aparece mais como uma RD universal de +2 para todos os tipos.
