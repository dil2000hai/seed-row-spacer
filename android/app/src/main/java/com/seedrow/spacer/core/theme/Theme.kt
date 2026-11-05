package com.seedrow.spacer.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val GardenLightScheme = lightColorScheme(
    primary = LeafPrimary,
    onPrimary = PaperBg,
    primaryContainer = SproutGreen,
    onPrimaryContainer = InkPrimary,
    secondary = SproutGreen,
    onSecondary = InkPrimary,
    secondaryContainer = PaperSurfaceAlt,
    onSecondaryContainer = InkPrimary,
    tertiary = RadishAccent,
    onTertiary = PaperBg,
    background = PaperBg,
    onBackground = InkPrimary,
    surface = PaperSurface,
    onSurface = InkPrimary,
    surfaceVariant = PaperSurfaceAlt,
    onSurfaceVariant = InkSecondary,
    outline = SageBorder,
    outlineVariant = SageBorder,
    error = RadishAccent,
    onError = PaperBg
)

private val GardenDarkScheme = darkColorScheme(
    primary = SproutGreen,
    onPrimary = NightSoil,
    primaryContainer = LeafPrimary,
    onPrimaryContainer = PaperBg,
    secondary = SageBorder,
    onSecondary = NightSoil,
    secondaryContainer = NightSurface,
    onSecondaryContainer = PaperBg,
    tertiary = RadishAccent,
    onTertiary = PaperBg,
    background = NightSoil,
    onBackground = PaperBg,
    surface = NightSurface,
    onSurface = PaperBg,
    surfaceVariant = NightSurface,
    onSurfaceVariant = SageBorder,
    outline = SageBorder,
    outlineVariant = InkSecondary,
    error = RadishAccent,
    onError = PaperBg
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) GardenDarkScheme else GardenLightScheme,
        typography = AppTypography,
        content = content
    )
}
