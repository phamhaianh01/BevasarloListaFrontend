package com.example.bevasarlolista

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.coroutineScope
import com.example.bevasarlolista.databinding.ActivityMainBinding
import com.example.bevasarlolista.holder.UserLoggedIn
import com.example.bevasarlolista.model.User
import com.example.bevasarlolista.network.NetworkManager
import com.example.bevasarlolista.network.UserApi
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.await
import retrofit2.awaitResponse

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var userApi: UserApi

    private val users = listOf(
        User(1, "Test", "123"),
        User(2, "Test2", "123")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //setupRetrofit()

        // Set login button listener
        binding.buttonLogin.setOnClickListener {
            val username = binding.editTextUsername.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                showToast("Please fill out all fields")
            } else {
                this.lifecycle.coroutineScope.launch {
                    authenticateUser(username, password)
                }
            }
        }
    }
    /*
    private fun setupRetrofit() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://your-api-url.com/") // Replace with your actual API URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        userApi = retrofit.create(UserApi::class.java)
    }

     */

    private suspend fun authenticateUser(username: String, password: String) {
        try {
            //navigateToListActivity(User(1, "asd", "asd"));
            val user = users.find { it.username == username && it.password == password }
            //val user = NetworkManager.authUser(username, password).await();
            if (user != null) {
                navigateToListActivity(user)
            } else {
                showToast("Invalid username or password")
            }
        } catch (ex: Exception){
            showToast(ex.message ?: "Login failed");
        }
    }

    private fun navigateToListActivity(user: User) {
        UserLoggedIn.setUser(user) // Add this line to set the logged-in user
        val intent = Intent(this, ListActivity::class.java)
        startActivity(intent)
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
