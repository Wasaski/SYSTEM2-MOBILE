package com.system2.mobile.linux

import com.system2.mobile.core.AppLogger

/**
 * SYSTEM2 LINUX — gerencia o ambiente Ubuntu 24.04 ARM64 via Termux.
 * O rootfs NAO vai no APK; a instalacao e progressiva via proot-distro.
 */
object UbuntuManager {

    const val DISTRO = "ubuntu"
    const val UBUNTU_VERSION = "24.04 LTS ARM64"

    sealed interface UbuntuState {
        data object Unknown : UbuntuState
        data object NotInstalled : UbuntuState
        data object Installing : UbuntuState
        data object Installed : UbuntuState
        data class Running(val pidInfo: String = "") : UbuntuState
        data class Stopped(val reason: String = "") : UbuntuState
        data class Error(val message: String) : UbuntuState
    }

    fun installCheckCommand(): Pair<String, List<String>> =
        "proot-distro" to listOf("list")

    fun bootstrapCommand(): Pair<String, List<String>> =
        "proot-distro" to listOf("install", DISTRO)

    fun startCommand(): Pair<String, List<String>> =
        "proot-distro" to listOf("login", DISTRO, "--", "bash", "-lc", "echo SYSTEM2_UBUNTU_OK")

    fun unameCommand(): Pair<String, List<String>> =
        "proot-distro" to listOf("login", DISTRO, "--", "uname", "-a")

    fun shutdownCommand(): Pair<String, List<String>> =
        "proot-distro" to listOf("login", DISTRO, "--", "shutdown", "-h", "now")

    fun validateState(installed: Boolean?, running: Boolean?): UbuntuState {
        return when {
            installed == null -> UbuntuState.Unknown
            !installed -> UbuntuState.NotInstalled
            running == true -> UbuntuState.Running()
            installed && running == false -> UbuntuState.Stopped()
            else -> UbuntuState.Installed
        }
    }

    fun logState(s: UbuntuState) = AppLogger.i("Ubuntu state=$s")
}
