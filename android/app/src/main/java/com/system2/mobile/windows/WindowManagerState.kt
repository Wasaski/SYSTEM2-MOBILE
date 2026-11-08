package com.system2.mobile.windows

import androidx.compose.ui.unit.dp

/**
 * Window Manager nativo do SYSTEM2 (interno ao Compose).
 * Independente do XFCE/Termux:X11.
 */
data class System2Window(
    val id: String,
    val title: String,
    val appPackageOrRoute: String,
    var isMinimized: Boolean = false,
    var isMaximized: Boolean = false,
    var zIndex: Int = 0,
    var offsetX: Float = 0f,
    var offsetY: Float = 0f,
    var widthDp: Int = 780,
    var heightDp: Int = 520
)

class WindowManagerState {
    private val _windows = mutableListOf<System2Window>()
    val windows: List<System2Window> get() = _windows
    private var topZ = 0

    fun open(window: System2Window): System2Window {
        _windows.removeAll { it.id == window.id }
        window.zIndex = ++topZ
        _windows.add(window)
        return window
    }

    fun close(id: String) { _windows.removeAll { it.id == id } }
    fun minimize(id: String) { _windows.firstOrNull { it.id == id }?.isMinimized = true }
    fun restore(id: String) {
        _windows.firstOrNull { it.id == id }?.let {
            it.isMinimized = false
            it.zIndex = ++topZ
            _windows.sortBy { w -> w.zIndex }
        }
    }
    fun toggleMaximize(id: String) {
        _windows.firstOrNull { it.id == id }?.let { it.isMaximized = !it.isMaximized }
    }
    fun bringToFront(id: String) {
        _windows.firstOrNull { it.id == id }?.let {
            it.zIndex = ++topZ
            _windows.sortBy { w -> w.zIndex }
        }
    }
    fun visibleWindows(): List<System2Window> =
        _windows.filter { !it.isMinimized }.sortedBy { it.zIndex }
}
