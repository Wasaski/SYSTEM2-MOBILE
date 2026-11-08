package com.system2.mobile.appcenter

import android.app.usage.StorageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.system2.mobile.core.AppLogger

enum class AppInstallState {
    NOT_INSTALLED, INSTALLING, INSTALLED, UPDATE_AVAILABLE,
    HIDDEN_IN_LAUNCHER, INCOMPATIBLE, ERROR
}

data class AppInfo(
    val packageName: String,
    val label: String,
    val versionName: String?,
    val versionCode: Long,
    val state: AppInstallState,
    val storageBytes: Long? = null,
    val cacheBytes: Long? = null,
    val permissions: List<String> = emptyList(),
    val isFavorite: Boolean = false,
    val profile: String = "Geral"
)

/**
 * SYSTEM2 APP CENTER — metadados reais via PackageManager.
 * Instalacao usa PackageInstaller (Session) com autorizacao do usuario.
 * Sem sandboxes falsos; dados privados de terceiros nunca sao acessados.
 */
object AppCenter {

    fun inspectInstalled(context: Context, packageName: String): AppInfo? {
        val pm = context.packageManager
        return try {
            val pi = pm.getPackageInfo(packageName, 0)
            val label = pi.applicationInfo.loadLabel(pm).toString()
            val ver = pi.versionName
            val code = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) pi.longVersionCode else pi.versionCode.toLong()
            val perms = (pi.requestedPermissions ?: emptyArray()).toList()
            var storage: Long? = null
            var cache: Long? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try {
                    val ssm = context.getSystemService(Context.STORAGE_STATS_SERVICE) as StorageStatsManager
                    val stats = ssm.queryStatsForPackage(packageName)
                    storage = stats.appBytes
                    cache = stats.cacheBytes
                } catch (_: Exception) { /* indisponivel -> null */ }
            }
            AppLogger.i("inspected $packageName v=$ver")
            AppInfo(packageName, label, ver, code, AppInstallState.INSTALLED, storage, cache, perms)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }

    fun listAllInstalled(context: Context): List<AppInfo> {
        val pm = context.packageManager
        val intent = android.content.Intent(android.content.Intent.ACTION_MAIN).apply {
            addCategory(android.content.Intent.CATEGORY_LAUNCHER)
        }
        val infos = pm.queryIntentActivities(intent, 0)
        val out = mutableListOf<AppInfo>()
        for (info in infos) {
            val pkg = info.activityInfo.packageName
            inspectInstalled(context, pkg)?.let { out.add(it) }
        }
        return out
    }
}
