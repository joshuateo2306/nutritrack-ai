package com.Joshua_Teo_35514140.nutritrack.data.FoodIntake


import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.Joshua_Teo_35514140.nutritrack.data.FoodIntake.FoodIntake
import com.Joshua_Teo_35514140.nutritrack.data.FoodIntake.FoodIntakeRepository
import com.Joshua_Teo_35514140.nutritrack.data.Patient.PatientRepository
import com.Joshua_Teo_35514140.nutritrack.data.Patient.PatientViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FoodIntakeViewModel(context: Context) : ViewModel() {
    private val repository = FoodIntakeRepository(context = context)
    private val _foodIntake = MutableStateFlow<FoodIntake?>(null)
    val foodIntake: StateFlow<FoodIntake?> = _foodIntake
    var fruits by mutableStateOf(false)
    var redMeat by mutableStateOf(false)
    var fish by mutableStateOf(false)
    var vegetables by mutableStateOf(false)
    var seafood by mutableStateOf(false)
    var eggs by mutableStateOf(false)
    var grains by mutableStateOf(false)
    var poultry by mutableStateOf(false)
    var nutsSeeds by mutableStateOf(false)

    var selectedPersona by mutableStateOf("")

    var biggestMealTime by mutableStateOf("--:--")
    var sleepTime by mutableStateOf("--:--")
    var wakeTime by mutableStateOf("--:--")

    /**
     * Fetches the food intake data for the given user ID and updates state.
     */
    fun loadFoodIntake(userId: String) {
        viewModelScope.launch {
            val result = repository.getFoodIntakeByUserId(userId)
            _foodIntake.value = result
            result?.let {
                fruits = it.fruits
                redMeat = it.redMeat
                fish = it.fish
                vegetables = it.vegetables
                seafood = it.seafood
                eggs = it.eggs
                grains = it.grains
                poultry = it.poultry
                nutsSeeds = it.nutsSeeds
                selectedPersona = it.selectedPersona
                biggestMealTime = it.biggestMealTime
                sleepTime = it.sleepTime
                wakeTime = it.wakeTime
            }
        }
    }
    fun constructCurrentFoodIntake(userId: String): FoodIntake {
        return FoodIntake(
            userId = userId,
            fruits = fruits,
            redMeat = redMeat,
            fish = fish,
            vegetables = vegetables,
            seafood = seafood,
            eggs = eggs,
            grains = grains,
            poultry = poultry,
            nutsSeeds = nutsSeeds,
            selectedPersona = selectedPersona,
            biggestMealTime = biggestMealTime,
            sleepTime = sleepTime,
            wakeTime = wakeTime
        )
    }
    /**
     * Inserts or updates the food intake record in the database.
     */
    fun saveFoodIntake(foodIntake: FoodIntake) {
        viewModelScope.launch {
            repository.insertFoodIntake(foodIntake)
            _foodIntake.value = foodIntake
        }
    }

    /**
     * Deletes the current food intake entry.
     */
    fun deleteFoodIntake() {
        viewModelScope.launch {
            _foodIntake.value?.let {
                repository.deleteFoodIntake(it)
                _foodIntake.value = null
            }
        }
    }
    class FoodIntakeViewModelFactory(context: Context) : ViewModelProvider.Factory {
        private val context = context.applicationContext
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            FoodIntakeViewModel(context) as T

    }
}
