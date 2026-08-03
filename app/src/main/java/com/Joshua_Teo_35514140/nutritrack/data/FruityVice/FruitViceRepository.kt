package com.Joshua_Teo_35514140.nutritrack.data.FruityVice

import android.content.Context
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FruitRepository(context: Context) {

    private val api: FruityViceApi

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.fruityvice.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(FruityViceApi::class.java)
    }

    suspend fun fetchFruit(name: String): FruitResponse = api.getFruitByName(name)
}