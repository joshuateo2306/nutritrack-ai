package com.Joshua_Teo_35514140.nutritrack.data.Patient

import android.content.Context
import com.Joshua_Teo_35514140.nutritrack.data.NutritrackDatabase
import kotlinx.coroutines.flow.Flow

class PatientRepository(context: Context) {

    private val patientDao = NutritrackDatabase.getDatabase(context).patientDao()

    suspend fun insertPatient(patient: Patient) {
        patientDao.insert(patient)
    }

    suspend fun getPatientByUserIdAndPhoneNumber(userId: String, phoneNumber: String): Patient? {
        return patientDao.getPatientByUserIdAndPhoneNumber(userId, phoneNumber)
    }

    suspend fun getPatientByUserIdAndPassword(userId: String, password: String): Patient? {
        return patientDao.getPatientByUserIdAndPassword(userId, password)
    }

    suspend fun getPatientByUserId(userId: String): Patient? {
        return patientDao.getPatientByUserId(userId)
    }
    suspend fun updatePassword(userId: String, newPassword: String) {
        patientDao.updatePassword(userId, newPassword)
    }
    suspend fun updateName(userId: String, newName: String) {
        patientDao.updateName(userId, newName)
    }
    suspend fun goPremium(userId: String){
        patientDao.goPremium(userId)
    }
    fun getAllPatientUserIds(): Flow<List<String>> {
        return patientDao.getAllPatientUserIds()
    }

    fun getAllMaleScore(): Flow<List<Double>> {
        return patientDao.getAllMaleScore()
    }

    fun getAllFemaleScore(): Flow<List<Double>> {
        return patientDao.getAllFemaleScore()
    }

    fun getRegisteredUserIds(): Flow<List<String>> = patientDao.getRegisteredUserIds()

    fun getAllPatients(): Flow<List<Patient>> {
        return patientDao.getAllPatients()
    }
    suspend fun deleteAllPatients() = patientDao.deleteAllPatients()

}