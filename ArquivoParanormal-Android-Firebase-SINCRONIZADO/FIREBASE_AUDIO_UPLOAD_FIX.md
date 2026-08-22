# Áudio personalizado — sem Firebase Storage

O upload de áudio foi substituído por importação local. O aplicativo não faz `putFile()`, não pede `downloadUrl` e não depende de bucket do Firebase para músicas.

O arquivo selecionado é copiado para `filesDir/audios_mestre/`. O Firestore guarda somente os metadados da faixa e o estado de reprodução da mesa.

Isso elimina o erro `Object does not exist at location` causado pela ausência de um bucket do Firebase Storage.

Não é necessário executar `firebase deploy --only storage` para o sistema de músicas. As regras do Firestore continuam sendo usadas para sincronizar os comandos e metadados.
