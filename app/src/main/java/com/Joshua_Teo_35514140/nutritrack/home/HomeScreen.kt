package com.Joshua_Teo_35514140.nutritrack.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.Joshua_Teo_35514140.nutritrack.ui.theme.Nutritrack_finalTheme
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.Joshua_Teo_35514140.nutritrack.R
import com.Joshua_Teo_35514140.nutritrack.data.Patient.PatientViewModel




// Composable function for displaying the Home screen.
@Composable
fun HomeScreen(innerPadding: PaddingValues, navController: NavHostController) {
    val context = LocalContext.current
    val patientViewModel: PatientViewModel = viewModel(
        factory = PatientViewModel.PatientViewModelFactory(context)
    )
    // Trigger loading of data
    LaunchedEffect(Unit) {
        patientViewModel.getLoggedInPatient()
    }
    val currentPatient = patientViewModel.currentPatient.collectAsState().value
    Log.d("The current patient", "$currentPatient")


    val userName = currentPatient?.Name ?: "User" // Default if null

    val scoreStr = if (currentPatient?.Sex?.equals("Male", ignoreCase = true) == true)
        currentPatient?.heifaTotalScoreMale
    else
        currentPatient?.heifaTotalScoreFemale


    Column(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())

    ) {
        GreetingSection(userName = userName, navController)
        FoodQualityImageSection()
        MyScoreSection(score = scoreStr, navController = navController)
        FoodQualityScoreInfo()
    }
}



// Composable for creating the bottom navigation bar
@Composable
fun MyBottomAppBar(navController: NavHostController) {
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination?.route
    val items = listOf("home", "insights", "nutricoach", "settings")

    NavigationBar(modifier = Modifier.height(56.dp)) {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    when (item) {
                        "home" -> Icon(Icons.Filled.Home, contentDescription = "Home")
                        "insights" -> Icon(Icons.Filled.Face, contentDescription = "Insights")
                        "nutricoach" -> Icon(Icons.Filled.Star, contentDescription = "NutriCoach")
                        "settings" -> Icon(Icons.Filled.Settings, contentDescription = "Settings")
                    }
                },
                label = { Text(item.replaceFirstChar { it.uppercaseChar() }) },
                selected = currentDestination == item,
                onClick = {
                    navController.navigate(item) {
                        popUpTo("home") { inclusive = false }
                        launchSingleTop = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Blue,   // Selected icon color
                    unselectedIconColor = Color.Gray  // Unselected icon color
                )
            )
        }
    }
}



@Composable
fun GreetingSection(userName: String, navController: NavHostController) {


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Greeting Text
        Text(
            text = "Hello, ",
            fontSize = 24.sp,
            fontWeight = FontWeight.Normal,
            color = Color.Gray
        )
        Text(
            text = userName,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))


        Row(horizontalArrangement = Arrangement.SpaceBetween){
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(2.dp)
                    .weight(2f)
            ){
                Text(
                    text = "Use the edit button to change details of questionnaire: ",
                    fontSize = 16.sp,
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .weight(1f)
            ){
                Button(
                    onClick = {
                        navController.navigate("questionnaire")
                    },
                    colors = ButtonDefaults.buttonColors(),
                    shape = RoundedCornerShape(2.dp)


                ) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Edit")
                }

            }

        }
    }
}


@Composable
fun FoodQualityImageSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.aaww),
            contentDescription = "Food Quality Image",
            modifier = Modifier
                .size(200.dp),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun MyScoreSection(score: Double?, navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "My Score",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        TextButton(onClick = { navController.navigate("insights")}) {
            Text(text = "See all scores", color = Color.Gray)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Upward Arrow",
            tint = Color.Black,
            modifier = Modifier.size(24.dp)
        )

        Text(
            text = "Your Food Quality Score",
            fontSize = 16.sp,
            modifier = Modifier.padding(start = 8.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "$score/100",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Green
        )
    }
}


@Composable
fun FoodQualityScoreInfo() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)



    ) {
        Text(
            text = "What is the Food Quality Score?",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Your Food Quality Score provides a snapshot of how well your eating patterns align with established food guidelines, helping you identify both strengths and opportunities for improvement in your diet.\n" +
                    "This personalized measurement considers various food groups including vegetables, fruits, whole grains, and proteins to give you practical insights for making healthier food choices.",
            fontSize = 14.sp,
            color = Color.Gray
        )

    }
}
