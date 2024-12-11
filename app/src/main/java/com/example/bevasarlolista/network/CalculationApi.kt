package com.example.bevasarlolista.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface CalculationApi {
    @GET("Calculate")
    fun getMonthlyExpense(
        @Query("year") year: Int,
        @Query("month") month: Int,
        @Query("userId") userId: Int
    ): Call<Double>

}