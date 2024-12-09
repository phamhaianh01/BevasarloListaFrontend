package com.example.bevasarlolista

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.bevasarlolista.databinding.ActivityAddEditItemBinding
import com.example.bevasarlolista.model.Item
import com.example.bevasarlolista.model.User
import java.util.Date

class AddEditItemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditItemBinding
    private lateinit var currentUser: User // Pass the currentUser from ListActivity

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
            binding.cbForUser.isChecked = item?.forUser != null
        }

        binding.btnSave.setOnClickListener {
            val name = binding.etItemName.text.toString()
            val amount = binding.etItemAmount.text.toString().toDoubleOrNull() ?: 0.0
            val price = binding.etItemPrice.text.toString().toDoubleOrNull() ?: 0.0

            val newItem = Item(
                id = item?.id ?: System.currentTimeMillis().toInt(),
                name = name,
                amount = amount,
                price = price,
                purchaseDate = item?.purchaseDate ?: Date(),
                forUser = if (binding.cbForUser.isChecked) currentUser else null, // Assign to currentUser or Shared
                checkedBy = null
            )

            val resultIntent = Intent()
            resultIntent.putExtra("item", newItem)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }
}
