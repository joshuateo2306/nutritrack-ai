package com.Joshua_Teo_35514140.nutritrack.home

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.Joshua_Teo_35514140.nutritrack.home.ui.theme.Nutritrack_finalTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Joshua_Teo_35514140.nutritrack.data.Patient.PatientViewModel

import com.Joshua_Teo_35514140.nutritrack.data.gemini.GenAIViewModel
import com.Joshua_Teo_35514140.nutritrack.data.gemini.UiState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClinicianLoginScreen(navController: NavController) {
    var clinicianKey by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Clinician Login") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = clinicianKey,
                onValueChange = { clinicianKey = it },
                label = { Text("Clinician Key") },
                placeholder = { Text("Enter your clinician key") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (clinicianKey == "dollar-entry-apples") {
                        navController.navigate("clinician_dashboard")
                    } else {
                        Toast.makeText(context, "Invalid clinician key", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(),
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = "Clinician Login")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Clinician Login")
            }
        }
    }
}

@Composable
fun ClinicianDashboardScreen(
    innerPadding: PaddingValues,
    navController: NavController
) {
    val context = LocalContext.current
    val patientViewModel: PatientViewModel = viewModel(
        factory = PatientViewModel.PatientViewModelFactory(context)
    )
    val geminiViewmodel: GenAIViewModel = viewModel(
        factory = GenAIViewModel.GenAiViewModelFactory(context)
    )

    val uiState by geminiViewmodel.uiState.collectAsState()
    val allPatients by patientViewModel.allPatients.collectAsState(emptyList())
    val scrollState = rememberScrollState()

    // Compute averages
    val maleAverage by patientViewModel.maleAverage.collectAsState()
    val femaleAverage by patientViewModel.femaleAverage.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Clinician Dashboard",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            ScoreBox("Average HEIFA (Male)", maleAverage)
            ScoreBox("Average HEIFA (Female)", femaleAverage)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                geminiViewmodel.generateTipsFromPatients(allPatients)
            },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors()
        ) {
            Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Find Data Pattern")
        }

        Spacer(modifier = Modifier.height(24.dp))

        when (uiState) {
            is UiState.Loading -> CircularProgressIndicator()
            is UiState.Success -> (uiState as UiState.Success).tips.forEach { TipCard(it) }
            is UiState.Error -> Text("Error: ${(uiState as UiState.Error).errorMessage}", color = Color.Red)
            UiState.Initial -> {}
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(48.dp),
            colors = ButtonDefaults.buttonColors()
        ) {
            Text("Done")
        }
    }
}

@Composable
fun ScoreBox(label: String, score: Double) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .background(Color(0xFFF0F0F0), shape = RoundedCornerShape(16.dp))
            .border(
                width = 1.dp,
                color = Color.Gray,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.DarkGray
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = String.format("%.1f", score),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Composable
fun TipCard(tip: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = tip, fontSize = 14.sp)
        }
    }
}
