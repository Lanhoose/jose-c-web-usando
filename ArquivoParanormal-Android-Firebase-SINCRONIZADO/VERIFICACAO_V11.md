# Verificação e correções V11

## Fotos dos agentes
- O Mestre não escolhe mais um arquivo no botão da lista de agentes.
- O botão agora grava `fotoSolicitadaEm` na ficha.
- O aparelho do jogador, ao receber a solicitação, usa a foto do perfil já salva localmente e responde com a miniatura.
- Se o arquivo local não existir, o jogador tenta responder com a miniatura já sincronizada no próprio documento de usuário.
- A solicitação só é limpa depois que uma imagem foi realmente obtida.
- A lista do Mestre verifica se `fotoArquivo` realmente existe antes de tentar exibi-lo e usa a miniatura sincronizada como fallback.

## Auditoria de informações
Foi feita uma auditoria estrutural do catálogo e uma conferência contra o PDF `Cópia de Arquivos-Secretos-07-v1.0(1).pdf` disponível na biblioteca.

Correções confirmadas:
- Origem Exorcizado: página 80, perícias Fortitude/Ocultismo e poder O Que Restou.
- Origem Sensitivo Rebelde: página 80, perícias Intuição/Vontade e poder Sussurros e Vultos.
- Monstruoso Especialista: progressão NEX 10/40/65/99 e conteúdo ajustado às páginas 81–84.
- Monstruoso Ocultista: NEX 10 confirmado na página 85; descrição ajustada para não inventar efeitos específicos não presentes no PDF disponível.
- Corrigido o nome `Ser Expulso` para `Ser Expurgado`.
- Removida a referência incorreta da página 80 para a variante de Monstruoso Combatente; essa variante é explicitamente indicada no PDF como variante de Sobrevivendo ao Horror.

## Limitação da conferência
O único PDF de regras disponível atualmente na Biblioteca é `Cópia de Arquivos-Secretos-07-v1.0(1).pdf`. Portanto, não é possível afirmar que todos os dados do Livro de Regras e de Sobrevivendo ao Horror estejam 100% conferidos nesta rodada. Esses dados precisam ser comparados com os PDFs correspondentes para uma auditoria completa.

## Build
Foi tentado `./gradlew :app:compileDebugKotlin --offline`. O Gradle Wrapper tentou baixar Gradle 9.3.1 e falhou por `UnknownHostException: services.gradle.org`; portanto, a compilação Android completa não pôde ser validada neste ambiente.

## Correção adicional de fontes
- O catálogo de trilhas agora diferencia corretamente a fonte de cada suplemento; antes, a função que criava trilhas marcava todas como Livro de Regras.
- Agente Secreto, Caçador, Erudito, Perseverante e Monstruoso Combatente foram marcados como Sobrevivendo ao Horror.
- Monstruoso Especialista e Monstruoso Ocultista foram marcados como Arquivos Secretos.
- A referência de Monstruoso Combatente não é mais falsamente apontada para Arquivos Secretos p. 80.
- As páginas das origens e trilha de Arquivos Secretos foram ajustadas para as páginas impressas conferidas no PDF disponível.
