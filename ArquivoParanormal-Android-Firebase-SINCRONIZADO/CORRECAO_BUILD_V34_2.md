# Correção de Build V34.2

Correções baseadas no log do Codemagic:

1. `RitualDef` agora possui `fonte`, permitindo identificar Livro de Regras, Sobrevivendo ao Horror e Arquivos Secretos.
2. `Ritual` agora persiste `fonte` separadamente de `origem` (origem = como foi adquirido/cadastrado; fonte = de qual livro/suplemento veio).
3. Corrigidos os `copy(fonte = ...)` de `RITUAIS_COMPLETOS`.
4. Criador de personagem passa `fonte` do catálogo para o ritual adquirido.
5. Ficha passa `fonte` do catálogo para o ritual cadastrado/adquirido.
6. Corrigida a expressão de `rituaisBloqueados`: o `distinctBy` agora é aplicado ao resultado completo do `if`, evitando a quebra de inferência de tipo em Kotlin.

Nenhuma funcionalidade de regra foi removida. A correção apenas completa o modelo de origem/fonte e corrige a expressão que o compilador não conseguia inferir.

Observação: a compilação Gradle local não pôde ser concluída neste ambiente porque o Gradle Wrapper precisa baixar Gradle 9.3.1 de services.gradle.org e a rede/DNS deste ambiente está indisponível. O código foi verificado estruturalmente contra os erros do log fornecido.
