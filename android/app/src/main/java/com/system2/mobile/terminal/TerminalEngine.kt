package com.system2.mobile.terminal

import com.system2.mobile.bridge.CommandSecurityManager
import com.system2.mobile.core.AppLogger
import com.system2.mobile.core.S2Result
import java.util.UUID

data class TerminalSession(
    val id: String = UUID.randomUUID().toString().take(8),
    val history: MutableList<String> = mutableListOf(),
    val output: MutableList<String> = mutableListOf(),
    var isRunning: Boolean = false
)

/**
 * Terminal integrado — executa comandos REAIS no Ubuntu via Bridge.
 * Milestone 1: comandos simples (uname/df/free/uptime) com saida real.
 * Historico, multiplas sessoes e streaming assincrono via coroutines.
 */
class TerminalEngine {
    private val sessions = mutableMapOf<String, TerminalSession>()

    fun newSession(): TerminalSession {
        val s = TerminalSession()
        sessions[s.id] = s
        AppLogger.i("terminal session ${s.id}")
        return s
    }

    fun validateInput(commandLine: String): S2Result<Unit> {
        if (commandLine.isBlank()) return S2Result.Err("Comando vazio.")
        // Milestone 1: apenas comandos allowlisted simples.
        val first = commandLine.trim().split("\\s+".toRegex()).first()
        return CommandSecurityManager.validate(first, listOf())
    }

    fun appendOutput(session: TerminalSession, line: String) {
        session.output.add(line)
        if (session.output.size > 500) session.output.removeAt(0) // rolagem limitada
    }

    fun recordHistory(session: TerminalSession, cmd: String) {
        session.history.add(cmd)
    }
}
