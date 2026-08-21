# Catálogo ampliado — Arquivo Paranormal

Fontes conferidas:
- Livro de Regras Oficial (332 páginas físicas no PDF enviado)
- Sobrevivendo ao Horror (226 páginas)
- Arquivos Secretos #7 (93 páginas)

Alterações:
- `ORIGENS_COMPLETAS`: reúne as origens básicas + 20 origens de Sobrevivendo ao Horror + 2 origens de Arquivos Secretos, sem duplicatas.
- Ficha: o seletor de origem agora usa `ORIGENS_COMPLETAS`.
- Catálogo de armas do Livro de Regras mantido com as 35 armas que aparecem na Tabela 3.3. A contagem 42/42 mencionada anteriormente não corresponde à Tabela 3.3 do PDF; não foram inventadas armas para atingir 42.
- Catálogo de munições do Livro de Regras mantido separado.
- Rituais básicos: 85 entradas existentes mantidas.
- Criado catálogo de rituais adicionais identificados em Sobrevivendo ao Horror.
- Criado catálogo de poderes adicionais identificados em Sobrevivendo ao Horror.
- Criadas entradas de trilhas adicionais identificadas em Sobrevivendo ao Horror e Arquivos Secretos.
- Criado catálogo de itens de Arquivos Secretos.
- Criado catálogo de ameaças de Sobrevivendo ao Horror e de várias ameaças do capítulo 7 do Livro de Regras.
- Criado `CatalogoCompletoData.kt` para centralizar o conteúdo adicional e as páginas dos PDFs.
- Compêndio agora exibe armas, munições, rituais e o catálogo ampliado.

Observação:
- O PDF de Sobrevivendo ao Horror é uma digitalização sem camada de texto; por isso os nomes/índices adicionais foram conferidos por OCR. As descrições completas continuam acessíveis pelo leitor de PDF do aplicativo.
- O build não pôde ser executado neste ambiente porque o Gradle tentou baixar sua distribuição e o ambiente não possui acesso à internet.
