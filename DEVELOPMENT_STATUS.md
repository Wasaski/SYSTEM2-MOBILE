# SYSTEM2 MOBILE — DEVELOPMENT_STATUS

## Etapa 1 — Auditoria (CONCLUIDA)
- Build no Actions: VERDE (run 42506041614, artifacts SYSTEM2-MOBILE-debug 16.5MB).
- Corrigido: packages via setup-android, build.gradle em android/app, package MainActivity, imports grid/clickable, StatFs API18, removido StorageStatsManager (assinatura incompativel SDK34 -> campo null/indisponivel).
- Ambiente: Android 16 aarch64, Termux, Java 21, git, gh (Wasaski), proot-distro disponivel.
- Sem Android SDK/Gradle local -> build SOMENTE no GitHub Actions (decisao confirmada).
- Arquitetura definida: Kotlin 2.0.20 + Compose BOM 2024.10.00 + AGP 8.5.2, minSdk 26, target 34.

## Etapa 2 — APK Base (CONCLUIDA + COMPILADA NO ACTIONS)
- Projeto Android criado, Compose + Material3, identidade visual aplicada.
- Navegacao Desktop/Launcher/Terminal/Monitor/Settings/Linux Desktop.
- CI: .github/workflows/android-build.yml (JDK17 + SDK34 + assembleDebug + artifacts + release por tag).

## Etapa 3 — Termux Bridge (CONCLUIDA em codigo, pendente teste em dispositivo)
- TermuxBridge.detectTermux + RUN_COMMAND Intent real (TermuxActivity) + Receiver background.
- CommandSecurityManager: allowlist (proot-distro, uname, df, free, uptime, ps, termux-info, termux-setup-storage, pkg, apt, bash, sh), destrutivos bloqueados, anti-injecao, timeout 60s.
- Proximas tarefas: testar em dispositivo ARM64 com Termux real + permissao RUN_COMMAND.

## Etapa 4 — Ubuntu (EM ANDAMENTO — codigo pronto)
- UbuntuManager com estados; scripts linux/ubuntu/bootstrap.sh, install-desktop.sh, start-stop-healthcheck.sh.
- packages.txt / packages-graphical.txt.
- Pendente: rodar bootstrap no Termux do dispositivo.

## Etapa 5 — Interface Hibrida (base pronta)
- LinuxDesktopScreen abre Termux:X11 (ComponentName com.termux.x11) com fallback F-Droid.
- Distincao clara: XFCE roda na atividade externa; WM nativo = WindowManagerState.

## Etapa 6 — Funcionalidades (parcial)
- Terminal Milestone1 (uname -a real) CONCLUIDO no codigo; historico/sessoes em TerminalEngine.
- Launcher: lista real via PackageManager + busca + abrir apps.
- Arquivos: FileScopeResolver (escopo interno real); Bridge para escopos Ubuntu/Termux pendente.
- Monitor: metricas reais do host (ActivityManager/StatFs/uptime); Ubuntu via Bridge pendente.
- Processos: ProcessRepository (ActivityManager real).
- Settings: SettingsStore (persistencia real); UI completa pendente.
- App Center: AppCenter.inspectInstalled/listAllInstalled (PackageManager real) + PackageInstaller na proxima iteracao; perfis logicos pendentes.

## Etapa 7 — Testes e Release (PENDENTE)
- CI roda testDebugUnitTest quando existirem; teste de aceitacao Milestone 1 em Android ARM64 pendente.

## Arquivos criados
Estrutura completa android/bridge/linux/.github + docs + scripts (ver repo).

## Problemas conhecidos
- Sem SDK local: compilacao so validada no Actions.
- RUN_COMMAND exige concessao de permissao pelo usuario no Termux.
- Ubuntu exige varios GB livres; instalacao progressiva obrigatoria (sem rootfs no APK).
- Android 10+ restringe lista de processos em background.

## Proximas tarefas
1. Push + build no Actions -> APK debug.
2. Instalar Termux + Ubuntu no dispositivo; teste uname -a.
3. Completar App Center (Session install), Settings UI, File Browser via Bridge.
