# SYSTEM2 MOBILE — Arquitetura

```
SYSTEM2 MOBILE (Android, Kotlin/Compose)
├── SYSTEM2 SHELL (android/.../desktop, launcher, windows, terminal, files, settings, monitor, processes, appcenter)
│   └── WindowManagerState (nativo, interno ao Compose)
├── SYSTEM2 BRIDGE (android/.../bridge)
│   ├── TermuxBridge: detect + Intent RUN_COMMAND real (TermuxActivity)
│   ├── CommandSecurityManager: allowlist, anti-injecao, timeout 60s
│   └── TermuxRunCommandReceiver: resultados background
└── SYSTEM2 LINUX (android/.../linux + linux/ubuntu/*.sh)
    └── Ubuntu 24.04 ARM64 via proot-distro (rootfs fora do APK)
        └── Termux:X11 + XFCE (atividade externa, separacao explicita)
```

Regras duras respeitadas:
- Sem dados simulados: launcher/monitor/processos leem APIs reais; indisponivel = exibido como indisponivel.
- Sem virtualizacao falsa: App Center usa PackageManager/PackageInstaller; perfis sao logicos (visibilidade/favoritos), nao sandboxes.
- Sem rootfs no APK: bootstrap progressivo no Termux.
- Sem comandos livres: todo RUN_COMMAND passa pela allowlist.
- Build zero-custo: GitHub Actions (JDK17 + SDK34), release por tag `v*`.

## Fluxo Milestone 1
UI Terminal -> UbuntuManager.unameCommand() -> CommandSecurityManager.validate()
-> TermuxBridge.buildUnameIntent() -> TermuxActivity (RUN_COMMAND)
-> onActivityResult (STDOUT/STDERR/EXIT_CODE) -> UI exibe saida real.
