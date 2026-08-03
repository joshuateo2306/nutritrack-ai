package com.Joshua_Teo_35514140.nutritrack.home

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.Joshua_Teo_35514140.nutritrack.data.AuthManager
import com.Joshua_Teo_35514140.nutritrack.data.FruityVice.FruitResponse
import com.Joshua_Teo_35514140.nutritrack.data.FruityVice.FruitViewModel
import com.Joshua_Teo_35514140.nutritrack.data.Patient.PatientViewModel
import com.Joshua_Teo_35514140.nutritrack.data.gemini.GenAIViewModel
import com.Joshua_Teo_35514140.nutritrack.data.gemini.UiState
import com.Joshua_Teo_35514140.nutritrack.home.ui.theme.Nutritrack_finalTheme


@Composable
fun NutriCoachScreen(innerPadding: PaddingValues) {
    val context = LocalContext.current

    val fruitViewModel: FruitViewModel =
        viewModel(factory = FruitViewModel.FruitViewModelFactory(context))
    val genAiViewModel: GenAIViewModel =
        viewModel(factory = GenAIViewModel.GenAiViewModelFactory(context))
    val patientViewModel: PatientViewModel =
        viewModel(factory = PatientViewModel.PatientViewModelFactory(context))

    val fruitState by fruitViewModel.fruit.collectAsState()
    val isLoading by fruitViewModel.isLoading.collectAsState()
    val errorMessage by fruitViewModel.errorMessage.collectAsState()

    val uiState by genAiViewModel.uiState.collectAsState()
    val generatedTip by genAiViewModel.motivationalTipState.collectAsState()
    val allTipsState by genAiViewModel.allTips.collectAsState()

    var fruitInput by rememberSaveable { mutableStateOf("") }
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var showDeleteConfirmation by rememberSaveable { mutableStateOf(false) }

    val userId = AuthManager.getUserId() ?: ""
    LaunchedEffect(Unit) {
        patientViewModel.getLoggedInPatient()
    }
    val patient = patientViewModel.currentPatient.collectAsState().value
    println(patient)
    val optimal = patientViewModel.isFruitIntakeOptimal()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "NutriCoach",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (!optimal) {
                FruitSearchSection(
                    fruitInput = fruitInput,
                    onFruitInputChange = { fruitInput = it },
                    onSearchClick = { fruitViewModel.loadFruit(fruitInput) },
                    isLoading = isLoading,
                    errorMessage = errorMessage,
                    fruitState = fruitState
                )

            } else {
                // Show a random motivational image
                val randomSeed = remember { (0..1000).random() }
                val imageUrl = "https://picsum.photos/seed/$randomSeed/400/300"
                Image(
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentDescription = "Motivational Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .align(Alignment.CenterHorizontally),
                    contentScale = ContentScale.Crop
                )
            }


            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (userId.isNotBlank()) {
                        genAiViewModel.generateMotivationalMessageFromPatient(patient)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Face, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Motivational Message (AI)", color = Color.White)
            }

            Spacer(modifier = Modifier.height(12.dp))

            when (val tipState = generatedTip) {
                is UiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }

                is UiState.Success -> {
                    val tip = tipState.tips.firstOrNull()
                    if (tip != null) {
                        Text(
                            text = tip,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            fontSize = 14.sp
                        )
                    }
                }

                is UiState.Error -> {
                    Text(
                        text = tipState.errorMessage,
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                else -> {}
            }

            Spacer(modifier = Modifier.height(12.dp))

            when (uiState) {
                is UiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))

                is UiState.Success -> {
                    val tips = (uiState as UiState.Success).tips
                    tips.forEachIndexed { index, tip ->
                        Text(
                            text = "${index + 1}. $tip",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            fontSize = 14.sp
                        )
                    }
                }

                is UiState.Error -> {
                    val error = (uiState as UiState.Error).errorMessage
                    Text(
                        text = error,
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                else -> {}
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    if (userId.isNotBlank()) {
                        genAiViewModel.loadMotivationalMessages(userId)
                        showDialog = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Show All Tips", color = Color.White)
            }
        }
        if (showDeleteConfirmation) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmation = false },
                title = { Text("Delete All Tips") },
                text = { Text("Are you sure you want to permanently delete all your saved tips?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDeleteConfirmation = false



                            genAiViewModel.deleteAllMessages(userId){ success ->
                                showDialog = !success
                                if (!success) {

                                }
                            }



                        }
                    ) {
                        Text("DELETE", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirmation = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Dialog: Show all motivational messages
        if (showDialog) {
            when (val state = allTipsState) {
                is UiState.Loading -> {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text("Loading") },
                        text = { CircularProgressIndicator() },
                        confirmButton = {}
                    )
                }

                is UiState.Success -> {
                    MotivationalTipsDialog(
                        messages = state.tips,
                        onDismiss = { showDialog = false },
                        onDeleteAll = { showDeleteConfirmation = true }
                    )
                }


                is UiState.Error -> {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text("Error") },
                        text = { Text(state.errorMessage) },
                        confirmButton = {
                            TextButton(onClick = { showDialog = false }) {
                                Text("OK")
                            }
                        }
                    )
                }

                else -> {}
            }
        }
    }
}

@Composable
fun FruitSearchSection(
    fruitInput: String,
    onFruitInputChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    isLoading: Boolean,
    errorMessage: String?,
    fruitState: FruitResponse?
) {
    Text("Fruit Name", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
    Spacer(modifier = Modifier.height(8.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = fruitInput,
            onValueChange = onFruitInputChange,
            placeholder = { Text("Enter fruit") },
            singleLine = true,
            shape = RoundedCornerShape(50),
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            onClick = onSearchClick,
            colors = ButtonDefaults.buttonColors(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Search, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(4.dp))
            Text("Details", color = Color.White)
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    when {
        isLoading -> CircularProgressIndicator()
        errorMessage != null -> Text(
            text = errorMessage,
            color = Color.Red,

        )
        fruitState != null -> FruitDetailsGrid(fruit = fruitState)
    }
}

@Composable
fun MotivationalTipsDialog(
    messages: List<String>,
    onDismiss: () -> Unit,
    onDeleteAll: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 4.dp,
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.65f)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "AI Tips",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                messages.forEach { message ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF6F6F6)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {
                        Text(
                            text = message,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Delete All Button
                    TextButton(
                        onClick = onDeleteAll,

                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            "Delete All",
                            color = Color.Red,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Done Button (unchanged)
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Done", color = Color.White)
                    }
                }
            }
        }
    }
}


@Composable
fun FruitDetailsGrid(fruit: FruitResponse) {
    val data = listOf(
        "family" to fruit.family,
        "calories" to fruit.nutritions.calories.toString(),
        "fat" to fruit.nutritions.fat.toString(),
        "sugar" to fruit.nutritions.sugar.toString(),
        "carbohydrates" to fruit.nutritions.carbohydrates.toString(),
        "protein" to fruit.nutritions.protein.toString()
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        data.forEach { (label, value) ->
            FruitDataRow(label, value)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun FruitDataRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label :",
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp
        )
        Text(
            text = value,
            fontSize = 15.sp
        )
    }
}