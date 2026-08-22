# Motor de Regras V33 — baseado nas fontes oficiais

Esta versão não usa o HTML como autoridade. A regra é derivada do Livro de Regras e dos suplementos utilizados pelo projeto; o HTML serve apenas como referência de interface anterior.

## Implementado

### NEX / Prestígio / Patente
- `Personagem.prestigio` foi adicionado.
- Patente é calculada exclusivamente por Prestígio:
  - 0 Recruta
  - 20 Operador
  - 50 Agente Especial
  - 100 Oficial de Operações
  - 200 Agente de Elite
- NEX não altera mais a patente.
- Limites de categoria de equipamento passam a usar a patente derivada do Prestígio.

### Carga
- Força 0 = 2 espaços.
- Força 1+ = Força x 5 espaços.
- Carga máxima = dobro da capacidade.
- Sobrecarga aplica:
  - -5 Defesa;
  - -5 em perícias afetadas pela carga;
  - -3m deslocamento.
- Técnico / Inventário Otimizado usa Força + Intelecto para capacidade.
- Muambeiro / Mascate acrescenta o bônus de carga catalogado.
- Proteção pesada sem proficiência aplica -2 dados em testes baseados em Força/Agilidade.

### Condições
- Condições agora possuem efeitos mecânicos separados da descrição visual.
- O motor diferencia penalidades numéricas de penalidades em dados d20.
- Defesa, ataques, perícias, deslocamento, estado desprevenido, imobilidade e bloqueio de ações são calculados quando aplicável.

### Ferimentos / morte
- PV nunca ficam abaixo de 0 na ficha.
- Ao chegar a 0 PV, a ficha recebe `Morrendo` + `Inconsciente`.
- Três inícios de turno morrendo na mesma cena causam morte.
- Medicina DT 20 encerra Morrendo.
- Dano Massivo é identificado quando o dano de uma única fonte é pelo menos metade dos PV máximos; a ficha calcula a DT `15 + 2 por cada 10 pontos de dano`.

### Sanidade
- Estados persistentes:
  - perturbado;
  - enlouquecendo;
  - turnos enlouquecendo;
  - insano.
- Histórico de efeitos de insanidade.
- Avanço de turno de Enlouquecendo.
- Ao atingir 3 turnos, a ficha recebe o estado Insano.

### Progressão
- Aumentos de atributo em NEX 20/50/80/95 ficam registrados por marco.
- Graus de treinamento de NEX 35/70 ficam registrados por marco e elevam o grau da perícia.
- Versatilidade fica registrada como escolha própria.
- Transcender continua separado das escolhas comuns.

### Poderes e trilhas
Foi criado `EfeitoRegra` e um ponto central de avaliação de efeitos. Nesta versão já estão mecanizados os efeitos estruturais auditados que afetam cálculos da ficha, incluindo:
- Graduado — Rituais Eficientes: bônus de DT de rituais;
- Intuitivo — Presença Poderosa: limite de PE específico para conjuração;
- Técnico — Inventário Otimizado: carga baseada em Força + Intelecto;
- Muambeiro — Mascate: bônus de carga;
- Monstruoso: atributo paranormal separado da Conexão de NEX 50%, usado nos cálculos de PE/DT conforme o elemento escolhido pela trilha.

### Equipamentos
- Item possui estado vestido, empunhado e proficiente.
- Arma possui estado empunhada e proficiente.
- A ficha limita dois itens empunhados.
- Grimório empunhado também ocupa uma das mãos.
- Limites de categoria são verificados na aquisição e na troca de categoria.
- Proteção Pesada usa RD físico 2 (balístico/corte/impacto/perfuração), não RD geral.

### Grimório
- Slots separados dos rituais conhecidos.
- Quantidade inicial baseada em Intelecto.
- Novo círculo acrescenta slots próprios.
- Ritual armazenado não é automaticamente um ritual conhecido normal.
- Estado empunhado e ritual relembrado foram adicionados.
- A interface informa a necessidade de folhear como ação completa.

### Afinidade
- Conexão e Afinidade são estados separados.
- A interface não permite mais desenvolver Afinidade manualmente.
- Afinidade é desenvolvida pelo recebimento do próximo poder paranormal após a conexão.

### Sobrevivente
- NEX 0 enquanto permanecer Sobrevivente.
- Estágio 1–5 persistente.
- Progressão por Estágios em vez de progressão normal de NEX.
- Valores de PV/PE/SAN seguem a progressão própria de Sobrevivente.
- Durão, Esperto e Esotérico são escolhidos a partir do Estágio 2.
- Aumento de atributo é tratado no Estágio 3.
- Segundo poder de trilha no Estágio 4.
- Cicatrizado no Estágio 5.
- Esotérico / Iniciado concede um ritual de 1º círculo com origem `SOBREVIVENTE_INICIADO`.
- O ritual de Iniciado fica separado dos rituais normais de Ocultista e é preservado no Treinamento Especial.
- Treinamento Especial pode converter a classe para Combatente, Especialista ou Ocultista no próximo avanço, mantendo a origem do ritual e os bônus de transição de PV/PE/SAN catalogados.

## Compatibilidade
Todos os novos campos possuem valores padrão para que fichas antigas continuem desserializáveis. A rotina de correção de legado também normaliza a RD antiga da Proteção Pesada de `Geral` para `Fisico`.

## Validação
- O Gradle Wrapper não pôde baixar Gradle 9.3.1 neste ambiente porque `services.gradle.org` está inacessível por DNS.
- Foi feita verificação estática dos arquivos Kotlin modificados e não foram encontrados erros de sintaxe nos novos blocos; os erros retornados pelo compilador standalone são principalmente dependências Android/Kotlinx ausentes fora do Gradle.
- A compilação final deve ser executada no Codemagic.
