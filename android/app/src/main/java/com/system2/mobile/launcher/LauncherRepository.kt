package com.system2.mobile.launcher

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.system2.mobile.core.AppLogger

data class InstalledApp(
    val packageName: String,
    val label: String,
    val isSystem2Internal: Boolean = false,
    val category: String = "Geral",
    val isFavorite: Boolean = false,
    val isVisible: Boolean = true
)

/**
 * Lista aplicativos Android instalados via PackageManager (dados reais).
 * Nao promete desativar apps de sistema sem privilegio.
 */
object LauncherRepository {

    fun listInstalledApps(context: Context, includeSystem: Boolean = false): List<InstalledApp> {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val infos = pm.queryIntentActivities(intent, 0)
        val result = mutableListOf<InstalledApp>()
        for (info in infos) {
            val pkg = info.activityInfo.packageName
            if (pkg == "com.system2.mobile") continue // o proprio SYSTEM2
            val label = info.loadLabel(pm).toString()
            result.add(InstalledApp(pkg, label))
        }
        AppLogger.i("launcher apps=${result.size}")
        return result.sortedBy { it.label.lowercase() }
    }

    fun launchApp(context: Context, packageName: String): Boolean {
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        if (intent == null) return false
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        return true
    }
}
