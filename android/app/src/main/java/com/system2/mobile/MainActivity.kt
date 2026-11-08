package com.system2.mobile

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.system2.mobile.bridge.CommandSecurityManager
import com.system2.mobile.bridge.TermuxBridge
import com.system2.mobile.core.AppLogger
import com.system2.mobile.core.S2Result
import com.system2.mobile.core.System2ColorScheme
import com.system2.mobile.core.System2Colors
import com.system2.mobile.desktop.LinuxDesktopScreen
import com.system2.mobile.desktop.System2Desktop
import com.system2.mobile.launcher.InstalledApp
import com.system2.mobile.launcher.LauncherRepository
import com.system2.mobile.monitor.SystemMonitor

class MainActivity : ComponentActivity() {

    private var lastStdout by mutableStateOf<String?>(null)
    private var lastStderr by mutableStateOf<String?>(null)
    private var lastExitCode by mutableStateOf<Int?>(null)
    private var running by mutableStateOf(false)

    private val runCommandLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        running = false
        if (result.resultCode == Activity.RESULT_OK) {
            lastStdout = result.data?.getStringExtra("com.termux.app.RunCommandService.STDOUT")
            lastStderr = result.data?.getStringExtra("com.termux.app.RunCommandService.STDERR")
            lastExitCode = result.data?.getIntExtra("com.termux.app.RunCommandService.EXIT_CODE", -1)
                .takeIf { it != Int.MIN_VALUE }
            AppLogger.i("RUN_COMMAND ok exit=$lastExitCode")
        } else {
            lastStderr = "Resultado RUN_COMMAND: code=${result.resultCode} (Termux devolveu cancel/erro). " +
                "Verifique permissao RUN_COMMAND e se o Ubuntu esta instalado (proot-distro login ubuntu)."
            AppLogger.w(lastStderr ?: "")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme(colorScheme = System2ColorScheme) {
                Surface(modifier = Modifier.fillMaxSize(), color = System2Colors.Background) {
                    System2NavHost(
                        onRunUname = { runUnameA() },
                        stdout = lastStdout,
                        stderr = lastStderr,
                        exitCode = lastExitCode,
                        running = running
                    )
                }
            }
        }
    }

    private fun runUnameA() {
        val cmdArgs = com.system2.mobile.linux.UbuntuManager.unameCommand()
        val cmd = cmdArgs.first
        val args = cmdArgs.second
        when (val v = CommandSecurityManager.validate(cmd, args)) {
            is S2Result.Err -> {
                lastStderr = "Bloqueado pela seguranca: ${v.reason}"
                return
            }
            is S2Result.Ok -> {}
        }
        running = true
        AppLogger.i("RUN_COMMAND uname -> Termux")
        try {
            runCommandLauncher.launch(TermuxBridge.buildUnameIntent())
        } catch (t: Throwable) {
            running = false
            lastStderr = "Falha ao iniciar Termux via RUN_COMMAND: ${t.message}"
        }
    }
}

private enum class S2Route { DESKTOP, LAUNCHER, TERMINAL, MONITOR, SETTINGS, LINUX_DESKTOP, APPS }

@Composable
private fun System2NavHost(
    onRunUname: () -> Unit,
    stdout: String?,
    stderr: String?,
    exitCode: Int?,
    running: Boolean
) {
    var route by remember { mutableStateOf(S2Route.DESKTOP) }

    when (route) {
        S2Route.DESKTOP -> System2Desktop(
            onOpenTerminal = { route = S2Route.TERMINAL },
            onOpenLauncher = { route = S2Route.LAUNCHER },
            onOpenMonitor = { route = S2Route.MONITOR },
            onOpenSettings = { route = S2Route.SETTINGS },
            onOpenFiles = { route = S2Route.APPS },
            onOpenLinuxDesktop = { route = S2Route.LINUX_DESKTOP },
            onOpenTermux = { route = S2Route.TERMINAL }
        )
        S2Route.LAUNCHER -> LauncherScreen(onBack = { route = S2Route.DESKTOP })
        S2Route.TERMINAL -> Milestone1Terminal(
            onBack = { route = S2Route.DESKTOP },
            onRunUname = onRunUname,
            stdout = stdout,
            stderr = stderr,
            exitCode = exitCode,
            running = running
        )
        S2Route.MONITOR -> MonitorScreen(onBack = { route = S2Route.DESKTOP })
        S2Route.SETTINGS -> SettingsScreen(onBack = { route = S2Route.DESKTOP })
        S2Route.LINUX_DESKTOP -> LinuxDesktopScreen(onBack = { route = S2Route.DESKTOP })
        S2Route.APPS -> AppsScreen(onBack = { route = S2Route.DESKTOP })
    }
}

