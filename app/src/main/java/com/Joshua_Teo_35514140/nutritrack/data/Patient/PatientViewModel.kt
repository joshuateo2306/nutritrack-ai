package com.Joshua_Teo_35514140.nutritrack.data.Patient


import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.Joshua_Teo_35514140.nutritrack.data.AuthManager
import com.Joshua_Teo_35514140.nutritrack.data.Patient.PatientRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData

class PatientViewModel(context: Context) : ViewModel() {

    private val repository = PatientRepository(context = context)

    val allPatients: Flow<List<Patient>> = repository.getAllPatients()

    val allPatientsId: Flow<List<String>> = repository.getAllPatientUserIds()

    val allMaleScore: Flow<List<Double>> = repository.getAllMaleScore()

    val allFemaleScore: Flow<List<Double>> = repository.getAllFemaleScore()

    val registeredUserIds: StateFlow<List<String>> = repository.getRegisteredUserIds()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    private val _currentPatient = MutableStateFlow<Patient?>(null)
    val currentPatient: StateFlow<Patient?> get() = _currentPatient

    private val _loginStatus = MutableStateFlow<Boolean?>(null)
    val loginStatus: StateFlow<Boolean?> get() = _loginStatus


    private val _maleAverage = MutableStateFlow(0.0)
    val maleAverage: StateFlow<Double> = _maleAverage


    private val _femaleAverage = MutableStateFlow(0.0)
    val femaleAverage: StateFlow<Double> = _femaleAverage


    suspend fun insert(patient: Patient) = repository.insertPatient(patient)

    /**
     * Retrieves a patient from the database by their UserID.
     * This is a suspend function and should be called within
     * a coroutine or another suspend function.
     *
     * @param userId The UserID of the patient to retrieve.
     * @return The [Patient] object with the specified UserID, or null if no such patient exists.
     */
    suspend fun getPatientByUserId(userId: String): Patient? {
        return repository.getPatientByUserId(userId)
    }
    fun updateName(userId: String, newName: String) {
        viewModelScope.launch {
            repository.updateName(userId, newName)
        }
    }

    fun goPremium(userId: String){
        viewModelScope.launch{
            repository.goPremium(userId)
        }
    }



    fun updatePassword(userId: String, newPassword: String) {
        viewModelScope.launch {
            repository.updatePassword(userId, newPassword)
        }
    }

    fun login(userId: String, password: String) {
        viewModelScope.launch {
            val patient = repository.getPatientByUserIdAndPassword(userId, password)
            if (patient != null) {
                AuthManager.login(patient.UserID)
                _loginStatus.value = true
            } else {
                _loginStatus.value = false
            }


        }

    }

    fun continueSession(userId:String){
        viewModelScope.launch {
            val patient = repository.getPatientByUserId(userId)
            if (patient != null) {
                AuthManager.login(patient.UserID)
                _loginStatus.value = true
            } else {
                _loginStatus.value = false
            }


        }
    }

    fun clearPatients() = viewModelScope.launch {
        repository.deleteAllPatients()
    }
    fun logout() {
        AuthManager.logout() // Clear user session
        _currentPatient.value = null
    }

    fun getLoggedInPatient() {
        viewModelScope.launch {
            val loggedInUserId = AuthManager.getUserId()
            if (loggedInUserId != null) {
                val patient = repository.getPatientByUserId(loggedInUserId)
                _currentPatient.value = patient
            } else {
                _currentPatient.value = null
            }
        }
    }
    fun isFruitIntakeOptimal(): Boolean {
        val patient = currentPatient.value

        if (patient != null) {
            val fruitScore = when (patient.Sex?.lowercase()) {
                "male" -> patient.fruitScoreMale
                "female" -> patient.fruitScoreFemale
                else -> null
            }

            return fruitScore != null &&
                    fruitScore >= 2.0 &&
                    patient.fruitVariation >= 2.0
        }

        return false
    }

    init {
        calculateAverageScores()
    }

    private fun calculateAverageScores() {
        viewModelScope.launch {
            allMaleScore.collect { maleScores ->
                _maleAverage.value = if (maleScores.isNotEmpty()) maleScores.average() else 0.0
            }
        }

        viewModelScope.launch {
            allFemaleScore.collect { femaleScores ->
                _femaleAverage.value = if (femaleScores.isNotEmpty()) femaleScores.average() else 0.0
            }
        }
    }




    // Factory class for creating instances of PatientViewModel
    class PatientViewModelFactory(context: Context) : ViewModelProvider.Factory {
        private val context = context.applicationContext
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            PatientViewModel(context) as T
    }
}