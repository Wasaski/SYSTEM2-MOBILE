#!/usr/bin/env bash
# SYSTEM2 MOBILE — bootstrap Ubuntu 24.04 ARM64 no Termux
# Executar DENTRO do Termux: bash bootstrap.sh
set -euo pipefail

DISTRO="ubuntu"
echo "[SYSTEM2] Verificando arquitetura..."
ARCH=$(uname -m)
if [ "$ARCH" != "aarch64" ]; then
  echo "[! ] Arquitetura nao suportada: $ARCH (esperado aarch64)"
  exit 1
fi

echo "[SYSTEM2] Verificando espaco..."
df -h "$PREFIX" || true

echo "[SYSTEM2] Instalando/atualizando proot-distro..."
pkg update -y && pkg install -y proot-distro

echo "[SYSTEM2] Instalando $DISTRO (progressivo, sem rootfs no APK)..."
proot-distro install "$DISTRO"

echo "[SYSTEM2] Configurando diretorios..."
proot-distro login "$DISTRO" -- mkdir -p /root/.config /tmp /var/tmp

echo "[SYSTEM2] Instalando ferramentas essenciais..."
proot-distro login "$DISTRO" -- apt-get update -y
proot-distro login "$DISTRO" -- apt-get install -y \
  bash coreutils curl wget git nano vim python3 python3-pip \
  nodejs npm openssh-client htop unzip zip build-essential

echo "[SYSTEM2] Healthcheck..."
proot-distro login "$DISTRO" -- uname -a
proot-distro login "$DISTRO" -- bash -lc "echo SYSTEM2_UBUNTU_OK"
echo "[SYSTEM2] Bootstrap concluido."
