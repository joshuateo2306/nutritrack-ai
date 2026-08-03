package com.Joshua_Teo_35514140.nutritrack.data

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.Joshua_Teo_35514140.nutritrack.utils.ui.theme.AppThemeType

object ThemeManager {
    private val _currentTheme = mutableStateOf(AppThemeType.SYSTEM)

    @Composable
    fun currentTheme(): AppThemeType {
        return _currentTheme.value
    }

    fun setTheme(theme: AppThemeType, context: Context) {
        _currentTheme.value = theme
        context.getSharedPreferences("app_theme", Context.MODE_PRIVATE)
            .edit()
            .putString("theme", theme.name)
            .apply()
    }

    fun initialize(context: Context) {
        val savedTheme = context.getSharedPreferences("app_theme", Context.MODE_PRIVATE)
            .getString("theme", "SYSTEM") ?: "SYSTEM"
        _currentTheme.value = AppThemeType.valueOf(savedTheme)
    }
}