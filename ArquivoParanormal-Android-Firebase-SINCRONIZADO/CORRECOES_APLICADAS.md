# Correções aplicadas (revisão de bugs)

## 1. [Crítico] Mestre sobrescrevia o dono (ownerUid) da ficha do jogador
**Arquivos:** `Models.kt`, `Repositorio.kt`

`salvarNuvem()` gravava sempre `ownerUid = uid da sessão atual`. Como o Mestre
enxerga todas as fichas e pode editá-las (ex.: ajustar PV em combate), ao
salvar a ficha de um jogador o campo `ownerUid` no Firestore era sobrescrito
com o uid do Mestre. Isso fazia a ficha sumir da consulta do próprio
jogador (`whereEqualTo("ownerUid", ...)`) e, pela regra do Firestore,
bloqueava a edição/remoção dela pelo dono de verdade.

**Correção:** `Personagem` agora guarda `ownerUid`/`ownerEmail`, preenchidos
uma única vez na criação (`Repositorio.salvar`) e preservados em todo
salvamento seguinte — inclusive quando quem salva é o Mestre. O listener de
sincronização também faz uma migração suave: se uma ficha antiga (payload
sem `ownerUid`) chegar do Firestore, o dono é herdado do campo de nível
superior do próprio documento antes de ser reenviado.

> Fichas que já tiverem sido corrompidas por este bug antes da correção
> (campo `ownerUid` do documento já apontando para o Mestre) precisam ser
> ajustadas manualmente uma vez no console do Firestore — o código agora
> impede que o problema volte a acontecer.

## 2. [Médio] Mapas sincronizados do site voltavam com Content-Type errado
**Arquivo:** `MapaImportador.kt`

`salvarDataUrl()` salvava o mapa recebido do site sempre com extensão
genérica `.img`. Como `Repositorio.gravarMesa()` decide o mime da data URL
pela extensão do arquivo, um mapa JPEG podia ser republicado como
`image/png`, quebrando a exibição no site depois de um "vai e volta"
(ex.: Mestre move um token e a mesa é regravada).

**Correção:** a extensão agora é derivada do cabeçalho real da data URL
(`image/jpeg` → `.jpg`, `image/webp` → `.webp`, etc.).

## 3. [Menor] Sincronização duplicada ao logar
**Arquivo:** `MainActivity.kt`

`repo.iniciarSincronizacao()` era chamado duas vezes seguidas no login (uma
vez no callback do login, outra pelo `LaunchedEffect(logado)`), recriando
todos os listeners do Firestore duas vezes. Removida a chamada duplicada.

## 4. [Menor] Escritas redundantes ao "semear" o bestiário
**Arquivo:** `Repositorio.kt`

`iniciarSincronizacaoMonstros()` podia reenviar todos os monstros locais a
cada snapshot vazio do Firestore, não só uma vez. Adicionada uma trava para
que isso ocorra no máximo uma vez por sessão de sincronização.

## 5. [Cosmético] Variável de estado morta
**Arquivo:** `MainActivity.kt`

`papel` era atribuída mas nunca lida (o papel real vem de `repo.ehMestre`).
Removida.
