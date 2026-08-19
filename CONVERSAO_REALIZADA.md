# Conversão Lovable → Android Nativo GeTech

Fonte analisada: `app-completeness-check-main.zip`.

## Implementação
- Kotlin + Jetpack Compose + Material 3.
- Navigation Compose para navegação interna.
- Persistência local com SharedPreferences e JSON nativo do Android.
- Separação básica UI → Repository → armazenamento local.
- Sessão local com perfis Cliente e Gestor.
- Bloqueio de navegação entre áreas por perfil.
- Home, Login, Cadastro, Recuperação de senha, Cliente, Portal, ERP e módulos existentes.
- Chatbot com interface nativa e comportamento local explícito, sem inventar backend.
- Tema claro/escuro.
- Logo real do projeto Lovable incorporado em `app/src/main/res/drawable/getech_logo.png`.
- Responsividade mobile com LazyColumn, cards, tabs, dialogs e rolagem.
- Sem WebView, iframe, HTML, React, href ou navegação interna por URL.

## Rotas/funções contempladas
Home, Login, Cadastro, Recuperação, Cliente, Portal, ERP, Estoque, Manutenção, Máquinas, RH/Ponto, Pedidos, Qualidade, Suprimentos, Produção, Logs, Sistema, Configurações do ERP, Configurações da conta, Contato, Materiais, Chatbot, Sobre, Funcionalidades, Planos, Depoimentos, FAQ, Ajuda, Privacidade/LGPD, Integrações, Realidade Aumentada, Blog, Mensagens e Orçamentos.

## Dados de demonstração
Foram reproduzidos os usuários e coleções demonstrativas encontradas no `src/lib/seed.ts`, sem backend fictício.

Contas demo:
- Gestor: `gestor@getech.com` / `getech123`
- Cliente: `cliente@getech.com` / `getech123`

## Codemagic
O `codemagic.yaml` permanece na raiz e mantém o workflow `android-debug-apk`, Java 21, `mac_mini_m2` e o caminho final do APK solicitado.

Observação: o ZIP recebido não continha o projeto Android anterior nem `google-services.json`; por isso nenhum arquivo Firebase fictício foi criado. A aplicação não depende de Firebase para as funcionalidades locais.
