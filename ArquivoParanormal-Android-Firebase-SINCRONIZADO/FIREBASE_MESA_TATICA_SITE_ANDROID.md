# Mesa tática — Site ↔ Android

A mesa tática agora usa o mesmo documento do Firestore:

`batalhas/principal`

O site grava:
- `tacticalPayload`: grade, tokens, posições, névoa, ambiente e rodada.
- `combatentesJson`: iniciativa/PV dos combatentes.

O Android:
- escuta `tacticalPayload` em tempo real;
- converte os tokens do site para `TokenMesa`;
- aceita `imagem` como data URL (`data:image/...;base64,...`);
- mostra a imagem na grade e no painel de edição do token;
- usa as dimensões de grade recebidas do site;
- publica de volta alterações da mesa quando o usuário é Mestre.

Jogadores ficam somente em leitura na mesa tática do Android.

## Teste

1. Faça login como Mestre no site.
2. Abra a Mesa de Batalha.
3. Crie um token ou importe uma imagem.
4. Posicione o token na grade.
5. Abra o Android com a mesma conta/mesa.
6. O token e a imagem devem aparecer automaticamente.
7. Mova o token no site e aguarde a atualização no Android.

## Build

Abra o projeto no Android Studio e execute:
- Sync Project with Gradle Files
- Build > Make Project
- Run

As regras do Firestore já permitem leitura autenticada de `batalhas` e escrita somente para Mestre.

## Mapas completos — atualização

A Mesa Tática agora lê o campo `mapa` do `batalhas/principal.tacticalPayload` quando o site publica a imagem como `data:image/...;base64,...`.

- O Android salva os bytes recebidos localmente sem redimensionar o mapa remoto.
- `mapaAjuste: "conter"` mostra a imagem inteira; `"cobrir"` preenche a grade e pode cortar bordas, igual ao site.
- O Mestre pode alternar entre **Mostrar inteiro** e **Cobrir grade**.
- O payload enviado pelo Android também inclui `mapa` e `mapaAjuste`, mantendo a sincronização nos dois sentidos.
