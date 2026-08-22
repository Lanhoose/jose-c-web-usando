# V23 — Cadeados de opções + Progressão de NEX

## 1. Opções bloqueadas na ficha

A ficha não remove silenciosamente opções incompatíveis.

### Rituais
- Rituais permitidos continuam no seletor normal.
- Rituais incompatíveis ficam em `🔒 Ver rituais bloqueados`.
- Cada ritual bloqueado pode abrir uma overlay com o motivo.
- Motivos considerados:
  - personagem não possui uma fonte que permita aprender rituais;
  - NEX insuficiente para o círculo;
  - 4º círculo do Ocultista ainda não alcançado;
  - ritual já conhecido.
- A lista é deduplicada por nome/elemento/círculo, com tratamento especial para Amaldiçoar Arma.

### Poderes
- Poderes elegíveis continuam no seletor normal.
- Poderes incompatíveis ficam em `🔒 Ver poderes bloqueados`.
- Cada poder pode abrir uma overlay com:
  - classe/trilha/origem de destino;
  - NEX mínimo;
  - requisito não cumprido;
  - descrição catalogada.
- Transcender continua fora do seletor genérico porque exige o fluxo específico de escolha de poder paranormal.

## 2. Progressão de NEX

Foi adicionada uma seção `Progressão de NEX` na ficha.

Ela mostra:
- desbloqueios do NEX atual;
- próximo marco;
- progressão completa de 5% a 99%;
- cadeado nos marcos futuros;
- rituais e círculos do Ocultista;
- poderes de classe;
- poderes de trilha;
- aumento de atributo;
- grau de treinamento;
- versatilidade;
- melhorias próprias de Combatente e Especialista.

### Referência normativa
A progressão normal usa os marcos 5%, 10%, 15%, 20%, 25%, 30%, 35%, 40%, 45%, 50%, 55%, 60%, 65%, 70%, 75%, 80%, 85%, 90%, 95% e 99%.

Combatente, Especialista e Ocultista têm tabelas próprias, e o app agora apresenta esses marcos de forma visual.

Para Ocultista também é mostrado:
- novo ritual ao avançar de NEX;
- 2º círculo em NEX 25%;
- 3º círculo em NEX 55%;
- 4º círculo em NEX 85%.

## 3. Observação

A compilação completa não pôde ser executada neste ambiente porque o Gradle 9.3.1 precisa ser baixado de `services.gradle.org`, mas o ambiente de execução não possui acesso DNS àquele host. A checagem estática das alterações foi feita e os arquivos modificados estão com chaves/parênteses balanceados.
