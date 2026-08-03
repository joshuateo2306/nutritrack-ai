package com.Joshua_Teo_35514140.nutritrack.home


import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TimePicker
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.Joshua_Teo_35514140.nutritrack.R

import com.Joshua_Teo_35514140.nutritrack.data.AuthManager
import com.Joshua_Teo_35514140.nutritrack.data.FoodIntake.FoodIntake
import com.Joshua_Teo_35514140.nutritrack.data.FoodIntake.FoodIntakeViewModel
import com.Joshua_Teo_35514140.nutritrack.data.FruityVice.FruitViewModel
import java.util.Calendar




@Composable
fun FoodIntakeQuestionnaireScreen(innerPadding: PaddingValues,navController: NavHostController){

    val context = LocalContext.current
    val viewModel: FoodIntakeViewModel =
        viewModel(factory = FoodIntakeViewModel.FoodIntakeViewModelFactory(context))

    val userId = AuthManager.getUserId() ?: ""
    Log.d("UserIDCheck", "Lets gooooo! Retrieved userId: $userId")

    if (userId.isBlank()) {
        Log.e("UserIDError", "User ID is empty lah! ")
    }

    LaunchedEffect(userId) {
        viewModel.loadFoodIntake(userId)
    }

    val foodCategories = remember {
        listOf(
            "fruits", "redMeat", "fish", "vegetables", "seafood",
            "eggs", "grains", "poultry", "nutsSeeds"
        )
    }

    val foodIntakeState by viewModel.foodIntake.collectAsState()
    Log.e("CheckfoodState", "$foodIntakeState")

    val fruitsChecked = rememberSaveable { mutableStateOf(false) }
    val redMeatChecked = rememberSaveable { mutableStateOf(false) }
    val fishChecked = rememberSaveable { mutableStateOf(false) }
    val vegetablesChecked = rememberSaveable { mutableStateOf(false) }
    val seafoodChecked = rememberSaveable { mutableStateOf(false) }
    val eggsChecked = rememberSaveable { mutableStateOf(false) }
    val grainsChecked = rememberSaveable { mutableStateOf(false) }
    val poultryChecked = rememberSaveable { mutableStateOf(false) }
    val nutsSeedsChecked = rememberSaveable { mutableStateOf(false) }

    var hasInitializedCheckboxes by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(foodIntakeState) {
        foodIntakeState?.let { intake ->
            if (!hasInitializedCheckboxes) {
                fruitsChecked.value = intake.fruits
                redMeatChecked.value = intake.redMeat
                fishChecked.value = intake.fish
                vegetablesChecked.value = intake.vegetables
                seafoodChecked.value = intake.seafood
                eggsChecked.value = intake.eggs
                grainsChecked.value = intake.grains
                poultryChecked.value = intake.poultry
                nutsSeedsChecked.value = intake.nutsSeeds
                hasInitializedCheckboxes = true
            }
        }
    }

    val checkboxStates = mapOf(
        "fruits" to fruitsChecked,
        "redMeat" to redMeatChecked,
        "fish" to fishChecked,
        "vegetables" to vegetablesChecked,
        "seafood" to seafoodChecked,
        "eggs" to eggsChecked,
        "grains" to grainsChecked,
        "poultry" to poultryChecked,
        "nutsSeeds" to nutsSeedsChecked
    )

    var selectedPersona by rememberSaveable { mutableStateOf("") }
    var hasInitializedPersona by rememberSaveable { mutableStateOf(false) }
    var biggestMealTime by rememberSaveable { mutableStateOf(foodIntakeState?.biggestMealTime ?: "--:--") }
    var sleepTime by rememberSaveable { mutableStateOf(foodIntakeState?.sleepTime ?: "--:--") }
    var wakeTime by rememberSaveable { mutableStateOf(foodIntakeState?.wakeTime ?: "--:--") }

    var hasInitializedTime by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(foodIntakeState) {
        foodIntakeState?.let {
            if (!hasInitializedPersona) {
                selectedPersona = it.selectedPersona
                hasInitializedPersona = true
            }
            if (!hasInitializedTime) {
                biggestMealTime = it.biggestMealTime
                sleepTime = it.sleepTime
                wakeTime = it.wakeTime
                hasInitializedTime = true
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.navigate("home") }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Food Intake Questionnaire",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider(
            color = Color.LightGray,
            thickness = 2.dp,
            modifier = Modifier.padding(top = 16.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            "Tick all the food categories you can eat",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )

        val groupedCategories = foodCategories.chunked(3)

        Row(modifier = Modifier.fillMaxWidth()) {
            groupedCategories.forEach { group ->
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp)
                ) {
                    group.forEach { category ->
                        CheckboxRow(
                            category,
                            checkboxStates[category]?.value ?: false
                        ) { newValue ->
                            checkboxStates[category]?.value = newValue
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Your Persona",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "People can be broadly classified into 6 different types based on their eating preferences. Click on each button below to find out the different types, and select the type that best fits you!",
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        PersonaButtons(onSelect = { selectedPersona = it })

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(
            color = Color.LightGray,
            thickness = 2.dp,
            modifier = Modifier.padding(top = 16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text("Which persona best fits you?", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        PersonaDropdownMenu(
            viewModel = viewModel,
            userId = userId,
            onPersonaSelected = { selectedPersona = it }
        )

        Spacer(modifier = Modifier.height(30.dp))
        Text(
            "Timings",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        TimePickerRow(context, "What time of day approx. do you normally eat your biggest meal?", biggestMealTime) {
                newTime -> biggestMealTime = newTime
        }

        TimePickerRow(context, "What time of day approx. do you go to sleep at night?", sleepTime) {
                newTime -> sleepTime = newTime
        }

        TimePickerRow(context, "What time of day approx. do you wake up in the morning?", wakeTime) {
                newTime -> wakeTime = newTime
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                viewModel.saveFoodIntake(
                    FoodIntake(
                        userId = userId,
                        fruits = fruitsChecked.value,
                        redMeat = redMeatChecked.value,
                        fish = fishChecked.value,
                        vegetables = vegetablesChecked.value,
                        seafood = seafoodChecked.value,
                        eggs = eggsChecked.value,
                        grains = grainsChecked.value,
                        poultry = poultryChecked.value,
                        nutsSeeds = nutsSeedsChecked.value,
                        selectedPersona = selectedPersona,
                        biggestMealTime = biggestMealTime,
                        sleepTime = sleepTime,
                        wakeTime = wakeTime
                    )
                )
                navController.navigate("home")
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(200.dp)
                .height(50.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors()
        ) {
            Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = "Save",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save", color = Color.White, fontSize = 16.sp)
            }
        }
    }
}


@Composable
fun CheckboxRow(text: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange // Updates the parent state only
        )
        Text(text)
    }
}

@Composable
fun PersonaButtons(onSelect: (String) -> Unit) {
    val personas = mapOf(
        "Health Devotee" to Pair(R.drawable.persona1, "I’m passionate about healthy eating & health plays a big part in my life. I use social media to follow active lifestyle personalities or get new recipes/exercise ideas. I may even buy superfoods or follow a particular type of diet. I like to think I am super healthy."),
        "Mindful Eater" to Pair(R.drawable.persona2, "I’m health-conscious and being healthy and eating healthy is important to me. Although health means different things to different people, I make conscious lifestyle decisions about eating based on what I believe healthy means. I look for new recipes and healthy eating information on social media."),
        "Wellness Striver" to Pair(R.drawable.persona3, "I aspire to be healthy (but struggle sometimes). Healthy eating is hard work! I’ve tried to improve my diet, but always find things that make it difficult to stick with the changes. Sometimes I notice recipe ideas or healthy eating hacks, and if it seems easy enough, I’ll give it a go."),
        "Balance Seeker" to Pair(R.drawable.persona4, "I try and live a balanced lifestyle, and I think that all foods are okay in moderation. I shouldn’t have to feel guilty about eating a piece of cake now and again. I get all sorts of inspiration from social media like finding out about new restaurants, fun recipes and sometimes healthy eating tips."),
        "Health Procrastinator" to Pair(R.drawable.persona5, "I’m contemplating healthy eating but it’s not a priority for me right now. I know the basics about what it means to be healthy, but it doesn’t seem relevant to me right now. I have taken a few steps to be healthier but I am not motivated to make it a high priority because I have too many other things going on in my life."),
        "Food Carefree" to Pair(R.drawable.persona6, "I’m not bothered about healthy eating. I don’t really see the point and I don’t think about it. I don’t really notice healthy eating tips or recipes and I don’t care what I eat.")
    )
    var modalPersona by remember { mutableStateOf<String?>(null) }
    Column {
        personas.keys.chunked(3).forEach { row -> // Make it so that each row has 3
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                row.forEach { persona ->
                    Button(onClick = { modalPersona = persona },
                        modifier = Modifier
                            .weight(1f)

                            .wrapContentWidth()
                            .padding(3.dp),
                        contentPadding = PaddingValues(8.dp),
                        shape = RoundedCornerShape(2.dp),
                        colors = ButtonDefaults.buttonColors()
                    ) {
                        Text(
                            persona,
                            color = Color.White,
                            maxLines = 1,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
    modalPersona?.let { persona ->
        // Omly runs when persona not null
        PersonaModal(
            persona // Passes persona key
            , personas[persona]!!.first, // Get first value from map, !! Treats value as non null
            personas[persona]!!.second // Get second value from map
        ) {
            modalPersona = null // Reset value to null when modal closed
        }
    }

}
@Composable
fun PersonaModal(persona: String, imageRes: Int, description: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,

        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = "$persona Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(10.dp))
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    persona,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(description, textAlign = TextAlign.Center)
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.width(150.dp),
                    colors = ButtonDefaults.buttonColors()
                ) {
                    Text("Dismiss")
                }
            }
        }
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonaDropdownMenu(
    viewModel: FoodIntakeViewModel,
    userId: String,
    onPersonaSelected: (String) -> Unit
) {
    val foodIntake by viewModel.foodIntake.collectAsState()
    val initialPersona = foodIntake?.selectedPersona

    var expanded by remember { mutableStateOf(false) }
    var selectedText by rememberSaveable { mutableStateOf("") }

    // Initialize selectedText only once when initialPersona becomes available
    LaunchedEffect(initialPersona) {
        if (!initialPersona.isNullOrEmpty() && selectedText.isEmpty()) {
            selectedText = initialPersona
        }
    }

    val options = listOf(
        "Health Devotee",
        "Mindful Eater",
        "Wellness Striver",
        "Balance Seeker",
        "Health Procrastinator",
        "Food Carefree"
    )

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selectedText.ifEmpty { "Select option" },
            onValueChange = { }, // Read-only
            readOnly = true,
            modifier = Modifier
                .menuAnchor()
                .height(48.dp)
                .fillMaxWidth(),
            trailingIcon = {
                Icon(
                    imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Expand/Collapse"
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                unfocusedTextColor = Color.DarkGray,
                focusedTextColor = Color.DarkGray,
                disabledTextColor = Color.DarkGray,
                disabledLabelColor = Color.LightGray,
                focusedLabelColor = Color.LightGray,
                unfocusedLabelColor = Color.LightGray,
                focusedLeadingIconColor = Color.DarkGray,
                unfocusedLeadingIconColor = Color.DarkGray,
                focusedIndicatorColor = Color.DarkGray,
                unfocusedIndicatorColor = Color.DarkGray,
            ),
            shape = RoundedCornerShape(50.dp)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        selectedText = option // update UI immediately
                        val updatedFoodIntake = foodIntake?.copy(selectedPersona = option)
                        updatedFoodIntake?.let { viewModel.saveFoodIntake(it) }
                        onPersonaSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}



@Composable
fun TimePickerRow(context: Context, label: String, time: String, onTimeSelected: (String) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(
            label,
            modifier = Modifier

                .weight(1f)
                .padding(8.dp),
            fontSize = 16.sp
        )
        Button(onClick = {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)
            TimePickerDialog(context, { _: TimePicker, selectedHour: Int, selectedMinute: Int ->
                onTimeSelected("%02d:%02d".format(selectedHour, selectedMinute))
            }, hour, minute, true).show()
        },
            modifier = Modifier
                .height(48.dp)
                .width(120.dp)
                .border(1.dp, Color.Gray, RoundedCornerShape(12.dp)),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent

            ),
            contentPadding = PaddingValues(8.dp)

        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Clock Icon",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = time, color = Color.Gray)
            }

        }
    }
}
