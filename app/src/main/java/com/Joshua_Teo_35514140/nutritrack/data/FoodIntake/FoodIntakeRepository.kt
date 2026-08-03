package com.Joshua_Teo_35514140.nutritrack.data.FoodIntake

import android.content.Context
import com.Joshua_Teo_35514140.nutritrack.data.NutritrackDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FoodIntakeRepository(context: Context) {
    private val foodIntakeDao = NutritrackDatabase.getDatabase(context).foodIntakeDao()
    /**
     * Inserts or updates a FoodIntake record in the database.
     */
    suspend fun insertFoodIntake(foodIntake: FoodIntake) {
        withContext(Dispatchers.IO) {
            foodIntakeDao.insertFoodIntake(foodIntake)
        }
    }

    /**
     * Retrieves a FoodIntake entry by userId.
     */
    suspend fun getFoodIntakeByUserId(userId: String): FoodIntake? {
        return withContext(Dispatchers.IO) {
            foodIntakeDao.getFoodIntakeByUserId(userId)
        }
    }

    /**
     * Deletes a FoodIntake record from the database.
     */
    suspend fun deleteFoodIntake(foodIntake: FoodIntake) {
        withContext(Dispatchers.IO) {
            foodIntakeDao.deleteFoodIntake(foodIntake)
        }
    }
}