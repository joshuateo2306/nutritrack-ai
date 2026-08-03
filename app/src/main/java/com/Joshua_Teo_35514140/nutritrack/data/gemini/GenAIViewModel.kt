package com.Joshua_Teo_35514140.nutritrack.data.gemini

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.Joshua_Teo_35514140.nutritrack.BuildConfig
import com.Joshua_Teo_35514140.nutritrack.data.FoodIntake.FoodIntakeRepository
import com.Joshua_Teo_35514140.nutritrack.data.Patient.Patient
import com.Joshua_Teo_35514140.nutritrack.data.Patient.PatientRepository
import com.Joshua_Teo_35514140.nutritrack.data.Patient.PatientViewModel
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.cancel

/*
 * ViewModel class for handling interactions with a generative AI model.
 * This class manages the UI state and communicates with the GenerativeModel
 * to generate content based on user prompts.
 */
class GenAIViewModel(context: Context) : ViewModel() {

    private val repository = MotivationalMessageRepository(context)
    private val patient_vm = PatientViewModel(context)

    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.0-flash",
        apiKey = BuildConfig.apiKey
    )

    private val _uiState = MutableStateFlow<UiState>(UiState.Initial)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _motivationalTipState = MutableStateFlow<UiState>(UiState.Initial)
    val motivationalTipState: StateFlow<UiState> = _motivationalTipState.asStateFlow()

    private val _allTips = MutableStateFlow<UiState>(UiState.Loading)
    val allTips: StateFlow<UiState> = _allTips

    // Used internally by both dataset and patient-based generation
    fun generateTipsFromDataset(dataset: String) {
        _uiState.value = UiState.Loading

        val prompt = """
            You are a clinical nutrition assistant. Analyze the dataset below and identify exactly three (3) insightful patterns or tips. These tips could focus on dietary habits, gender differences, or nutritional trends.

            IMPORTANT:
            - You must return EXACTLY 3 tips.
            - Format them as a numbered list.
            
            - Just output:
            1. ...
            2. ...
            3. ...

            The output must be precisely 3 entries. Now analyze this dataset:

            $dataset
        """.trimIndent()

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = generativeModel.generateContent(content { text(prompt) })
                val tipsText = response.text

                if (tipsText != null) {
                    val tips = tipsText
                        .split(Regex("\\d+\\.\\s+"))
                        .map { it.trim() }
                        .filter { it.isNotEmpty() }
                        .take(3)

                    _uiState.value = UiState.Success(tips)
                } else {
                    _uiState.value = UiState.Error("No tips returned by AI.")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "Error generating tips")
            }
        }
    }

    // Generates tips based on patient data
    fun generateTipsFromPatients(patients: List<Patient>) {
        val dataset = buildString {
            appendLine("UserID, Name, Sex, heifaTotalScore, discretionary, vegetables, fruit, grains, wholegrains, meat, dairy, sodium, alcohol, water, sugar, saturatedFat, unsaturatedFat")
            patients.forEach { patient ->
                val isMale = patient.Sex.equals("Male", ignoreCase = true)

                appendLine(listOf(
                    patient.UserID,
                    patient.Name,
                    patient.Sex,
                    if (isMale) patient.heifaTotalScoreMale else patient.heifaTotalScoreFemale,
                    if (isMale) patient.discretionaryScoreMale else patient.discretionaryScoreFemale,
                    if (isMale) patient.vegetablesScoreMale else patient.vegetablesScoreFemale,
                    if (isMale) patient.fruitScoreMale else patient.fruitScoreFemale,
                    if (isMale) patient.grainsScoreMale else patient.grainsScoreFemale,
                    if (isMale) patient.wholegrainsScoreMale else patient.wholegrainsScoreFemale,
                    if (isMale) patient.meatScoreMale else patient.meatScoreFemale,
                    if (isMale) patient.dairyScoreMale else patient.dairyScoreFemale,
                    if (isMale) patient.sodiumScoreMale else patient.sodiumScoreFemale,
                    if (isMale) patient.alcoholScoreMale else patient.alcoholScoreFemale,
                    if (isMale) patient.waterScoreMale else patient.waterScoreFemale,
                    if (isMale) patient.sugarScoreMale else patient.sugarScoreFemale,
                    if (isMale) patient.saturatedFatScoreMale else patient.saturatedFatScoreFemale,
                    if (isMale) patient.unsaturatedFatScoreMale else patient.unsaturatedFatScoreFemale
                ).joinToString())
            }
        }

        generateTipsFromDataset(dataset)
    }
    suspend fun getMessageCount(userId: String): Int {
        return try {
            repository.countMessagesForToday(userId)
        } catch (e: Exception) {
            -1
        }
    }



    // Delete all messages for user
    fun deleteAllMessages(userId: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                repository.deleteAllMessagesForUser(userId)
                loadMotivationalMessages(userId) // Refresh the list
                onResult(true)
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }
    suspend fun isUserPremium(userId: String): Boolean {
        return patient_vm.getPatientByUserId(userId)?.isPremium ?: false
    }

    // Generates and stores a single motivational message for the given user
    fun generateMotivationalMessageFromPatient(patient: Patient?) {
        val MAX_FREE_MESSAGES = 10

        if (patient == null) {
            _motivationalTipState.value = UiState.Error("No patient data available.")
            return
        }



        _motivationalTipState.value = UiState.Loading

        val isMale = patient.Sex.equals("Male", ignoreCase = true)

        val dataset = buildString {
            appendLine("UserID, Name, Sex, heifaTotalScore, discretionary, vegetables, fruit, grains, wholegrains, meat, dairy, sodium, alcohol, water, sugar, saturatedFat, unsaturatedFat")
            appendLine(
                listOf(
                    patient.UserID,
                    patient.Name,
                    patient.Sex,
                    if (isMale) patient.heifaTotalScoreMale else patient.heifaTotalScoreFemale,
                    if (isMale) patient.discretionaryScoreMale else patient.discretionaryScoreFemale,
                    if (isMale) patient.vegetablesScoreMale else patient.vegetablesScoreFemale,
                    if (isMale) patient.fruitScoreMale else patient.fruitScoreFemale,
                    if (isMale) patient.grainsScoreMale else patient.grainsScoreFemale,
                    if (isMale) patient.wholegrainsScoreMale else patient.wholegrainsScoreFemale,
                    if (isMale) patient.meatScoreMale else patient.meatScoreFemale,
                    if (isMale) patient.dairyScoreMale else patient.dairyScoreFemale,
                    if (isMale) patient.sodiumScoreMale else patient.sodiumScoreFemale,
                    if (isMale) patient.alcoholScoreMale else patient.alcoholScoreFemale,
                    if (isMale) patient.waterScoreMale else patient.waterScoreFemale,
                    if (isMale) patient.sugarScoreMale else patient.sugarScoreFemale,
                    if (isMale) patient.saturatedFatScoreMale else patient.saturatedFatScoreFemale,
                    if (isMale) patient.unsaturatedFatScoreMale else patient.unsaturatedFatScoreFemale
                ).joinToString()
            )
        }

        val prompt = """
        You are a motivational nutrition assistant. Based on the personal nutrition dataset below, generate ONE personalized, fresh, and motivating health tip for the user. 
        Focus on one area where the user can improve (e.g. low fruit intake or high sugar intake) and recommend a positive action. 
        Avoid generic clichés, be warm, and make it specific to the user.

        Data:
        $dataset
    """.trimIndent()

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val currentCount = getMessageCount(patient.UserID)
                val isPremium = patient.isPremium ?: false

                if (!isPremium && currentCount >= MAX_FREE_MESSAGES) {
                    _motivationalTipState.value = UiState.Error(
                        "You've reached your limit of $MAX_FREE_MESSAGES tips. " +
                                "Please delete some tips or upgrade to premium."
                    )
                    return@launch
                }


                val response = generativeModel.generateContent(content { text(prompt) })
                val messageText = response.text?.trim()

                if (!messageText.isNullOrEmpty()) {
                    repository.insert(MotivationalMessage(
                        userId = patient.UserID,
                        message = messageText,
                        maxMessages = if (isPremium) Int.MAX_VALUE else MAX_FREE_MESSAGES
                    ))
                    _motivationalTipState.value = UiState.Success(listOf(messageText))
                } else {
                    _motivationalTipState.value = UiState.Error("No motivational message returned by AI.")
                }
            } catch (e: Exception) {
                _motivationalTipState.value = UiState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    // Loads all previously stored motivational messages for a user
    fun loadMotivationalMessages(userId: String) {
        _allTips.value = UiState.Loading


        viewModelScope.launch(Dispatchers.IO) {
            try {
                val messages = repository.getMessagesForUser(userId).map { it.message }
                _allTips.value = UiState.Success(messages)
            } catch (e: Exception) {
                _allTips.value = UiState.Error(e.localizedMessage ?: "Error fetching messages")
            }
        }
    }

    // ViewModel factory
    class GenAiViewModelFactory(context: Context) : ViewModelProvider.Factory {
        private val appContext = context.applicationContext
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return GenAIViewModel(appContext) as T
        }
    }
}

