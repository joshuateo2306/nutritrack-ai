package com.Joshua_Teo_35514140.nutritrack.home

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.Joshua_Teo_35514140.nutritrack.data.AuthManager
import com.Joshua_Teo_35514140.nutritrack.data.Patient.PatientViewModel
import com.Joshua_Teo_35514140.nutritrack.data.ThemeManager
import com.Joshua_Teo_35514140.nutritrack.screenViewmodel.SettingsViewModel
import com.Joshua_Teo_35514140.nutritrack.utils.ui.theme.AppThemeType

@Composable
fun SettingsScreen(navController: NavController, innerPadding: PaddingValues) {
    val context = LocalContext.current
    val patientViewModel: PatientViewModel = viewModel(
        factory = PatientViewModel.PatientViewModelFactory(context)
    )
    val currentPatient by patientViewModel.currentPatient.collectAsState()

    val isPremium = currentPatient?.isPremium

    val Name = currentPatient?.Name

    val settingViewmodel: SettingsViewModel = viewModel(

    )

    val currentTheme by settingViewmodel.currentTheme.collectAsState()

    val userId = currentPatient?.UserID

    var showNameDialog by remember { mutableStateOf(false) }

    var showLogoutDialog by remember { mutableStateOf(false) }



    LaunchedEffect(Unit) {
        patientViewModel.getLoggedInPatient()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp)
    ) {
        // Settings Title
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ACCOUNT Header
        Text(
            text = "ACCOUNT",
            style = MaterialTheme.typography.labelMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(8.dp))

        SettingsRow(
            icon = Icons.Default.Person,
            text = Name.toString(),
            isEditable = isPremium?: false,
            onClick = { showNameDialog = true }
        )

        if (showNameDialog && isPremium?: false) {
            NameEditDialog(
                currentName = Name.toString(),
                onDismiss = { showNameDialog = false },
                onSave = { newName ->
                    patientViewModel.updateName(userId.toString(), newName)
                    showNameDialog = false
                }
            )
        }

        currentPatient?.let { user ->

            SettingsRow(icon = Icons.Default.Phone, text = user.PhoneNumber)
            SettingsRow(icon = Icons.Default.AccountCircle, text = user.UserID)
        }

        Divider(modifier = Modifier.padding(vertical = 16.dp))

        // OTHER SETTINGS Header
        Text(
            text = "OTHER SETTINGS",
            style = MaterialTheme.typography.labelMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(8.dp))

        SettingsRow(
            icon = Icons.Default.ExitToApp,
            text = "Logout",
            isAction = true,
            onClick = { showLogoutDialog = true }
        )

        SettingsRow(
            icon = Icons.Default.Person,
            text = "Clinician Login",
            isAction = true,
            onClick = { navController.navigate("clinician_login") }
        )


        Spacer(modifier = Modifier.height(32.dp))
        if (!(isPremium ?: false)){
            GoPremiumButton(navController = navController, modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp))


        }

        if (isPremium ?: false) {
            ThemeSelection(
                currentTheme = currentTheme,
                onThemeSelected = { newTheme ->
                    ThemeManager.setTheme(newTheme, context)
                }
            )
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Confirm Logout") },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                TextButton(onClick = {
                    // Get SharedPreferences instance
                    val sharedPreferences = context.getSharedPreferences(
                        "app_preferences",
                        Context.MODE_PRIVATE
                    )
                    // Clear the logged-in user ID
                    sharedPreferences.edit()
                        .remove("logged_in_user_id")
                        .apply()

                    ThemeManager.setTheme(AppThemeType.LIGHT, context)

                    // Call ViewModel logout

                    patientViewModel.logout()

                    // Restart the MainActivity to reset navigation flow
                    val intent = Intent(context, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    context.startActivity(intent)
                    (context as? Activity)?.finish()
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("No")
                }
            }
        )
    }


}



@Composable
fun SettingsRow(
    icon: ImageVector,
    text: String,
    isEditable: Boolean = false,
    isAction: Boolean = false,  // New parameter for action items
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.weight(1f))
        when {
            isEditable -> Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit",
                tint = MaterialTheme.colorScheme.primary
            )
            isAction && onClick != null -> Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Go"
            )
            // Else case shows no icon
        }
    }
}

@Composable
fun NameEditDialog(
    currentName: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var newName by remember { mutableStateOf(currentName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Name") },
        text = {
            TextField(
                value = newName,
                onValueChange = { newName = it },
                label = { Text("Your Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(newName) },
                enabled = newName.isNotBlank() && newName != currentName
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun GoPremiumButton(navController: NavController, modifier: Modifier = Modifier) {
    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(Color(0xFFFFC107), Color(0xFFFF9800))
    )

    Button(
        onClick = { navController.navigate("upgrade_to_premium") },
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues(),
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(brush = gradientBrush, shape = RoundedCornerShape(20.dp))
            .border(2.dp, Color(0xFFFFD700), RoundedCornerShape(20.dp))
            .shadow(6.dp, RoundedCornerShape(20.dp), clip = false)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Premium Icon",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Go Premium",
                fontSize = 18.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
