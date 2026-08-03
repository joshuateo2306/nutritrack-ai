package com.Joshua_Teo_35514140.nutritrack.data



import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

object AuthManager {
    val _userId: MutableState<String?> = mutableStateOf(null)

    fun login(userId: String) {
        Log.d("AuthManager", "Logged in with userId: $userId")
        _userId.value = userId
    }

    fun logout() {
        _userId.value = null
    }

    fun getUserId(): String? {
        return _userId.value
    }
}