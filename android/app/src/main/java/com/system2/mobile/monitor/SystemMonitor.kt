package com.system2.mobile.monitor

import android.app.ActivityManager
import android.content.Context
import android.os.Environment
import android.os.StatFs
import com.system2.mobile.core.AppLogger

data class SystemMetrics(
    val cpuCores: Int,
    val totalRamMb: Long,
    val availRamMb: Long,
    val totalStorageGb: Double,
    val freeStorageGb: Double,
    val uptimeMin: Long,
    val termuxState: String,
    val ubuntuState: String,
    val x11State: String
)

/**
 * Metricas REAIS do Android hospedeiro (ActivityManager, StatFs, uptime).
 * Metricas do Ubuntu sao obtidas via Bridge (df/free/uptime no PRoot).
 * Nada e simulado; campo indisponivel = "indisponivel".
 */
object SystemMonitor {

    fun collectHostMetrics(context: Context): SystemMetrics {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val mi = ActivityManager.MemoryInfo()
        am.getMemoryInfo(mi)
        val totalMb = mi.totalMem / (1024 * 1024)
        val availMb = mi.availMem / (1024 * 1024)

        val path = Environment.getDataDirectory()
        val stat = StatFs(path.path)
        val totalBytes = stat.totalBytes
        val freeBytes = stat.freeBytes

        val uptimeMin = (android.os.SystemClock.elapsedRealtime()) / 60_000

        AppLogger.i("metrics totalMb=$totalMb availMb=$availMb")

        return SystemMetrics(
            cpuCores = Runtime.getRuntime().availableProcessors(),
            totalRamMb = totalMb,
            availRamMb = availMb,
            totalStorageGb = totalBytes / 1e9,
            freeStorageGb = freeBytes / 1e9,
            uptimeMin = uptimeMin,
            termuxState = "verificar via Bridge",
            ubuntuState = "verificar via Bridge",
            x11State = "verificar via Bridge"
        )
    }
}
