# V20 — Aprender Ritual: novo ritual ou substituição

Implementado na etapa de Poderes Paranormais da criação de personagem.

## Fluxos

### Aprender novo ritual
- Seleciona `Aprender Ritual`.
- Escolhe `Aprender novo ritual`.
- O catálogo mostra somente rituais até o círculo permitido pelo NEX.
- O novo ritual é adicionado à ficha e vinculado ao poder `Aprender Ritual`.

### Substituir ritual conhecido
- Seleciona `Aprender Ritual`.
- Escolhe `Substituir ritual conhecido`.
- Seleciona o ritual antigo.
- Seleciona o novo ritual.
- O ritual antigo é substituído pelo novo na mesma posição da lista.
- O poder `Aprender Ritual` passa a apontar para o novo ritual.
- Nenhum ritual adicional é criado: a quantidade total é mantida.

## Proteção contra duplicação

Rituais concedidos automaticamente por classe/trilha não aparecem como candidatos à substituição nessa etapa. Eles continuam vinculados à habilidade que os concede e são recriados automaticamente pelo sistema caso necessário.

## Vários Aprender Ritual

Cada escolha de `Aprender Ritual` possui seu próprio vínculo com o ritual aprendido. A ficha mostra `Trocar ritual`, e quando há várias escolhas mostra `Trocar ritual 1`, `Trocar ritual 2`, etc.

## Validação

O build Android completo não foi executado neste ambiente porque o Gradle 9.3.1 não pôde ser baixado de services.gradle.org. A alteração foi revisada estruturalmente e o ZIP final foi validado.
