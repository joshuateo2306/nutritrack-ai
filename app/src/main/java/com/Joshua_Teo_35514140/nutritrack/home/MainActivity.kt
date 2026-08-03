package com.Joshua_Teo_35514140.nutritrack.home

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.Joshua_Teo_35514140.nutritrack.R
import com.Joshua_Teo_35514140.nutritrack.data.Patient.PatientViewModel
import com.Joshua_Teo_35514140.nutritrack.data.ThemeManager
import com.Joshua_Teo_35514140.nutritrack.screenViewmodel.RegistrationViewmodel
import com.Joshua_Teo_35514140.nutritrack.screenViewmodel.SettingsViewModel
import com.Joshua_Teo_35514140.nutritrack.ui.theme.Nutritrack_finalTheme
import com.Joshua_Teo_35514140.nutritrack.utils.CSVReader
import com.Joshua_Teo_35514140.nutritrack.utils.ui.theme.AppThemeType
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPrefs = getSharedPreferences("app_theme", MODE_PRIVATE)
        val savedTheme = sharedPrefs.getString("theme", "SYSTEM") ?: "SYSTEM"
        ThemeManager.initialize(this)


        setContent {


            Nutritrack_finalTheme {
                val navController = rememberNavController()
                val viewModel: PatientViewModel = viewModel(
                    factory = PatientViewModel.PatientViewModelFactory(application)
                )

                val sharedPreferences: SharedPreferences =
                    applicationContext.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

                val isFirstLaunch = sharedPreferences.getBoolean("isFirstLaunch", true)
                val loggedInUserId = sharedPreferences.getString("logged_in_user_id", null)
                val isLoggedIn = loggedInUserId != null




                // Load CSV data on first launch
                LaunchedEffect(isFirstLaunch) {
                    if (isFirstLaunch) {
                        val patientsFromCSV = CSVReader.readPatientsFromCsv(applicationContext)
                        Log.d("UserIdsDebug", "Loaded CSV data: ${patientsFromCSV.size} patients found.")
                        for (patient in patientsFromCSV) {
                            Log.d("UserIdsDebug", "Inserting patient: ${patient.UserID}")
                            viewModel.insert(patient)
                        }
                        sharedPreferences.edit().putBoolean("isFirstLaunch", false).apply()
                    }
                }

                LaunchedEffect(loggedInUserId) {
                    loggedInUserId?.let {
                        viewModel.continueSession(it)
                    }
                }

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .navigationBarsPadding(), // Handles gesture/navigation bar space on bottom

                    bottomBar = {
                        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
                        val showBottomBar = currentRoute in listOf("home", "insights", "nutricoach", "settings")

                        if (showBottomBar) {
                            MyBottomAppBar(navController)
                        }
                    }

                ) { innerPadding ->

                    val loginStatus by viewModel.loginStatus.collectAsState()

                    val actualStartDestination = when {
                        isLoggedIn && loginStatus == true -> "home"
                        else -> "landing"
                    }
                    NavHost(
                        navController = navController,
                        startDestination = actualStartDestination,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        // Auth flow
                        composable("landing") { LandingScreen(navController) }
                        composable("register") {
                            RegistrationScreen(navController, viewModel)
                        }
                        composable("login") {
                            LoginScreen(navController, viewModel) { userId ->
                                sharedPreferences.edit()
                                    .putString("logged_in_user_id", userId)
                                    .apply()
                                navController.navigate("home") {
                                    popUpTo("landing") { inclusive = true }
                                }
                            }
                        }

                        // Main app screens
                        composable("upgrade_to_premium") {
                            UpgradeToPremiumScreen(navController)
                        }
                        composable("questionnaire") { FoodIntakeQuestionnaireScreen(innerPadding, navController) }
                        composable("home") { HomeScreen(innerPadding, navController) }
                        composable("insights") { InsightsScreen(innerPadding, navController) }
                        composable("nutricoach") { NutriCoachScreen(innerPadding) }
                        composable("settings") { SettingsScreen(navController, innerPadding) }
                        composable("clinician_login") { ClinicianLoginScreen(navController) }
                        composable("clinician_dashboard") { ClinicianDashboardScreen(innerPadding, navController) }
                    }
                }
            }
        }
    }
}


