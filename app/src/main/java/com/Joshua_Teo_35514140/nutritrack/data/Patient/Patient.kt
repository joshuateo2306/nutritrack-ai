package com.Joshua_Teo_35514140.nutritrack.data.Patient

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "patients")
data class Patient(
    @PrimaryKey val UserID: String,
    val PhoneNumber: String,
    val Name: String,
    val Sex: String,
    val heifaTotalScoreMale: Double,
    val heifaTotalScoreFemale: Double,
    val password: String?,
    val isPremium: Boolean = false,
    val fruitVariation: Double,
    // Values for scores
    val discretionaryScoreMale: Double,
    val discretionaryScoreFemale: Double,
    val vegetablesScoreMale: Double,
    val vegetablesScoreFemale: Double,
    val fruitScoreMale: Double,
    val fruitScoreFemale: Double,
    val grainsScoreMale: Double,
    val grainsScoreFemale: Double,
    val wholegrainsScoreMale: Double,
    val wholegrainsScoreFemale: Double,
    val meatScoreMale: Double,
    val meatScoreFemale: Double,
    val dairyScoreMale: Double,
    val dairyScoreFemale: Double,
    val sodiumScoreMale: Double,
    val sodiumScoreFemale: Double,
    val alcoholScoreMale: Double,
    val alcoholScoreFemale: Double,
    val waterScoreMale: Double,
    val waterScoreFemale: Double,
    val sugarScoreMale: Double,
    val sugarScoreFemale: Double,
    val saturatedFatScoreMale: Double,
    val saturatedFatScoreFemale: Double,
    val unsaturatedFatScoreMale: Double,
    val unsaturatedFatScoreFemale: Double
)