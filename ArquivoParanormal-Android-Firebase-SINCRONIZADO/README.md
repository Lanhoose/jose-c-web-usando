# Arquivo Paranormal

Projeto Android nativo em Kotlin + Jetpack Compose.

## Abrir no Android Studio
1. Extraia este projeto.
2. Abra a pasta `ArquivoParanormal` no Android Studio.
3. Aguarde a sincronização do Gradle.
4. Execute o módulo `app` em um dispositivo/emulador.

## Gerar APK
No Android Studio: **Build > Build APK(s)**.

O projeto não depende de GitHub Actions para compilar.


## Novas funções desta versão

- **Compêndio com busca global**: pesquisa rituais, armas, itens, ameaças, origens, classes, perícias, condições e elementos; resultados com página abrem o PDF.
- **Inventário inteligente**: categorias de Armas, Proteções, Itens, Munições, Itens amaldiçoados e Outros; cálculo de carga e alerta de excesso.
- **Armas automatizadas**: armas do catálogo preenchem seus dados; a ficha oferece `ATACAR` e `DANO` com rolagens.
- **Modo Mestre**: painel com atalhos para batalha, ameaças, compêndio e mesa.
- **Diário de campanha**: notas, objetivos, pistas, locais, NPCs e sessões.
- **Backup/Importação**: exporta agentes, ameaças customizadas, batalha, mapa, NPCs, anotações e sessões em JSON; o mapa atual é incluído em Base64 para poder ser restaurado em outro aparelho.
- **Sincronização de campanha**: `/campanhas/principal` no Firestore. Mestre escreve; jogadores autenticados leem em tempo real.
