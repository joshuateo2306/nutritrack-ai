package com.Joshua_Teo_35514140.nutritrack.data.Patient

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PatientDao {
    // Insert patient data (including Name, PhoneNumber, UserID, and password)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(patient: Patient)

    // Get Patient by UserID, with or without password (during first-time login)
    @Query("SELECT * FROM patients WHERE UserID = :userId AND PhoneNumber = :phoneNumber")
    suspend fun getPatientByUserIdAndPhoneNumber(userId: String, phoneNumber: String): Patient?

    // Get Patient by UserID and password (for subsequent logins)
    @Query("SELECT * FROM patients WHERE UserID = :userId AND password = :password")
    suspend fun getPatientByUserIdAndPassword(userId: String, password: String): Patient?

    // Get Patient by UserID (used for checking existence of a user during registration)
    @Query("SELECT * FROM patients WHERE UserID = :userId")
    suspend fun getPatientByUserId(userId: String): Patient?

    @Query("SELECT heifaTotalScoreMale FROM patients ORDER BY rowid ASC")
    fun getAllMaleScore(): Flow<List<Double>>

    @Query("SELECT heifaTotalScoreFemale FROM patients ORDER BY rowid ASC")
    fun getAllFemaleScore(): Flow<List<Double>>

    @Query("SELECT UserID FROM patients ORDER BY rowid ASC")
    fun getAllPatientUserIds(): Flow<List<String>>

    @Query("UPDATE patients SET password = :newPassword WHERE UserID = :userId")
    suspend fun updatePassword(userId: String, newPassword: String)

    @Query("UPDATE patients SET name = :newName WHERE UserID = :userId")
    suspend fun updateName(userId: String, newName: String)

    @Query("UPDATE patients SET isPremium = 1 WHERE UserID = :userId")
    suspend fun goPremium(userId: String)

    @Query("SELECT * FROM patients")
    fun getAllPatients(): Flow<List<Patient>>

    @Query("DELETE FROM patients")
    suspend fun deleteAllPatients()

    @Query("SELECT UserID FROM patients WHERE password != 'default'")
    fun getRegisteredUserIds(): Flow<List<String>>
}