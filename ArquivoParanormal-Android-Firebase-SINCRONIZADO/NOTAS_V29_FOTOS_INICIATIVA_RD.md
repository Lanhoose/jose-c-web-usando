# V29 — fotos automáticas, importação para mesa e RD compacta

## Fotos
- Removido o botão "Carregar foto do jogador" da tela de agentes.
- O Mestre sincroniza automaticamente a miniatura `photoThumb` do perfil do dono da ficha quando a ficha ainda não possui `fotoJogadorThumb`.
- A atualização remota da foto usa transação do Firestore e altera somente o payload da foto, preservando edições simultâneas de PV, perícias, inventário etc.
- Quando o jogador troca a foto, a propagação para suas fichas também usa transação para evitar sobrescrever dados antigos.

## Iniciativa / Mesa tática
- "Importar agentes" continua adicionando os agentes à iniciativa.
- Agora também cria automaticamente um token de cada agente na Mesa Tática.
- O token usa a miniatura do jogador quando disponível.
- O ID do token é o mesmo do combatente importado.
- Os tokens são posicionados nas células livres da mesa.

## Resistências
- RD Geral continua sendo universal somente quando uma fonte declarar explicitamente "Geral".
- `Fisico` é expandido somente para Balístico, Corte, Impacto e Perfuração.
- Proteção Pesada, portanto, não concede RD contra Fogo, Frio, Eletricidade, Químico, Mental ou Paranormal.
- RD específica continua limitada ao tipo indicado.
- Override `rdGeral` do Mestre entra no cálculo de qualquer tipo, enquanto `rd:<tipo>` altera apenas aquele tipo.
- A tela do Mestre foi compactada em grupos de duas colunas, mantendo edição e botão de origem.
