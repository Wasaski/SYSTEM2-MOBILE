package com.system2.mobile.settings

data class System2Settings(
    val wallpaperName: String = "system2-default",
    val taskbarPosition: String = "bottom",
    val clockFormat24h: Boolean = true,
    val activeProfile: String = "Geral",
    val wifiOnlyInstall: Boolean = true
)

object SettingsStore {
    private const val FILE = "system2_settings"
    private var cache: System2Settings? = null

    fun load(context: android.content.Context): System2Settings {
        cache?.let { return it }
        val prefs = context.getSharedPreferences(FILE, android.content.Context.MODE_PRIVATE)
        return System2Settings(
            wallpaperName = prefs.getString("wallpaper", "system2-default") ?: "system2-default",
            taskbarPosition = prefs.getString("taskbar_pos", "bottom") ?: "bottom",
            clockFormat24h = prefs.getBoolean("clock24", true),
            activeProfile = prefs.getString("profile", "Geral") ?: "Geral",
            wifiOnlyInstall = prefs.getBoolean("wifi_only", true)
        ).also { cache = it }
    }

    fun save(context: android.content.Context, s: System2Settings) {
        cache = s
        context.getSharedPreferences(FILE, android.content.Context.MODE_PRIVATE).edit()
            .putString("wallpaper", s.wallpaperName)
            .putString("taskbar_pos", s.taskbarPosition)
            .putBoolean("clock24", s.clockFormat24h)
            .putString("profile", s.activeProfile)
            .putBoolean("wifi_only", s.wifiOnlyInstall)
            .apply()
    }
}
