# Arquivo Paranormal — Correções completas V5

Principais correções desta versão:

- Fila persistente de fichas pendentes: alterações locais não são apagadas por snapshots remotos antes da confirmação do Firestore.
- Fila pendente sobrevive ao encerramento do aplicativo e é removida somente quando a gravação correspondente é confirmada.
- Proteção contra confirmação atrasada apagando uma alteração local mais nova.
- Exclusão de ficha também remove a entrada da fila pendente.
- Mesa Tática com debounce de gravação no Firestore para reduzir gravações em movimentos rápidos.
- Mesa Tática ignora snapshots remotos anteriores à última alteração local.
- Mapas da Mesa são redimensionados/compactados antes da publicação e limitados para manter margem abaixo do limite de documento do Firestore.
- Se um mapa não puder ser compactado com segurança, a publicação remota daquele estado é preservada em vez de gravar um documento inválido.
- Removidos `!!` desnecessários em telas e leitor PDF.
- Fluxo de criação de conta tenta desfazer a conta Auth quando a criação do perfil Firestore falha.
- `atualizarNome()` agora diferencia sucesso e falha do Firestore.
- Release não usa mais a chave de assinatura debug.

Observação: a compilação automática neste ambiente não pôde baixar o Gradle 9.3.1 porque `services.gradle.org` está bloqueado pela rede do ambiente. O projeto foi mantido com o wrapper original e deve ser validado no Codemagic.
