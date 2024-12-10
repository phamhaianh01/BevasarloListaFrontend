package com.example.bevasarlolista.network

import com.example.bevasarlolista.model.User
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface UserApi {
    @GET("Auth")
    fun authUser(@Query("username") username: String,
                 @Query("password") password: String): Call<User?>
    @GET("User")
    fun getUsers(): Call<List<User>>


}