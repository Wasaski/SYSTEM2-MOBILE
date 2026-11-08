# SYSTEM2 MOBILE

Ambiente operacional Linux Mobile (Kotlin + Jetpack Compose) hospedado no Android,
com Ubuntu 24.04 ARM64 executado via Termux + PRoot (proot-distro) + Termux:X11/XFCE.

- **SYSTEM2 SHELL**: Desktop, Launcher, Taskbar, Window Manager, Monitor, Settings (Compose/Material3).
- **SYSTEM2 BRIDGE**: comunicacao real com o Termux via RUN_COMMAND (`com.termux.app.TermuxActivity`),
  com allowlist de comandos, timeout e protecao anti-injecao shell.
- **SYSTEM2 LINUX**: Ubuntu 24.04 ARM64 instalado progressivamente no Termux (rootfs NAO vai no APK).
- **SYSTEM2 APP CENTER**: inspecao real de APKs instalados via PackageManager, instalacao via PackageInstaller.

## Identidade visual
Paleta propria: fundo `#090909`, superficies `#191919`, vermelho `#D71920`/`#8F1016`.
Sem emojis, sem icones genericos de "IA".

## Build (GitHub Actions — sem build local)
Localmente nao ha Android SDK; o build roda no CI:

```
git push -> workflow android-build.yml -> portal artifacts
git tag v0.1.0 -> Release publicada
```

## Milestone 1 (aceitacao)
APK compilando -> Desktop -> detectar Termux -> `proot-distro login ubuntu -- uname -a`
via RUN_COMMAND -> saida real dentro do app (tela Terminal).

## Pré-requisitos no dispositivo
1. Termux (F-Droid: https://f-droid.org/packages/com.termux/).
2. Termux:API (opcional), Termux:X11 (Etapa 5).
3. Conceder permissao `com.termux.permission.RUN_COMMAND` ao SYSTEM2.
4. No Termux: `bash linux/ubuntu/bootstrap.sh` (instala Ubuntu 24.04 ARM64).
5. Grafico: `bash linux/ubuntu/install-desktop.sh`.

Ver [DEVELOPMENT_STATUS.md](DEVELOPMENT_STATUS.md) e [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md).
