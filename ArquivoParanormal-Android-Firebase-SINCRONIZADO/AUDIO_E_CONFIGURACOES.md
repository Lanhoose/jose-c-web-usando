# Áudio personalizado e Configurações

## Áudio personalizado sem Firebase Storage

Os áudios personalizados não usam Firebase Storage. O Mestre seleciona um arquivo MP3/OGG/WAV/M4A/AAC e o aplicativo copia o arquivo para o armazenamento privado do próprio aparelho.

O Firestore sincroniza apenas:

- nome do áudio;
- categoria;
- identificação da faixa;
- estado tocar/pausar/parar;
- volume e repetição.

O caminho local do arquivo nunca é enviado ao Firestore.

### Importante

A música personalizada fica disponível para reprodução no aparelho em que foi importada. Os jogadores recebem o comando e os metadados, mas não recebem os bytes da música. Isso evita Firebase Storage e mantém o projeto no plano atual. Para os jogadores ouvirem a mesma faixa personalizada no próprio aparelho, o mesmo arquivo precisa estar disponível localmente nesses aparelhos.

Os seis ambientes padrão continuam embutidos no APK e podem ser reproduzidos normalmente em qualquer aparelho.

## Configurações

A tela de Configurações inclui conta, sincronização Firebase, tema claro/escuro/sistema, cor principal, escala de texto, escala de ícones, animações, efeitos e alto contraste.
