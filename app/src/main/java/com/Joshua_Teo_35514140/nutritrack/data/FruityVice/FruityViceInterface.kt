package com.Joshua_Teo_35514140.nutritrack.data.FruityVice

import retrofit2.http.GET
import retrofit2.http.Path

interface FruityViceApi {

    @GET("api/fruit/{name}")
    suspend fun getFruitByName(@Path("name") name: String): FruitResponse
}