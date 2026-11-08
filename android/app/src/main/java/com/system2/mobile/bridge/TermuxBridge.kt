package com.system2.mobile.bridge

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import com.system2.mobile.core.AppLogger
import com.system2.mobile.core.S2Result

/**
 * SYSTEM2 BRIDGE — comunicacao com o Termux via RUN_COMMAND.
 *
 * Fluxo real:
 * 1) [detectTermux] verifica pacote com.termux instalado.
 * 2) [runCommand] envia Intent para com.termux.app.TermuxActivity
 *    com extras COMMAND / ARGUMENTS / BACKGROUND=false e retorna
 *    stdout/stderr/exitCode via ActivityResult (resultCode) OU via
 *    BroadcastReceiver [TermuxRunCommandReceiver] quando BACKGROUND=true.
 * 3) Toda execucao passa por [CommandSecurityManager] (allowlist, timeout,
 *    sem shell arbitrario).
 * Milestone 1: `uname -a` dentro do Ubuntu real (proot-distro).
 */
object TermuxBridge {

    const val TERMUX_PACKAGE = "com.termux"
    const val TERMUX_API_PACKAGE = "com.termux.api"
    const val PERMISSION_RUN_COMMAND = "com.termux.permission.RUN_COMMAND"
    private const val TERMUX_ACTIVITY = "com.termux.app.TermuxActivity"

    fun detectTermux(context: Context): S2Result<Boolean> {
        return try {
            val pm = context.packageManager
            val installed = try {
                pm.getPackageInfo(TERMUX_PACKAGE, 0)
                true
            } catch (e: PackageManager.NameNotFoundException) {
                false
            }
            AppLogger.i("Termux installed=$installed")
            S2Result.Ok(installed)
        } catch (t: Throwable) {
            S2Result.Err("Falha ao detectar Termux: ${t.message}", t)
        }
    }

    fun isRunCommandPermissionGranted(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context.checkSelfPermission(PERMISSION_RUN_COMMAND) == PackageManager.PERMISSION_GRANTED
        } else true
    }

    /** Monta a Intent RUN_COMMAND real para o Termux. */
    fun buildRunCommandIntent(
        command: String,
        arguments: List<String> = emptyList(),
        workDir: String? = null,
        background: Boolean = false,
        useLoginShell: Boolean = false
    ): Intent {
        val intent = Intent().apply {
            component = ComponentName(TERMUX_PACKAGE, TERMUX_ACTIVITY)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra("com.termux.app.RunCommandService.COMMAND", command)
            putExtra("com.termux.app.RunCommandService.ARGUMENTS", arguments.toTypedArray())
            putExtra("com.termux.app.RunCommandService.WORKDIR", workDir ?: "")
            putExtra("com.termux.app.RunCommandService.BACKGROUND", background)
            putExtra("com.termux.app.RunCommandService.USE_LOGIN_SHELL", useLoginShell)
        }
        return intent
    }

    /**
     * Atalho para o aceite do Milestone 1:
     * executa `proot-distro login ubuntu -- uname -a` dentro do Termux.
     */
    fun buildUnameIntent(): Intent =
        buildRunCommandIntent(
            command = "proot-distro",
            arguments = listOf("login", "ubuntu", "--", "uname", "-a")
        )

    /** Abre a Play Store / F-Droid para instalar o Termux (orientacao). */
    fun openTermuxInstallPage(context: Context) {
        val url = "https://f-droid.org/packages/com.termux/"
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }
}
