package com.Joshua_Teo_35514140.nutritrack.screenViewmodel

import android.app.Application
import android.content.Context
import android.content.res.Resources.Theme
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Joshua_Teo_35514140.nutritrack.utils.ui.theme.AppThemeType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val sharedPrefs = application.getSharedPreferences("app_theme", Context.MODE_PRIVATE)
    private val themeKey = "theme_preference"

    private val _currentTheme = MutableStateFlow(AppThemeType.SYSTEM)
    val currentTheme: StateFlow<AppThemeType> = _currentTheme

    init {
        val savedThemeName = sharedPrefs.getString(themeKey, null)
        _currentTheme.value = if (savedThemeName != null) {
            AppThemeType.fromName(savedThemeName)
        } else {
            AppThemeType.SYSTEM
        }
    }

    fun setTheme(theme: AppThemeType) {
        _currentTheme.value = theme

        sharedPrefs.edit()
            .putString(themeKey, theme.name)
            .apply()
    }


}