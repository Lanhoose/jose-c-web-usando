# GeTech Android v2 — conversão fiel de public + app

Esta versão usa diretamente a árvore original `site/public` e `site/app` do ZIP GeTech como conteúdo do aplicativo.

Entrada:
`site/public/pages/index.html` (Site C/public)

Hierarquia:
- Gestor → `site/app/app.html` e módulos originais.
- Cliente → `site/public/pages/cliente.html`.
- Visitante → páginas públicas.

Firebase não foi incluído. Login, sessão, configurações, chamados e dados dos módulos usam armazenamento local.

O chatbot original do Site C foi mantido e seus chamados continuam em `localStorage`.

A página de Realidade Aumentada original foi mantida, incluindo o catálogo, Three.js e suporte do `model-viewer`; AR espacial real continua dependente do suporte do aparelho/modelos e de recursos externos.
