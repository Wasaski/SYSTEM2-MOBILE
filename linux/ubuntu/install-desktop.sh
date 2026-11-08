#!/usr/bin/env bash
# SYSTEM2 MOBILE — instala ambiente grafico XFCE + Termux:X11
set -euo pipefail
echo "[SYSTEM2] Instalando pacotes graficos no Ubuntu..."
proot-distro login ubuntu -- apt-get update -y
proot-distro login ubuntu -- apt-get install -y \
  xfce4 xfce4-terminal thunar dbus-x11
echo "[SYSTEM2] XFCE instalado."
echo "[SYSTEM2] Instale o app 'Termux:X11' (F-Droid) e abra a tela Linux Desktop no SYSTEM2."
echo "[SYSTEM2] Para iniciar a sessao grafica:"
echo "  DISPLAY=:0 dbus-launch startxfce4 &"
echo "  (com o Termux:X11 rodando em :0)"
