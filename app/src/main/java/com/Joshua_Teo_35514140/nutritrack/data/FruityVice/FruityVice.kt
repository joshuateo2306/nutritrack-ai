package com.Joshua_Teo_35514140.nutritrack.data.FruityVice

data class FruitResponse(
    val name: String,
    val family: String,
    val nutritions: NutritionInfo
)

data class NutritionInfo(
    val calories: Double,
    val fat: Double,
    val sugar: Double,
    val carbohydrates: Double,
    val protein: Double
)
