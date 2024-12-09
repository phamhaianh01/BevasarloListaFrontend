package com.example.bevasarlolista.holder

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.bevasarlolista.model.User
import com.example.bevasarlolista.network.NetworkManager
import retrofit2.awaitResponse

object UserLoggedIn {
    private var user = MutableLiveData<User?>(null)

    suspend fun authUser(username: String, password: String): Boolean{
        val response = try{
            NetworkManager.authUser(username, password).awaitResponse()
        } catch (e: java.lang.Exception){
            return false
        }

        if(response == null || !response.isSuccessful || response.body() == null)
            return false

        user.value = response.body()
        return true
    }
    /*
    fun getLiveEditor(): LiveData<Editor?>{
        return editor
    }

     */

    fun getUser(): User?{
        return user.value
    }

    fun logout(){
        user.value = null
    }
}