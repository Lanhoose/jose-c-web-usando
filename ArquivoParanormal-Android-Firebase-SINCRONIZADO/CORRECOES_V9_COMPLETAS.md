# Correções V9 — Fotos, Poderes, Perícias, Condições, Rituais e Cores

## Fotos de agentes
- Adicionado `fotoAgenteThumb` ao modelo da ficha.
- A foto do agente continua em resolução local e uma miniatura é sincronizada no payload da ficha.
- A lista de agentes usa a foto local, a miniatura sincronizada ou a foto de perfil como fallback.
- Ao abrir/listar fichas antigas, a miniatura é gerada automaticamente quando o arquivo local existe.
- Adicionado botão **Carregar foto** em cada agente para recuperar retratos cujo caminho local antigo não existe mais.
- A ficha também consegue mostrar a miniatura sincronizada quando o arquivo original não está disponível no aparelho.

## Cor principal
- Adicionada roda de cores visual.
- Adicionado campo HEX para usar qualquer cor no formato `#RRGGBB`.
- A cor personalizada é persistida e aplicada imediatamente ao tema.

## Poderes
- A seleção continua filtrada por origem, classe, trilha e NEX.
- Poderes que não cumprem os requisitos conhecidos não aparecem para o personagem.
- Poderes automáticos ficam separados dos poderes manuais.
- Adicionado **Ver detalhes** com descrição, categoria, classe, trilha, origem, NEX, requisitos, custo e referência de página quando disponível.
- O poder **Na Trilha Certa** recebeu o custo acumulativo em PE e descrição detalhada.
- Poderes de *Sobrevivendo ao Horror* que estavam no catálogo receberam descrições baseadas nas páginas fornecidas.

## Habilidades
- Criada seção própria **Habilidades**.
- Poderes/habilidades selecionados aparecem como cartões individuais.
- Cada cartão mostra descrição, custo/requisitos quando disponíveis e botão **Ver detalhes**.
- É possível remover uma habilidade individualmente.

## Perícias
- Perícias automáticas da origem permanecem cinzas e bloqueadas.
- Perícias escolhidas manualmente possuem botão **Trocar**.
- A troca substitui a vaga existente em vez de consumir uma nova.
- Perícias não treinadas não recebem o atributo como bônus de teste; o bônus de atributo só entra quando a perícia está treinada.
- Ataques por perícia e iniciativa passaram a usar a mesma regra de bônus da ficha.

## Condições
- Condições selecionadas agora mostram uma descrição detalhada diretamente abaixo da lista.
- A condição **Morrendo** recebeu descrição explicando que o personagem não pode se mover nem realizar ações enquanto estiver nesse estado, além do teste de morte indicado pelas regras.

## Rituais
- Cada ritual mostra custo base em PE.
- Cada ritual mostra a DT do custo do paranormal (`20 + PE gasto`).
- Cada ritual mostra a DT de resistência da ficha.
- Adicionada estrutura para forma Normal/Discente/Verdadeira e custos extras específicos quando catalogados.
- O custo base acompanha automaticamente o círculo (1/3/6/10 PE).

## Limitação de validação
A compilação Android completa não pôde ser executada neste ambiente porque o Gradle Wrapper precisa baixar `gradle-9.3.1` de `services.gradle.org`, que está inacessível na sessão. A estrutura dos arquivos foi verificada estaticamente e os delimitadores Kotlin dos arquivos alterados foram validados.
