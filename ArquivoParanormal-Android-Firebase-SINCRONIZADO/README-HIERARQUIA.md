# Hierarquia de navegação

```text
SITE C (Home)
  └── Login
       ├── Cliente
       │    └── Área do Cliente
       │         ├── Chatbot
       │         └── AR
       │
       └── Gestor
            └── PUBLIC
                 ├── Institucional
                 └── Entrar no ERP / APP
                      └── APP
                           ├── Visão Geral
                           ├── Estoque
                           ├── Manutenção
                           ├── RH
                           ├── Pedidos
                           ├── Qualidade
                           ├── Suprimentos
                           ├── Produção
                           ├── Auditoria & Logs
                           └── S.I.U.
```

A camada `PUBLIC` é protegida por role e não pode ser acessada por Cliente.
