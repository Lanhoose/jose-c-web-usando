# Firebase Storage

O sistema de músicas personalizadas do aplicativo não depende mais de Firebase Storage.

Você não precisa ativar o Storage nem fazer upgrade do projeto para usar as músicas personalizadas.

O arquivo fica no armazenamento privado do aparelho do Mestre e o Firestore sincroniza somente os metadados e os comandos de reprodução.

O Firebase Storage ainda pode existir no projeto para outros recursos que já dependam dele, como foto de perfil, mas ele não é necessário para o áudio personalizado.
