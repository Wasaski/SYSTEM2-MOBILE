# SYSTEM2 BRIDGE — protocolo RUN_COMMAND
#
# O app SYSTEM2 envia Intent para o Termux:
#   Component: com.termux / com.termux.app.TermuxActivity
#   Extras:
#     com.termux.app.RunCommandService.COMMAND     (String, obrigatorio)
#     com.termux.app.RunCommandService.ARGUMENTS   (String[])
#     com.termux.app.RunCommandService.WORKDIR     (String)
#     com.termux.app.RunCommandService.BACKGROUND  (boolean)
#     com.termux.app.RunCommandService.USE_LOGIN_SHELL (boolean)
#
# Resultado (foreground, via onActivityResult):
#     com.termux.app.RunCommandService.STDOUT   (String)
#     com.termux.app.RunCommandService.STDERR   (String)
#     com.termux.app.RunCommandService.EXIT_CODE (int)
#
# Resultado (background, via BroadcastReceiver):
#     action: com.termux.app.RunCommandService.ACTION_RESULT
#     extras idem + sessionAction
#
# Permissao exigida no manifest: com.termux.permission.RUN_COMMAND
# Autorizacao: o usuario deve conceder a permissao ao SYSTEM2 nas
# configuracoes do Termux (ou o Termux a solicita na primeira execucao).
#
# Allowlist de comandos (CommandSecurityManager):
#   proot-distro, uname, df, free, uptime, ps, termux-info,
#   termux-setup-storage, pkg, apt, bash, sh
# Destrutivos (rm, mkfs, dd, chmod, chown): exigem confirmacao.
# Metacaracteres de shell (; && || ` $( | > < \n): rejeitados.
# Timeout padrao: 60s. Logs apenas locais (logcat).
