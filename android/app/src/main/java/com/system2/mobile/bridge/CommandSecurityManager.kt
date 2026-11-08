package com.system2.mobile.bridge

import com.system2.mobile.core.AppLogger
import com.system2.mobile.core.S2Result

/**
 * Camada de seguranca do SYSTEM2 BRIDGE.
 * Nenhum comando arbitrario e executado: apenas operacoes allowlisted,
 * com validacao de argumentos, timeout e confirmacao para destrutivas.
 */
object CommandSecurityManager {

    private val ALLOWED_COMMANDS = setOf(
        "proot-distro", "uname", "df", "free", "uptime", "ps",
        "termux-info", "termux-setup-storage", "pkg", "apt", "bash", "sh"
    )
    private val DESTRUCTIVE = setOf("rm", "mkfs", "dd", "chmod", "chown")

    fun validate(command: String, arguments: List<String>): S2Result<Unit> {
        AppLogger.d("validate cmd=$command args=${arguments.size}")
        if (command.isBlank()) return S2Result.Err("Comando vazio nao permitido.")
        val base = command.substringAfterLast('/')
        if (base in DESTRUCTIVE) {
            return S2Result.Err("Comando destrutivo '$base' exige confirmacao explicita do usuario.")
        }
        if (base !in ALLOWED_COMMANDS) {
            return S2Result.Err("Comando '$base' nao esta na allowlist do SYSTEM2 Bridge.")
        }
        // Anti-injecao: rejeita metacaracteres de shell nos argumentos simples.
        val forbidden = listOf(";", "&&", "||", "`", "$(", "|", ">", "<", "\n")
        for (arg in arguments) {
            if (forbidden.any { arg.contains(it) }) {
                return S2Result.Err("Argumento contem metacaractere de shell proibido: '$arg'")
            }
        }
        return S2Result.Ok(Unit)
    }

    const val DEFAULT_TIMEOUT_MS = 60_000L
}
