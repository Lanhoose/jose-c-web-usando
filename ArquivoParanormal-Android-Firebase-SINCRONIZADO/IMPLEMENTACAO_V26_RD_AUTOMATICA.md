# V26 — Resistências a dano 100% derivadas das fontes

## Objetivo
A ficha não deve exigir que o jogador digite valores de RD. Os valores são derivados das fontes reais da ficha.

## Alterações
- RD geral calculada a partir de equipamentos equipados, habilidades e efeitos/rituais ativos que concedem RD geral.
- RD específica calculada por tipo (Balístico, Corte, Impacto, Perfuração, etc.).
- Equipamentos continuam fornecendo seus valores a partir do catálogo.
- Proteção Pesada fornece RD geral 2 automaticamente quando equipada.
- Tanque de Guerra acrescenta RD geral 2 quando Proteção Pesada está equipada.
- Mutação acrescenta RD geral 2.
- Eu Já Sabia usa Intelecto como RD Mental.
- Inabalável fornece RD Mental 10 e RD Paranormal 10.
- Resistir a Elemento fornece RD 10 ou 20 conforme Afinidade.
- Rituais ativos com resistências continuam sendo considerados.
- A ficha mostra a origem de cada resistência em overlay.
- O campo manual de RD foi removido da interface da ficha.
- Na edição de proteções, Defesa/RD são somente leitura; os valores vêm do catálogo.
- O calculador de dano usa automaticamente as fontes e soma RD geral + RD específica aplicável.
- RD não é confundida com Bloqueio, redução percentual ou imunidade.

## Compatibilidade
O campo legado `Personagem.resistencias` continua no modelo para compatibilidade com fichas antigas, mas não é mais usado como entrada manual no cálculo de RD.

## Validação
O projeto foi compactado e inspecionado estruturalmente. O build local não pôde ser executado porque o ambiente atual não consegue resolver `services.gradle.org`; o Codemagic deve ser usado para a validação Gradle final.
