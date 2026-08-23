# Sincronização em tempo real da batalha

O aplicativo usa `batalhas/principal` como a sala de batalha compartilhada.

- Mestre: pode criar, editar, remover combatentes, alterar PV/iniciativa e avançar a rodada.
- Jogadores: recebem a batalha por `addSnapshotListener` e veem as alterações em tempo real.
- As alterações são salvas como `payload` JSON no Firestore.
- O estado local continua sendo mantido no SharedPreferences para funcionar offline até a próxima sincronização.

## Regras

Publique `firestore.rules` no mesmo projeto Firebase usado pelo aplicativo.

No terminal, na pasta do projeto que contém o Firebase CLI configurado:

```cmd
firebase deploy --only firestore:rules
```

O usuário Mestre precisa ter `/usuarios/{uid}.role == "mestre"`.

## Bestiário compartilhado com o site

A coleção `monstros` agora é sincronizada em tempo real. Jogadores possuem leitura; somente o Mestre publica alterações. As ameaças pré-prontas continuam sendo catálogo local idêntico ao catálogo do site.

O documento `batalhas/principal` continua sendo a fonte da iniciativa e também recebe a representação tática publicada pelo site.
