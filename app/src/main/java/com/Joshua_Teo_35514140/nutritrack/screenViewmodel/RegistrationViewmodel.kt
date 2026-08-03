package com.Joshua_Teo_35514140.nutritrack.screenViewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RegistrationViewmodel : ViewModel() {
    val selectedUserId = mutableStateOf("")
    val phoneInput = mutableStateOf("")
    val nameInput = mutableStateOf("")
    val passwordInput = mutableStateOf("")
    val confirmPasswordInput = mutableStateOf("")
    val expanded = mutableStateOf(false)
}