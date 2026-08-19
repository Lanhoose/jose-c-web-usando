GeTech v5 - versão nativa resistente a crash

- Kotlin + Jetpack Compose
- Sem WebView
- Sem HTML/JS
- Sem Firebase
- Dados locais defensivos
- JSON corrompido não deve derrubar a aplicação
- Erros de inicialização exibem tela de recuperação
- Crash handler grava a última exceção em SharedPreferences
- Camera/AR protegida contra falhas de inicialização
- Contas demo:
  gestor@getech.local / 123456
  cliente@getech.local / 123456

IMPORTANTE:
A tela de erro é deliberadamente visível em vez de fechar o app. Se ainda houver crash antes
da tela, envie o logcat do Codemagic/Android para localizar a exceção exata.
