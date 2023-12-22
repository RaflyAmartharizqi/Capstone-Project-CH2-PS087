package com.bangkitacademy.nutrace.data.network

import com.bangkitacademy.nutrace.data.model.FoodResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    companion object {

        private const val BASE_URL = "https://capstone-project-408107.du.r.appspot.com/"
        private val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        fun create(): ApiService {
            return retrofit.create(ApiService::class.java)
        }
    }

    data class FoodRequestBody(
        val food_name: String
    )

    @POST("search")
    //fun getSearchFood(): Call<FoodResponse>
    fun getSearchFood(@Body body: FoodRequestBody): Call<FoodResponse>
}