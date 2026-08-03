package com.Joshua_Teo_35514140.nutritrack.data.gemini

import android.content.Context
import com.Joshua_Teo_35514140.nutritrack.data.NutritrackDatabase

class MotivationalMessageRepository(context: Context) {
    private val dao = NutritrackDatabase.getDatabase(context).motivationalMessageDao()
    private val patientDao = NutritrackDatabase.getDatabase(context).patientDao()
    suspend fun insert(message: MotivationalMessage) {
        dao.insert(message)

        val patient = patientDao.getPatientByUserId(message.userId)
        val isPremium = patient?.isPremium ?: false

        if (!isPremium) {
            val allMessages = dao.getMessagesForUser(message.userId)
            if (allMessages.size > message.maxMessages) {
                val messagesToDelete = allMessages.drop(message.maxMessages)
                dao.deleteMessages(messagesToDelete)
            }
        }
    }
    suspend fun deleteAllMessagesForUser(userId: String) {
        dao.deleteAllForUser(userId)
    }
    suspend fun getMessagesForUser(userId: String) = dao.getMessagesForUser(userId)

    suspend fun countMessagesForToday(userId: String) = dao.countMessagesForToday(userId)
}