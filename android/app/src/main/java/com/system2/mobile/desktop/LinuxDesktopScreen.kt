package com.system2.mobile.desktop

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.system2.mobile.bridge.TermuxBridge
import com.system2.mobile.core.System2Colors

/**
 * Tela Linux Desktop — guia para Termux:X11 + XFCE.
 * NAO afirma que XFCE esta embutido no Compose. Acede o Termux:X11
 * via Intent quando instalado; caso contrario, guia de instalacao.
 */
@Composable
fun LinuxDesktopScreen(onBack: () -> Unit) {
    val ctx = LocalContext.current
    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Text("Linux Desktop", color = System2Colors.TextPrimary, fontSize = 22.sp)
        Spacer(Modifier.height(8.dp))
        Text(
            "Ambiente grafico Ubuntu 24.04 (XFCE) executado via Termux:X11. " +
            "Esta tela inicia o servidor grafico e abre a atividade do Termux:X11. " +
            "As janelas do XFCE rodam na atividade externa do Termux:X11, " +
            "separadas do window manager nativo do SYSTEM2.",
            color = System2Colors.TextSecondary, fontSize = 13.sp
        )
        Spacer(Modifier.height(20.dp))
        Button(
            onClick = {
                // Abre o Termux:X11 (pacote com.termux.x11). Se nao estiver
                // instalado, o Android mostrara a Play Store/F-Droid.
                try {
                    ctx.startActivity(
                        android.content.Intent().apply {
                            component = android.content.ComponentName(
                                "com.termux.x11", "com.termux.x11.MainActivity"
                            )
                            addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                    )
                } catch (_: Exception) {
                    ctx.startActivity(
                        android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                            setData(android.net.Uri.parse("https://f-droid.org/packages/com.termux.x11/"))
                            addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Abrir Termux:X11") }
        Spacer(Modifier.height(12.dp))
        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Voltar") }
    }
}
