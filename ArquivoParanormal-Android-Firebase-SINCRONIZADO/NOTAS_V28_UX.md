# V28 — Ajustes de UX: Perícias e descrições minimizadas

## Corrigido
- Etapa de Perícias: o botão `Trocar` agora fica na mesma linha da perícia selecionada, em vez de criar uma nova linha abaixo e empurrar o restante da lista.
- Perícias automáticas da origem continuam cinzas e bloqueadas.
- Perícias manuais continuam com `Trocar`.
- Descrições longas das armas na ficha agora ficam minimizadas e abrem por `Ver detalhes da arma`.
- Descrições longas dos itens do inventário agora ficam minimizadas e abrem por `Ver detalhes do item`.
- A descrição completa continua disponível para edição dentro da overlay de detalhes do item.
- Descrições de poderes e rituais já possuíam mecanismo de detalhes e foram mantidas.

## Verificação
- O projeto foi descompactado e os arquivos Kotlin alterados foram inspecionados.
- O build local foi tentado, mas este ambiente não conseguiu baixar Gradle 9.3.1 por `UnknownHostException: services.gradle.org`. Portanto, o build final deve ser validado no Codemagic.
