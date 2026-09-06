package com.example.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class AppTheme(val displayName: String) {
    OXFORD_NAVY("Oxford Navy"),
    CAMBRIDGE_EMERALD("Cambridge Emerald"),
    NORDIC_OBSIDIAN("Nordic Obsidian")
}

private val OxfordNavyColorScheme = darkColorScheme(
    primary = OxfordNavyPrimary,
    onPrimary = Color(0xFF0A0F1D),
    primaryContainer = OxfordNavyPrimaryContainer,
    onPrimaryContainer = TextPrimary,
    secondary = OxfordNavyAccent,
    onSecondary = Color(0xFF1F1202),
    tertiary = OxfordNavyPrimary,
    background = OxfordNavyBackground,
    onBackground = TextPrimary,
    surface = OxfordNavySurface,
    onSurface = TextPrimary,
    surfaceVariant = OxfordNavyCard,
    onSurfaceVariant = TextSecondary,
    outline = TextMuted
)

private val CambridgeEmeraldColorScheme = darkColorScheme(
    primary = CambridgePrimary,
    onPrimary = Color(0xFF041611),
    primaryContainer = CambridgePrimaryContainer,
    onPrimaryContainer = TextPrimary,
    secondary = CambridgeAccent,
    onSecondary = Color(0xFF241C04),
    tertiary = CambridgePrimary,
    background = CambridgeBackground,
    onBackground = TextPrimary,
    surface = CambridgeSurface,
    onSurface = TextPrimary,
    surfaceVariant = CambridgeCard,
    onSurfaceVariant = TextSecondary,
    outline = TextMuted
)

private val NordicObsidianColorScheme = darkColorScheme(
    primary = ObsidianPrimary,
    onPrimary = Color(0xFF090A0C),
    primaryContainer = ObsidianPrimaryContainer,
    onPrimaryContainer = TextPrimary,
    secondary = ObsidianAccent,
    onSecondary = Color(0xFF0F172A),
    tertiary = ObsidianAccent,
    background = ObsidianBackground,
    onBackground = TextPrimary,
    surface = ObsidianSurface,
    onSurface = TextPrimary,
    surfaceVariant = ObsidianCard,
    onSurfaceVariant = TextSecondary,
    outline = TextMuted
)

@Composable
fun MyApplicationTheme(
    appTheme: AppTheme = AppTheme.OXFORD_NAVY,
    content: @Composable () -> Unit,
) {
    val colorScheme = when (appTheme) {
        AppTheme.OXFORD_NAVY -> OxfordNavyColorScheme
        AppTheme.CAMBRIDGE_EMERALD -> CambridgeEmeraldColorScheme
        AppTheme.NORDIC_OBSIDIAN -> NordicObsidianColorScheme
    }

    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

