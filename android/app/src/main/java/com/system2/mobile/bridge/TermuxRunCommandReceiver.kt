package com.system2.mobile.bridge

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.system2.mobile.core.AppLogger

/**
 * Recebe resultados de execucoes RUN_COMMAND em background.
 * Extras do Termux: STDOUT (String), STDERR (String), EXIT_CODE (Int).
 */
class TermuxRunCommandReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null) return
        val stdout = intent.getStringExtra("com.termux.app.RunCommandService.STDOUT") ?: ""
        val stderr = intent.getStringExtra("com.termux.app.RunCommandService.STDERR") ?: ""
        val exitCode = intent.getIntExtra("com.termux.app.RunCommandService.EXIT_CODE", -1)
        AppLogger.i("RUN_COMMAND bg result exit=$exitCode stdout_len=${stdout.length}")
        // Repassa via broadcast interno para o ViewModel coletar.
        val fwd = Intent("com.system2.mobile.RUN_COMMAND_RESULT")
        fwd.putExtra("stdout", stdout)
        fwd.putExtra("stderr", stderr)
        fwd.putExtra("exit_code", exitCode)
        fwd.setPackage(context?.packageName)
        context?.sendBroadcast(fwd)
    }
}
