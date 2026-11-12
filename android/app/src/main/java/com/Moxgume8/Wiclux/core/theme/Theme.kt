package com.Moxgume8.Wiclux.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val IceLakeColorScheme = lightColorScheme(
    primary = AccentCyan,
    onPrimary = TextOnWater,
    primaryContainer = AccentDeep,
    onPrimaryContainer = TextOnWater,
    secondary = AccentDeep,
    onSecondary = TextOnWater,
    secondaryContainer = IceCard,
    onSecondaryContainer = TextOnIce,
    tertiary = MintHighlight,
    onTertiary = DeepWater,
    background = IceSurface,
    onBackground = TextOnIce,
    surface = IceSurface,
    onSurface = TextOnIce,
    surfaceVariant = IceCard,
    onSurfaceVariant = TextSecondary,
    outline = IceBorder,
    outlineVariant = IceMuted,
    error = WarnAmber,
    onError = DeepWater
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = IceLakeColorScheme,
        typography = AppTypography,
        content = content
    )
}
