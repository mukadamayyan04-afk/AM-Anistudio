package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class StudioThemeMode {
    CYBER_NEON,
    ROYAL_GOLD,
    MIDNIGHT_VIOLET
}

private val CyberNeonColorScheme = darkColorScheme(
    primary = CyberCyan,
    onPrimary = Color(0xFF04141A),
    primaryContainer = CyberSurfaceVariant,
    onPrimaryContainer = CyberCyan,
    secondary = CyberMagenta,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF381024),
    onSecondaryContainer = Color(0xFFFFB8D8),
    tertiary = CyberViolet,
    onTertiary = Color.White,
    background = CyberBackground,
    onBackground = CyberTextPrimary,
    surface = CyberSurface,
    onSurface = CyberTextPrimary,
    surfaceVariant = CyberSurfaceVariant,
    onSurfaceVariant = CyberTextSecondary,
    outline = CyberCardBorder,
    error = CyberRed,
    onError = Color.White
)

private val RoyalGoldColorScheme = darkColorScheme(
    primary = RoyalGoldPrimary,
    onPrimary = Color(0xFF241C04),
    primaryContainer = RoyalSurfaceVariant,
    onPrimaryContainer = RoyalGoldPrimary,
    secondary = RoyalGoldSecondary,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF3D2C10),
    onSecondaryContainer = RoyalGoldPrimary,
    tertiary = CyberGreen,
    onTertiary = Color.Black,
    background = RoyalBackground,
    onBackground = RoyalTextPrimary,
    surface = RoyalSurface,
    onSurface = RoyalTextPrimary,
    surfaceVariant = RoyalSurfaceVariant,
    onSurfaceVariant = RoyalTextSecondary,
    outline = RoyalCardBorder,
    error = CyberRed,
    onError = Color.White
)

private val MidnightVioletColorScheme = darkColorScheme(
    primary = MidnightVioletPrimary,
    onPrimary = Color.White,
    primaryContainer = MidnightSurfaceVariant,
    onPrimaryContainer = MidnightVioletPrimary,
    secondary = MidnightIndigo,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF281E57),
    onSecondaryContainer = Color(0xFFDDD6FE),
    tertiary = CyberCyan,
    onTertiary = Color.Black,
    background = MidnightBackground,
    onBackground = MidnightTextPrimary,
    surface = MidnightSurface,
    onSurface = MidnightTextPrimary,
    surfaceVariant = MidnightSurfaceVariant,
    onSurfaceVariant = MidnightTextSecondary,
    outline = MidnightCardBorder,
    error = CyberRed,
    onError = Color.White
)

@Composable
fun AMAniStudioTheme(
    themeMode: StudioThemeMode = StudioThemeMode.CYBER_NEON,
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeMode) {
        StudioThemeMode.CYBER_NEON -> CyberNeonColorScheme
        StudioThemeMode.ROYAL_GOLD -> RoyalGoldColorScheme
        StudioThemeMode.MIDNIGHT_VIOLET -> MidnightVioletColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
