# Taiji Browser

Navegador Android com bloqueio de anúncios (filtros + DNS local) e integração
com Spotify, Instagram e outros apps. Design inspirado no conceito de Taiji
(太極): fluido, delicado, minimalista, com curvas orgânicas e movimento contínuo.

## Arquitetura

| Camada | Tecnologia |
|---|---|
| Engine de renderização | GeckoView (Mozilla) |
| UI | Jetpack Compose + Material 3 (cores dinâmicas do sistema) |
| Bloqueio de anúncios (filtros) | Lista estilo EasyList, interceptação via `NavigationDelegate` do GeckoView |
| Bloqueio de anúncios (DNS) | `VpnService` local, sinkhole 100% no dispositivo (sem servidor externo) |
| Integrações | Deep link (`Intent.ACTION_VIEW`), Share Sheet, preview via oEmbed |
| CI/CD | GitHub Actions → build automático de APK a cada push na `main` |

## Setup local (uma vez, antes do primeiro push)

Este projeto **não inclui o binário do Gradle Wrapper** (`gradle-wrapper.jar`)
porque foi gerado num ambiente sem acesso à internet. Antes do primeiro commit,
rode localmente (com Gradle instalado):

```bash
gradle wrapper --gradle-version 8.9
```

Isso vai gerar `gradlew`, `gradlew.bat` e `gradle/wrapper/gradle-wrapper.jar`.
Depois disso, o wrapper fica versionado e os pushes seguintes funcionam normalmente.
(O workflow de CI já funciona mesmo sem esse passo, pois provisiona o Gradle
diretamente — o wrapper é só para build local confortável.)

## Como publicar no GitHub

```bash
git init
git add .
git commit -m "Initial commit: estrutura base do Taiji Browser"
git branch -M main
git remote add origin https://github.com/SEU_USUARIO/taiji-browser.git
git push -u origin main
```

A cada push na `main`, o GitHub Actions builda o APK debug e cria uma Release
automaticamente com o `.apk` anexado (aba **Releases** do repositório).

## Limitações conhecidas / próximos passos

- **DNS VpnService**: o esqueleto do túnel está criado (`DnsBlockVpnService`),
  mas o loop de leitura/escrita de pacotes UDP/DNS ainda precisa ser implementado
  (parsing de pacotes DNS brutos e resposta sinkhole). Hoje ele sobe a VPN mas
  não filtra de fato — é o próximo passo de maior prioridade.
- **Listas de filtro**: os arquivos em `assets/filters/` são exemplos mínimos.
  Trocar por download periódico (WorkManager) da EasyList/EasyPrivacy e
  StevenBlack hosts completos.
- **oEmbed do Instagram**: requer app registrado na Meta for Developers com
  Instagram Graph API — o Spotify oEmbed já funciona sem autenticação.
- **Contador de bloqueios na UI**: `BrowserBar` já tem o parâmetro `blockedCount`,
  falta conectar ao `FilterListBlocker` real.
- **Ícone do app**: usar `@mipmap/ic_launcher` placeholder do Android Studio até
  a identidade visual final (Taiji) ser desenhada.

## Requisitos para compilar

- Android Studio Koala ou mais recente
- JDK 17
- Android SDK 35 (compileSdk) / minSdk 26
- Dispositivo/root **não é necessário** para este app — ele não requer root.
