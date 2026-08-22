# V33.4 — Correção de compilação

Correções aplicadas a partir do compilar_apk_debug.log (11):

1. MaldicaoCatalogoDef: removidos os argumentos nomeados `elemento=...` duplicados. O elemento já é o terceiro parâmetro posicional do construtor.
2. FichaScreen: adicionados imports de `Alignment` e `Spacer`.
3. FichaScreen: adicionados imports de `ITENS_AMALDICOADOS_LIVRO` e `MALDICOES_CATALOGO_COMPLETO`.
4. Mantida a função `patentePermiteItemAmaldicoado` e seu import.
5. Mantida `circuloMaximoOcultista` e o sistema de rituais/patente.

Os erros `it`, `nome`, `categoria`, `descricao`, `elemento`, etc. no bloco de FichaScreen eram erros em cascata causados pela coleção não resolvida; não foram removidas funcionalidades para contorná-los.
