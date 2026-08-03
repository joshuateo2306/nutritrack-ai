package com.Joshua_Teo_35514140.nutritrack.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.Joshua_Teo_35514140.nutritrack.data.ThemeManager
import com.Joshua_Teo_35514140.nutritrack.utils.ui.theme.AppThemeType

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)
private val PastelColorScheme = lightColorScheme(
    primary = Color(0xFFF8BBD0),  // Light pink
    secondary = Color(0xFFB2EBF2),  // Light blue
    tertiary = Color(0xFFDCEDC8),  // Light green
    background = Color(0xFFFFF9F9),
    surface = Color(0xFFFFF9F9),
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onTertiary = Color.Black
)

// Ocean Theme
private val OceanColorScheme = darkColorScheme(
    primary = Color(0xFF0097A7),  // Teal
    secondary = Color(0xFFB2EBF2),  // Light cyan
    tertiary = Color(0xFF4DD0E1),  // Light teal
    background = Color(0xFF001F29),
    surface = Color(0xFF001F29),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onTertiary = Color.Black
)

// Sunset Theme
private val SunsetColorScheme = darkColorScheme(
    primary = Color(0xFFFF7043),  // Deep orange
    secondary = Color(0xFFFFA000),  // Amber
    tertiary = Color(0xFFFFD180),  // Light orange
    background = Color(0xFF1A0000),
    surface = Color(0xFF1A0000),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onTertiary = Color.Black
)

@Composable
fun Nutritrack_finalTheme(
    content: @Composable () -> Unit
) {
    val themeType = ThemeManager.currentTheme()
    val systemDarkTheme = isSystemInDarkTheme()

    val colorScheme = when {
        themeType == AppThemeType.SYSTEM && systemDarkTheme -> DarkColorScheme
        themeType == AppThemeType.SYSTEM -> LightColorScheme
        themeType == AppThemeType.LIGHT -> LightColorScheme
        themeType == AppThemeType.DARK -> DarkColorScheme
        themeType == AppThemeType.PASTEL -> PastelColorScheme
        themeType == AppThemeType.OCEAN -> OceanColorScheme
        themeType == AppThemeType.SUNSET -> SunsetColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}