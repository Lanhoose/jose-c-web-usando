# V30 — Auditoria e correção de Resistências a Dano

## Correções implementadas

1. **Proteção Pesada = RD geral 2**, não RD física. O Livro de Regras usa explicitamente o exemplo de um combatente com proteção pesada (RD 2) que reduz dano balístico/corte/impacto/perfuração em 2 e também qualquer outro dano em 2.
2. **Tanque de Guerra** adiciona +2 à RD fornecida pela Proteção Pesada, portanto é **RD geral +2** enquanto a proteção pesada estiver equipada.
3. **Intuitivo — Mente Sã (NEX 10%)**: RD paranormal +5.
4. **Intuitivo — Inabalável (NEX 65%)**: RD mental +10 e RD paranormal +10.
5. **Tropa de Choque — Inquebrável (NEX 99%)**: RD geral +5 somente enquanto o personagem estiver machucado; o código considera PV atual <= metade do PV máximo (inclui 0 PV/morrendo).
6. **Mutação** continua como RD geral +2, conforme a descrição catalogada.
7. O cálculo continua somando fontes compatíveis e mantendo RD geral separada das resistências específicas.

## Fontes de personagem verificadas no Livro de Regras

| Fonte | Tipo | Resistência | Valor | Quando | Observação |
|---|---|---:|---:|---|---|
| Proteção Pesada | Equipamento | Geral | 2 | Equipada | Afeta qualquer tipo de dano |
| Tanque de Guerra | Poder de Combatente | Geral | +2 | Proteção Pesada equipada | Aumenta a RD fornecida pela proteção |
| Mutação | Origem | Geral | 2 | Origem escolhida | RD a dano, sem tipo específico |
| Eu Já Sabia | Origem Teórico da Conspiração | Mental | INT | Sempre | Valor igual ao Intelecto |
| Mente Sã | Trilha Intuitivo | Paranormal | 5 | NEX 10%+ | Também dá +5 em testes de resistência contra efeitos paranormais; isso é separado de RD |
| Inabalável | Trilha Intuitivo | Mental | 10 | NEX 65%+ | Também dá efeito especial em testes de Vontade contra dano paranormal |
| Inabalável | Trilha Intuitivo | Paranormal | 10 | NEX 65%+ | Soma com Mente Sã por serem habilidades diferentes |
| Inquebrável | Trilha Tropa de Choque | Geral | 5 | NEX 99% e machucado | Não é permanente; depende da condição |
| Resistir a Elemento | Poder Paranormal | Conhecimento / Energia / Morte / Sangue | 10 | Poder escolhido | O jogador escolhe 1 dos quatro elementos |
| Resistir a Elemento + Afinidade | Poder Paranormal | Elemento escolhido | 20 | Com Afinidade | A resistência do poder aumenta para 20 |
| Proteção Elemental | Maldição para acessório | Elemento | 10 | Acessório equipado | O PDF diz “um elemento”; o elemento deve ser armazenado na escolha do item |
| Escudo Mental | Maldição para acessório | Mental | 10 | Acessório equipado | Resistência mental, não RD geral |
| Profética | Maldição para proteção | Conhecimento | 10 | Proteção equipada | Resistência específica |
| Voltaica | Maldição para proteção | Energia | 10 | Proteção equipada | Resistência específica |
| Repulsiva | Maldição para proteção | Morte | 10 | Proteção equipada | Resistência específica |
| Regenerativa | Maldição para proteção | Sangue | 10 | Proteção equipada | Resistência específica |
| Cinética | Maldição para proteção | Geral | 2 leve/escudo; 5 pesada | Proteção equipada | RD geral |
| Casaco de Lodo | Item paranormal | Corte, Impacto, Morte, Perfuração | 5 | Equipado | Também dá vulnerabilidade a Balístico e Energia |
| Máscara das Pessoas nas Sombras | Item paranormal | Conhecimento | 10 | Equipada | Resistência específica |
| Jaqueta de Veríssimo | Item único | Paranormal | 15 | Equipada | Categoria IV |
| Traje Hazmat | Equipamento | Químico | 10 | Equipado | Também +5 em testes de resistência contra efeitos ambientais |
| Proteção contra Rituais | Ritual | Paranormal | 5 | Efeito ativo | Verdadeira aumenta para 10 |
| Armadura de Sangue — Discente | Ritual | Balístico, Corte, Impacto, Perfuração | 5 | Forma Discente ativa | Requer 3º círculo |
| Armadura de Sangue — Verdadeira | Ritual | Balístico, Corte, Impacto, Perfuração | 10 | Forma Verdadeira ativa | Requer 4º círculo + Afinidade |
| Ódio Incontrolável | Ritual | Balístico, Corte, Impacto, Perfuração | 5 | Forma aplicável ativa | A forma Verdadeira não deve ser convertida em RD se a regra dela for redução diferente |

## O que NÃO deve ser transformado em RD

- +5 em testes de resistência não é RD.
- +5 na Defesa não é RD.
- Reduzir dano à metade não é RD.
- Imunidade não é RD.
- Vulnerabilidade não é RD.
- Relação entre elementos não cria RD.
- Elemento opressor não dobra dano; ele modifica teste de resistência quando aplicável.

## Tipos de dano

O Livro de Regras lista Balístico, Corte, Eletricidade, Fogo, Frio, Impacto, Mental, Paranormal, Perfuração e Químico. Dano paranormal possui subtipo ligado a Conhecimento, Energia, Medo, Morte ou Sangue.

## Testes manuais recomendados

### Teste 1 — Proteção Pesada
- Equipe Proteção Pesada.
- Esperado: RD geral 2.
- Balístico 10 -> 8.
- Fogo 10 -> 8.
- Mental 10 -> 8.
- Morte 10 -> 8.

### Teste 2 — Proteção Pesada + Tanque de Guerra
- Equipe ambos.
- Esperado: RD geral 4.
- Balístico 10 -> 6.
- Fogo 10 -> 6.

### Teste 3 — Resistir a Elemento
- Escolha Energia.
- Sem Afinidade: Energia 10.
- Com Afinidade: Energia 20.
- A resistência não deve virar Fogo 10/20 automaticamente, pois Fogo é um tipo de dano distinto; Energia é subtipo paranormal.

### Teste 4 — Intuitivo
- NEX 10: Paranormal 5.
- NEX 65: Paranormal 15 e Mental 10.

### Teste 5 — Tropa de Choque 99%
- Com PV acima da metade: RD Inquebrável não aparece.
- Com PV igual ou abaixo da metade: RD geral +5 aparece.
- Curando acima da metade: o +5 desaparece.

### Teste 6 — Eu Já Sabia
- Origem Teórico da Conspiração, INT 3.
- Mental +3.
- Outros tipos não recebem esse +3.

### Teste 7 — cumulatividade
- Proteção Pesada +2 geral + Resistir a Morte 10.
- Dano de Morte 20 -> 8.
- Dano balístico 20 -> 18.

## Referências

- Livro de Regras oficial, tipos de dano e RD.
- Livro de Regras oficial, progressão/trilhas.
- Livro de Regras oficial, poderes paranormais e maldições.
- Sobrevivendo ao Horror e Arquivos Secretos foram consultados para conteúdos adicionais; as fichas de ameaças desses suplementos não devem ser tratadas como fontes automáticas de RD para personagens jogadores.
