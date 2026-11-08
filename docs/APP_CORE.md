# System2 App Core — notas de implementacao

- `AppCenter.inspectInstalled`: PackageManager (label, versao, permissoes, storage/cache via StorageStatsManager).
- `AppCenter.listAllInstalled`: CATEGORY_LAUNCHER real.
- Instalacao: AndroidX PackageInstaller.Session (autorizacao do usuario) — roadmap proxima iteracao.
- Visibilidade no launcher != desativacao do pacote (sem privilegios de sistema).
- Dados internos: /data/data/com.system2.mobile/ (filesDir/shared prefs) isolados de terceiros.
- Perfis (Development/Productivity/Communication/Utilities/Personal): controlam visibilidade/favoritos/atalhos; nao sao Work Profiles.

## Isolamento avancado (pesquisa p/ roadmap, sem implementacao falsa)
- Work Profile requer Device Owner/provisionamento; limites explicitos na UI.
- Sem clonagem de diretorios privados nem modificacao silenciosa de dados alheios.
- Sem root no MVP.
