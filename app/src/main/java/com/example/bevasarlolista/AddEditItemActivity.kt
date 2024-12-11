package com.example.bevasarlolista

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bevasarlolista.databinding.ActivityAddEditItemBinding
import com.example.bevasarlolista.model.Item
import com.example.bevasarlolista.model.User
import com.example.bevasarlolista.network.NetworkManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.await
import java.util.Date

class AddEditItemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditItemBinding
    private lateinit var currentUser: User
    private val ioScope = CoroutineScope(Dispatchers.IO)
    private val userMap = mutableMapOf<String, Int>() // Map of usernames to user IDs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val item = intent.getSerializableExtra("item") as? Item
        currentUser = intent.getSerializableExtra("currentUser") as User

        val isEditMode = item != null

        if (isEditMode) {
            binding.etItemName.setText(item?.name)
            binding.etItemAmount.setText(item?.amount.toString())
            binding.etItemPrice.setText(item?.price.toString())
        }

        // Load users into AutoCompleteTextView
        ioScope.launch {
            loadUsers()
            withContext(Dispatchers.Main) {
                setupUserAutoComplete(item)
            }
        }

        binding.btnSave.setOnClickListener {
            val name = binding.etItemName.text.toString()
            val amount = binding.etItemAmount.text.toString().toDoubleOrNull() ?: 0.0
            val price = binding.etItemPrice.text.toString().toDoubleOrNull() ?: 0.0

            if (name.isBlank()) {
                Toast.makeText(this, "Item name cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val forUserId = userMap[binding.acForUserId.text.toString()]
            val checkedById = userMap[binding.acCheckedById.text.toString()]

            val newItem = Item(
                id = item?.id ?: 0,
                name = name,
                amount = amount,
                price = price,
                purchaseDate = item?.purchaseDate ?: Date(),
                forUserId = forUserId,
                checkedById = checkedById
            )

            saveItem(newItem, isEditMode)
        }
    }

    private suspend fun loadUsers() {
        try {
            val users = NetworkManager.getUsers().await()
            users.forEach { user ->
                userMap[user.username] = user.id
            }
        } catch (ex: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@AddEditItemActivity, "Failed to load users: ${ex.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupUserAutoComplete(item: Item?) {
        val usernames = userMap.keys.toList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, usernames)

        binding.acForUserId.setAdapter(adapter)
        binding.acCheckedById.setAdapter(adapter)

        // Pre-fill AutoCompleteTextView if editing
        item?.forUserId?.let { id ->
            binding.acForUserId.setText(userMap.entries.find { it.value == id }?.key ?: "")
        }

        item?.checkedById?.let { id ->
            binding.acCheckedById.setText(userMap.entries.find { it.value == id }?.key ?: "")
        }
    }

    private fun saveItem(item: Item, isEditMode: Boolean) {
        ioScope.launch {
            try {
                val response = if (isEditMode) {
                    NetworkManager.updateItem(item).execute()
                } else {
                    NetworkManager.addItem(item).execute()
                }

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val resultItem = response.body()
                        if (resultItem != null) {
                            val resultIntent = Intent()
                            resultIntent.putExtra("item", resultItem)
                            setResult(Activity.RESULT_OK, resultIntent)
                            Toast.makeText(
                                this@AddEditItemActivity,
                                if (isEditMode) "Item updated successfully" else "Item added successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        } else {
                            Toast.makeText(
                                this@AddEditItemActivity,
                                "Unexpected response format",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@AddEditItemActivity,
                            "Failed to save item: ${response.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (ex: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@AddEditItemActivity,
                        "Error: ${ex.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("AddEditItem", "Error saving item", ex)
                }
            }
        }
    }
}
