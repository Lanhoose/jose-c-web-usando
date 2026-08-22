# V25 — Correção do build após log do Codemagic

Correções aplicadas a partir do erro real do Codemagic:

1. `PoderParanormalSelecionado` agora é `@Serializable`, permitindo serialização dentro de `Personagem`.
2. Corrigido escopo de `automaticos` no fluxo de substituir ritual da criação; agora usa a lista de rituais concedidos automaticamente pela classe/trilha.
3. Corrigida referência corrompida `maxCirculoPermitidoº` para interpolação Kotlin válida.
4. Corrigidas referências inexistentes `RitualDef.efeitoCampo`; `RitualDef` possui `efeito`, que agora é usado ao criar `Ritual`.
5. Removido bloco de detalhes de ritual que havia sido inserido acidentalmente dentro do catálogo de armas em `FichaScreen.kt`.
6. Corrigido botão de adicionar arma que referenciava `ritualProntoParaAdicionar`.
7. Removida declaração duplicada de `custoEfetivo` em `FichaScreen.kt`.
8. Mantidos os detalhes de ritual no bloco correto de rituais da ficha.

Observação: os warnings de AGP/Kotlin sobre `android.builtInKotlin`, `android.newDsl`, `android {}` e `kotlinOptions.jvmTarget` não são a causa da falha apresentada. O erro fatal foi `:app:compileDebugKotlin`.
