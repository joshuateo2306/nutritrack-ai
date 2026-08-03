package com.Joshua_Teo_35514140.nutritrack.utils.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

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
// Pastel Theme
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
enum class AppThemeType {
    SYSTEM, LIGHT, DARK, PASTEL, OCEAN, SUNSET;

    companion object {
        fun fromName(name: String): AppThemeType {
            return values().firstOrNull { it.name == name } ?: SYSTEM
        }
    }
}

@Composable
fun Nutritrack_finalTheme(
    themeType: AppThemeType = AppThemeType.SYSTEM,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val systemDarkTheme = isSystemInDarkTheme()

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            when (themeType) {
                AppThemeType.SYSTEM -> if (systemDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
                AppThemeType.LIGHT -> dynamicLightColorScheme(context)
                AppThemeType.DARK -> dynamicDarkColorScheme(context)
                else -> getCustomColorScheme(themeType)
            }
        }

        else -> when (themeType) {
            AppThemeType.SYSTEM -> if (systemDarkTheme) DarkColorScheme else LightColorScheme
            AppThemeType.LIGHT -> LightColorScheme
            AppThemeType.DARK -> DarkColorScheme
            else -> getCustomColorScheme(themeType)
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

private fun getCustomColorScheme(themeType: AppThemeType): ColorScheme {
    return when (themeType) {
        AppThemeType.PASTEL -> PastelColorScheme
        AppThemeType.OCEAN -> OceanColorScheme
        AppThemeType.SUNSET -> SunsetColorScheme
        else -> LightColorScheme // Fallback
    }
}

