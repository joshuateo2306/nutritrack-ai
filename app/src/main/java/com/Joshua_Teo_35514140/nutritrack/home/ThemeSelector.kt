package com.Joshua_Teo_35514140.nutritrack.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import com.Joshua_Teo_35514140.nutritrack.utils.ui.theme.AppThemeType


@Composable
fun ThemeSelection(
    currentTheme: AppThemeType,
    onThemeSelected: (AppThemeType) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    SettingsRow(
        icon = Icons.Default.Edit,
        text = "App Theme",
        isAction = true,
        onClick = { showDialog = true }
    )

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Select Theme") },
            text = {
                Column {
                    ThemeOption("System Default", AppThemeType.SYSTEM, currentTheme, onThemeSelected)
                    ThemeOption("Light", AppThemeType.LIGHT, currentTheme, onThemeSelected)
                    ThemeOption("Dark", AppThemeType.DARK, currentTheme, onThemeSelected)
                    ThemeOption("Pastel", AppThemeType.PASTEL, currentTheme, onThemeSelected)
                    ThemeOption("Ocean", AppThemeType.OCEAN, currentTheme, onThemeSelected)
                    ThemeOption("Sunset", AppThemeType.SUNSET, currentTheme, onThemeSelected)
                }
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
private fun ThemeOption(
    name: String,
    type: AppThemeType,
    currentTheme: AppThemeType,
    onSelected: (AppThemeType) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelected(type) }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = currentTheme == type,
            onClick = { onSelected(type) }
        )
        Text(
            text = name,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}