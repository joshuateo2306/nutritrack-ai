package com.Joshua_Teo_35514140.nutritrack.data.FoodIntake

import androidx.room.*

@Dao
interface FoodIntakeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodIntake(foodIntake: FoodIntake)

    @Query("SELECT * FROM food_intake WHERE userId = :userId")
    suspend fun getFoodIntakeByUserId(userId: String): FoodIntake?

    @Delete
    suspend fun deleteFoodIntake(foodIntake: FoodIntake)
}