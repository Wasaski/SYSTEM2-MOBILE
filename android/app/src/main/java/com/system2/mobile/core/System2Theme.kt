package com.system2.mobile.core

import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

object System2Colors {
    val Background = Color(0xFF090909)
    val BackgroundSecondary = Color(0xFF111111)
    val Surface = Color(0xFF191919)
    val PrimaryRed = Color(0xFFD71920)
    val SecondaryRed = Color(0xFF8F1016)
    val TextPrimary = Color(0xFFF2F2F2)
    val TextSecondary = Color(0xFF858585)
    val Border = Color(0xFF292929)
}

val System2ColorScheme = darkColorScheme(
    primary = System2Colors.PrimaryRed,
    onPrimary = System2Colors.TextPrimary,
    secondary = System2Colors.SecondaryRed,
    background = System2Colors.Background,
    surface = System2Colors.Surface,
    onBackground = System2Colors.TextPrimary,
    onSurface = System2Colors.TextPrimary
)
