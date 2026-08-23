# V36.5 — Correção do sistema de maldições

## Base da correção
O Livro de Regras define que itens amaldiçoados se dividem em:
- armas, proteções e acessórios, que recebem uma ou mais maldições;
- itens especiais, que possuem mecânicas próprias.

A aquisição de armas, proteções e acessórios amaldiçoados segue as regras de equipamento e fica disponível apenas para Agente Especial, Oficial de Operações e Agente de Elite. A primeira maldição aumenta a categoria do item em II e cada maldição subsequente aumenta em I. Maldições iguais não se acumulam e maldições de elementos opressores não podem ser combinadas.

## Alterações aplicadas
1. **Patente passou a ser exigida também para aplicar maldições**, e não apenas para adicionar itens amaldiçoados catalogados.
2. **Maldições de arma só aparecem para armas.**
3. **Maldições de proteção só aparecem para proteções.**
4. **Maldições de acessório só aparecem para itens marcados como acessório.**
5. Itens genéricos não são mais tratados automaticamente como acessórios.
6. Foi adicionada a propriedade `tipoEquipamento` aos itens para distinguir `Proteção`, `Acessório` e `Outro`.
7. Proteções adicionadas pelo catálogo são marcadas automaticamente como `Proteção`.
8. A ficha permite classificar manualmente um item como acessório quando isso for apropriado.
9. Ao aplicar uma maldição, a categoria do item agora aumenta corretamente: **+II na primeira** e **+I nas seguintes**.
10. A regra de não duplicação da mesma maldição continua sendo aplicada.
11. A regra de incompatibilidade entre elementos opressores continua sendo aplicada.
12. A maldição `Proteção Elemental` continua exigindo a escolha do elemento e registra a RD correspondente.

## RD
O motor mantém a separação de `Fisico` como chave interna que se expande somente para Balístico, Corte, Impacto e Perfuração. Ela não é tratada como RD paranormal/elemental universal.

## Validação
O ZIP foi alterado diretamente a partir da V36.4.1. A compilação Android completa **não foi executada neste ambiente**, pois o Gradle 9.3.1 tentou baixar `services.gradle.org` e o ambiente não possui acesso de rede. Portanto, não afirmar que a build passou até o teste no Codemagic.
