package com.example.bevasarlolista.network

import com.example.bevasarlolista.model.Item
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ItemApi {
    @GET("item")
    fun getItems(): Call<List<Item>>

    @GET("item/{id}")
    fun getItem(
        @Path("id") id: Long,
    ): Call<Item?>?


}