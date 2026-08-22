# Correção da Build V33.1

Correções aplicadas após análise do `compilar_apk_debug.log (8).txt`.

## Falhas corrigidas

- Removida a dependência da patente no NEX.
- Adicionado `Personagem.prestigio`.
- Criada `patentePorPrestigio(prestigio)` com os limiares oficiais: 0/20/50/100/200.
- A sincronização de recursos derivados passa a normalizar a patente a partir do Prestígio.
- Criador: alteração de NEX não altera patente.
- Criador: alteração de Prestígio recalcula a patente.
- Tela da ficha não permite editar a patente manualmente; ela é derivada do Prestígio.
- Corrigida capacidade de carga: FOR 0 = 2 espaços; FOR >= 1 = FOR * 5.
- Imports de `Alignment`/`Spacer` e catálogos de itens amaldiçoados/maldições foram regularizados.
- `circuloMaximoOcultista`, `ITENS_AMALDICOADOS_LIVRO` e `MALDICOES_CATALOGO_COMPLETO` existem no módulo de dados e são importáveis pela UI.
- A implementação evita reintroduzir `patenteParaNex`.

## Sobre o log

O log também apontava uma declaração duplicada de `atributoParanormal` e chamadas de `MaldicaoCatalogoDef` com argumentos duplicados. A árvore de código fornecida junto deste reparo não contém a declaração duplicada nem essas chamadas na forma problemática do log; a classe `MaldicaoCatalogoDef` usa parâmetros nomeados de forma consistente.

## Verificação

Foi feita verificação estática dos símbolos e referências. A compilação Gradle não pôde ser executada neste ambiente porque o Wrapper precisa baixar Gradle 9.3.1 de `services.gradle.org` e o ambiente não resolve DNS/rede para esse servidor.

No Codemagic, executar:

    chmod +x gradlew
    ./gradlew clean assembleDebug --stacktrace