@Composable
fun Milestone1Terminal(
    onBack: () -> Unit,
    onRunUname: () -> Unit,
    stdout: String?,
    stderr: String?,
    exitCode: Int?,
    running: Boolean
) {
    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Text("Terminal — Ubuntu real (Termux)", color = System2Colors.TextPrimary, fontSize = 20.sp)
        Spacer(Modifier.height(8.dp))
        Text(
            "Milestone 1: executa `proot-distro login ubuntu -- uname -a` via RUN_COMMAND e exibe a saida real.",
            color = System2Colors.TextSecondary, fontSize = 12.sp
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = onRunUname, enabled = !running, modifier = Modifier.fillMaxWidth()) {
            Text(if (running) "Executando..." else "Executar: uname -a")
        }
        Spacer(Modifier.height(12.dp))
        Card(
            modifier = Modifier.fillMaxWidth().weight(1f),
            colors = CardDefaults.cardColors(containerColor = System2Colors.Surface)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                if (exitCode != null) Text("exit_code=$exitCode", color = System2Colors.TextSecondary, fontSize = 11.sp)
                Text(
                    text = stdout ?: "— aguardando execucao —",
                    color = System2Colors.TextPrimary, fontSize = 12.sp,
                    modifier = Modifier.fillMaxWidth()
                )
                if (stderr != null) {
                    Spacer(Modifier.height(8.dp))
                    Text("stderr: $stderr", color = System2Colors.PrimaryRed, fontSize = 11.sp)
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Voltar ao desktop") }
    }
}

@Composable
fun LauncherScreen(onBack: () -> Unit) {
    val ctx = LocalContext.current
    var apps by remember { mutableStateOf<List<InstalledApp>>(emptyList()) }
    var query by remember { mutableStateOf("") }
    LaunchedEffect(Unit) { apps = LauncherRepository.listInstalledApps(ctx) }
    val filtered = apps.filter { it.label.contains(query, ignoreCase = true) }
    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Text("Launcher — Apps Android", color = System2Colors.TextPrimary, fontSize = 20.sp)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = query, onValueChange = { query = it },
            label = { Text("Pesquisar") }, modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        if (filtered.isEmpty()) {
            Text("Nenhum aplicativo encontrado.", color = System2Colors.TextSecondary, fontSize = 13.sp)
        } else {
            androidx.compose.foundation.lazy.LazyColumn {
                items(filtered.size) { i ->
                    val app = filtered[i]
                    ListItem(
                        headlineContent = { Text(app.label, color = System2Colors.TextPrimary) },
                        supportingContent = { Text(app.packageName, color = System2Colors.TextSecondary, fontSize = 11.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { LauncherRepository.launchApp(ctx, app.packageName) }
                    )
                }
            }
        }
        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Voltar") }
    }
}

@Composable
fun MonitorScreen(onBack: () -> Unit) {
    val ctx = LocalContext.current
    val metrics = remember { SystemMonitor.collectHostMetrics(ctx) }
    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Text("Monitor do Sistema", color = System2Colors.TextPrimary, fontSize = 20.sp)
        Spacer(Modifier.height(8.dp))
        Text("Metricas reais do Android hospedeiro. Ubuntu/Termux/X11: via Bridge (tela Terminal).", color = System2Colors.TextSecondary, fontSize = 12.sp)
        Spacer(Modifier.height(12.dp))
        Text("CPU cores: ${metrics.cpuCores}", color = System2Colors.TextPrimary, fontSize = 13.sp)
        Text("RAM: ${metrics.availRamMb}/${metrics.totalRamMb} MB livres", color = System2Colors.TextPrimary, fontSize = 13.sp)
        Text("Armazenamento: ${"%.1f".format(metrics.freeStorageGb)}/${"%.1f".format(metrics.totalStorageGb)} GB livres", color = System2Colors.TextPrimary, fontSize = 13.sp)
        Text("Uptime: ${metrics.uptimeMin} min", color = System2Colors.TextPrimary, fontSize = 13.sp)
        Spacer(Modifier.height(16.dp))
        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Voltar") }
    }
}

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Text("Configuracoes", color = System2Colors.TextPrimary, fontSize = 20.sp)
        Spacer(Modifier.height(12.dp))
        Text("Papel de parede, posicao da barra, formato do relogio e perfil ativo serão persistidos aqui (Etapa 6).", color = System2Colors.TextSecondary, fontSize = 13.sp)
        Spacer(Modifier.height(16.dp))
        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Voltar") }
    }
}

@Composable
fun AppsScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Text("App Center + Arquivos", color = System2Colors.TextPrimary, fontSize = 20.sp)
        Spacer(Modifier.height(8.dp))
        Text("Importacao de APK, perfis logicos e gerenciador de arquivos completos: Etapa 6.", color = System2Colors.TextSecondary, fontSize = 13.sp)
        Spacer(Modifier.height(16.dp))
        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Voltar") }
    }
}
