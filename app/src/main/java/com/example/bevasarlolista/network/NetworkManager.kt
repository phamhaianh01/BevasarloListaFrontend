package com.example.bevasarlolista.network

import com.example.bevasarlolista.model.Item
import com.example.bevasarlolista.model.User
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkManager {
    private var SERVICE_URL = "http://10.0.2.2:5257/api/"

    private var retrofit: Retrofit
    private lateinit var userApi: UserApi
    private lateinit var itemApi: ItemApi

    init {

        retrofit = Retrofit.Builder()
            .baseUrl(SERVICE_URL)
            .client(OkHttpClient.Builder()
                .callTimeout(3, TimeUnit.SECONDS)
                .build())
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder()
                        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                        //.registerTypeAdapter(Date::class.java, DateDeserializer())
                        .create()
                ))
            .build()

        userApi = retrofit.create(UserApi::class.java)
        itemApi = retrofit.create(ItemApi::class.java)
    }

    //User
    fun authUser(username: String, password: String): Call<User?>{
        return userApi.authUser(username, password)
    }

    //Item
    fun getItems(): Call<List<Item>>{
        return itemApi.getItems()
    }

    fun searchItem(id: Long): Call<Item?>? {
        return itemApi.getItem(id)
    }
}