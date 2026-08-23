# V36.6 — Detalhes de Trilhas + Aprender Ritual

## Alterações

### 1. Etapa de Trilha
- Todas as habilidades exibidas na etapa de Trilha agora possuem **Ver detalhes**.
- O resumo continua na lista para não deixar a etapa excessivamente longa.
- Ao tocar em **Ver detalhes**, abre uma janela com:
  - NEX da habilidade;
  - explicação de como a habilidade funciona;
  - requisitos, quando catalogados;
  - livro de origem.
- As descrições das 15 trilhas principais do Livro de Regras foram detalhadas com seus custos, ações, limites e efeitos relevantes.
- A habilidade **Resgate** do Médico de Campo foi completada com o bônus de Defesa e a regra de carga do personagem carregado.

### 2. Aprender Ritual
A overlay agora apresenta explicitamente a regra antes da escolha:
- qualquer personagem pode aprender rituais por **Aprender Ritual**;
- o limite de rituais aprendidos dessa forma é igual ao **Intelecto**;
- o poder pode ser escolhido várias vezes, respeitando esse limite;
- rituais recebidos por habilidades de classe não consomem esse limite;
- antes de NEX 45%, o poder permite ritual de até 1º círculo;
- a partir de NEX 45%, até 2º círculo;
- a partir de NEX 75%, até 3º círculo;
- é possível substituir um ritual conhecido, mantendo a quantidade total;
- o ritual escolhido conta como poder do elemento correspondente para requisitos de poderes paranormais.

### 3. Fluxo
A lógica existente de seleção foi mantida:
- **Aprender novo ritual** adiciona o ritual e vincula sua origem a `APRENDER_RITUAL`;
- **Substituir ritual conhecido** troca o ritual sem criar um ritual extra;
- rituais concedidos automaticamente por classe/trilha continuam separados da substituição manual.

## Fonte da regra
A implementação foi conferida contra o Livro de Regras Oficial fornecido no projeto. O livro informa que Aprender Ritual é uma forma de qualquer personagem aprender rituais, limitada pelo Intelecto, enquanto rituais concedidos pelas habilidades de classe do Ocultista não entram nesse limite.

## Validação da build
Foi tentada uma compilação local de `:app:compileDebugKotlin`, mas o Gradle Wrapper tentou baixar o Gradle 9.3.1 e o ambiente não possuía acesso DNS/rede (`UnknownHostException: services.gradle.org`). Portanto, esta versão deve ser validada no Codemagic.
