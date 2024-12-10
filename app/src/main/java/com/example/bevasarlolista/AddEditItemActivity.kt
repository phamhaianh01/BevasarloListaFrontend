package com.example.bevasarlolista

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import java.util.Date

class AddEditItemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditItemBinding
    private lateinit var currentUser: User // Pass the currentUser from ListActivity
    private val ioScope = CoroutineScope(Dispatchers.IO) // Coroutine scope for backend communication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val item = intent.getSerializableExtra("item") as? Item
        currentUser = intent.getSerializableExtra("currentUser") as User // Get currentUser from intent
        val isEditMode = item != null

        if (isEditMode) {
            binding.etItemName.setText(item?.name)
            binding.etItemAmount.setText(item?.amount.toString())
            binding.etItemPrice.setText(item?.price.toString())
            binding.cbForUser.isChecked = item?.forUserId != null
        }

        binding.btnSave.setOnClickListener {
            val name = binding.etItemName.text.toString()
            val amount = binding.etItemAmount.text.toString().toDoubleOrNull() ?: 0.0
            val price = binding.etItemPrice.text.toString().toDoubleOrNull() ?: 0.0
            //val date = DateConverter.convertToSimpleDate(item?.purchaseDate ?: Date())


            if (name.isBlank()) {
                Toast.makeText(this, "Item name cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newItem = Item(
                id = item?.id ?: 0,
                name = name,
                amount = amount,
                price = price,
                purchaseDate = item?.purchaseDate ?: Date(), // Ensure this is properly formatted as "yyyy-MM-dd"
                forUserId = if (binding.cbForUser.isChecked) currentUser.id else null,
                checkedById = null
            )


            // Save the item to the backend
            saveItem(newItem, isEditMode)
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
                        val resultItem = response.body() // Safely deserialize the response
                        if (resultItem != null) {
                            val resultIntent = Intent()
                            resultIntent.putExtra("item", resultItem) // Use deserialized item
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
                    Log.e("AddEditItem", "Error saving item", ex) // Log the exception for debugging
                }
            }
        }
    }

}
