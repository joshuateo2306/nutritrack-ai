package com.Joshua_Teo_35514140.nutritrack.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.Joshua_Teo_35514140.nutritrack.data.Patient.PatientViewModel
import com.Joshua_Teo_35514140.nutritrack.home.ui.theme.Nutritrack_finalTheme

@Composable
fun InsightsScreen(
    innerPadding: PaddingValues,navController: NavHostController
) {
    val context = LocalContext.current
    val patientViewModel: PatientViewModel = viewModel(
        factory = PatientViewModel.PatientViewModelFactory(context)
    )


    val patient by patientViewModel.currentPatient.collectAsState()


    // Trigger loading of data
    LaunchedEffect(Unit) {
        patientViewModel.getLoggedInPatient()
    }



    val currentPatient = patient

    Log.d("Test current patient", "$currentPatient")
    if (currentPatient != null) {
        val isMale = currentPatient.Sex.equals("Male", ignoreCase = true)
        val totalScore = if (isMale) currentPatient.heifaTotalScoreMale else currentPatient.heifaTotalScoreFemale

        val categories = listOf(
            "Discretionary" to ((if (isMale) currentPatient.discretionaryScoreMale else currentPatient.discretionaryScoreFemale) to 10),
            "Vegetables" to ((if (isMale) currentPatient.vegetablesScoreMale else currentPatient.vegetablesScoreFemale) to 10),
            "Fruit" to ((if (isMale) currentPatient.fruitScoreMale else currentPatient.fruitScoreFemale) to 10),
            "Grains & Cereals" to ((if (isMale) currentPatient.grainsScoreMale else currentPatient.grainsScoreFemale) to 5),
            "Whole Grains" to ((if (isMale) currentPatient.wholegrainsScoreMale else currentPatient.wholegrainsScoreFemale) to 5),
            "Meat & Alternatives" to ((if (isMale) currentPatient.meatScoreMale else currentPatient.meatScoreFemale) to 10),
            "Dairy" to ((if (isMale) currentPatient.dairyScoreMale else currentPatient.dairyScoreFemale) to 10),
            "Sodium" to ((if (isMale) currentPatient.sodiumScoreMale else currentPatient.sodiumScoreFemale) to 10),
            "Alcohol" to ((if (isMale) currentPatient.alcoholScoreMale else currentPatient.alcoholScoreFemale) to 5),
            "Water" to ((if (isMale) currentPatient.waterScoreMale else currentPatient.waterScoreFemale) to 5),
            "Sugar" to ((if (isMale) currentPatient.sugarScoreMale else currentPatient.sugarScoreFemale) to 10),
            "Saturated Fat" to ((if (isMale) currentPatient.saturatedFatScoreMale else currentPatient.saturatedFatScoreFemale) to 5),
            "Unsaturated Fat" to ((if (isMale) currentPatient.unsaturatedFatScoreMale else currentPatient.unsaturatedFatScoreFemale) to 5)
        )


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Food Quality Score: $totalScore / 100",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 8.dp),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Here’s how your food intake compares to national guidelines.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp),
                textAlign = TextAlign.Center
            )

            categories.forEach { (category, scorePair) ->
                val (score, max) = scorePair
                Text(
                    text = "$category: $score / $max",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                LinearProgressIndicator(
                    progress = score.toFloat() / max,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    color = Color(0xFF6A1B9A)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            Button(
                onClick = { navController.navigate("nutricoach") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                colors = ButtonDefaults.buttonColors()
            ) {
                Text("Improve My Diet!")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val shareMessage = "My Food Quality Score is $totalScore / 100! \nHow does your diet compare?"
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, shareMessage)
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(sendIntent, "Share your score via:"))
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors()
            ) {
                Text("Share My Score")
            }
        }
    }
}