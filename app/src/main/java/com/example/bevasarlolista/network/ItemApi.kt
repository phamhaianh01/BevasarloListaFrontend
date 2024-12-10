package com.example.bevasarlolista.network

import com.example.bevasarlolista.model.Item
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ItemApi {
    @GET("Item")
    fun getItems(): Call<List<Item>>

    @GET("Item/{id}")
    fun getItem(
        @Path("id") id: Int,
    ): Call<Item?>?

    @POST("Item")
    fun addItem(@Body item: Item): Call<Item>

    @PUT("Item/{id}")
    fun updateItem(@Path("id") id: Int, @Body item: Item): Call<Item>



}