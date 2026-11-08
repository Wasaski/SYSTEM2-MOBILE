package com.system2.mobile.processes

data class ProcessInfo(
    val pid: Int,
    val name: String,
    val memoryKb: Long?,
    val isSystem: Boolean
)

/**
 * Gerenciador de processos — usa ActivityManager.getRunningAppProcesses
 * (dados reais). Android 10+ restringe lista; indisponivel = "indisponivel".
 * Nunca matar processos de terceiros silenciosamente.
 */
object ProcessRepository {
    fun listRunning(context: android.content.Context): List<ProcessInfo> {
        val am = context.getSystemService(android.content.Context.ACTIVITY_SERVICE) as android.app.ActivityManager
        val running = am.runningAppProcesses ?: return emptyList()
        return running.map { rp ->
            ProcessInfo(
                pid = rp.pid,
                name = rp.processName,
                memoryKb = null, // requer details; indisponivel aqui
                isSystem = rp.importance >= android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_SERVICE
            )
        }
    }
}
