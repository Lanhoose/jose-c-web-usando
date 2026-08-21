# Correções adicionais — CORRIGIDO7

- Proteção contra snapshots antigos de Batalha e Mesa Tática.
- Separação de `battleUpdatedAt` e `tacticalUpdatedAt` para impedir que uma gravação da batalha seja confundida com atualização da Mesa.
- Tombstones persistentes para exclusões de fichas offline.
- Tokens criados manualmente nunca entram com iniciativa 0.
- Ordenação determinística da iniciativa em empates.
- Tratamento de erros do MediaPlayer para áudio interno, local e remoto.
- Limpeza/recuperação de estado local de fichas excluídas.
