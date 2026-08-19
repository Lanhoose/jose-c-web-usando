# GeTech — aplicativo nativo

Esta versão foi criada **do zero em Kotlin + Jetpack Compose**, usando a arquitetura e a linguagem do projeto Arquivo Paranormal como base técnica.

Importante:
- NÃO usa WebView.
- NÃO abre HTML por `file://`.
- NÃO depende dos links do site para navegar.
- NÃO usa Firebase.
- Não copia a pasta `public` como páginas HTML: o conteúdo e a hierarquia foram reconstruídos como telas nativas.
- Dados de usuários, sessão e chamados são locais via SharedPreferences.

Hierarquia:
Visitante → Home → páginas públicas → Login/Cadastro
Cliente → Área do Cliente → Chatbot / AR / páginas públicas
Gestor → Painel ERP → Geral / Estoque / Manutenção / RH / Ordens / Qualidade / Suprimentos / Produção / Logs / Sistema

Contas:
gestor@getech.local / 123456
cliente@getech.local / 123456

A Realidade Aumentada usa câmera nativa como base e HUD de inspeção; AR espacial 3D real depende de ARCore/modelos 3D.