@Composable
fun LandingScreen(navController: NavController) {
    val context = LocalContext.current  // Get context for Intent

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "NutriTrack",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        Image(
            painter = painterResource(id = R.drawable.fat),
            contentDescription = "NutriTrack Logo",
            modifier = Modifier.size(100.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "This app provides general health and nutrition information for educational purposes only. " +
                    "It is not intended as medical advice, diagnosis, or treatment. Always consult a qualified healthcare professional " +
                    "before making any changes to your diet, exercise, or health regimen. Use this app at your own risk.\n\n" +
                    "If you'd like to see an Accredited Practicing Dietitian (APD), please visit the Monash Nutrition/Dietetics Clinic:\n" +
                    "https://www.monash.edu/medicine/scs/nutrition/clinics/nutrition",
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp),
            fontStyle = FontStyle.Italic
        )

        Spacer(modifier = Modifier.height(24.dp))
        //Starts login screen upon click
        Button(
            onClick = {

                navController.navigate("login")
            },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.padding(horizontal = 32.dp),
            colors = ButtonDefaults.buttonColors()
        ) {
            Text(text = "Login", color = Color.White, fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Designed by Joshua Teo Jia Shuo (35514140)",
            fontSize = 12.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    navController: NavController,
    viewModel: PatientViewModel,
    registrationViewModel: RegistrationViewmodel = viewModel()
) {
    val context = LocalContext.current
    val userIds by viewModel.allPatientsId.collectAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()

    val selectedUserId = registrationViewModel.selectedUserId
    val phoneInput = registrationViewModel.phoneInput
    val nameInput = registrationViewModel.nameInput
    val passwordInput = registrationViewModel.passwordInput
    val confirmPasswordInput = registrationViewModel.confirmPasswordInput
    val expanded = registrationViewModel.expanded

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Register",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 24.dp)
        )

        ExposedDropdownMenuBox(
            expanded = expanded.value,
            onExpandedChange = { expanded.value = it }
        ) {
            OutlinedTextField(
                value = selectedUserId.value,
                onValueChange = {},
                readOnly = true,
                label = { Text("My Id (Provided by your Clinician)", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 18.sp),
                trailingIcon = {
                    Icon(
                        imageVector = if (expanded.value) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                        contentDescription = null
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedLeadingIconColor = Color.DarkGray,
                    unfocusedLeadingIconColor = Color.DarkGray,
                    focusedIndicatorColor = Color.DarkGray,
                    unfocusedIndicatorColor = Color.DarkGray,
                    unfocusedTextColor = Color.DarkGray,
                    focusedTextColor = Color.DarkGray,
                    disabledTextColor = Color.DarkGray,
                    disabledLabelColor = Color.DarkGray,
                    focusedLabelColor = Color.DarkGray,
                    unfocusedLabelColor = Color.DarkGray,
                ),
                shape = RoundedCornerShape(16.dp)
            )

            ExposedDropdownMenu(
                expanded = expanded.value,
                onDismissRequest = { expanded.value = false }
            ) {
                userIds.forEach { user ->
                    DropdownMenuItem(
                        text = { Text(user, fontSize = 24.sp) },
                        onClick = {
                            selectedUserId.value = user
                            expanded.value = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = phoneInput.value,
            onValueChange = { phoneInput.value = it },
            label = { Text("Phone Number", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 18.sp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = fieldColors()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = nameInput.value,
            onValueChange = { nameInput.value = it },
            label = { Text("Full Name", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 18.sp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            shape = RoundedCornerShape(16.dp),
            colors = fieldColors()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = passwordInput.value,
            onValueChange = { passwordInput.value = it },
            label = { Text("Password", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
            visualTransformation = PasswordVisualTransformation(),
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 18.sp),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = fieldColors()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = confirmPasswordInput.value,
            onValueChange = { confirmPasswordInput.value = it },
            label = { Text("Confirm Password", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
            visualTransformation = PasswordVisualTransformation(),
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 18.sp),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = fieldColors()
        )

        Spacer(Modifier.height(16.dp))

        Text(
            "This app is only for pre-registered users.\nPlease enter your ID, phone number and password to claim your account.",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                coroutineScope.launch {
                    val patient = viewModel.getPatientByUserId(selectedUserId.value)
                    if (patient != null) {
                        when {
                            patient.password != "default" -> {
                                Toast.makeText(context, "User has already registered", Toast.LENGTH_SHORT).show()
                            }
                            patient.PhoneNumber != phoneInput.value -> {
                                Toast.makeText(context, "Phone number doesn't match", Toast.LENGTH_SHORT).show()
                            }
                            passwordInput.value != confirmPasswordInput.value -> {
                                Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                viewModel.updatePassword(selectedUserId.value, passwordInput.value)
                                viewModel.updateName(selectedUserId.value, nameInput.value)
                                Toast.makeText(context, "Registered successfully!", Toast.LENGTH_SHORT).show()
                                navController.navigate("login")
                            }
                        }
                    } else {
                        Toast.makeText(context, "Invalid user", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors()
        ) {
            Text("Register", fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { navController.navigate("login") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors()
        ) {
            Text("Login", fontSize = 18.sp)
        }
    }
}

@Composable
private fun fieldColors() = TextFieldDefaults.colors(
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White,
    focusedIndicatorColor = Color.DarkGray,
    unfocusedIndicatorColor = Color.DarkGray,
    focusedTextColor = Color.DarkGray,
    unfocusedTextColor = Color.DarkGray,
    focusedLabelColor = Color.DarkGray,
    unfocusedLabelColor = Color.DarkGray,
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: PatientViewModel,
    context: Context = LocalContext.current,
    onLoginSuccess: (String) -> Unit
) {
    val userIds by viewModel.registeredUserIds.collectAsState()
    var selectedUserId by rememberSaveable { mutableStateOf("") }
    var expanded by rememberSaveable { mutableStateOf(false) }
    var password by rememberSaveable { mutableStateOf("") }

    val loginStatus by viewModel.loginStatus.collectAsState(initial = null)

    val scrollState = rememberScrollState()

    val sharedPreferences = remember {
        context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    }

    LaunchedEffect(loginStatus) {
        when (loginStatus) {
            true -> {
                // Save the logged-in user ID to SharedPreferences
                sharedPreferences.edit()
                    .putString("logged_in_user_id", selectedUserId)
                    .apply()

                // Call the success callback
                onLoginSuccess(selectedUserId)

                // Navigate to QuestionnaireScreen
                navController.navigate("questionnaire")
            }
            false -> {
                Toast.makeText(context, "Invalid credentials. Please try again.", Toast.LENGTH_SHORT).show()
            }
            null -> Unit
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // LOGIN HEADER
        Text(
            text = "Login",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // USER ID DROPDOWN
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = selectedUserId,
                onValueChange = {},
                readOnly = true,
                label = {
                    Text(
                        "My Id (Provided by your Clinician)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 18.sp),
                trailingIcon = {
                    Icon(
                        imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Expand/Collapse"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedLeadingIconColor = Color.DarkGray,
                    unfocusedLeadingIconColor = Color.DarkGray,
                    focusedIndicatorColor = Color.DarkGray,
                    unfocusedIndicatorColor = Color.DarkGray,
                    unfocusedTextColor = Color.DarkGray,
                    focusedTextColor = Color.DarkGray,
                    disabledTextColor = Color.DarkGray,
                    disabledLabelColor = Color.DarkGray,
                    focusedLabelColor = Color.DarkGray,
                    unfocusedLabelColor = Color.DarkGray,
                ),
                shape = RoundedCornerShape(16.dp)
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                userIds.forEach { user ->
                    DropdownMenuItem(
                        text = { Text(user, fontSize = 24.sp) },
                        onClick = {
                            selectedUserId = user
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // PASSWORD FIELD
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 18.sp),
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color.DarkGray,
                unfocusedIndicatorColor = Color.DarkGray,
                unfocusedTextColor = Color.DarkGray,
                focusedTextColor = Color.DarkGray,
                disabledTextColor = Color.DarkGray,
                disabledLabelColor = Color.DarkGray,
                focusedLabelColor = Color.DarkGray,
                unfocusedLabelColor = Color.DarkGray,
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // INSTRUCTIONAL TEXT
        Text(
            text = "This app is only for pre-registered users. Please enter your ID, phone number and password to claim your account.",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.DarkGray,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // CONTINUE BUTTON
        Button(
            onClick = { viewModel.login(selectedUserId, password) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors()
        ) {
            Text("Continue", fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // REGISTER BUTTON
        Button(
            onClick = { navController.navigate("register") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors()
        ) {
            Text("Register", fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}