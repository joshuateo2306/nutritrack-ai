package com.Joshua_Teo_35514140.nutritrack.data
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.Joshua_Teo_35514140.nutritrack.data.FoodIntake.FoodIntakeDao
import com.Joshua_Teo_35514140.nutritrack.data.Patient.PatientDao
import com.Joshua_Teo_35514140.nutritrack.data.Patient.Patient
import com.Joshua_Teo_35514140.nutritrack.data.FoodIntake.FoodIntake
import com.Joshua_Teo_35514140.nutritrack.data.gemini.MotivationalMessage
import com.Joshua_Teo_35514140.nutritrack.data.gemini.MotivationalMessageDao

@Database(entities = [Patient::class, FoodIntake::class, MotivationalMessage::class], version = 4, exportSchema = false)
abstract class NutritrackDatabase : RoomDatabase() {

    /**
     * Provides access to the PatientDao interface for performing
     * database operations on Patient entities.
     * @return PatientDao instance.
     */
    abstract fun patientDao(): PatientDao

    /**
     * Provides access to the FoodIntakeDao interface for
     * performing database operations on FoodIntake entities.
     * @return FoodIntakeDao instance.
     */
    abstract fun foodIntakeDao(): FoodIntakeDao

    abstract fun motivationalMessageDao(): MotivationalMessageDao

    companion object {
        // Singleton instance of the database
        @Volatile
        private var INSTANCE: NutritrackDatabase? = null

        /**
         * Retrieves the singleton instance of the database.
         * If an instance already exists, it returns the existing
         * instance. Otherwise, it creates a new instance of the database.
         * @param context The context of the application.
         * @return The singleton instance of NutriTrackDatabase.
         */
        fun getDatabase(context: Context): NutritrackDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    NutritrackDatabase::class.java,
                    "nutri_track_database"
                )
                    .fallbackToDestructiveMigration() // In case of schema change, it destroys the old database
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}