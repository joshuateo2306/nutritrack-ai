package com.Joshua_Teo_35514140.nutritrack.data.FoodIntake

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "food_intake")
data class FoodIntake(
    @PrimaryKey val userId: String,

    // Food category preferences
    val fruits: Boolean = false,
    val redMeat: Boolean = false,
    val fish: Boolean = false,
    val vegetables: Boolean = false,
    val seafood: Boolean = false,
    val eggs: Boolean = false,
    val grains: Boolean = false,
    val poultry: Boolean = false,
    val nutsSeeds: Boolean = false,

    // Persona selection
    val selectedPersona: String = "",

    // Time-related fields
    val biggestMealTime: String = "--:--",
    val sleepTime: String = "--:--",
    val wakeTime: String = "--:--"
)