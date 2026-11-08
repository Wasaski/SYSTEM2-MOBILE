package com.system2.mobile.desktop

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.system2.mobile.bridge.TermuxBridge
import com.system2.mobile.core.System2Colors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class DesktopAppEntry(
    val id: String,
    val title: String,
    val icon: @Composable () -> Unit = {}
)

@Composable
fun System2Desktop(
    onOpenTerminal: () -> Unit,
    onOpenLauncher: () -> Unit,
    onOpenMonitor: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenFiles: () -> Unit,
    onOpenLinuxDesktop: () -> Unit,
    onOpenTermux: () -> Unit
) {
    var timeText by remember { mutableStateOf("") }
    var dateText by remember { mutableStateOf("") }
    val context = LocalContext.current
    var termuxInstalled by remember { mutableStateOf<Boolean?>(null) }

    LaunchedEffect(Unit) {
        while (true) {
            val now = Date()
            timeText = SimpleDateFormat("HH:mm", Locale.getDefault()).format(now)
            dateText = SimpleDateFormat("EEE, dd MMM", Locale("pt", "BR")).format(now)
            kotlinx.coroutines.delay(1000)
        }
    }
    LaunchedEffect(Unit) {
        termuxInstalled = when (val r = TermuxBridge.detectTermux(context)) {
            is com.system2.mobile.core.S2Result.Ok -> r.value
            is com.system2.mobile.core.S2Result.Err -> false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(System2Colors.Background)
    ) {
        // Papel de parede minimalista: fundo escuro + sinal vermelho discreto
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0D0D0D))
        ) {
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .align(Alignment.Center)
                    .background(System2Colors.SecondaryRed.copy(alpha = 0.12f))
            )
        }

        Column(modifier = Modifier.fillMaxSize()) {
            // Barra superior: relogio + indicadores
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "SYSTEM2 MOBILE",
                    color = System2Colors.TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Column(horizontalAlignment = Alignment.End) {
                    Text(timeText, color = System2Colors.TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Medium)
                    Text(dateText, color = System2Colors.TextSecondary, fontSize = 12.sp)
                }
            }

            // Painel Termux / Ubuntu (Milestone 1)
            TermuxStatusCard(
                termuxInstalled = termuxInstalled,
                onOpenTermux = onOpenTermux,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(Modifier.height(16.dp))

            // Grade de atalhos do desktop
            val entries = listOf(
                Triple("terminal", "Terminal", onOpenTerminal),
                Triple("launcher", "Apps", onOpenLauncher),
                Triple("monitor", "Monitor", onOpenMonitor),
                Triple("files", "Arquivos", onOpenFiles),
                Triple("linux", "Linux Desktop", onOpenLinuxDesktop),
                Triple("settings", "Configuracoes", onOpenSettings)
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(entries) { (id, title, action) ->
                    DesktopTile(title = title, onClick = action)
                }
            }

            // Barra de tarefas inferior
            Taskbar(onOpenLauncher = onOpenLauncher, timeText = timeText)
        }
    }
}

@Composable
private fun TermuxStatusCard(
    termuxInstalled: Boolean?,
    onOpenTermux: () -> Unit,
    modifier: Modifier = Modifier
) {
    val ctx = LocalContext.current
    val (dotColor, statusText) = when (termuxInstalled) {
        true -> System2Colors.PrimaryRed to "Termux detectado"
        false -> Color(0xFF858585) to "Termux nao instalado — toque para instalar"
        null -> Color(0xFF858585) to "Verificando Termux..."
    }
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = System2Colors.Surface),
        onClick = {
            if (termuxInstalled == false) TermuxBridge.openTermuxInstallPage(ctx)
            else onOpenTermux()
        }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(dotColor, androidx.compose.foundation.shape.CircleShape)
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text("Ambiente Linux", color = System2Colors.TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Text(statusText, color = System2Colors.TextSecondary, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun DesktopTile(title: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().height(84.dp),
        colors = CardDefaults.cardColors(containerColor = System2Colors.Surface.copy(alpha = 0.85f)),
        onClick = onClick
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(title, color = System2Colors.TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun Taskbar(onOpenLauncher: () -> Unit, timeText: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(System2Colors.BackgroundSecondary)
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TextButton(onClick = onOpenLauncher) {
            Text("Aplicativos", color = System2Colors.TextPrimary, fontSize = 13.sp)
        }
        Text(timeText, color = System2Colors.TextSecondary, fontSize = 12.sp)
    }
}
