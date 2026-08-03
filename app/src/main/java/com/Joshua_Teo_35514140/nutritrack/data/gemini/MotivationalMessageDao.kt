package com.Joshua_Teo_35514140.nutritrack.data.gemini

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MotivationalMessageDao {
    @Insert
    suspend fun insert(message: MotivationalMessage)

    @Query("SELECT * FROM motivational_messages WHERE userId = :userId ORDER BY timestamp DESC")
    suspend fun getMessagesForUser(userId: String): List<MotivationalMessage>

    @Delete
    suspend fun deleteMessages(messages: List<MotivationalMessage>)

    @Query("DELETE FROM motivational_messages WHERE userId = :userId")
    suspend fun deleteAllForUser(userId: String)

    @Query("SELECT COUNT(*) FROM motivational_messages WHERE userId = :userId")
    suspend fun countMessagesForToday(userId: String): Int
}