# Correção do crash na inicialização

O crash vinha de Theme.kt: a tipografia estava multiplicando `lineHeight` quando ele podia ser `TextUnit.Unspecified`. Isso gerava `IllegalArgumentException: Cannot perform operation for Unspecified type.`.

A correção escala apenas `fontSize` e limita a escala de texto a 0.85..1.25. Valores persistidos também são normalizados ao carregar.
