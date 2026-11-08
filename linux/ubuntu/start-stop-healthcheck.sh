#!/usr/bin/env bash
# SYSTEM2 MOBILE — start/stop/healthcheck do Ubuntu no Termux
set -euo pipefail
case "$1" in
  start)
    proot-distro login ubuntu -- bash -lc "echo SYSTEM2_START; uname -a"
    ;;
  stop)
    proot-distro login ubuntu -- bash -lc "echo SYSTEM2_STOP"
    ;;
  healthcheck)
    echo "== arquitetura =="; uname -m
    echo "== termux =="; which proot-distro && proot-distro list
    echo "== ubuntu uname =="; proot-distro login ubuntu -- uname -a
    ;;
  *)
    echo "Uso: $0 {start|stop|healthcheck}"
    exit 1
    ;;
esac
