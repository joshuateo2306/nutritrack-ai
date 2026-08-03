package com.Joshua_Teo_35514140.nutritrack.data.FruityVice



import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.Joshua_Teo_35514140.nutritrack.data.FoodIntake.FoodIntakeRepository
import com.Joshua_Teo_35514140.nutritrack.data.Patient.PatientViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FruitViewModel(context: Context) : ViewModel() {
    private val repository = FruitRepository(context = context)

    private val _fruit = MutableStateFlow<FruitResponse?>(null)
    val fruit: StateFlow<FruitResponse?> = _fruit

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun loadFruit(name: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val result = repository.fetchFruit(name.lowercase())
                _fruit.value = result
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "Could not fetch fruit data."
            } finally {
                _isLoading.value = false
            }
        }
    }
    class FruitViewModelFactory(context: Context) : ViewModelProvider.Factory {
        private val context = context.applicationContext
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            FruitViewModel(context) as T


    }

}