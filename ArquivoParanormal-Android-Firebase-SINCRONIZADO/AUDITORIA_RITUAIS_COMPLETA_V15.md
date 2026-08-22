# Auditoria completa dos rituais — V15

## Escopo

Fonte primária: `livro de regras oficial.pdf`, seção **Descrição dos Rituais**. Foram reprocessados os **82 rituais únicos do Livro de Regras que já estavam no catálogo do aplicativo**.

### Campos conferidos

- nome;
- círculo;
- elemento;
- execução;
- alcance;
- alvo, área ou efeito;
- duração quando o PDF informa um campo próprio;
- resistência;
- descrição completa do efeito básico;
- Discente;
- custo adicional de Discente;
- requisito de Discente;
- Verdadeira;
- custo adicional de Verdadeira;
- requisito de Verdadeira.

## Resultado

- **82/82** rituais encontrados e reprocessados.
- **82/82** possuem execução, alcance e descrição.
- **82/82** possuem alvo, área ou efeito.
- **57** possuem forma Discente explícita no PDF.
- **68** possuem forma Verdadeira explícita no PDF.
- **41** possuem resistência explícita no PDF.
- Custos-base foram normalizados pela regra do círculo: 1/3/6/10 PE para 1º/2º/3º/4º círculos.
- `OpData.kt` passou pela compilação isolada com `kotlinc`.
- O ZIP passou pelo teste de integridade.

O fato de alguns rituais não terem Discente ou Verdadeira não é considerado erro: a forma simplesmente não existe quando o PDF não a apresenta. Da mesma forma, quando o PDF não fornece um campo separado de duração, o aplicativo não inventa uma duração.

## Correções relevantes

As descrições resumidas anteriores foram substituídas pelas descrições completas da seção de rituais. Os cabeçalhos foram reconstruídos para evitar misturar texto do efeito com duração ou resistência.

Exemplos conferidos diretamente no PDF:

- **Descarnar**: padrão, toque, 1 ser, instantânea, Fortitude parcial, 6d8, hemorragia; Discente +3 PE/3º círculo; Verdadeira +7 PE/3º círculo e afinidade.
- **Hemofagia**: padrão, toque, 1 ser, instantânea, Fortitude reduz à metade, 6d6 de Sangue e cura igual à metade do dano; Discente +3 PE; Verdadeira +7 PE/4º círculo.
- **Armadura de Sangue**: +5 Defesa na forma normal; Discente +5 PE com RD 5 físico; Verdadeira +9 PE com RD 10 físico.
- **Dissonância Acústica**: área de 6m; Discente +1 PE; Verdadeira +3 PE e requisito de 3º círculo.

## Observação sobre “Vampirismo”

`Vampirismo` não aparece como nome de ritual no Livro de Regras conferido. O ritual de Sangue encontrado com a mecânica de absorver sangue e recuperar PV é **Hemofagia**.

## Limite

O catálogo `RITUAIS_LIVRO` contém os 82 rituais únicos do Livro de Regras. Os PDFs de suplementos enviados existem separadamente, mas os rituais adicionais desses suplementos **não estavam representados como uma segunda lista estruturada no catálogo auditado**. Portanto, esta V15 corrige integralmente os 82 registros do Livro de Regras sem fingir que os suplementos já foram incorporados.

## Build

O build Android completo não pôde ser executado nesta sessão porque o Gradle Wrapper tentou acessar `services.gradle.org` e o ambiente não possui acesso DNS/rede para esse domínio. Isso é uma limitação do ambiente de build desta sessão, não um erro Kotlin encontrado no catálogo de rituais.
