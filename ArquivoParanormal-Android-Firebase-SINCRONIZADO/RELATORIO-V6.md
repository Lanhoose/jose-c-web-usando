# GeTech Native v6.0 — Relatório

## O que foi atualizado
- Aplicativo continua 100% nativo em Kotlin + Jetpack Compose.
- Site não é aberto por URL e não foi incorporado via WebView.
- O projeto Android v5.3 foi mantido como base; não foi recriado do zero.
- `codemagic.yaml`, Gradle Wrapper, versões e configuração de build foram preservados byte a byte.
- Tema inspirado nos tokens reais do CSS do site, incluindo dark/light e preferência local.
- Home reconstruída a partir do `site/public/pages/index.html` e `index.css`.
- Conteúdo público ampliado: Funcionalidades, Planos, Depoimentos, Integrações, FAQ, Sobre, Ajuda, Contato, Privacidade, Configurações e Blog.
- Login/cadastro/sessão local preservados.
- Área do Cliente ampliada com chamados, chatbot, AR e páginas públicas.
- Painel Gestor ampliado para uma estrutura ERP com Visão Geral, Inventário, Manutenção, RH, Pedidos, Qualidade, Suprimentos, Produção, Auditoria e S.I.U.
- Manutenção: cadastro local de máquinas, ativos e visão de O.S.
- Inventário: itens, mínimos e indicadores.
- Pedidos: criação local de ordens e acompanhamento adaptado para celular.
- RH: estrutura de funcionários/ponto local preparada.
- Auditoria & Logs: histórico local e busca.
- Chatbot: fluxo nome → e-mail → problema → chamado local.
- AR: câmera nativa e HUD mantidos.
- Armazenamento continua local; Firebase não foi reintroduzido.

## Base Web analisada
Foram analisados os diretórios `site/public`, `site/app` e `site/Site C`, incluindo páginas, CSS e JavaScript. A implementação Android usa esses arquivos como referência de conteúdo, identidade visual, hierarquia e comportamento, sem transportar HTML/JS para a UI.

## Limitações que permanecem
1. O backend real do site não foi convertido para servidor: esta versão permanece local por solicitação.
2. Pagamento/assinatura dos planos não é real; os planos são apresentação/seleção local.
3. Integrações externas, APIs e sincronização multiusuário não existem sem backend.
4. A AR atual continua baseada na câmera nativa/HUD. ARCore e modelos 3D reais não foram adicionados como dependência nova para evitar aumentar o risco do build.
5. Exportações avançadas (Excel/PDF) do site não foram reproduzidas integralmente; a base de dados local está preparada para expansão.
6. O S.I.U. do site usa Pyodide/Python no navegador. Não foi portado para Pyodide/HTML; o conceito foi convertido para uma interface nativa.

## Build
O `./gradlew clean assembleDebug` não pôde ser executado neste ambiente porque o Gradle Wrapper tentou baixar Gradle 8.14 de `services.gradle.org`, mas o ambiente de execução não possui acesso de rede. Portanto, não é correto afirmar que o APK foi compilado aqui. Os arquivos de build do projeto foram preservados e a versão deve ser validada no Codemagic.

## Observação
O pacote foi produzido em cima da v5.3 que já continha as correções dos erros de `MainActivity` e `GeTechApp`.
