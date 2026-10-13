package com.example.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Default Luxury Dark Color Scheme for Stylek
private val DarkColorScheme = darkColorScheme(
    primary = StylekPrimaryBlue,
    onPrimary = Color.White,
    primaryContainer = StylekCardElevated,
    onPrimaryContainer = Color.White,
    secondary = StylekSecondaryBlue,
    onSecondary = Color.White,
    secondaryContainer = StylekBorder,
    onSecondaryContainer = StylekTextSecondary,
    tertiary = StylekGold,
    onTertiary = StylekBackground,
    background = StylekBackground,
    onBackground = StylekTextPrimary,
    surface = StylekCard,
    onSurface = StylekTextPrimary,
    surfaceVariant = StylekCardElevated,
    onSurfaceVariant = StylekTextSecondary,
    outline = StylekBorder,
    outlineVariant = StylekBorderSubtle
)

// In case light theme is toggled, maintain the dark luxury feel or soft contrast
private val LightColorScheme = DarkColorScheme

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Stylek is a Dark Premium Fashion App
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = AppTypography,
        content = content
    )
}
