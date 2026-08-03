package com.Joshua_Teo_35514140.nutritrack.data.gemini

sealed interface UiState {
    object Initial : UiState
    object Loading : UiState
    data class Success(val tips: List<String>) : UiState
    data class Error(val errorMessage: String) : UiState
}