package com.Joshua_Teo_35514140.nutritrack.data.gemini

import androidx.room.Entity
import androidx.room.PrimaryKey


// Basically the "NutriCoachTips" that assignment spec wants. I think motivational message is more clear because clinician tips also has similar name.
@Entity(tableName = "motivational_messages")
data class MotivationalMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,
    val message: String,
    val maxMessages: Int = 10,
    val timestamp: Long = System.currentTimeMillis(),

)
