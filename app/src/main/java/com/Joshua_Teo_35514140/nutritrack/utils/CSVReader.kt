package com.Joshua_Teo_35514140.nutritrack.utils

import android.content.Context
import com.Joshua_Teo_35514140.nutritrack.data.Patient.Patient
import java.io.BufferedReader
import java.io.InputStreamReader




object CSVReader {

    fun readPatientsFromCsv(context: Context, fileName: String = "patients.csv"): List<Patient> {
        val patients = mutableListOf<Patient>()

        try {
            val inputStream = context.assets.open(fileName)
            val reader = BufferedReader(InputStreamReader(inputStream))

            val headerLine = reader.readLine() ?: return emptyList()
            val headers = headerLine.split(",")

            reader.forEachLine { line ->
                val values = line.split(",")
                if (values.size < headers.size) return@forEachLine

                val row = headers.zip(values).toMap()

                try {
                    val patient = Patient(
                        UserID = row["User_ID"]?.trim() ?: "",
                        Name = "default",
                        PhoneNumber = row["PhoneNumber"]?.trim() ?: "",
                        Sex = row["Sex"]?.trim() ?: "",
                        password = "default", // Default since password isn't in CSV

                        fruitVariation = row["Fruitvariationsscore"]?.toDoubleOrNull() ?: 0.0,

                        heifaTotalScoreMale = row["HEIFAtotalscoreMale"]?.toDoubleOrNull() ?: 0.0,
                        heifaTotalScoreFemale = row["HEIFAtotalscoreFemale"]?.toDoubleOrNull() ?: 0.0,

                        discretionaryScoreMale = row["DiscretionaryHEIFAscoreMale"]?.toDoubleOrNull() ?: 0.0,
                        discretionaryScoreFemale = row["DiscretionaryHEIFAscoreFemale"]?.toDoubleOrNull() ?: 0.0,

                        vegetablesScoreMale = row["VegetablesHEIFAscoreMale"]?.toDoubleOrNull() ?: 0.0,
                        vegetablesScoreFemale = row["VegetablesHEIFAscoreFemale"]?.toDoubleOrNull() ?: 0.0,

                        fruitScoreMale = row["FruitHEIFAscoreMale"]?.toDoubleOrNull() ?: 0.0,
                        fruitScoreFemale = row["FruitHEIFAscoreFemale"]?.toDoubleOrNull() ?: 0.0,

                        grainsScoreMale = row["GrainsandcerealsHEIFAscoreMale"]?.toDoubleOrNull() ?: 0.0,
                        grainsScoreFemale = row["GrainsandcerealsHEIFAscoreFemale"]?.toDoubleOrNull() ?: 0.0,

                        wholegrainsScoreMale = row["WholegrainsHEIFAscoreMale"]?.toDoubleOrNull() ?: 0.0,
                        wholegrainsScoreFemale = row["WholegrainsHEIFAscoreFemale"]?.toDoubleOrNull() ?: 0.0,

                        meatScoreMale = row["MeatandalternativesHEIFAscoreMale"]?.toDoubleOrNull() ?: 0.0,
                        meatScoreFemale = row["MeatandalternativesHEIFAscoreFemale"]?.toDoubleOrNull() ?: 0.0,

                        dairyScoreMale = row["DairyandalternativesHEIFAscoreMale"]?.toDoubleOrNull() ?: 0.0,
                        dairyScoreFemale = row["DairyandalternativesHEIFAscoreFemale"]?.toDoubleOrNull() ?: 0.0,

                        sodiumScoreMale = row["SodiumHEIFAscoreMale"]?.toDoubleOrNull() ?: 0.0,
                        sodiumScoreFemale = row["SodiumHEIFAscoreFemale"]?.toDoubleOrNull() ?: 0.0,

                        alcoholScoreMale = row["AlcoholHEIFAscoreMale"]?.toDoubleOrNull() ?: 0.0,
                        alcoholScoreFemale = row["AlcoholHEIFAscoreFemale"]?.toDoubleOrNull() ?: 0.0,

                        waterScoreMale = row["WaterHEIFAscoreMale"]?.toDoubleOrNull() ?: 0.0,
                        waterScoreFemale = row["WaterHEIFAscoreFemale"]?.toDoubleOrNull() ?: 0.0,

                        sugarScoreMale = row["SugarHEIFAscoreMale"]?.toDoubleOrNull() ?: 0.0,
                        sugarScoreFemale = row["SugarHEIFAscoreFemale"]?.toDoubleOrNull() ?: 0.0,

                        saturatedFatScoreMale = row["SaturatedFatHEIFAscoreMale"]?.toDoubleOrNull() ?: 0.0,
                        saturatedFatScoreFemale = row["SaturatedFatHEIFAscoreFemale"]?.toDoubleOrNull() ?: 0.0,

                        unsaturatedFatScoreMale = row["UnsaturatedFatHEIFAscoreMale"]?.toDoubleOrNull() ?: 0.0,
                        unsaturatedFatScoreFemale = row["UnsaturatedFatHEIFAscoreFemale"]?.toDoubleOrNull() ?: 0.0
                    )

                    patients.add(patient)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        patients.forEach {
            println("DEBUG: USER ${it.UserID}: ${it}")
        }
        return patients
    }
}
